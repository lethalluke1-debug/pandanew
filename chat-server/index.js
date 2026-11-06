// Lethal Chat server (client edition): merges Twitch + YouTube + TikTok live chat and pushes it to the
// Lethal Client's in-game Stream overlay, instantly over a websocket.
// Connect with: wss://<this server>/chat?twitch=channel&youtube=@handle&tiktok=username
import http from 'node:http';
import { WebSocketServer, WebSocket } from 'ws';
import { TikTokLiveConnection, WebcastEvent, ControlEvent, UserOfflineError } from 'tiktok-live-connector';
import { Innertube } from 'youtubei.js';

const PORT = process.env.PORT || 10000;
const RETRY_OFFLINE_MS = 60_000;
const MAX_SOURCES = Number(process.env.MAX_SOURCES || 300);   // distinct live channels at once (set higher on a bigger plan)

// ---------- debug info (open /debug on the server to see it) ----------
const debug = new Map(); // key -> { log:[], counts:{} }
function dbg(key) { if (!debug.has(key)) debug.set(key, { log: [], counts: {} }); return debug.get(key); }
function note(key, line) { const d = dbg(key); d.log.push(new Date().toISOString().slice(11, 19) + ' ' + line); if (d.log.length > 40) d.log.shift(); console.log('[' + key + '] ' + line); }
function src_count(key) { const s = sources.get(key); return s ? s.subs.size : 0; }
function count(key, what) { const c = dbg(key).counts; c[what] = (c[what] || 0) + 1; }

// ---------- shared sources: one reader per channel, shared by every open page ----------
const sources = new Map(); // key -> { subs:Set<ws>, stop(), status }

function broadcast(src, msg) {
  const text = JSON.stringify(msg);
  if (msg.type === 'status') src.status = msg;
  for (const ws of src.subs) if (ws.readyState === 1) ws.send(text);
}

function subscribe(key, ws, create) {
  let src = sources.get(key);
  if (!src) {
    if (sources.size >= MAX_SOURCES) {
      ws.send(JSON.stringify({ type: 'status', platform: key.split(':')[0], state: 'err', text: 'Server is busy, try again soon' }));
      return;
    }
    src = { subs: new Set([ws]), status: null, stop: () => {} };
    sources.set(key, src);
    src.stop = create(msg => broadcast(src, msg));
  } else {
    if (src.status && ws.readyState === 1) ws.send(JSON.stringify(src.status));
    src.subs.add(ws);
  }
  ws.on('close', () => {
    src.subs.delete(ws);
    if (src.subs.size === 0) {
      // keep it briefly in case the page is just reloading
      setTimeout(() => { if (src.subs.size === 0 && sources.get(key) === src) { src.stop(); sources.delete(key); } }, 30_000);
    }
  });
}

// ---------- TikTok ----------
function tiktokSource(username) {
  const K = 'tiktok:' + username;
  return emit => {
    let stopped = false, conn = null, timer = null;
    const status = (state, text) => { note(K, 'status ' + state + ': ' + text); emit({ type: 'status', platform: 'tiktok', state, text }); };
    const start = async () => {
      if (stopped) return;
      status('wait', 'Connecting to @' + username + '…');
      conn = new TikTokLiveConnection(username, { processInitialData: false });
      conn.on(ControlEvent.WEBSOCKET_CONNECTED, () => note(K, 'websocket connected'));
      conn.on(ControlEvent.ERROR, e => note(K, 'error: ' + String((e && (e.exception && e.exception.message || e.info || e.message)) || e).slice(0, 200)));
      conn.on(ControlEvent.WEBSOCKET_DATA, () => count(K, 'websocketData'));
      conn.on(WebcastEvent.MEMBER, () => count(K, 'member'));
      conn.on(WebcastEvent.LIKE, () => count(K, 'like'));
      conn.on(WebcastEvent.ROOM_USER, d => {
        count(K, 'roomUser');
        const n = d && (d.viewerCount != null ? d.viewerCount : d.viewer_count);
        if (n != null) emit({ type: 'viewers', platform: 'tiktok', count: Number(n) });
      });
      conn.on(WebcastEvent.CHAT, d => {
        count(K, 'chat');
        const u = d.user || {};
        const text = d.comment || d.content || '';
        const name = u.nickname || u.displayId || u.uniqueId || d.nickname || d.uniqueId || 'viewer';
        if (dbg(K).counts.chat <= 5) note(K, 'chat from ' + name + ': ' + JSON.stringify(String(text)).slice(0, 80) + ' -> sent to ' + src_count(K) + ' page(s)');
        if (text) emit({ type: 'chat', platform: 'tiktok', name, text: String(text) });
      });
      conn.on(WebcastEvent.STREAM_END, () => { status('wait', 'Live ended, waiting for next live'); retry(); });
      conn.on('disconnected', () => { if (!stopped) { status('wait', 'Reconnecting…'); retry(10_000); } });
      try {
        await conn.connect();
        status('on', 'Live: @' + username);
      } catch (e) {
        note(K, 'connect failed: ' + String((e && e.message) || e).slice(0, 200));
        if (e instanceof UserOfflineError || /offline|not live|isn't live/i.test(String(e && e.message))) status('wait', '@' + username + ' isn\'t live yet. Checking every minute');
        else status('err', 'Couldn\'t connect: ' + String((e && e.message) || e).slice(0, 80));
        retry();
      }
    };
    const retry = (ms = RETRY_OFFLINE_MS) => {
      clearTimeout(timer);
      try { conn && conn.disconnect(); } catch (e) {}
      if (!stopped) timer = setTimeout(start, ms);
    };
    start();
    return () => { stopped = true; clearTimeout(timer); try { conn && conn.disconnect(); } catch (e) {} };
  };
}

// ---------- YouTube ----------
let innertube = null;
const getInnertube = async () => innertube || (innertube = await Innertube.create({ retrieve_player: false }));

async function findLiveViaInnertube(input, K) {
  const yt = await getInnertube();
  let browseId = (input.match(/(UC[\w-]{22})/) || [])[1];
  if (!browseId) {
    const handle = '@' + input.replace(/^.*youtube\.com\//, '').replace(/^@/, '').split(/[/?]/)[0];
    const ep = await yt.resolveURL('https://www.youtube.com/' + handle);
    browseId = ep && ep.payload && ep.payload.browseId;
    note(K, 'handle ' + handle + ' -> channel ' + browseId);
  }
  if (!browseId) throw new Error('Couldn\'t find that YouTube channel');
  const channel = await yt.getChannel(browseId);
  let tab;
  try { tab = await channel.getLiveStreams(); } catch (e) { note(K, 'no live tab: ' + e.message); return null; }
  const vids = tab.videos || [];
  note(K, 'live tab has ' + vids.length + ' items');
  for (const v of vids) {
    let live = false;
    try { live = !!v.is_live; } catch (e) {}
    if (!live) { try { live = /"(LIVE|BADGE_STYLE_TYPE_LIVE_NOW)"|LIVE NOW/i.test(JSON.stringify(v)); } catch (e) {} }
    const id = v.video_id || v.id || (v.content_id);
    if (live && id) return id;
  }
  return null;
}

async function findLiveVideoId(input, K) {
  const direct0 = input.trim().match(/(?:v=|youtu\.be\/|\/live\/|\/shorts\/|\/embed\/)([\w-]{11})/) || input.trim().match(/^([\w-]{11})$/);
  if (direct0) return direct0[1];
  try {
    const id = await findLiveViaInnertube(input, K);
    note(K, 'innertube search found: ' + id);
    if (id) return id;
  } catch (e) { note(K, 'innertube search failed: ' + String(e && e.message).slice(0, 200)); }
  const id = await findLiveViaPage(input, K);
  note(K, 'page search found: ' + id);
  return id;
}

async function findLiveViaPage(input, K) {
  input = input.trim();
  const direct = input.match(/(?:v=|youtu\.be\/|\/live\/|\/shorts\/|\/embed\/)([\w-]{11})/) || input.match(/^([\w-]{11})$/);
  if (direct) return direct[1];
  let path;
  const chan = input.match(/(UC[\w-]{22})/);
  if (chan) path = '/channel/' + chan[1];
  else path = '/@' + input.replace(/^.*youtube\.com\//, '').replace(/^@/, '').split(/[/?]/)[0];
  const res = await fetch('https://www.youtube.com' + path + '/live', {
    headers: { 'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0 Safari/537.36', 'accept-language': 'en-US,en;q=0.9', cookie: 'CONSENT=YES+1' }
  });
  if (res.status === 404) throw new Error('Couldn\'t find that YouTube channel');
  const html = await res.text();
  note(K, 'live page status ' + res.status + ', ' + html.length + ' bytes' + (/not a bot|consent\.youtube/i.test(html) ? ' (YouTube bot check!)' : ''));
  const canon = html.match(/<link rel="canonical" href="https:\/\/www\.youtube\.com\/watch\?v=([\w-]{11})"/);
  if (!canon) return null;
  if (!/"isLive":true|"isLiveNow":true|"liveBroadcastDetails"/.test(html)) return null;
  return canon[1];
}

// Pull a concurrent-viewer number out of a youtubei.js VideoInfo, tolerant of shape changes.
function ytViewerCount(info) {
  try {
    const vc = info && info.primary_info && info.primary_info.view_count;
    const candidates = [
      vc && vc.view_count && vc.view_count.text,
      vc && vc.short_view_count && vc.short_view_count.text,
      vc && vc.original_view_count,
      info && info.basic_info && info.basic_info.view_count
    ];
    for (const c of candidates) {
      if (c == null) continue;
      const s = String(c);
      // Prefer live "watching now" counts; fall back to any number.
      const digits = s.replace(/[^\d]/g, '');
      if (digits) return Number(digits);
    }
  } catch (e) {}
  return -1;
}

function youtubeSource(handle) {
  const K = 'youtube:' + handle.toLowerCase();
  return emit => {
    let stopped = false, chat = null, timer = null, poll = null, yt = null, videoId = null;
    const status = (state, text) => { note(K, 'status ' + state + ': ' + text); emit({ type: 'status', platform: 'youtube', state, text }); };
    const stopPoll = () => { clearInterval(poll); poll = null; };
    const retry = (ms = RETRY_OFFLINE_MS) => {
      clearTimeout(timer);
      stopPoll();
      emit({ type: 'viewers', platform: 'youtube', count: -1 });
      try { chat && chat.stop(); } catch (e) {}
      chat = null;
      if (!stopped) timer = setTimeout(start, ms);
    };
    const pollViewers = async () => {
      if (stopped || !yt || !videoId) return;
      try {
        const vi = await yt.getInfo(videoId);
        const n = ytViewerCount(vi);
        if (n >= 0) emit({ type: 'viewers', platform: 'youtube', count: n });
      } catch (e) { note(K, 'viewer poll: ' + String((e && e.message) || e).slice(0, 80)); }
    };
    const start = async () => {
      if (stopped) return;
      status('wait', 'Looking for your live stream…');
      try {
        const id = await findLiveVideoId(handle, K);
        note(K, 'using video ' + id);
        if (!id) { status('wait', 'Not live yet. Checking every minute'); return retry(); }
        yt = await getInnertube();
        videoId = id;
        const info = await yt.getInfo(id);
        // First viewer reading + a 30s poll for the rest of the stream.
        const first = ytViewerCount(info);
        if (first >= 0) emit({ type: 'viewers', platform: 'youtube', count: first });
        stopPoll();
        poll = setInterval(pollViewers, 30_000);
        chat = info.getLiveChat();
        chat.on('start', () => {
          status('on', 'Live');
          try { chat.applyFilter('LIVE_CHAT'); note(K, 'switched to all live chat'); } catch (e) { note(K, 'filter: ' + e.message); }
        });
        chat.on('chat-update', action => {
          count(K, 'update:' + action.type);
          if (action.type !== 'AddChatItemAction' || !action.item) return;
          const item = action.item;
          count(K, 'item:' + item.type);
          if (item.type !== 'LiveChatTextMessage' && item.type !== 'LiveChatPaidMessage') return;
          const text = item.message ? item.message.toString() : '';
          const name = item.author && item.author.name ? String(item.author.name) : 'viewer';
          const badges = [];
          if (item.author && item.author.is_moderator) badges.push('mod');
          if (item.author && item.author.badges && item.author.badges.some(b => /member/i.test(String(b.tooltip || '')))) badges.push('sub');
          if ((dbg(K).counts['item:' + item.type] || 0) <= 3) note(K, 'chat from ' + name + ': ' + JSON.stringify(text).slice(0, 80) + ' -> sent to ' + src_count(K) + ' page(s)');
          if (text) emit({ type: 'chat', platform: 'youtube', name: name.replace(/^@/, ''), text, badges });
        });
        chat.on('end', () => { status('wait', 'Live ended, waiting for next live'); retry(); });
        chat.on('error', e => {
          note(K, 'chat error: ' + String((e && e.message) || e).slice(0, 200)); status('wait', 'Reconnecting…'); retry(15_000); });
        chat.start();
      } catch (e) {
        note(K, 'failed: ' + String((e && e.stack) || e).slice(0, 300));
        status('err', String((e && e.message) || e).slice(0, 80) + '. Trying again soon');
        retry();
      }
    };
    start();
    return () => { stopped = true; clearTimeout(timer); stopPoll(); try { chat && chat.stop(); } catch (e) {} };
  };
}

// ---------- Twitch (anonymous IRC — no login needed) ----------
// Public web Client-ID used read-only by many overlay tools to query stream state.
const TWITCH_GQL_ID = 'kimne78kx3ncx6brgo4mv6wki5h1ko';

// Live status + concurrent viewers without auth. Returns { live, viewers, roomId } or null on error.
async function twitchStreamInfo(login) {
  try {
    const q = { query: `query{user(login:"${login}"){id stream{viewersCount type}}}` };
    const r = await fetch('https://gql.twitch.tv/gql', {
      method: 'POST',
      headers: { 'Client-ID': TWITCH_GQL_ID, 'Content-Type': 'application/json' },
      body: JSON.stringify(q)
    });
    if (!r.ok) return null;
    const j = await r.json();
    const u = j && j.data && j.data.user;
    if (!u) return null;
    const live = !!(u.stream && (u.stream.type === 'live' || u.stream.viewersCount != null));
    return { live, viewers: live ? Number(u.stream.viewersCount || 0) : 0, roomId: u.id || '' };
  } catch (e) { return null; }
}

function twitchSource(channel) {
  const chan = channel.toLowerCase();
  const K = 'twitch:' + chan;
  return emit => {
    let stopped = false, ws = null, timer = null, poll = null, roomId = '';
    const status = (state, text) => { note(K, 'status ' + state + ': ' + text); emit({ type: 'status', platform: 'twitch', state, text }); };
    const retry = (ms = 15_000) => { clearTimeout(timer); try { ws && ws.close(); } catch (e) {} if (!stopped) timer = setTimeout(start, ms); };
    // Poll real live status + viewer count (anonymous IRC alone can't tell if the channel is live).
    const checkLive = async () => {
      const info = await twitchStreamInfo(chan);
      if (stopped || !info) return;
      roomId = info.roomId || roomId;
      emit({ type: 'viewers', platform: 'twitch', count: info.live ? info.viewers : -1 });
      status(info.live ? 'on' : 'wait', info.live ? ('Live — ' + info.viewers + ' viewers') : 'Chat connected (offline)');
    };
    const start = () => {
      if (stopped) return;
      status('wait', 'Connecting to Twitch #' + chan + '…');
      ws = new WebSocket('wss://irc-ws.chat.twitch.tv:443');
      ws.on('open', () => {
        ws.send('CAP REQ :twitch.tv/tags twitch.tv/commands');
        ws.send('NICK justinfan' + Math.floor(Math.random() * 90000 + 10000));
        ws.send('JOIN #' + chan);
        checkLive();
      });
      ws.on('message', buf => {
        for (const line of buf.toString().split('\r\n')) {
          if (!line) continue;
          if (line.startsWith('PING')) { ws.send('PONG :tmi.twitch.tv'); continue; }
          const m = line.match(/^(?:@([^ ]+) )?:([^!]+)![^ ]+ PRIVMSG #[^ ]+ :(.*)$/);
          if (!m) continue;
          const tags = {};
          if (m[1]) for (const kv of m[1].split(';')) { const i = kv.indexOf('='); tags[kv.slice(0, i)] = kv.slice(i + 1); }
          const name = tags['display-name'] || m[2];
          const text = m[3];
          count(K, 'chat');
          if (dbg(K).counts.chat <= 5) note(K, 'chat from ' + name + ': ' + JSON.stringify(text).slice(0, 80) + ' -> ' + src_count(K) + ' page(s)');
          if (text) emit({
            type: 'chat', platform: 'twitch', name, text,
            color: tags['color'] || '',
            badges: tags['badges'] || '',       // e.g. "broadcaster/1,subscriber/12"
            emotes: tags['emotes'] || '',       // e.g. "25:0-4,12-16"
            roomId: tags['room-id'] || roomId
          });
        }
      });
      ws.on('close', () => { if (!stopped) { status('wait', 'Reconnecting…'); retry(); } });
      ws.on('error', e => note(K, 'error: ' + String((e && e.message) || e).slice(0, 120)));
    };
    start();
    poll = setInterval(checkLive, 30_000);
    return () => { stopped = true; clearTimeout(timer); clearInterval(poll); try { ws && ws.close(); } catch (e) {} };
  };
}

// ---------- server ----------
const server = http.createServer((req, res) => {
  if (req.url.startsWith('/debug')) {
    const out = {};
    for (const [k, d] of debug) out[k] = { open_pages: sources.has(k) ? sources.get(k).subs.size : 0, counts: d.counts, log: d.log };
    res.writeHead(200, { 'content-type': 'application/json', 'access-control-allow-origin': '*' });
    res.end(JSON.stringify(out, null, 2));
    return;
  }
  res.writeHead(200, { 'content-type': 'text/plain', 'access-control-allow-origin': '*' });
  res.end('Lethal Chat server is running.');
});
const wss = new WebSocketServer({ server, path: '/chat' });
const clean = v => String(v || '').trim().slice(0, 120);

wss.on('connection', (ws, req) => {
  const q = new URL(req.url, 'http://x').searchParams;
  const twitch = clean(q.get('twitch')).replace(/^.*twitch\.tv\//, '').replace(/^@/, '').split(/[/?]/)[0].toLowerCase();
  const tiktok = clean(q.get('tiktok')).replace(/^.*tiktok\.com\//, '').replace(/^@/, '').split(/[/?]/)[0].toLowerCase();
  const youtube = clean(q.get('youtube'));
  if (twitch && /^[\w]{2,30}$/.test(twitch)) subscribe('twitch:' + twitch, ws, twitchSource(twitch));
  if (tiktok && /^[\w.]{2,30}$/.test(tiktok)) subscribe('tiktok:' + tiktok, ws, tiktokSource(tiktok));
  if (youtube) subscribe('youtube:' + youtube.toLowerCase(), ws, youtubeSource(youtube));
  ws.send(JSON.stringify({ type: 'hello' }));
  const ping = setInterval(() => { if (ws.readyState === 1) ws.ping(); }, 25_000);
  ws.on('close', () => clearInterval(ping));
});

server.listen(PORT, () => console.log('Lethal Chat server listening on ' + PORT));

// Keep-alive: ping ourselves so a free host doesn't spin the server down while people are connected.
const SELF = process.env.RENDER_EXTERNAL_URL || process.env.SELF_URL;
if (SELF) setInterval(() => { fetch(SELF).catch(() => {}); }, 10 * 60 * 1000);
