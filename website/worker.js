// Lethal Client license server (Cloudflare Worker).
//
// Needs, in the Worker's Settings:
//   D1 database binding   DB                   -> where keys and settings are stored (changes are instant)
//   KV namespace binding  LICENSES             -> stores mod updates and shared cape pictures (and the old keys, copied into DB once)
//   Secret                ADMIN_PASSWORD       -> the password for the /admin page
//   Secret                LICENSE_PRIVATE_KEY  -> the Ed25519 private key (PKCS8, base64) that matches the mod
//
// For the store (Stripe checkout) — only needed if you want people to buy on the site:
//   Secret                STRIPE_SECRET_KEY     -> Stripe secret key (sk_live_... or sk_test_...)
//   Secret                STRIPE_WEBHOOK_SECRET -> signing secret of the /api/stripe-webhook endpoint (whsec_...)
//   Secret                STRIPE_PRICE_MONTHLY  -> Price ID of the $2.99/month recurring price (price_...)
//   Secret                STRIPE_PRICE_YEARLY   -> Price ID of the $9.99/year recurring price (price_...)
//   Secret                STRIPE_PRICE_LIFETIME -> Price ID of the $14.99 one-time price (price_...)
//   Secret (optional)     RESEND_API_KEY        -> to also email the key to the buyer (via resend.com)
//   Secret (optional)     EMAIL_FROM            -> the from-address for that email (e.g. keys@yourdomain.com)
//
// Routes:
//   GET  /                  the store / landing page (Buy buttons)
//   GET  /success           shown after a paid checkout; mints and displays the license key
//   POST /api/checkout      the Buy buttons call this: {plan:"monthly"|"lifetime"} -> Stripe hosted checkout URL
//   POST /api/stripe-webhook Stripe calls this: mints keys on payment, revokes on subscription cancel
//   GET  /admin             key manager website
//   POST /api/verify        called by the mod: {key, hwid, user, nonce, version, uuid, cape, nearby}
//   POST /api/cape          the mod uploads its player's cape picture
//   GET  /api/cape/<sha>    other players' games download a cape picture
//   GET  /api/update        the mod downloads the newest uploaded jar (only with a working key)
//   /api/admin/*            used by the website (Authorization: Bearer <ADMIN_PASSWORD>)

// Only fill these in if you can't find "Variables and Secrets" in the Worker's settings.
// The secrets there take priority. Never share this file once these are filled in.
// No recovery password in the source any more: admin is ONLY the ADMIN_PASSWORD secret you set in
// Cloudflare. You MUST set ADMIN_PASSWORD (Workers -> Settings -> Variables -> Secret) or the admin panel
// can't be opened at all. This keeps the source free of any admin credential, so a leaked copy is useless.
const FALLBACK_ADMIN_PASSWORD = "";
const FALLBACK_PRIVATE_KEY = "";
const FALLBACK_STRIPE_SECRET_KEY = "";
const FALLBACK_STRIPE_WEBHOOK_SECRET = "";
const FALLBACK_STRIPE_PRICE_MONTHLY = "";
const FALLBACK_STRIPE_PRICE_LIFETIME = "";
const FALLBACK_STRIPE_PRICE_YEARLY = "";
const FALLBACK_RESEND_API_KEY = "";
const FALLBACK_EMAIL_FROM = "";

const GRACE_MS = 12 * 60 * 60 * 1000; // how long the mod may keep working offline after a good check
const KEY_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

// Bump this every time the file changes. Open https://lethalclient.com/api/version on the live site:
// if it shows this exact string, your paste-and-Deploy worked and the newest code is live. If it shows
// something older (or 404s), Cloudflare is still running an old file and the deploy didn't take.
const BUILD_VERSION = "2026-11-04-targeted-builds-and-total-only";

// ---------- launch countdown + early-access key ----------
// Launch: Saturday, October 31 2026 at 1:00 PM Pacific. On that date Pacific is PDT (UTC-7, since
// daylight saving doesn't end until Nov 1), so the offset is -07:00 — this fires at 1 PM on the
// Pacific wall clock. Until this moment, visitors see the countdown page.
const LAUNCH_TS = Date.parse("2026-10-31T13:00:00-07:00");
// The secret key that unlocks the site before launch (typed into the key box in the countdown page).
// Early-access gate (the pre-launch "coming soon" page). The site is already launched, so this is dormant.
// Both are blank so no early-access secret sits in the source; the guards below make blank never grant access.
// If you ever need the gate again, set ACCESS_KEY/ACCESS_TOKEN as Cloudflare secrets and read them from env.
const ACCESS_KEY = "";
const ACCESS_TOKEN = "";

function isLaunched() {
  return !isNaN(LAUNCH_TS) && Date.now() >= LAUNCH_TS;
}

function hasAccess(request) {
  if (!ACCESS_TOKEN) return false;    // blank token never grants access
  const cookie = request.headers.get("Cookie") || "";
  return cookie.split(/;\s*/).some((c) => c === "lc_access=" + ACCESS_TOKEN);
}

async function grantAccess(request) {
  const body = await request.json().catch(() => ({}));
  if (ACCESS_KEY && body && body.key === ACCESS_KEY) {    // blank key never unlocks
    return new Response(JSON.stringify({ ok: true }), {
      status: 200,
      headers: {
        "content-type": "application/json",
        "cache-control": "no-store",
        "Set-Cookie": "lc_access=" + ACCESS_TOKEN + "; Path=/; Max-Age=31536000; HttpOnly; Secure; SameSite=Lax",
      },
    });
  }
  return new Response(JSON.stringify({ ok: false }), {
    status: 200,
    headers: { "content-type": "application/json", "cache-control": "no-store" },
  });
}

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    try {
      // ---- Abuse protection: shed load BEFORE any database work, so a flood can't melt D1. ----
      // Stripe's webhook is never throttled/killed (Stripe must always reach it).
      const isApi = url.pathname.startsWith("/api/") && url.pathname !== "/api/stripe-webhook";
      if (isApi) {
        const clientIp = request.headers.get("cf-connecting-ip") || request.headers.get("x-real-ip") || "0.0.0.0";
        const tooMany = () => new Response(JSON.stringify({ error: "Too many requests. Slow down." }),
          { status: 429, headers: { "content-type": "application/json", "cache-control": "no-store", "retry-after": "60" } });
        const unavailable = () => new Response(JSON.stringify({ error: "Temporarily unavailable." }),
          { status: 503, headers: { "content-type": "application/json", "cache-control": "no-store", "retry-after": "120" } });
        // 1) INSTANT KILL SWITCHES — add these as Worker Variables in the dashboard to stop a live attack
        //    with no redeploy. DISABLE_API=1 sheds all API traffic; DISABLE_VERIFY=1 sheds just the mod's
        //    /api/verify flood (the heaviest, unauthenticated DB path).
        if (env.DISABLE_API === "1") return unavailable();
        if (url.pathname === "/api/verify" && env.DISABLE_VERIFY === "1") return unavailable();
        if (url.pathname === "/api/auth/signup" && env.DISABLE_SIGNUP === "1") return unavailable();
        // 2) PER-IP RATE LIMIT — needs a Rate Limiting binding named API_LIMITER (see the comment block
        //    at the top of this file). Rejects before touching D1. Safe no-op if the binding is absent.
        if (env.API_LIMITER && typeof env.API_LIMITER.limit === "function") {
          try {
            const rl = await env.API_LIMITER.limit({ key: clientIp + "|" + url.pathname });
            if (rl && rl.success === false) return tooMany();
          } catch (_) { /* binding misconfigured — never take the site down over it */ }
        }
      }
      if (url.pathname === "/api/version") {
        // Pure deploy check — no DB, no secrets. If this matches BUILD_VERSION, the newest file is live.
        return new Response(JSON.stringify({ version: BUILD_VERSION }), {
          headers: { "content-type": "application/json", "cache-control": "no-store", "access-control-allow-origin": "*" },
        });
      }
      if (url.pathname === "/admin") {
        // Optional one-click login: /admin?key=PASSWORD opens straight into the panel (no typing).
        const key = (url.searchParams.get("key") || "").trim();
        let html = ADMIN_HTML;
        if (key && adminPwOk(key, env)) {
          html = html.replace("<script>", "<script>try{localStorage.setItem('lc_admin_pw'," + JSON.stringify(key) + ");}catch(e){}\n");
        }
        return new Response(html, { headers: { "content-type": "text/html; charset=utf-8", "cache-control": "no-store" } });
      }
      if (url.pathname === "/" || url.pathname === "/buy") {
        // Before launch, everyone sees the countdown — unless they've unlocked it with the key.
        const launched = isLaunched();
        if (launched || hasAccess(request)) {
          // The logout button only appears when you got in with the key before launch.
          const html = STORE_HTML.replace("{{LOGOUT}}", !launched && hasAccess(request) ? LOGOUT_BUTTON : "");
          return new Response(html, { headers: { "content-type": "text/html; charset=utf-8", "cache-control": "no-store" } });
        }
        return new Response(COUNTDOWN_HTML, { headers: { "content-type": "text/html; charset=utf-8", "cache-control": "no-store" } });
      }
      if (url.pathname === "/api/access" && request.method === "POST") {
        return await grantAccess(request);
      }
      if (url.pathname === "/api/logout" && request.method === "POST") {
        // Clear the access cookie so the countdown locks again until the key is re-entered.
        return new Response(JSON.stringify({ ok: true }), {
          status: 200,
          headers: {
            "content-type": "application/json",
            "cache-control": "no-store",
            "Set-Cookie": "lc_access=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Lax",
          },
        });
      }
      await setup(env);
      if (url.pathname === "/login") {
        if (await currentUser(request, env)) return Response.redirect(url.origin + "/dashboard", 302);
        // Inject the Turnstile captcha into the signup form only when it's configured (TURNSTILE_SITEKEY set).
        const sitekey = env.TURNSTILE_SITEKEY || "";
        const html = LOGIN_HTML
          .replace("{{TURNSTILE_SCRIPT}}", sitekey ? '<script src="https://challenges.cloudflare.com/turnstile/v0/api.js" async defer></script>' : "")
          .replace("{{TURNSTILE_WIDGET}}", sitekey ? '<div class="cf-turnstile" data-sitekey="' + sitekey + '" data-theme="dark" style="margin:10px 0"></div>' : "");
        return storePage(html);
      }
      if (url.pathname === "/dashboard") {
        if (!(await currentUser(request, env))) return Response.redirect(url.origin + "/login", 302);
        return storePage(DASHBOARD_HTML);
      }
      if (url.pathname === "/api/auth/signup" && request.method === "POST") return await authSignup(request, env);
      if (url.pathname === "/api/auth/login" && request.method === "POST") return await authLogin(request, env);
      if (url.pathname === "/api/auth/logout" && request.method === "POST") return await authLogout(request, env);
      if (url.pathname === "/api/account" && request.method === "GET") return await accountInfo(request, env);
      if (url.pathname === "/api/redeem" && request.method === "POST") return await redeemKey(request, env);
      if (url.pathname === "/api/download" && request.method === "GET") return await dashDownload(request, env);
      if (url.pathname === "/download" && request.method === "GET") return await publicDownload(request, env, url);
      if (url.pathname === "/api/stats" && request.method === "GET") return await downloadStats(env);
      if (url.pathname === "/api/billing" && request.method === "GET") return await billingInfo(request, env);
      if (url.pathname === "/api/billing/cancel" && request.method === "POST") return await billingCancel(request, env);
      if (url.pathname === "/api/billing/resume" && request.method === "POST") return await billingResume(request, env);
      if (url.pathname === "/api/billing/switch" && request.method === "POST") return await billingSwitch(request, env);
      if (url.pathname === "/api/billing/upgrade" && request.method === "POST") return await billingUpgrade(request, env, url);
      if (url.pathname === "/success") {
        return await stripeSuccess(request, env, url);
      }
      if (url.pathname === "/api/checkout" && request.method === "POST") {
        return await stripeCheckout(request, env, url);
      }
      if (url.pathname === "/api/cosmetics" && request.method === "GET") {
        return await cosmeticsCatalog(request, env);
      }
      if (url.pathname === "/api/cosmetics/buy" && request.method === "POST") {
        return await cosmeticsBuy(request, env, url);
      }
      if (url.pathname === "/api/stripe-webhook" && request.method === "POST") {
        return await stripeWebhook(request, env);
      }
      if (url.pathname === "/api/cape" && request.method === "POST") {
        return await uploadCape(request, env);
      }
      if (url.pathname.startsWith("/api/cape/") && request.method === "GET") {
        return await downloadCape(url.pathname.slice("/api/cape/".length), env, request, ctx);
      }
      if (url.pathname === "/api/verify" && request.method === "POST") {
        return await verify(request, env);
      }
      if (url.pathname === "/api/update" && request.method === "GET") {
        return await downloadUpdate(request, env);
      }
      if (url.pathname.startsWith("/api/admin/")) {
        if (!(await isAdmin(request, env))) return json({ error: "Wrong password" }, 401);
        if (url.pathname === "/api/admin/upload" && request.method === "POST") return await uploadUpdate(request, env);
        return await admin(url.pathname.slice("/api/admin/".length), request, env);
      }
      return json({ error: "Not found" }, 404);
    } catch (e) {
      return json({ error: String(e && e.message || e) }, 500);
    }
  },
};

// ---------- mod endpoint ----------

async function verify(request, env) {
  const body = await request.json().catch(() => ({}));
  const key = normalizeKey(body.key);
  const hwid = String(body.hwid || "").toLowerCase().replace(/[^a-f0-9]/g, "").slice(0, 128);
  const user = String(body.user || "").slice(0, 32);
  const nonce = String(body.nonce || "").replace(/[^A-Za-z0-9]/g, "").slice(0, 64);
  const now = Date.now();

  const reply = (status, message, extra = {}, outer = {}) =>
    signed(env, { ok: status === "active", status, message, key, hwid, nonce, issued: now, ...extra }, outer);

  // ---- Free / keyless mode ----
  // The client is free: no key, no HWID lock, no tamper check. We identify a player by their Minecraft
  // UUID, register them so they show up in the admin, and hand back their admin-assigned role plus the
  // mod-user list so nametag badges work for everyone. Changing a role in the admin reaches them here.
  if (body.keyless === true) {
    const rawKey = String(body.key || "");           // echoed back verbatim so the mod's signature check passes
    const rawHwid = String(body.hwid || "");
    const uuid = cleanUuid(body.uuid);
    if (!uuid) {
      return signed(env, { ok: false, status: "invalid", message: "No player id.", key: rawKey, hwid: rawHwid, nonce, issued: now });
    }
    const name = user;
    const rawCape = String(body.cape || "");
    const cape = /^[0-9a-f]{64}$/.test(rawCape) || /^anim:[a-z0-9_]{1,24}$/.test(rawCape) ? rawCape : "";
    const rawCos = String(body.cos || "");
    const cos = /^[a-z_]{1,16}:[0-9](,[a-z_]{1,16}:[0-9]){0,7}$/.test(rawCos) ? rawCos : "";
    // The mini figure this player has equipped and is sharing ("" if none / sharing turned off).
    const rawFig = String(body.fig || "");
    const fig = /^[a-z0-9_]{1,24}$/.test(rawFig) ? rawFig : "";
    const me = await env.DB.prepare("SELECT cape, cos, fig, seen, role, ver, mc FROM players WHERE uuid = ?").bind(uuid).first();
    const myRole = me ? me.role : null;
    const pVer = String(body.version || "").slice(0, 32);
    const pMc = (String(body.mc || "").trim() || "26.2").slice(0, 16);
    if (!me || me.cape !== cape || (me.cos || "") !== cos || (me.fig || "") !== fig || (me.ver || "") !== pVer || (me.mc || "") !== pMc || now - (me.seen || 0) > 5 * 60000) {
      // Upsert; never overwrite the admin-assigned role on a routine check-in.
      await env.DB.prepare(
        "INSERT INTO players (uuid, name, cape, seen, key, cos, fig, ver, mc) VALUES (?, ?, ?, ?, '', ?, ?, ?, ?) " +
        "ON CONFLICT(uuid) DO UPDATE SET name=excluded.name, cape=excluded.cape, seen=excluded.seen, cos=excluded.cos, fig=excluded.fig, ver=excluded.ver, mc=excluded.mc"
      ).bind(uuid, name, cape, now, cos, fig, pVer, pMc).run();
    }
    let needCape = false;
    if (/^[0-9a-f]{64}$/.test(cape)) needCape = !(await env.DB.prepare("SELECT sha FROM capes WHERE sha = ?").bind(cape).first());
    const { players, ranks, cosmetics, figures } = await modUsers(env, Array.isArray(body.nearby) ? body.nearby : [], now);
    const settings = await getSettings(env);
    const rank = cleanRank(myRole || "");
    const clientMc = String(body.mc || "").trim() || "26.2";
    const mcL = (settings.mcLatest || {})[clientMc];
    let latestVer = "", latestSha = "";
    if (mcL && mcL.sha) { latestVer = mcL.version; latestSha = mcL.sha; }
    else if (clientMc === "26.2" && settings.update.sha) { latestVer = settings.update.version; latestSha = settings.update.sha; }
    const update = targetUpdateFor(settings, name, uuid, body.version, clientMc)
      || (latestSha && compareVersions(latestVer, String(body.version || "0")) > 0 ? { updateSha: latestSha, updateVersion: latestVer } : {});
    const freeOwned = COSMETICS_CATALOG.map((c) => c.id);
    const base = { key: rawKey, hwid: rawHwid, nonce, issued: now };
    // Tamper check: the mod sends a SHA-256 fingerprint of its own jar. If a fingerprint we recognise for
    // this version exists and this one differs, the jar was modified/cracked — refuse it (cosmetics off).
    // Owners are exempt (they test custom builds); dev runs send no fingerprint and are skipped; an unknown
    // version we have no fingerprint for is allowed, so legit old builds never break.
    const buildFp = String(body.build || "").toLowerCase().replace(/[^a-f0-9]/g, "").slice(0, 64);
    if (buildFp && cleanRank(myRole) !== "owner") {
      const officialFp = new Set();
      if (settings.update.sha) officialFp.add(settings.update.sha.toLowerCase());
      (settings.versionList || []).forEach((x) => { if (x && x.sha) officialFp.add(String(x.sha).toLowerCase()); });
      Object.values(settings.mcLatest || {}).forEach((x) => { if (x && x.sha) officialFp.add(String(x.sha).toLowerCase()); });
      if (!officialFp.has(buildFp)) {
        const reqVer = String(body.version || "");
        const verRec = (settings.versionList || []).find((x) => x && x.v === reqVer);
        const expected = verRec ? String(verRec.sha).toLowerCase()
          : (reqVer && reqVer === settings.update.version ? (settings.update.sha || "").toLowerCase() : "");
        if (expected && expected !== buildFp) {
          return signed(env, { ok: false, status: "invalid", message: "This copy of Lethal Client has been modified. Reinstall the official client.", ...base });
        }
      }
    }
    if (settings.requiredVersion && compareVersions(String(body.version || "0"), settings.requiredVersion) < 0) {
      return signed(env, { ok: false, status: "outdated", message: "A new version of Lethal Client is out. You need it to keep using the client.", ...base, required: settings.requiredVersion, rank, ...update }, { players, ranks, cosmetics, figures, needCape });
    }
    return signed(env, {
      ok: true, status: "active", message: "Lethal Client active.", ...base,
      validUntil: now + GRACE_MS, expires: 0, owned: freeOwned,
      maintenance: settings.maintenance, rank, ...update,
    }, { players, ranks, cosmetics, figures, needCape });
  }

  if (!key || !hwid) return reply("invalid", "Invalid license key.");
  const rec = await getKey(env, key);
  if (!rec) {
    // Old keyed client whose key was wiped when we went free: don't just say "invalid" (that traps them
    // on the key screen). Tell them to update, and hand over the newest build so they restart into the
    // free keyless client.
    const s = await getSettings(env);
    const clientMc = String(body.mc || "").trim() || "26.2";
    const mcL = (s.mcLatest || {})[clientMc];
    let latestVer = "", latestSha = "";
    if (mcL && mcL.sha) { latestVer = mcL.version; latestSha = mcL.sha; }
    else if (clientMc === "26.2" && s.update.sha) { latestVer = s.update.version; latestSha = s.update.sha; }
    if (latestSha) {
      return reply("outdated", "Lethal Client is now free. Updating...", {
        required: "999.0.0", updateSha: latestSha, updateVersion: latestVer,
      });
    }
    return reply("invalid", "Invalid license key.");
  }
  if (rec.revoked) return reply("revoked", "This license key has been revoked.");
  if (rec.expires && now > rec.expires) return reply("expired", "This license key has expired.");

  // Tamper check: the mod sends a fingerprint (SHA-256) of its own jar. If it doesn't match the
  // official build we published for this version, the jar was modified — refuse to activate it.
  // Unknown/older versions we have no fingerprint for are allowed, so legit old builds never break.
  // Owner keys are exempt entirely: the shop owner tests custom/unreleased builds and must never be
  // locked out of their own client.
  const build = String(body.build || "").toLowerCase().replace(/[^a-f0-9]/g, "").slice(0, 64);
  if (build && cleanRank(rec.rank) !== "owner") {
    const s = await getSettings(env);
    const official = new Set();
    if (s.update.sha) official.add(s.update.sha.toLowerCase());
    (s.versionList || []).forEach((x) => { if (x && x.sha) official.add(String(x.sha).toLowerCase()); });
    // Each Minecraft version has its own official jar (same client version, different SHA), so every
    // per-MC build is a legitimate fingerprint. Without this, uploading builds for several MC versions
    // would leave only the last-uploaded SHA recognised and lock the other versions' players out.
    Object.values(s.mcLatest || {}).forEach((x) => { if (x && x.sha) official.add(String(x.sha).toLowerCase()); });
    if (!official.has(build)) {
      const reqVer = String(body.version || "");
      const verRec = (s.versionList || []).find((x) => x && x.v === reqVer);
      const expected = verRec ? String(verRec.sha).toLowerCase()
        : (reqVer && reqVer === s.update.version ? (s.update.sha || "").toLowerCase() : "");
      if (expected && expected !== build) {
        return reply("invalid", "This copy of Lethal Client has been modified. Reinstall the official client.");
      }
    }
  }

  if (!rec.hwid) {
    rec.hwid = hwid;
    rec.firstUsed = now;
  } else if (rec.hwid !== hwid) {
    rec.lastBlocked = now;
    rec.lastBlockedUser = user;
    await putKey(env, rec);
    return reply("hwid_mismatch", "This key is already in use on another PC.");
  }
  const settings = await getSettings(env);
  // Offer the newest build for THIS client's Minecraft version, so each version updates on its own
  // track (a 1.21.x game never gets pushed a 26.x jar). Old clients that don't report a version
  // default to 26.2 and fall back to the single global build during the transition.
  const clientMc = String(body.mc || "").trim() || "26.2";
  const mcL = (settings.mcLatest || {})[clientMc];
  let latestVer = "", latestSha = "";
  if (mcL && mcL.sha) {
    latestVer = mcL.version; latestSha = mcL.sha;
  } else if (clientMc === "26.2" && settings.update.sha) {
    latestVer = settings.update.version; latestSha = settings.update.sha;
  }
  const update = targetUpdateFor(settings, user, body.uuid, body.version, clientMc)
    || (latestSha && compareVersions(latestVer, String(body.version || "0")) > 0
      ? { updateSha: latestSha, updateVersion: latestVer } : {});

  // Games check in every minute; only save "last seen" every few minutes to keep database writes low.
  if (rec.firstUsed === now || rec.user !== (user || rec.user) || now - (rec.lastSeen || 0) > 5 * 60000) {
    rec.user = user || rec.user;
    rec.lastSeen = now;
    await putKey(env, rec);
  }

  // Remember this player as a mod user (with their cape) and tell them which nearby players use the mod.
  // This also happens while a game waits for an update, so its badge and cape keep showing.
  const uuid = cleanUuid(body.uuid);
  // A cape is the fingerprint of an uploaded picture, or "anim:<name>" for one of the client's animated capes.
  const rawCape = String(body.cape || "");
  const capeRaw = /^[0-9a-f]{64}$/.test(rawCape) || /^anim:[a-z0-9_]{1,24}$/.test(rawCape) ? rawCape : "";
  // Cosmetics from the client's Cosmetics tab, like "tophat:0,hoodie:2".
  const rawCos = String(body.cos || "");
  const cosValid = /^[a-z_]{1,16}:[0-9](,[a-z_]{1,16}:[0-9]){0,7}$/.test(rawCos) ? rawCos : "";
  // Paid cosmetics are OFF until you flip COSMETICS_PAID=1 (once the store + in-mod lock are live). Until
  // then every cosmetic stays free for everyone. When ON, only keep what the account owns, so a modified
  // client can never show unpaid cosmetics to others; capes are gated by the "capes" unlock.
  const paidCosmetics = env.COSMETICS_PAID === "1";
  const owned = paidCosmetics
    ? await ownedItems(env, await userForKey(env, key))
    : new Set(COSMETICS_CATALOG.map((c) => c.id));
  const cos = paidCosmetics ? filterOwnedCos(cosValid, owned) : cosValid;
  const cape = paidCosmetics && !owned.has("capes") ? "" : capeRaw;
  const rawFig = String(body.fig || "");
  const fig = /^[a-z0-9_]{1,24}$/.test(rawFig) ? rawFig : "";
  let needCape = false;
  let myRole = null;
  if (uuid) {
    const me = await env.DB.prepare("SELECT cape, cos, fig, seen, role, ver, mc FROM players WHERE uuid = ?").bind(uuid).first();
    myRole = me ? me.role : null;
    const pVer = String(body.version || "").slice(0, 32);
    const pMc = (String(body.mc || "").trim() || "26.2").slice(0, 16);
    if (!me || me.cape !== cape || (me.cos || "") !== cos || (me.fig || "") !== fig || (me.ver || "") !== pVer || (me.mc || "") !== pMc || now - me.seen > 5 * 60000) {
      // Upsert that leaves the admin-assigned role untouched (never wipe it on a routine check-in).
      await env.DB.prepare(
        "INSERT INTO players (uuid, name, cape, seen, key, cos, fig, ver, mc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON CONFLICT(uuid) DO UPDATE SET name=excluded.name, cape=excluded.cape, seen=excluded.seen, key=excluded.key, cos=excluded.cos, fig=excluded.fig, ver=excluded.ver, mc=excluded.mc"
      ).bind(uuid, user, cape, now, key, cos, fig, pVer, pMc).run();
    }
    if (/^[0-9a-f]{64}$/.test(cape)) needCape = !(await env.DB.prepare("SELECT sha FROM capes WHERE sha = ?").bind(cape).first());
  }
  const { players, ranks, cosmetics, figures } = await modUsers(env, Array.isArray(body.nearby) ? body.nearby : [], now);
  // The player's own badge: their admin-assigned role if they have one, otherwise their key's rank.
  const rank = cleanRank(myRole || rec.rank);

  // Too old a version: lock it until the new one is in.
  if (settings.requiredVersion && compareVersions(String(body.version || "0"), settings.requiredVersion) < 0) {
    return reply("outdated", "A new version of Lethal Client is out. You need it to keep using the client.", {
      required: settings.requiredVersion, rank, ...update,
    }, { players, ranks, cosmetics, figures, needCape });
  }

  const validUntil = rec.expires ? Math.min(rec.expires, now + GRACE_MS) : now + GRACE_MS;
  return reply("active", "License active.", {
    validUntil, expires: rec.expires || 0, owned: [...owned],
    maintenance: settings.maintenance, rank, ...update,
  }, { players, ranks, cosmetics, figures, needCape });
}

// ---------- admin endpoints ----------

/** Refund every past paid charge. Preview (doIt=false) just totals them up; run (doIt=true) refunds them.
 *  Re-running is safe — already-refunded charges are skipped. Used by the admin panel's Refunds section. */
async function adminRefunds(env, doIt) {
  if (!(env.STRIPE_SECRET_KEY || FALLBACK_STRIPE_SECRET_KEY)) return json({ error: "Stripe isn't set up." }, 400);
  let startingAfter = "";
  let scanned = 0;
  const refundable = [];
  for (;;) {
    const q = new URLSearchParams({ limit: "100" });
    if (startingAfter) q.set("starting_after", startingAfter);
    const page = await stripeApi(env, "/charges?" + q.toString(), "GET", null);
    for (const ch of page.data) {
      scanned++;
      if (ch.paid && !ch.refunded && (ch.amount_refunded || 0) < ch.amount && !ch.disputed && ch.status === "succeeded") {
        refundable.push(ch);
      }
    }
    if (!page.has_more || !page.data.length) break;
    startingAfter = page.data[page.data.length - 1].id;
  }
  let total = 0;
  for (const c of refundable) total += c.amount;
  if (!doIt) {
    return json({ preview: true, scanned, count: refundable.length, totalCents: total });
  }
  let ok = 0;
  let failed = 0;
  for (const ch of refundable) {
    try {
      await stripeApi(env, "/refunds", "POST", { charge: ch.id });
      ok++;
    } catch (_) {
      failed++;
    }
  }
  return json({ done: true, refunded: ok, failed, totalCents: total });
}

async function admin(action, request, env) {
  const body = request.method === "POST" ? await request.json().catch(() => ({})) : {};

  if (action === "refunds-preview" || action === "refunds-run") {
    return await adminRefunds(env, action === "refunds-run");
  }

  if (action === "players") {
    // Everyone who has run the client (their Minecraft name + uuid), newest first.
    const { results } = await env.DB.prepare(
      "SELECT uuid, name, seen, role, ver, mc FROM players ORDER BY seen DESC LIMIT 500"
    ).all();
    const settings = await getSettings(env);
    // The newest uploaded client version for a given Minecraft version.
    const latestFor = (mc) => {
      const m = (mc || "26.2");
      const mcL = (settings.mcLatest || {})[m];
      if (mcL && mcL.version) return mcL.version;
      if (m === "26.2" && settings.update && settings.update.version) return settings.update.version;
      return "";
    };
    return json({
      players: results.map((p) => {
        const mc = p.mc || "";
        const ver = p.ver || "";
        const latest = latestFor(mc);
        // up to date if we know both versions and the player's is >= the newest; unknown if we lack data.
        let uptodate = null;
        if (ver && latest) uptodate = compareVersions(ver, latest) >= 0;
        return { uuid: p.uuid, name: p.name || "", seen: p.seen || 0, role: cleanRank(p.role), ver, mc, latest, uptodate };
      }),
    });
  }

  if (action === "setrole") {
    const uuid = cleanUuid(body.uuid);
    const role = cleanRank(String(body.role || ""));
    if (!uuid) return json({ error: "No player." }, 400);
    await env.DB.prepare("UPDATE players SET role = ? WHERE uuid = ?").bind(role, uuid).run();
    return json({ ok: true, uuid, role });
  }

  if (action === "untarget") {
    // Remove a targeted build so those players fall back to the public version again.
    const ver = String(body.version || "");
    const prior = await getSettings(env);
    const targets = (prior.targets || []).filter((t) => t && t.version !== ver);
    await env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('targets', ?)").bind(JSON.stringify(targets)).run();
    return json({ ok: true, settings: await getSettings(env) });
  }

  if (action === "list") {
    const { results } = await env.DB.prepare("SELECT data FROM keys ORDER BY created DESC").all();
    return json({ keys: results.map(row => JSON.parse(row.data)), settings: await getSettings(env) });
  }

  if (action === "settings") {
    if (typeof body.maintenance === "boolean") await setSetting(env, "maintenance", body.maintenance ? "1" : "0");
    if (typeof body.requiredVersion === "string") {
      const v = body.requiredVersion.trim();
      if (v && !/^\d+(\.\d+){0,3}$/.test(v)) return json({ error: "Version should look like 1.0.2" }, 400);
      await setSetting(env, "required_version", v);
    }
    return json({ settings: await getSettings(env) });
  }

  if (action === "create") {
    const count = Math.max(1, Math.min(50, parseInt(body.count) || 1));
    const days = parseFloat(body.days) || 0;
    const created = [];
    for (let i = 0; i < count; i++) {
      const rec = {
        key: newKey(),
        note: String(body.note || "").slice(0, 80),
        created: Date.now(),
        expires: days > 0 ? Date.now() + days * 86400000 : 0,
        hwid: "", user: "", firstUsed: 0, lastSeen: 0, revoked: false,
      };
      await putKey(env, rec);
      created.push(rec);
    }
    return json({ created });
  }

  const key = normalizeKey(body.key);
  const rec = key && await getKey(env, key);
  if (!rec) return json({ error: "Key not found" }, 404);

  if (action === "reset") { rec.hwid = ""; rec.firstUsed = 0; }
  else if (action === "revoke") rec.revoked = true;
  else if (action === "restore") rec.revoked = false;
  else if (action === "note") rec.note = String(body.note || "").slice(0, 80);
  else if (action === "rank") rec.rank = cleanRank(String(body.rank || ""));
  else if (action === "delete") { await env.DB.prepare("DELETE FROM keys WHERE key = ?").bind(key).run(); return json({ deleted: key }); }
  else return json({ error: "Unknown action" }, 400);

  await putKey(env, rec);
  return json({ key: rec });
}

/** True when `given` is the admin password. If ADMIN_PASSWORD is set in Cloudflare, ONLY that works — the
 *  recovery password is ignored, so a leaked copy of this source can't get in. The recovery password works
 *  only as a last resort when ADMIN_PASSWORD isn't set yet (so you can never be fully locked out). Trimmed. */
function adminPwOk(given, env) {
  const g = (given || "").trim();
  const want = (env.ADMIN_PASSWORD || "").trim();
  if (want) return g.length > 0 && g === want;     // ADMIN_PASSWORD set -> it is the ONLY key (no backdoor)
  if (FALLBACK_ADMIN_PASSWORD && g === FALLBACK_ADMIN_PASSWORD.trim()) return true;  // only while unset
  return false;
}

async function isAdmin(request, env) {
  const given = (request.headers.get("authorization") || "").replace(/^Bearer /, "");
  return adminPwOk(given, env);
}

// ---------- storage ----------

let ready;

/** Creates the tables on first use and copies any keys from the old KV store across once. */
function setup(env) {
  if (!env.DB) throw new Error("The D1 database is not connected. Add a D1 binding called DB in the Worker's settings.");
  if (!ready) {
    ready = (async () => {
      await env.DB.batch([
        env.DB.prepare("CREATE TABLE IF NOT EXISTS keys (key TEXT PRIMARY KEY, data TEXT NOT NULL, created INTEGER NOT NULL)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS settings (name TEXT PRIMARY KEY, value TEXT NOT NULL)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, cape TEXT, seen INTEGER NOT NULL, key TEXT)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS capes (sha TEXT PRIMARY KEY, size INTEGER, at INTEGER)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS stripe_map (ref TEXT PRIMARY KEY, lic TEXT NOT NULL, kind TEXT, at INTEGER)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS users (id TEXT PRIMARY KEY, username TEXT, email TEXT, pass TEXT NOT NULL, lic TEXT, created INTEGER NOT NULL)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS sessions (token TEXT PRIMARY KEY, uid TEXT NOT NULL, created INTEGER NOT NULL)"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS entitlements (user_id TEXT NOT NULL, item TEXT NOT NULL, at INTEGER NOT NULL, PRIMARY KEY (user_id, item))"),
        env.DB.prepare("CREATE TABLE IF NOT EXISTS dl_ips (ip TEXT PRIMARY KEY, first_day TEXT NOT NULL, last_day TEXT NOT NULL)"),
        // One row per (who, Minecraft version): downloads are counted once each, so a re-download never
        // re-counts. "who" is "u:<account id>" for signed-in downloads or "ip:<address>" for public ones.
        env.DB.prepare("CREATE TABLE IF NOT EXISTS dl_log (ident TEXT NOT NULL, mc TEXT NOT NULL, at INTEGER NOT NULL, PRIMARY KEY (ident, mc))"),
      ]);
      // Older databases don't have the cosmetics / role columns yet.
      const cols = await env.DB.prepare("PRAGMA table_info(players)").all();
      if (!cols.results.some(c => c.name === "cos")) {
        await env.DB.prepare("ALTER TABLE players ADD COLUMN cos TEXT").run();
      }
      if (!cols.results.some(c => c.name === "role")) {
        await env.DB.prepare("ALTER TABLE players ADD COLUMN role TEXT").run();
      }
      // The equipped mini figure, shared so other Lethal Client users can see it follow you.
      if (!cols.results.some(c => c.name === "fig")) {
        await env.DB.prepare("ALTER TABLE players ADD COLUMN fig TEXT").run();
      }
      // The client version and Minecraft version each player last checked in on (so the admin panel can
      // show whether they're on the newest update).
      if (!cols.results.some(c => c.name === "ver")) {
        await env.DB.prepare("ALTER TABLE players ADD COLUMN ver TEXT").run();
      }
      if (!cols.results.some(c => c.name === "mc")) {
        await env.DB.prepare("ALTER TABLE players ADD COLUMN mc TEXT").run();
      }
      // Seed the historical download base once (the ~77 downloads from before unique-IP counting began).
      const dlBase = await env.DB.prepare("SELECT value FROM settings WHERE name = 'dl_base'").first();
      if (!dlBase) {
        await env.DB.prepare("INSERT OR IGNORE INTO settings (name, value) VALUES ('dl_base', '77')").run();
      }
      const migrated = await env.DB.prepare("SELECT value FROM settings WHERE name = 'migrated'").first();
      // One-time reset: going keyless/free, so clear every license key. Runs once per database.
      // Marking 'migrated' here too, so the old KV keys are never copied back in after the wipe.
      const keysWiped = await env.DB.prepare("SELECT value FROM settings WHERE name = 'keys_wiped_v1'").first();
      if (!keysWiped) {
        await env.DB.prepare("DELETE FROM keys").run();
        await env.DB.batch([
          env.DB.prepare("INSERT OR IGNORE INTO settings (name, value) VALUES ('keys_wiped_v1', '1')"),
          env.DB.prepare("INSERT OR IGNORE INTO settings (name, value) VALUES ('migrated', '1')"),
        ]);
      }
      // One-time reset: clear the player list so the keyless/free client starts fresh. Runs once.
      const playersWiped = await env.DB.prepare("SELECT value FROM settings WHERE name = 'players_wiped_v1'").first();
      if (!playersWiped) {
        await env.DB.prepare("DELETE FROM players").run();
        await env.DB.prepare("INSERT OR IGNORE INTO settings (name, value) VALUES ('players_wiped_v1', '1')").run();
      }
      if (!keysWiped) return;
      if (migrated) return;
      const copy = [];
      if (env.LICENSES) {
        let cursor;
        do {
          const page = await env.LICENSES.list({ prefix: "key:", cursor });
          for (const k of page.keys) {
            if (k.metadata) {
              copy.push(env.DB.prepare("INSERT OR IGNORE INTO keys (key, data, created) VALUES (?, ?, ?)")
                .bind(k.metadata.key, JSON.stringify(k.metadata), k.metadata.created || 0));
            }
          }
          cursor = page.list_complete ? undefined : page.cursor;
        } while (cursor);
      }
      for (let i = 0; i < copy.length; i += 50) await env.DB.batch(copy.slice(i, i + 50));
      await env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('migrated', ?)").bind(String(copy.length)).run();
    })().catch(e => { ready = undefined; throw e; });
  }
  return ready;
}

async function getKey(env, key) {
  const row = await env.DB.prepare("SELECT data FROM keys WHERE key = ?").bind(key).first();
  return row ? JSON.parse(row.data) : null;
}

async function putKey(env, rec) {
  await env.DB.prepare("INSERT OR REPLACE INTO keys (key, data, created) VALUES (?, ?, ?)")
    .bind(rec.key, JSON.stringify(rec), rec.created || 0).run();
}

async function getSettings(env) {
  const { results } = await env.DB.prepare("SELECT name, value FROM settings").all();
  const map = Object.fromEntries(results.map(row => [row.name, row.value]));
  let versionList = [], mcAvailable = [], mcLatest = {}, targets = [];
  try { versionList = JSON.parse(map.versions || "[]"); } catch (_) { versionList = []; }
  try { mcAvailable = JSON.parse(map.mc_available || "[]"); } catch (_) { mcAvailable = []; }
  try { mcLatest = JSON.parse(map.mc_latest || "{}"); } catch (_) { mcLatest = {}; }
  try { targets = JSON.parse(map.targets || "[]"); } catch (_) { targets = []; }
  return {
    maintenance: map.maintenance === "1",
    requiredVersion: map.required_version || "",
    update: { sha: map.update_sha || "", version: map.update_version || "", size: Number(map.update_size || 0), at: Number(map.update_at || 0) },
    versionList,
    mcAvailable,
    mcLatest,
    targets,
  };
}

/** If this player is on a targeted-build list and that build is newer than what they're running, return the
 *  update to hand them (overrides the public auto-update). Matches by Minecraft name or UUID. */
function targetUpdateFor(settings, name, uuid, curVer, clientMc) {
  const nm = String(name || "").trim().toLowerCase();
  const id = String(uuid || "").replace(/-/g, "").toLowerCase();
  const mc = String(clientMc || "").trim();
  for (const t of (settings.targets || [])) {
    if (!t || !t.sha || !t.version) continue;
    // A targeted build only goes to players on the same Minecraft version it was built for — never hand a
    // 26.2 jar to a 1.21.11 player (or the reverse). When either side's MC is unknown, fall through.
    if (mc && t.mc && String(t.mc) !== mc) continue;
    const names = (t.names || []).map((s) => String(s).trim().toLowerCase());
    const uuids = (t.uuids || []).map((s) => String(s).replace(/-/g, "").toLowerCase());
    const hit = (nm && names.includes(nm)) || (id && uuids.includes(id));
    if (hit && compareVersions(t.version, String(curVer || "0")) > 0) {
      return { updateSha: t.sha, updateVersion: t.version };
    }
  }
  return null;
}

/** Compares versions like 1.0.10 and 1.0.9 number by number. */
function compareVersions(a, b) {
  const pa = String(a).split(/[^0-9]+/).filter(Boolean).map(Number);
  const pb = String(b).split(/[^0-9]+/).filter(Boolean).map(Number);
  for (let i = 0; i < Math.max(pa.length, pb.length); i++) {
    const d = (pa[i] || 0) - (pb[i] || 0);
    if (d !== 0) return d;
  }
  return 0;
}

function setSetting(env, name, value) {
  return env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES (?, ?)").bind(name, String(value)).run();
}

// ---------- players and capes ----------

const PLAYER_FRESH_MS = 15 * 60000; // a player counts as using the mod if their game checked in this recently

function cleanUuid(u) {
  const s = String(u || "").toLowerCase();
  return /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(s) ? s : "";
}

const RANKS = ["member", "vip", "creator", "owner"];

function cleanRank(r) {
  return RANKS.includes(r) ? r : "member";
}

/**
 * Of the given players, which use the mod right now and which cape each one has ("" for none),
 * plus the staff and owner ranks among them (everyone else is a member) and what cosmetics they wear.
 */
async function modUsers(env, list, now) {
  const uuids = [...new Set(list.slice(0, 300).map(cleanUuid).filter(Boolean))];
  const players = {};
  const ranks = {};
  const cosmetics = {};
  const figures = {};
  for (let i = 0; i < uuids.length; i += 90) {
    const part = uuids.slice(i, i + 90);
    const { results } = await env.DB.prepare(
      "SELECT p.uuid, p.cape, p.cos, p.fig, COALESCE(p.role, json_extract(k.data, '$.rank')) AS rank FROM players p LEFT JOIN keys k ON k.key = p.key " +
      "WHERE p.seen > ? AND p.uuid IN (" + part.map(() => "?").join(",") + ")"
    ).bind(now - PLAYER_FRESH_MS, ...part).all();
    for (const row of results) {
      players[row.uuid] = row.cape || "";
      const rank = cleanRank(row.rank);
      if (rank !== "member") ranks[row.uuid] = rank;
      if (row.cos) cosmetics[row.uuid] = row.cos;
      if (row.fig) figures[row.uuid] = row.fig;
    }
  }
  return { players, ranks, cosmetics, figures };
}

async function uploadCape(request, env) {
  if (!env.LICENSES) return json({ error: "Add the KV binding called LICENSES to store capes." }, 500);
  // Only games with a working key may upload.
  const rec = await getKey(env, normalizeKey(request.headers.get("x-key")));
  const hwid = String(request.headers.get("x-hwid") || "").toLowerCase();
  if (!rec || rec.revoked || !rec.hwid || rec.hwid !== hwid || (rec.expires && Date.now() > rec.expires)) {
    return json({ error: "Not allowed" }, 403);
  }
  const bytes = new Uint8Array(await request.arrayBuffer());
  const png = [0x89, 0x50, 0x4e, 0x47];
  if (bytes.length < 100 || png.some((b, i) => bytes[i] !== b)) return json({ error: "Not a PNG" }, 400);
  if (bytes.length > 3 * 1024 * 1024) return json({ error: "Cape too big" }, 400);
  const sha = [...new Uint8Array(await crypto.subtle.digest("SHA-256", bytes))].map(b => b.toString(16).padStart(2, "0")).join("");
  await env.LICENSES.put("cape:" + sha, bytes);
  await env.DB.prepare("INSERT OR REPLACE INTO capes (sha, size, at) VALUES (?, ?, ?)").bind(sha, bytes.length, Date.now()).run();
  return json({ sha });
}

async function downloadCape(sha, env, request, ctx) {
  if (!/^[0-9a-f]{64}$/.test(sha) || !env.LICENSES) return json({ error: "Not found" }, 404);
  // A cape's address is its fingerprint, so it never changes. Serve repeat hits straight from
  // Cloudflare's edge cache so a busy server (hundreds of players) barely touches KV at all.
  const cache = caches.default;
  if (request) {
    const hit = await cache.match(request);
    if (hit) return hit;
  }
  const png = await env.LICENSES.get("cape:" + sha, "arrayBuffer");
  if (!png) return json({ error: "Not found" }, 404);
  const res = new Response(png, { headers: { "content-type": "image/png", "cache-control": "public, max-age=31536000, immutable" } });
  if (request && ctx) ctx.waitUntil(cache.put(request, res.clone()));
  return res;
}

// ---------- mod updates ----------

async function uploadUpdate(request, env) {
  if (!env.LICENSES) return json({ error: "Add the KV binding called LICENSES to store updates." }, 500);
  const bytes = new Uint8Array(await request.arrayBuffer());
  // A jar is a zip file, which always starts with "PK".
  if (bytes.length < 100 || bytes[0] !== 0x50 || bytes[1] !== 0x4b) return json({ error: "That isn't a .jar file." }, 400);
  if (bytes.length > 24 * 1024 * 1024) return json({ error: "The jar is too big (24 MB max)." }, 400);
  let mod;
  try { mod = JSON.parse(await readZipEntry(bytes, "fabric.mod.json")); } catch (_) { mod = null; }
  if (!mod || mod.id !== "lethalauction") return json({ error: "That isn't a Lethal Client jar." }, 400);
  const version = String(mod.version || "");
  if (!/^\d+(\.\d+){0,3}$/.test(version)) return json({ error: "The jar's version (" + version + ") should look like 1.0.3" }, 400);
  // Which Minecraft version this jar is built for, read from its own fabric.mod.json.
  const mcVersion = (function () {
    const d = (mod.depends && mod.depends.minecraft) || "";
    const m = String(d).match(/\d+(?:\.\d+)+/);
    return m ? m[0] : "";
  })();
  const sha = [...new Uint8Array(await crypto.subtle.digest("SHA-256", bytes))].map(b => b.toString(16).padStart(2, "0")).join("");
  // Every uploaded build is stored by its fingerprint too, so it can be served directly by sha — this is
  // how a targeted build reaches exactly the right person (the client asks /api/update?sha=<its sha>).
  await env.LICENSES.put("jar:sha:" + sha, bytes);
  await env.LICENSES.put("jar:" + version, bytes);

  // Targeted upload: "x-target" is a comma-separated list of Minecraft names (and/or UUIDs). The build goes
  // ONLY to those people (they get auto-updated to it); it does NOT become the public latest for everyone.
  const targetRaw = String(request.headers.get("x-target") || "").trim();
  if (targetRaw) {
    const ids = targetRaw.split(",").map((s) => s.trim()).filter(Boolean).slice(0, 200);
    const uuids = ids.filter((s) => /^[0-9a-fA-F-]{32,36}$/.test(s)).map((s) => s.replace(/-/g, "").toLowerCase());
    const names = ids.filter((s) => !/^[0-9a-fA-F-]{32,36}$/.test(s));
    const prior = await getSettings(env);
    let targets = (prior.targets || []).filter((t) => t && t.version !== version);
    targets.unshift({ version, sha, size: bytes.length, mc: mcVersion, names, uuids, at: Date.now() });
    targets = targets.slice(0, 50);
    await env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('targets', ?)").bind(JSON.stringify(targets)).run();
    return json({ settings: await getSettings(env), targeted: { version, count: names.length + uuids.length } });
  }

  // Normal upload: this build becomes the public newest for its Minecraft version.
  await env.LICENSES.put("update:jar", bytes);
  if (mcVersion) await env.LICENSES.put("jar:mc:" + mcVersion, bytes);
  const now = String(Date.now());
  // Keep a newest-first list of downloadable client builds (deduped, capped).
  const prior = await getSettings(env);
  let versionList = (prior.versionList || []).filter((x) => x && x.v !== version);
  versionList.unshift({ v: version, size: bytes.length, at: Date.now(), sha });
  versionList = versionList.slice(0, 24);
  // Track which Minecraft versions actually have a build (so the download picker can offer them).
  let mcList = (prior.mcAvailable || []).filter((x) => x && x.mc !== mcVersion);
  if (mcVersion) mcList.unshift({ mc: mcVersion, build: version, size: bytes.length, at: Date.now() });
  // The latest build PER Minecraft version — what the per-version auto-updater serves each game.
  const mcLatest = prior.mcLatest || {};
  if (mcVersion) mcLatest[mcVersion] = { version, sha, size: bytes.length, at: Date.now() };
  const writes = [
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('update_sha', ?)").bind(sha),
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('update_version', ?)").bind(version),
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('update_size', ?)").bind(String(bytes.length)),
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('update_at', ?)").bind(now),
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('versions', ?)").bind(JSON.stringify(versionList)),
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('mc_available', ?)").bind(JSON.stringify(mcList)),
    env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('mc_latest', ?)").bind(JSON.stringify(mcLatest)),
  ];
  // "Make everyone update": older versions are locked until the new one is in.
  if (request.headers.get("x-require") === "1") {
    writes.push(env.DB.prepare("INSERT OR REPLACE INTO settings (name, value) VALUES ('required_version', ?)").bind(version));
  }
  await env.DB.batch(writes);
  return json({ settings: await getSettings(env) });
}

async function downloadUpdate(request, env) {
  // The client is free: anyone may download the newest build (this is also how old keyed clients, whose
  // keys were wiped when we went free, pull the keyless update and restart into it).
  // If the client asks for a specific build by fingerprint (?sha=...), serve exactly that one. This is how a
  // targeted person pulls their assigned build instead of the public latest.
  const wantSha = String(new URL(request.url).searchParams.get("sha") || "").toLowerCase().replace(/[^a-f0-9]/g, "").slice(0, 64);
  if (wantSha) {
    const byShaBuf = env.LICENSES && await env.LICENSES.get("jar:sha:" + wantSha, "arrayBuffer");
    if (byShaBuf) return new Response(byShaBuf, { headers: { "content-type": "application/java-archive", "cache-control": "no-store" } });
    // fall through to the normal latest if we don't have that fingerprint stored
  }
  // Serve the build for this game's own Minecraft version. Old clients send no version → treat as 26.2.
  const clientMc = String(request.headers.get("x-mc") || "").trim() || "26.2";
  let jar = env.LICENSES && await env.LICENSES.get("jar:mc:" + clientMc, "arrayBuffer");
  if (!jar && clientMc === "26.2") jar = env.LICENSES && await env.LICENSES.get("update:jar", "arrayBuffer");
  if (!jar) return json({ error: "No update uploaded" }, 404);
  return new Response(jar, { headers: { "content-type": "application/java-archive", "cache-control": "no-store" } });
}

/** Reads one file out of a zip (a jar), to find the mod's version. */
async function readZipEntry(bytes, wanted) {
  const v = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  let end = -1;
  for (let i = bytes.length - 22; i >= Math.max(0, bytes.length - 65557); i--) {
    if (v.getUint32(i, true) === 0x06054b50) { end = i; break; }
  }
  if (end < 0) throw new Error("not a zip");
  const count = v.getUint16(end + 10, true);
  let p = v.getUint32(end + 16, true);
  for (let n = 0; n < count && v.getUint32(p, true) === 0x02014b50; n++) {
    const method = v.getUint16(p + 10, true);
    const size = v.getUint32(p + 20, true);
    const nameLen = v.getUint16(p + 28, true);
    const skip = nameLen + v.getUint16(p + 30, true) + v.getUint16(p + 32, true);
    const local = v.getUint32(p + 42, true);
    const name = new TextDecoder().decode(bytes.subarray(p + 46, p + 46 + nameLen));
    if (name === wanted) {
      const start = local + 30 + v.getUint16(local + 26, true) + v.getUint16(local + 28, true);
      const data = bytes.subarray(start, start + size);
      if (method === 0) return new TextDecoder().decode(data);
      const out = new Response(new Blob([data]).stream().pipeThrough(new DecompressionStream("deflate-raw")));
      return await out.text();
    }
    p += 46 + skip;
  }
  throw new Error("missing " + wanted);
}

function normalizeKey(k) {
  return String(k || "").trim().toUpperCase().replace(/[^A-Z0-9-]/g, "").slice(0, 40);
}

function newKey() {
  const bytes = crypto.getRandomValues(new Uint8Array(16));
  const chars = Array.from(bytes, b => KEY_ALPHABET[b % KEY_ALPHABET.length]).join("");
  return "LETHAL-" + chars.match(/.{4}/g).join("-");
}

// ---------- signing ----------

let signingKey;

async function signed(env, payloadObj, outer = {}) {
  if (!signingKey) {
    const der = Uint8Array.from(atob((env.LICENSE_PRIVATE_KEY || FALLBACK_PRIVATE_KEY).trim()), c => c.charCodeAt(0));
    signingKey = await crypto.subtle.importKey("pkcs8", der, { name: "Ed25519" }, false, ["sign"]);
  }
  const payload = toBase64(new TextEncoder().encode(JSON.stringify(payloadObj)));
  const sig = await crypto.subtle.sign({ name: "Ed25519" }, signingKey, new TextEncoder().encode(payload));
  return json({ payload, sig: toBase64(new Uint8Array(sig)), ...outer });
}

function toBase64(bytes) {
  let s = "";
  for (const b of bytes) s += String.fromCharCode(b);
  return btoa(s);
}

function json(obj, status = 200) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { "content-type": "application/json", "cache-control": "no-store" },
  });
}

// ---------- store checkout (Stripe) ----------

async function stripeApi(env, path, method, params) {
  const key = env.STRIPE_SECRET_KEY || FALLBACK_STRIPE_SECRET_KEY;
  if (!key) throw new Error("Stripe isn't set up yet (STRIPE_SECRET_KEY).");
  const opts = { method, headers: { Authorization: "Bearer " + key } };
  if (params) {
    opts.headers["Content-Type"] = "application/x-www-form-urlencoded";
    opts.body = stripeForm(params);
  }
  const res = await fetch("https://api.stripe.com/v1" + path, opts);
  const data = await res.json();
  if (!res.ok) throw new Error((data.error && data.error.message) || ("Stripe error " + res.status));
  return data;
}

// Stripe wants application/x-www-form-urlencoded with bracket notation for nested fields.
function stripeForm(obj, prefix) {
  const parts = [];
  for (const k of Object.keys(obj)) {
    const name = prefix ? prefix + "[" + k + "]" : k;
    const v = obj[k];
    if (v !== null && typeof v === "object") parts.push(stripeForm(v, name));
    else parts.push(encodeURIComponent(name) + "=" + encodeURIComponent(v));
  }
  return parts.join("&");
}

// ---------- Paid cosmetics: catalog, ownership, per-item checkout ----------
// The client is free; cosmetics are the paid part. Free items below are owned by everyone; the rest are
// a one-time purchase per account. Prices are in cents — change `cents` to re-price, `free` to give away.
const STICKER_ORDER = ["halloween", "xmas", "pumpkin", "heart", "sixseven", "star", "skull", "fire", "diamond"];
const COSMETICS_CATALOG = [
  { id: "cap",        name: "Cap",              type: "Hat",        cents: 0,   free: true },
  { id: "tophat",     name: "Top Hat",          type: "Hat",        cents: 249, free: false },
  { id: "beanie",     name: "Beanie",           type: "Hat",        cents: 249, free: false },
  { id: "running",    name: "Running Shoes",    type: "Shoes",      cents: 0,   free: true },
  { id: "jordanlows", name: "Jordan Lows",      type: "Shoes",      cents: 299, free: false },
  { id: "basketball", name: "Basketball Shoes", type: "Shoes",      cents: 299, free: false },
  { id: "headphones", name: "Headphones",       type: "Headphones", cents: 249, free: false },
  { id: "sticker_heart",     name: "Heart Sticker",         type: "Sticker", cents: 0,  free: true },
  { id: "sticker_sixseven",  name: "67 Sticker",            type: "Sticker", cents: 0,  free: true },
  { id: "sticker_halloween", name: "Halloween Sticker",     type: "Sticker", cents: 99, free: false },
  { id: "sticker_xmas",      name: "Christmas Tree Sticker",type: "Sticker", cents: 99, free: false },
  { id: "sticker_pumpkin",   name: "Pumpkin Sticker",       type: "Sticker", cents: 99, free: false },
  { id: "sticker_star",      name: "Star Sticker",          type: "Sticker", cents: 99, free: false },
  { id: "sticker_skull",     name: "Skull Sticker",         type: "Sticker", cents: 99, free: false },
  { id: "sticker_fire",      name: "Fire Sticker",          type: "Sticker", cents: 99, free: false },
  { id: "sticker_diamond",   name: "Diamond Sticker",       type: "Sticker", cents: 99, free: false },
  { id: "capes",      name: "Capes Unlock",     type: "Capes",      cents: 399, free: false },
];
function cosmeticItem(id) { return COSMETICS_CATALOG.find((c) => c.id === id) || null; }
function freeItemIds() { return COSMETICS_CATALOG.filter((c) => c.free).map((c) => c.id); }

/** The set of cosmetic ids this account owns (free items plus everything it has bought). */
async function ownedItems(env, userId) {
  const owned = new Set(freeItemIds());
  if (userId) {
    const { results } = await env.DB.prepare("SELECT item FROM entitlements WHERE user_id = ?").bind(userId).all();
    for (const r of results) owned.add(r.item);
  }
  return owned;
}
async function grantItem(env, userId, item) {
  if (!userId || !cosmeticItem(item)) return;
  await env.DB.prepare("INSERT OR IGNORE INTO entitlements (user_id, item, at) VALUES (?, ?, ?)")
    .bind(userId, item, Date.now()).run();
}
/** The account (user id) that a client key belongs to, or null. */
async function userForKey(env, key) {
  if (!key) return null;
  const row = await env.DB.prepare("SELECT id FROM users WHERE lic = ?").bind(key).first();
  return row ? row.id : null;
}
/** Turn one worn-cosmetic token ("tophat:0", "sticker:3", "headphones:2") into its catalog item id. */
function cosPartToItem(part) {
  const id = part.split(":")[0];
  if (id === "sticker") {
    const s = STICKER_ORDER[parseInt(part.split(":")[1], 10)];
    return s ? "sticker_" + s : null;
  }
  return id;
}
/** Drop any worn cosmetics the account doesn't own — this is what actually enforces the paywall, so a
 *  hacked client can't show unpaid cosmetics to other players. */
function filterOwnedCos(cos, owned) {
  if (!cos) return "";
  return cos.split(",").filter((p) => { const it = cosPartToItem(p); return it && owned.has(it); }).join(",");
}

/** The store listing for the dashboard: every item with its price and whether this account owns it. */
async function cosmeticsCatalog(request, env) {
  const user = await currentUser(request, env);
  const owned = await ownedItems(env, user ? user.id : null);
  return json({
    items: COSMETICS_CATALOG.map((c) => ({ id: c.id, name: c.name, type: c.type, cents: c.cents, free: c.free, owned: owned.has(c.id) })),
  });
}

/** Start a Stripe checkout for a single cosmetic (one-time payment), granted to the account on success. */
async function cosmeticsBuy(request, env, url) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Please sign in first." }, 401);
  const b = await request.json().catch(() => ({}));
  const item = cosmeticItem(String(b.item || ""));
  if (!item) return json({ error: "No such cosmetic." }, 400);
  if (item.free) return json({ error: "That cosmetic is already free." }, 400);
  const owned = await ownedItems(env, user.id);
  if (owned.has(item.id)) return json({ error: "You already own that." }, 400);
  try {
    const session = await stripeApi(env, "/checkout/sessions", "POST", {
      mode: "payment",
      line_items: [{
        quantity: 1,
        price_data: { currency: "usd", unit_amount: item.cents, product_data: { name: "Lethal Client — " + item.name } },
      }],
      success_url: url.origin + "/dashboard?bought=" + encodeURIComponent(item.id),
      cancel_url: url.origin + "/dashboard?canceled=1",
      metadata: { kind: "cosmetic", item: item.id, uid: user.id },
    });
    return json({ url: session.url });
  } catch (e) {
    return json({ error: String(e.message || e) }, 500);
  }
}

async function stripeCheckout(request, env, url) {
  const body = await request.json().catch(() => ({}));
  const plan = ["monthly", "yearly", "lifetime"].includes(body.plan) ? body.plan : "lifetime";
  const price = plan === "monthly" ? (env.STRIPE_PRICE_MONTHLY || FALLBACK_STRIPE_PRICE_MONTHLY)
    : plan === "yearly" ? (env.STRIPE_PRICE_YEARLY || FALLBACK_STRIPE_PRICE_YEARLY)
    : (env.STRIPE_PRICE_LIFETIME || FALLBACK_STRIPE_PRICE_LIFETIME);
  if (!price) return json({ error: "That plan isn't set up yet." }, 400);
  try {
    const session = await stripeApi(env, "/checkout/sessions", "POST", {
      mode: (plan === "monthly" || plan === "yearly") ? "subscription" : "payment",
      line_items: [{ price: price, quantity: 1 }],
      success_url: url.origin + "/success?session_id={CHECKOUT_SESSION_ID}",
      cancel_url: url.origin + "/?canceled=1",
      allow_promotion_codes: true,
      metadata: { plan: plan },
    });
    return json({ url: session.url });
  } catch (e) {
    return json({ error: String(e.message || e) }, 500);
  }
}

// One license key per paid checkout session (idempotent, so refreshes and the webhook don't double up).
async function createKeyForSession(env, session) {
  const existing = await env.DB.prepare("SELECT lic FROM stripe_map WHERE ref = ?").bind(session.id).first();
  if (existing) return existing.lic;
  const plan = (session.metadata && session.metadata.plan) || (session.mode === "subscription" ? "monthly" : "lifetime");
  const planLabel = plan === "monthly" ? "Monthly" : plan === "yearly" ? "Yearly" : "Lifetime";
  const email = (session.customer_details && session.customer_details.email) || "";
  const rec = {
    key: newKey(),
    note: planLabel + (email ? " · " + email : ""),
    created: Date.now(),
    expires: 0,
    hwid: "", user: "", firstUsed: 0, lastSeen: 0, revoked: false,
  };
  await putKey(env, rec);
  await env.DB.prepare("INSERT OR IGNORE INTO stripe_map (ref, lic, kind, at) VALUES (?, ?, 'session', ?)").bind(session.id, rec.key, Date.now()).run();
  if (session.subscription) {
    await env.DB.prepare("INSERT OR IGNORE INTO stripe_map (ref, lic, kind, at) VALUES (?, ?, 'sub', ?)").bind(session.subscription, rec.key, Date.now()).run();
  }
  // Map the payment intent too, so a refund/chargeback on this charge can be traced back to the key.
  if (session.payment_intent) {
    await env.DB.prepare("INSERT OR IGNORE INTO stripe_map (ref, lic, kind, at) VALUES (?, ?, 'pi', ?)").bind(session.payment_intent, rec.key, Date.now()).run();
  }
  await stripeEmailKey(env, email, rec.key, plan);
  return rec.key;
}

async function stripeSuccess(request, env, url) {
  const id = url.searchParams.get("session_id") || "";
  if (!/^cs_[A-Za-z0-9_]+$/.test(id)) {
    return storePage(storeSuccessHtml(null, "We couldn't find your order."));
  }
  try {
    const session = await stripeApi(env, "/checkout/sessions/" + id, "GET", null);
    if (!(session.payment_status === "paid" || session.status === "complete")) {
      return storePage(storeSuccessHtml(null, "Your payment is still processing — refresh in a moment."));
    }
    const isUpgrade = session.metadata && session.metadata.upgrade === "lifetime";
    const key = isUpgrade ? await upgradeKeyToLifetime(env, session) : await createKeyForSession(env, session);
    return storePage(storeSuccessHtml(key, null));
  } catch (e) {
    return storePage(storeSuccessHtml(null, "Couldn't verify the payment: " + (e.message || e)));
  }
}

/** The license key behind a Stripe charge — via its payment intent (one-time) or its invoice's
 *  subscription (recurring). Used to revoke on refunds and chargebacks. */
async function licForCharge(env, charge) {
  if (!charge) return null;
  if (charge.payment_intent) {
    const row = await env.DB.prepare("SELECT lic FROM stripe_map WHERE ref = ?").bind(charge.payment_intent).first();
    if (row) return row.lic;
  }
  if (charge.invoice) {
    try {
      const inv = await stripeApi(env, "/invoices/" + encodeURIComponent(charge.invoice), "GET", null);
      if (inv && inv.subscription) {
        const row = await env.DB.prepare("SELECT lic FROM stripe_map WHERE ref = ?").bind(inv.subscription).first();
        if (row) return row.lic;
      }
    } catch (_) {}
  }
  return null;
}

async function revokeLic(env, lic) {
  if (!lic) return;
  const rec = await getKey(env, lic);
  if (rec && !rec.revoked) { rec.revoked = true; await putKey(env, rec); }
}

async function stripeWebhook(request, env) {
  const secret = env.STRIPE_WEBHOOK_SECRET || FALLBACK_STRIPE_WEBHOOK_SECRET;
  const payload = await request.text();
  if (secret) {
    const ok = await stripeVerify(payload, request.headers.get("stripe-signature") || "", secret);
    if (!ok) return json({ error: "Bad signature" }, 400);
  }
  let event;
  try { event = JSON.parse(payload); } catch (_) { return json({ error: "Bad payload" }, 400); }
  try {
    if (event.type === "checkout.session.completed") {
      const s = event.data.object;
      if (s.payment_status === "paid" || s.status === "complete") {
        if (s.metadata && s.metadata.kind === "cosmetic") await grantItem(env, s.metadata.uid, s.metadata.item);
        else if (s.metadata && s.metadata.upgrade === "lifetime") await upgradeKeyToLifetime(env, s);
        else await createKeyForSession(env, s);
      }
    } else if (event.type === "customer.subscription.deleted") {
      const row = await env.DB.prepare("SELECT lic FROM stripe_map WHERE ref = ?").bind(event.data.object.id).first();
      if (row) await revokeLic(env, row.lic);
    } else if (event.type === "charge.refunded") {
      // A card refund: revoke the key once the charge is fully refunded (partial refunds leave it active).
      const ch = event.data.object;
      if (ch && ch.amount > 0 && (ch.amount_refunded || 0) >= ch.amount) {
        await revokeLic(env, await licForCharge(env, ch));
      }
    } else if (event.type === "charge.dispute.created") {
      // A chargeback: the customer disputed the payment with their bank — revoke immediately.
      const d = event.data.object;
      let ch = null;
      if (d && d.charge) { try { ch = await stripeApi(env, "/charges/" + encodeURIComponent(d.charge), "GET", null); } catch (_) {} }
      await revokeLic(env, await licForCharge(env, ch || { payment_intent: d && d.payment_intent }));
    }
  } catch (e) {
    return json({ error: String(e.message || e) }, 500);
  }
  return json({ received: true });
}

// Verify Stripe's webhook signature (HMAC-SHA256 over "timestamp.payload").
async function stripeVerify(payload, header, secret) {
  const fields = {};
  header.split(",").forEach((p) => {
    const i = p.indexOf("=");
    if (i > 0) { const k = p.slice(0, i), v = p.slice(i + 1); (fields[k] = fields[k] || []).push(v); }
  });
  const t = fields.t && fields.t[0];
  const sigs = fields.v1 || [];
  if (!t || !sigs.length) return false;
  if (Math.abs(Date.now() / 1000 - Number(t)) > 600) return false;
  const mac = await crypto.subtle.importKey("raw", new TextEncoder().encode(secret), { name: "HMAC", hash: "SHA-256" }, false, ["sign"]);
  const sig = await crypto.subtle.sign("HMAC", mac, new TextEncoder().encode(t + "." + payload));
  const expected = [...new Uint8Array(sig)].map((b) => b.toString(16).padStart(2, "0")).join("");
  return sigs.some((s) => s.length === expected.length && stripeTimingSafe(s, expected));
}

function stripeTimingSafe(a, b) {
  let d = 0;
  for (let i = 0; i < a.length; i++) d |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return d === 0;
}

// Emails the key (only if you've set a Resend API key + a from-address). Otherwise the success page is the delivery.
async function stripeEmailKey(env, email, key, plan) {
  const apiKey = env.RESEND_API_KEY || FALLBACK_RESEND_API_KEY;
  const from = env.EMAIL_FROM || FALLBACK_EMAIL_FROM;
  if (!apiKey || !from || !email) return;
  try {
    await fetch("https://api.resend.com/emails", {
      method: "POST",
      headers: { Authorization: "Bearer " + apiKey, "Content-Type": "application/json" },
      body: JSON.stringify({
        from: from,
        to: email,
        subject: "Your Lethal Client license key",
        text: "Thanks for getting Lethal Client (" + plan + " plan)!\n\nYour license key:\n\n" + key +
          "\n\nTo download the client:\n" +
          "1. Go to https://lethalclient.com/login and create a free account (or log in).\n" +
          "2. Open \"Redeem Key\" and paste the key above to unlock the download.\n" +
          "3. Download the client, drop it in your mods folder, and launch Minecraft.\n\n" +
          "Your key activates on one PC. Moving to a new computer? Contact support and we'll move it for you.",
      }),
    });
  } catch (_) {}
}

function storePage(htmlBody) {
  return new Response(htmlBody, { headers: { "content-type": "text/html; charset=utf-8", "cache-control": "no-store" } });
}

function storeSuccessHtml(key, error) {
  const inner = key
    ? '<p class="ok">Payment complete — here is your key</p>' +
      '<div class="keybox"><code id="k">' + escapeHtml(key) + '</code>' +
      '<button onclick="navigator.clipboard.writeText(document.getElementById(\'k\').textContent).then(()=>{this.textContent=\'Copied\'})">Copy</button></div>' +
      '<p class="muted">Create a free account, redeem this key, and download the client. If you entered an email, we sent the key there too. It works on one PC and you can move it any time from your dashboard.</p>' +
      '<a class="back" href="/login" style="display:inline-block;margin-top:8px;background:linear-gradient(100deg,#66c8f4,#2e8fc4);color:#fff;padding:11px 20px;border-radius:10px;text-decoration:none;font-weight:700">Go to dashboard &rarr;</a>'
    : '<p class="err">' + escapeHtml(error || "Something went wrong.") + '</p>' +
      '<p class="muted">If you were charged and did not get a key, contact support and we will sort it out.</p>';
  return '<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">' +
    '<title>Lethal Client</title><link rel="icon" type="image/svg+xml" href="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA2NCA2NCI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCIgeTE9IjAiIHgyPSIwIiB5Mj0iMSI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjNjZjOGY0Ii8+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjMTU2ZTk3Ii8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZD0iTTMyIDYgNTQgMTR2MTZjMCAxNC05IDI0LTIyIDI4QzE5IDU0IDEwIDQ0IDEwIDMwVjE0eiIgZmlsbD0idXJsKCNnKSIvPjxwYXRoIGQ9Ik0yMyAzMmw2IDYgMTItMTMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4="><style>' +
    'body{margin:0;background:#06070e;color:#eef1fa;font:16px/1.6 ui-sans-serif,system-ui,sans-serif;display:grid;place-items:center;min-height:100vh;padding:24px}' +
    '.card{max-width:520px;width:100%;background:linear-gradient(170deg,#141829,#0e1120);border:1px solid #20263c;border-radius:20px;padding:34px;text-align:center;box-shadow:0 30px 80px -30px #000}' +
    'h1{font-size:22px;margin:0 0 4px;font-family:ui-sans-serif,system-ui}.brand{background:linear-gradient(100deg,#66c8f4,#156e97);-webkit-background-clip:text;background-clip:text;color:transparent}' +
    '.ok{color:#36d39a;font-weight:700;font-size:18px;margin:16px 0 6px}.err{color:#ff6b6b;font-weight:700}' +
    '.keybox{display:flex;gap:8px;align-items:center;justify-content:center;flex-wrap:wrap;background:#070a13;border:1px solid #20263c;border-radius:12px;padding:12px;margin:14px 0}' +
    'code{font:15px ui-monospace,Menlo,monospace;color:#fff;word-break:break-all}' +
    'button{background:linear-gradient(100deg,#66c8f4,#2e8fc4);color:#fff;border:0;border-radius:9px;padding:9px 15px;font-weight:600;cursor:pointer}' +
    '.muted{color:#9aa2ba;font-size:14px}a.back{display:inline-block;margin-top:16px;color:#7aa2ff;text-decoration:none;font-weight:600}' +
    '</style></head><body><div class="card"><h1>Lethal <span class="brand">Client</span></h1>' + inner +
    '<a class="back" href="/">Back to site</a></div></body></html>';
}

function escapeHtml(s) {
  return String(s == null ? "" : s).replace(/[&<>"']/g, (c) => "&#" + c.charCodeAt(0) + ";");
}


// ========================= accounts & dashboard =========================
// Anyone can make a free account and sign in. The client download stays LOCKED
// until the account redeems a valid license key. One key links to one account.

function hex(buf) {
  return [...new Uint8Array(buf)].map((b) => b.toString(16).padStart(2, "0")).join("");
}
function unhex(s) {
  const a = new Uint8Array(s.length / 2);
  for (let i = 0; i < a.length; i++) a[i] = parseInt(s.substr(i * 2, 2), 16);
  return a;
}
function timingSafeEqualHex(a, b) {
  if (a.length !== b.length) return false;
  let r = 0;
  for (let i = 0; i < a.length; i++) r |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return r === 0;
}
async function pbkdf2Bits(password, salt, iters) {
  const key = await crypto.subtle.importKey("raw", new TextEncoder().encode(password), "PBKDF2", false, ["deriveBits"]);
  return new Uint8Array(await crypto.subtle.deriveBits({ name: "PBKDF2", salt, iterations: iters, hash: "SHA-256" }, key, 256));
}
async function hashPassword(password) {
  const salt = crypto.getRandomValues(new Uint8Array(16));
  const bits = await pbkdf2Bits(password, salt, 100000);
  return "pbkdf2$100000$" + hex(salt) + "$" + hex(bits);
}
async function verifyPassword(password, stored) {
  const p = String(stored || "").split("$");
  if (p.length !== 4 || p[0] !== "pbkdf2") return false;
  const bits = await pbkdf2Bits(password, unhex(p[2]), Number(p[1]) || 100000);
  return timingSafeEqualHex(hex(bits), p[3]);
}

function sessionToken(request) {
  const m = (request.headers.get("Cookie") || "").match(/(?:^|;\s*)lc_sess=([a-f0-9]+)/);
  return m ? m[1] : "";
}
async function currentUser(request, env) {
  const token = sessionToken(request);
  if (!token) return null;
  const s = await env.DB.prepare("SELECT uid FROM sessions WHERE token = ?").bind(token).first();
  if (!s) return null;
  return await env.DB.prepare("SELECT id, username, email, lic, created FROM users WHERE id = ?").bind(s.uid).first();
}
async function makeSession(env, uid) {
  const token = hex(crypto.getRandomValues(new Uint8Array(24)));
  await env.DB.prepare("INSERT INTO sessions (token, uid, created) VALUES (?, ?, ?)").bind(token, uid, Date.now()).run();
  return "lc_sess=" + token + "; Path=/; Max-Age=2592000; HttpOnly; Secure; SameSite=Lax";
}
function jsonCookie(obj, cookie, status) {
  return new Response(JSON.stringify(obj), {
    status: status || 200,
    headers: { "content-type": "application/json", "cache-control": "no-store", "Set-Cookie": cookie },
  });
}

/** Verify a Cloudflare Turnstile token against the siteverify API. Returns true only on a genuine pass. */
async function verifyTurnstile(env, token, ip) {
  try {
    const form = new URLSearchParams();
    form.append("secret", env.TURNSTILE_SECRET);
    form.append("response", token);
    if (ip) form.append("remoteip", ip);
    const r = await fetch("https://challenges.cloudflare.com/turnstile/v0/siteverify", { method: "POST", body: form });
    const d = await r.json();
    return !!(d && d.success);
  } catch (_) {
    return false;
  }
}

/** Mint a free client key and link it to this account, so the client download unlocks with no purchase.
 *  The client is free now — cosmetics are the paid part. Returns the key. Safe to call repeatedly. */
async function grantFreeKey(env, userId, email) {
  const key = newKey();
  await putKey(env, {
    key, note: "Free account" + (email ? " · " + email : ""), created: Date.now(), expires: 0,
    hwid: "", user: "", firstUsed: 0, lastSeen: 0, revoked: false,
  });
  await env.DB.prepare("UPDATE users SET lic = ? WHERE id = ?").bind(key, userId).run();
  return key;
}

async function authSignup(request, env) {
  const b = await request.json().catch(() => ({}));
  const username = String(b.username || "").trim();
  const email = String(b.email || "").trim().toLowerCase();
  const password = String(b.password || "");
  // Bot check: when Turnstile is configured, every signup must carry a valid captcha token. This is what
  // stops the distributed signup flood while still letting real people register (so the blanket block can go).
  if (env.TURNSTILE_SECRET) {
    const token = String(b.turnstile || "");
    if (!token || !(await verifyTurnstile(env, token, request.headers.get("cf-connecting-ip")))) {
      return json({ error: "Captcha failed — please tick the box and try again." }, 400);
    }
  }
  if (!/^[A-Za-z0-9_]{3,24}$/.test(username)) return json({ error: "Username must be 3–24 letters, numbers or underscores." }, 400);
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email) || email.length > 120) return json({ error: "Enter a valid email address." }, 400);
  if (password.length < 6) return json({ error: "Password must be at least 6 characters." }, 400);
  const exists = await env.DB.prepare("SELECT id FROM users WHERE lower(email) = ? OR lower(username) = ?").bind(email, username.toLowerCase()).first();
  if (exists) return json({ error: "That email or username is already taken." }, 400);
  const id = hex(crypto.getRandomValues(new Uint8Array(16)));
  const pass = await hashPassword(password);
  await env.DB.prepare("INSERT INTO users (id, username, email, pass, lic, created) VALUES (?, ?, ?, ?, '', ?)")
    .bind(id, username, email, pass, Date.now()).run();
  // The client is free: give the new account a free key right away so the download is unlocked.
  await grantFreeKey(env, id, email);
  return jsonCookie({ ok: true }, await makeSession(env, id));
}
async function authLogin(request, env) {
  const b = await request.json().catch(() => ({}));
  const who = String(b.email || b.username || "").trim().toLowerCase();
  const password = String(b.password || "");
  const u = await env.DB.prepare("SELECT id, pass FROM users WHERE lower(email) = ? OR lower(username) = ?").bind(who, who).first();
  if (!u || !(await verifyPassword(password, u.pass))) return json({ error: "Wrong email/username or password." }, 401);
  return jsonCookie({ ok: true }, await makeSession(env, u.id));
}
async function authLogout(request, env) {
  const token = sessionToken(request);
  if (token) await env.DB.prepare("DELETE FROM sessions WHERE token = ?").bind(token).run();
  return jsonCookie({ ok: true }, "lc_sess=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Lax");
}

function planFromNote(note) {
  const n = String(note || "");
  if (n.startsWith("Monthly")) return "Monthly";
  if (n.startsWith("Yearly")) return "Yearly";
  if (n.startsWith("Lifetime")) return "Lifetime";
  return "Active";
}
function maskKey(k) {
  k = String(k || "");
  if (k.length <= 10) return k;
  return k.slice(0, 10) + "••••" + k.slice(-4);
}

async function accountInfo(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Not signed in." }, 401);
  let linked = false, status = "none", plan = "", hwid = false, keyMask = "";
  if (user.lic) {
    const rec = await getKey(env, user.lic);
    if (rec) {
      linked = true;
      hwid = !!rec.hwid;
      status = rec.revoked ? "revoked" : (rec.expires && Date.now() > rec.expires ? "expired" : "active");
      plan = planFromNote(rec.note);
      keyMask = maskKey(user.lic);
    }
  }
  const settings = await getSettings(env);
  let versions = (settings.versionList || []).map((x) => ({ v: x.v, size: x.size || 0 }));
  if (versions.length === 0 && settings.update.version) versions = [{ v: settings.update.version, size: settings.update.size || 0 }];
  let mcAvailable = (settings.mcAvailable || []).map((x) => x.mc);
  if (mcAvailable.length === 0 && settings.update.sha) mcAvailable = ["26.2"];
  // Versions this account has already downloaded — each is offered only once.
  const downloaded = await downloadedVersions(env, "u:" + user.id);
  return json({
    username: user.username,
    email: user.email,
    linked, status, plan, hwid, key: keyMask,
    canDownload: linked && status === "active",
    version: settings.update.version || "",
    size: settings.update.size || 0,
    hasJar: mcAvailable.length > 0,
    versions,
    mcAvailable,
    downloaded,
  });
}
async function redeemKey(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Please sign in first." }, 401);
  const b = await request.json().catch(() => ({}));
  const key = normalizeKey(b.key);
  if (!key) return json({ error: "Enter your license key." }, 400);
  const rec = await getKey(env, key);
  if (!rec) return json({ error: "That license key doesn't exist." }, 400);
  if (rec.revoked) return json({ error: "That license key has been revoked." }, 400);
  if (rec.expires && Date.now() > rec.expires) return json({ error: "That license key has expired." }, 400);
  const other = await env.DB.prepare("SELECT id FROM users WHERE lic = ? AND id != ?").bind(key, user.id).first();
  if (other) return json({ error: "That key is already linked to another account." }, 400);
  await env.DB.prepare("UPDATE users SET lic = ? WHERE id = ?").bind(key, user.id).run();
  return json({ ok: true });
}
// --- Self-serve billing: the dashboard manages the Stripe subscription directly, no hosted portal. ---

/** The Stripe subscription id linked to a user's license key, or null (lifetime / manual key). */
async function subForUser(env, user) {
  if (!user || !user.lic) return null;
  const row = await env.DB.prepare("SELECT ref FROM stripe_map WHERE lic = ? AND kind = 'sub' ORDER BY at DESC").bind(user.lic).first();
  return row ? row.ref : null;
}

/** Current plan + renewal + cancel state, read live from Stripe, for the dashboard Billing panel. */
async function billingInfo(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Not signed in." }, 401);
  if (!user.lic) return json({ hasSub: false, plan: "none" });
  const subId = await subForUser(env, user);
  if (!subId) {
    const rec = await getKey(env, user.lic);
    // No subscription on file → a lifetime purchase (or a manually-issued key): nothing to manage.
    return json({ hasSub: false, plan: rec ? (planFromNote(rec.note).toLowerCase() || "lifetime") : "" });
  }
  let sub;
  try { sub = await stripeApi(env, "/subscriptions/" + encodeURIComponent(subId), "GET", null); }
  catch (e) { return json({ hasSub: false, plan: "", error: "Couldn't load your subscription right now." }); }
  const item = sub.items && sub.items.data && sub.items.data[0];
  const interval = item && item.price && item.price.recurring && item.price.recurring.interval;
  const plan = interval === "year" ? "yearly" : "monthly";
  return json({
    hasSub: true,
    plan,
    status: sub.status,
    renewsAt: (sub.current_period_end || 0) * 1000,
    cancelAtPeriodEnd: !!sub.cancel_at_period_end,
    canSwitchTo: plan === "monthly" ? "yearly" : "monthly",
  });
}

/** Cancel at the end of the paid period — the customer keeps access until then. */
async function billingCancel(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Not signed in." }, 401);
  const subId = await subForUser(env, user);
  if (!subId) return json({ error: "No active subscription to cancel." }, 400);
  try {
    const sub = await stripeApi(env, "/subscriptions/" + encodeURIComponent(subId), "POST", { cancel_at_period_end: true });
    return json({ ok: true, cancelAtPeriodEnd: !!sub.cancel_at_period_end, renewsAt: (sub.current_period_end || 0) * 1000 });
  } catch (e) { return json({ error: String(e.message || e) }, 500); }
}

/** Undo a pending cancellation. */
async function billingResume(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Not signed in." }, 401);
  const subId = await subForUser(env, user);
  if (!subId) return json({ error: "No subscription found." }, 400);
  try {
    const sub = await stripeApi(env, "/subscriptions/" + encodeURIComponent(subId), "POST", { cancel_at_period_end: false });
    return json({ ok: true, cancelAtPeriodEnd: !!sub.cancel_at_period_end });
  } catch (e) { return json({ error: String(e.message || e) }, 500); }
}

/** Switch between Monthly and Yearly, prorated. The license key stays active throughout. */
async function billingSwitch(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Not signed in." }, 401);
  const body = await request.json().catch(() => ({}));
  const target = body.plan === "yearly" ? "yearly" : body.plan === "monthly" ? "monthly" : "";
  if (!target) return json({ error: "Pick a plan to switch to." }, 400);
  const subId = await subForUser(env, user);
  if (!subId) return json({ error: "No active subscription to change." }, 400);
  const price = target === "yearly" ? (env.STRIPE_PRICE_YEARLY || FALLBACK_STRIPE_PRICE_YEARLY)
    : (env.STRIPE_PRICE_MONTHLY || FALLBACK_STRIPE_PRICE_MONTHLY);
  if (!price) return json({ error: "That plan isn't set up yet." }, 400);
  try {
    const sub = await stripeApi(env, "/subscriptions/" + encodeURIComponent(subId), "GET", null);
    const item = sub.items && sub.items.data && sub.items.data[0];
    if (!item) return json({ error: "Subscription has no billable item." }, 500);
    if (item.price && item.price.id === price) return json({ ok: true, plan: target }); // already on it
    await stripeApi(env, "/subscriptions/" + encodeURIComponent(subId), "POST", {
      items: [{ id: item.id, price: price }],
      proration_behavior: "create_prorations",
      cancel_at_period_end: false,
    });
    // Keep the key's note roughly in sync for the admin list.
    const rec = await getKey(env, user.lic);
    if (rec) { rec.note = (target === "yearly" ? "Yearly" : "Monthly") + (user.email ? " · " + user.email : ""); await putKey(env, rec); }
    return json({ ok: true, plan: target });
  } catch (e) { return json({ error: String(e.message || e) }, 500); }
}

/** Upgrade a subscription to Lifetime: a one-time $14.99 checkout that converts THIS key, then
 *  cancels the subscription. Full price — past subscription payments don't carry over. */
async function billingUpgrade(request, env, url) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Not signed in." }, 401);
  if (!user.lic) return json({ error: "No license key on this account." }, 400);
  const rec = await getKey(env, user.lic);
  if (rec && planFromNote(rec.note) === "Lifetime") return json({ error: "You're already on Lifetime." }, 400);
  const subId = await subForUser(env, user);
  if (!subId) return json({ error: "No active subscription to upgrade." }, 400);
  const price = env.STRIPE_PRICE_LIFETIME || FALLBACK_STRIPE_PRICE_LIFETIME;
  if (!price) return json({ error: "Lifetime isn't set up yet." }, 400);
  try {
    const session = await stripeApi(env, "/checkout/sessions", "POST", {
      mode: "payment",
      line_items: [{ price: price, quantity: 1 }],
      success_url: url.origin + "/success?session_id={CHECKOUT_SESSION_ID}",
      cancel_url: url.origin + "/dashboard",
      allow_promotion_codes: true,
      // Server-set, so a user can only upgrade their OWN key and sub.
      metadata: { plan: "lifetime", upgrade: "lifetime", lic: user.lic, sub: subId },
    });
    return json({ url: session.url });
  } catch (e) { return json({ error: String(e.message || e) }, 500); }
}

/** Convert the existing key (named in the session metadata) to Lifetime, then cancel the old sub.
 *  Used instead of minting a new key when a subscriber upgrades. Idempotent per checkout session. */
async function upgradeKeyToLifetime(env, session) {
  const lic = session.metadata && session.metadata.lic;
  if (!lic) return null;
  const existing = await env.DB.prepare("SELECT lic FROM stripe_map WHERE ref = ?").bind(session.id).first();
  if (existing) return existing.lic;
  const email = (session.customer_details && session.customer_details.email) || "";
  const rec = await getKey(env, lic);
  if (rec) {
    rec.expires = 0;
    rec.revoked = false;
    rec.note = "Lifetime" + (email ? " · " + email : "");
    await putKey(env, rec);
  }
  await env.DB.prepare("INSERT OR IGNORE INTO stripe_map (ref, lic, kind, at) VALUES (?, ?, 'session', ?)").bind(session.id, lic, Date.now()).run();
  if (session.payment_intent) {
    await env.DB.prepare("INSERT OR IGNORE INTO stripe_map (ref, lic, kind, at) VALUES (?, ?, 'pi', ?)").bind(session.payment_intent, lic, Date.now()).run();
  }
  // They own it outright now — stop the recurring subscription immediately.
  const sub = session.metadata && session.metadata.sub;
  if (sub) { try { await stripeApi(env, "/subscriptions/" + encodeURIComponent(sub), "DELETE", null); } catch (_) {} }
  return lic;
}

/** Record a download as a UNIQUE person: one row per IP, so the same IP downloading many times counts once.
 *  Best-effort — never fail a download over counting. */
/** Records one download of a given Minecraft version by a given identity, counted at most once
 *  (a re-download of the same version by the same person never re-counts). */
async function recordDownload(env, ident, mc) {
  if (!ident || !mc) return;
  try {
    await env.DB.prepare("INSERT OR IGNORE INTO dl_log (ident, mc, at) VALUES (?, ?, ?)").bind(ident, mc, Date.now()).run();
  } catch (_) {
    // counting is non-critical
  }
}

/** Which Minecraft versions this identity has already downloaded (so each can be offered only once). */
async function downloadedVersions(env, ident) {
  if (!ident) return [];
  try {
    const rows = await env.DB.prepare("SELECT mc FROM dl_log WHERE ident = ?").bind(ident).all();
    return (rows.results || []).map((r) => String(r.mc));
  } catch (_) {
    return [];
  }
}

/** Download counters for the website, counted ONCE per (person, Minecraft version):
 *  { counts: {"26.2": n, "1.21.11": m}, total, available }. The old IP counts + the historical
 *  base all predate per-MC tracking, so they fold into 26.2. */
async function downloadStats(env) {
  const counts = {};
  let base = 0, ipTotal = 0;
  try {
    const rows = await env.DB.prepare("SELECT mc, COUNT(*) AS c FROM dl_log GROUP BY mc").all();
    (rows.results || []).forEach((r) => { counts[String(r.mc)] = (counts[String(r.mc)] || 0) + (r.c || 0); });
    const b = await env.DB.prepare("SELECT value FROM settings WHERE name = 'dl_base'").first();
    base = b ? (parseInt(b.value, 10) || 0) : 0;
    const t = await env.DB.prepare("SELECT COUNT(*) AS c FROM dl_ips").first();
    ipTotal = (t && t.c) || 0;
  } catch (_) {
    // tables may not exist yet on a brand-new DB
  }
  counts["26.2"] = (counts["26.2"] || 0) + base + ipTotal;
  let available = [];
  try {
    const settings = await getSettings(env);
    available = (settings.mcAvailable || []).map((x) => x.mc);
    if (available.length === 0 && settings.update && settings.update.sha) available = ["26.2"];
  } catch (_) {}
  const total = Object.keys(counts).reduce((a, k) => a + (counts[k] || 0), 0);
  return json({ counts, total, available, "26.2": counts["26.2"] || 0, "1.21.11": counts["1.21.11"] || 0 });
}

/** The client is free with no account — anyone can download the newest jar straight from the site. */
async function publicDownload(request, env, url) {
  const settings = await getSettings(env);
  const mc = (String(url.searchParams.get("mc") || "26.2").trim()) || "26.2";
  let avail = (settings.mcAvailable || []).map((x) => x.mc);
  if (avail.length === 0 && settings.update.sha) avail = ["26.2"];
  if (!avail.includes(mc)) return json({ error: "There's no Lethal Client build for Minecraft " + mc + " yet." }, 404);
  let jar = env.LICENSES && await env.LICENSES.get("jar:mc:" + mc, "arrayBuffer");
  if (!jar && mc === "26.2") jar = env.LICENSES && await env.LICENSES.get("update:jar", "arrayBuffer");
  if (!jar) return json({ error: "That version isn't available for download yet." }, 404);
  await recordDownload(env, "ip:" + (request.headers.get("cf-connecting-ip") || "0.0.0.0"), mc);
  return new Response(jar, {
    headers: {
      "content-type": "application/java-archive",
      "content-disposition": 'attachment; filename="lethal-client-mc' + mc + '.jar"',
      "cache-control": "no-store",
    },
  });
}

async function dashDownload(request, env) {
  const user = await currentUser(request, env);
  if (!user) return json({ error: "Please sign in to download." }, 401);
  // The client is free: any signed-in account without a key gets a free one now, so the download works.
  if (!user.lic) { user.lic = await grantFreeKey(env, user.id, user.email); }
  const rec = await getKey(env, user.lic);
  if (!rec || rec.revoked || (rec.expires && Date.now() > rec.expires)) return json({ error: "Your account's download access isn't active. Contact support." }, 403);
  const settings = await getSettings(env);
  const url = new URL(request.url);
  // Preferred path: pick by Minecraft version.
  const mc = String(url.searchParams.get("mc") || "").trim();
  if (mc) {
    let avail = (settings.mcAvailable || []).map((x) => x.mc);
    if (avail.length === 0 && settings.update.sha) avail = ["26.2"]; // the only build before per-MC tracking
    if (!avail.includes(mc)) return json({ error: "There's no Lethal Client build for Minecraft " + mc + " yet." }, 404);
    let jar = env.LICENSES && await env.LICENSES.get("jar:mc:" + mc, "arrayBuffer");
    if (!jar && mc === "26.2") jar = env.LICENSES && await env.LICENSES.get("update:jar", "arrayBuffer");
    if (!jar) return json({ error: "That version isn't available for download." }, 404);
    await recordDownload(env, "u:" + user.id, mc);
    return new Response(jar, {
      headers: {
        "content-type": "application/java-archive",
        "content-disposition": 'attachment; filename="lethal-client-mc' + mc + '.jar"',
        "cache-control": "no-store",
      },
    });
  }
  const latest = settings.update.version || "";
  const want = String(url.searchParams.get("version") || "").trim();
  const known = (settings.versionList || []).map((x) => x.v);
  // Default to latest; an older version is served from its per-version copy.
  let ver = latest || "latest", kvKey = "update:jar";
  if (want && want !== latest && known.includes(want)) { ver = want; kvKey = "jar:" + want; }
  else if (want && want === latest) { ver = latest; kvKey = "update:jar"; }
  const jar = env.LICENSES && await env.LICENSES.get(kvKey, "arrayBuffer");
  if (!jar) return json({ error: "That version isn't available for download." }, 404);
  await recordDownload(env, "u:" + user.id, "26.2");
  const fname = "lethal-client-" + ver + ".jar";
  return new Response(jar, {
    headers: {
      "content-type": "application/java-archive",
      "content-disposition": 'attachment; filename="' + fname + '"',
      "cache-control": "no-store",
    },
  });
}

const LOGIN_HTML = `<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Lethal Client — Sign in</title>
{{TURNSTILE_SCRIPT}}
<link rel="icon" type="image/svg+xml" href="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA2NCA2NCI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCIgeTE9IjAiIHgyPSIwIiB5Mj0iMSI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjNjZjOGY0Ii8+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjMTU2ZTk3Ii8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZD0iTTMyIDYgNTQgMTR2MTZjMCAxNC05IDI0LTIyIDI4QzE5IDU0IDEwIDQ0IDEwIDMwVjE0eiIgZmlsbD0idXJsKCNnKSIvPjxwYXRoIGQ9Ik0yMyAzMmw2IDYgMTItMTMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4=">
<style>
  *{box-sizing:border-box}
  body{margin:0;min-height:100vh;display:grid;grid-template-columns:1fr;place-items:center;padding:24px;color:#eef1fa;font:16px/1.6 Manrope,ui-sans-serif,system-ui,sans-serif;
    background:#06070e;position:relative;overflow:hidden}
  .aura{position:fixed;inset:0;z-index:0;background:
    radial-gradient(600px 400px at 18% 12%,rgba(102,200,244,.14),transparent 60%),
    radial-gradient(700px 500px at 85% 90%,rgba(21,110,151,.18),transparent 60%),
    linear-gradient(180deg,#080c16,#06070e)}
  .aura::after{content:"";position:absolute;inset:0;background-image:linear-gradient(#0d1726 1px,transparent 1px),linear-gradient(90deg,#0d1726 1px,transparent 1px);background-size:46px 46px;opacity:.35;mask:radial-gradient(700px 500px at 50% 40%,#000,transparent 75%)}
  .box{position:relative;z-index:1;width:100%;max-width:410px}
  .top{text-align:center;margin-bottom:22px}
  .top .mk{display:inline-grid;place-items:center;width:52px;height:52px;border-radius:15px;background:linear-gradient(145deg,#66c8f4,#156e97);box-shadow:0 12px 30px -8px rgba(60,160,220,.75);margin-bottom:12px}
  .top .mk svg{width:28px;height:28px}
  .top b{display:block;font-family:"Chakra Petch",system-ui;font-weight:700;font-size:24px;letter-spacing:.28em;padding-left:.28em}
  .top span{color:#7f8ba6;font-size:13px;letter-spacing:.04em}
  .panel{background:linear-gradient(180deg,rgba(17,26,43,.82),rgba(10,16,28,.82));border:1px solid #1a2740;border-radius:22px;padding:28px;backdrop-filter:blur(16px);box-shadow:0 40px 90px -40px #000}
  .seg{display:flex;background:#0a1120;border:1px solid #1a2740;border-radius:12px;padding:4px;margin-bottom:22px}
  .seg button{flex:1;background:transparent;border:0;color:#8893ac;font:700 13.5px Manrope,sans-serif;padding:10px;border-radius:9px;cursor:pointer;transition:.15s}
  .seg button.on{color:#06131d;background:linear-gradient(100deg,#66c8f4,#2e8fc4)}
  form{display:none;flex-direction:column;gap:13px}
  form.on{display:flex}
  label{font-size:12px;letter-spacing:.04em;color:#8893ac;font-weight:600;margin-bottom:-7px}
  input{width:100%;background:#070d18;border:1px solid #1e2a44;border-radius:12px;color:#eef1fa;font:15px Manrope,sans-serif;padding:14px 15px;outline:none;transition:.15s}
  input::placeholder{color:#55617d}
  input:focus{border-color:#66c8f4;box-shadow:0 0 0 3px rgba(102,200,244,.14)}
  .sub{margin-top:6px;width:100%;padding:15px;border:0;border-radius:12px;background:linear-gradient(100deg,#66c8f4,#2e8fc4 55%,#156e97);color:#fff;font-weight:800;font-size:14px;letter-spacing:.1em;cursor:pointer;box-shadow:0 16px 34px -14px rgba(70,170,230,.85)}
  .sub:hover{filter:brightness(1.08)}.sub:disabled{opacity:.6;cursor:default}
  .msg{min-height:18px;text-align:center;font-size:13.5px;margin-top:4px;color:#ff6b6b}
  .msg.ok{color:#36d39a}
</style></head>
<body>
<div class="aura"></div>
<div class="box">
  <a class="top" href="/" title="Back to the website" style="display:block;text-decoration:none;color:inherit;cursor:pointer">
    <span class="mk"><svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m14 13-7.5 7.5c-.83.83-2.17.83-3 0a2.12 2.12 0 0 1 0-3L11 10"/><path d="m16 16 6-6"/><path d="m8 8 6-6"/><path d="m9 7 8 8"/><path d="m21 11-8-8"/></svg></span>
    <b>LETHAL</b><span>Your client dashboard</span>
  </a>
  <div class="panel">
    <div class="seg"><button id="tabLogin" class="on" onclick="swap('login')">Log in</button><button id="tabSignup" onclick="swap('signup')">Sign up</button></div>
    <form id="loginForm" class="on">
      <label>Email or username</label>
      <input id="l_who" placeholder="you@example.com" autocomplete="username">
      <label>Password</label>
      <input id="l_pass" type="password" placeholder="••••••••" autocomplete="current-password">
      <button class="sub" id="l_btn">LOG IN</button>
      <div class="msg" id="l_msg"></div>
    </form>
    <form id="signupForm">
      <label>Username</label>
      <input id="s_user" placeholder="Pick a username" autocomplete="username">
      <label>Email</label>
      <input id="s_email" type="email" placeholder="you@example.com" autocomplete="email">
      <label>Password</label>
      <input id="s_pass" type="password" placeholder="At least 6 characters" autocomplete="new-password">
      {{TURNSTILE_WIDGET}}
      <button class="sub" id="s_btn">CREATE ACCOUNT</button>
      <div class="msg" id="s_msg"></div>
    </form>
  </div>
</div>
<script>
  function swap(which){
    document.getElementById("loginForm").classList.toggle("on", which==="login");
    document.getElementById("signupForm").classList.toggle("on", which==="signup");
    document.getElementById("tabLogin").classList.toggle("on", which==="login");
    document.getElementById("tabSignup").classList.toggle("on", which==="signup");
    document.getElementById("l_msg").textContent=""; document.getElementById("s_msg").textContent="";
  }
  function el(id){return document.getElementById(id);}
  async function post(url,data){
    var r=await fetch(url,{method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify(data)});
    return {ok:r.ok, j:await r.json().catch(function(){return {};})};
  }
  el("l_btn").addEventListener("click", async function(e){
    e.preventDefault(); var b=el("l_btn"); b.disabled=true;
    var res=await post("/api/auth/login",{email:el("l_who").value.trim(),password:el("l_pass").value});
    if(res.ok&&res.j.ok){ location.href="/dashboard"; return; }
    el("l_msg").className="msg"; el("l_msg").textContent=res.j.error||"Could not sign in."; b.disabled=false;
  });
  el("s_btn").addEventListener("click", async function(e){
    e.preventDefault(); var b=el("s_btn"); b.disabled=true;
    var token=(window.turnstile&&typeof turnstile.getResponse==="function")?(turnstile.getResponse()||""):"";
    var res=await post("/api/auth/signup",{username:el("s_user").value.trim(),email:el("s_email").value.trim(),password:el("s_pass").value,turnstile:token});
    if(res.ok&&res.j.ok){ location.href="/dashboard"; return; }
    el("s_msg").className="msg"; el("s_msg").textContent=res.j.error||"Could not create account.";
    if(window.turnstile&&typeof turnstile.reset==="function"){ try{turnstile.reset();}catch(_){} }
    b.disabled=false;
  });
  [["l_pass","l_btn"],["s_pass","s_btn"]].forEach(function(p){
    el(p[0]).addEventListener("keydown",function(e){ if(e.key==="Enter") el(p[1]).click(); });
  });
</script>
</body></html>`;

const DASHBOARD_HTML = `<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Lethal Client — Dashboard</title>
<link rel="icon" type="image/svg+xml" href="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA2NCA2NCI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCIgeTE9IjAiIHgyPSIwIiB5Mj0iMSI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjNjZjOGY0Ii8+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjMTU2ZTk3Ii8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZD0iTTMyIDYgNTQgMTR2MTZjMCAxNC05IDI0LTIyIDI4QzE5IDU0IDEwIDQ0IDEwIDMwVjE0eiIgZmlsbD0idXJsKCNnKSIvPjxwYXRoIGQ9Ik0yMyAzMmw2IDYgMTItMTMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4=">
<style>
  *{box-sizing:border-box}
  body{margin:0;min-height:100vh;color:#e8eef8;font:15px/1.55 Manrope,ui-sans-serif,system-ui,sans-serif;
    background:radial-gradient(900px 500px at 80% -5%,rgba(21,110,151,.22),transparent 60%),radial-gradient(700px 400px at 0% 0%,rgba(102,200,244,.08),transparent 55%),#070a12}
  a{color:inherit;text-decoration:none}
  /* top bar */
  .bar{position:sticky;top:0;z-index:20;display:flex;align-items:center;gap:18px;padding:13px clamp(16px,4vw,40px);
    background:rgba(8,12,20,.82);backdrop-filter:blur(14px);border-bottom:1px solid #141e31}
  .logo{display:flex;align-items:center;gap:10px;text-decoration:none;color:inherit;cursor:pointer;transition:opacity .15s}
  .logo:hover{opacity:.82}
  .logo .mk{width:32px;height:32px;border-radius:9px;background:linear-gradient(145deg,#66c8f4,#156e97);display:grid;place-items:center;flex:none}
  .logo .mk svg{width:18px;height:18px}
  .logo b{font-family:"Chakra Petch",system-ui;font-weight:700;font-size:17px;letter-spacing:.14em}
  .tabs{display:flex;gap:4px;margin-left:10px;background:#0b1424;border:1px solid #17243c;border-radius:12px;padding:4px}
  .tabs button{display:flex;align-items:center;gap:8px;background:transparent;border:0;border-radius:9px;padding:9px 15px;color:#8b98b3;font:700 13.5px Manrope,sans-serif;cursor:pointer;transition:.13s}
  .tabs button svg{width:16px;height:16px}
  .tabs button:hover{color:#dce6f5}
  .tabs button.on{color:#06131d;background:linear-gradient(100deg,#66c8f4,#2e8fc4)}
  .acct{margin-left:auto;display:flex;align-items:center;gap:11px;background:#0b1424;border:1px solid #17243c;border-radius:12px;padding:7px 9px 7px 11px}
  .acct .t{text-align:right;line-height:1.15}
  .acct .nm{font-weight:700;font-size:13px}
  .acct .pl{font-size:11px;color:#36d39a;font-weight:700}
  .acct .av{width:32px;height:32px;border-radius:8px;background:linear-gradient(135deg,#66c8f4,#156e97);display:grid;place-items:center;font-weight:800;color:#06131d}
  .acct .out{background:0;border:0;color:#67748f;cursor:pointer;padding:3px}
  .acct .out:hover{color:#ff6b6b}
  main{max-width:980px;margin:0 auto;padding:30px clamp(16px,4vw,40px) 60px}
  .pane{display:none}.pane.on{display:block}
  .hl{font-family:"Chakra Petch",system-ui;font-weight:700;font-size:25px;margin:0}
  .hls{color:#8695af;margin:5px 0 24px;font-size:14px}
  /* hero */
  .hero{position:relative;display:flex;align-items:center;gap:26px;flex-wrap:wrap;
    background:radial-gradient(240px 200px at 88% -30%,rgba(102,200,244,.22),transparent 72%),linear-gradient(120deg,rgba(21,110,151,.22),rgba(12,20,36,.6) 55%);
    border:1px solid #1d3350;border-radius:22px;padding:26px 28px;overflow:visible;
    box-shadow:0 30px 70px -40px rgba(40,140,200,.5)}
  .hero .ico{width:64px;height:64px;border-radius:17px;background:linear-gradient(145deg,#66c8f4,#156e97);display:grid;place-items:center;flex:none;box-shadow:0 14px 30px -10px rgba(60,160,220,.7)}
  .hero .ico svg{width:34px;height:34px}
  .hero .info{min-width:200px}
  .hero .info .nm{font-family:"Chakra Petch",system-ui;font-weight:700;font-size:21px;display:flex;align-items:center;gap:10px}
  .hero .info .ver{color:#9fb1c9;font-size:13.5px;margin-top:3px}
  .pill{display:inline-flex;align-items:center;gap:6px;padding:4px 11px;border-radius:999px;font-size:11.5px;font-weight:700}
  .pill.lat{background:rgba(54,211,154,.14);border:1px solid rgba(54,211,154,.4);color:#36d39a}
  .pill.a{background:rgba(54,211,154,.14);color:#36d39a}.pill.b{background:rgba(255,107,107,.14);color:#ff6b6b}
  .hero .act{margin-left:auto}
  .dlbtn{display:inline-flex;align-items:center;gap:10px;padding:15px 30px;border:0;border-radius:14px;background:linear-gradient(100deg,#66c8f4,#2e8fc4 55%,#156e97);color:#fff;font-weight:800;font-size:15px;cursor:pointer;box-shadow:0 16px 34px -12px rgba(70,170,230,.9)}
  .dlbtn:hover{filter:brightness(1.08)}.dlbtn svg{width:19px;height:19px}
  .dlbtn .caret{display:inline-block;margin-left:2px;transition:transform .25s cubic-bezier(.2,.8,.2,1)}
  .dlbtn.open .caret{transform:rotate(180deg)}
  /* animated Minecraft-version picker */
  .dlpick{position:relative;display:inline-block}
  .dlmenu{position:absolute;top:calc(100% + 12px);right:0;width:310px;max-height:360px;overflow:auto;z-index:40;
    background:linear-gradient(180deg,#0d1a2c,#0a1322);border:1px solid #1d3350;border-radius:16px;padding:8px;
    box-shadow:0 36px 80px -34px #000,0 0 0 1px rgba(102,200,244,.06);
    opacity:0;transform:translateY(-10px) scale(.96);transform-origin:top right;pointer-events:none;
    transition:opacity .22s cubic-bezier(.2,.8,.2,1),transform .22s cubic-bezier(.2,.8,.2,1)}
  .dlmenu.open{opacity:1;transform:translateY(0) scale(1);pointer-events:auto}
  .dlmenu::-webkit-scrollbar{width:8px}.dlmenu::-webkit-scrollbar-thumb{background:#1d3350;border-radius:9px}
  .dlmenu-h{font-size:10.5px;letter-spacing:.1em;text-transform:uppercase;color:#64788f;font-weight:700;padding:9px 11px 7px}
  .dlitem{display:flex;align-items:center;gap:10px;padding:11px 12px;border-radius:11px;cursor:pointer;color:#dce6f5;font-weight:700;font-size:14px}
  .dlmenu.open .dlitem{animation:dlin .34s cubic-bezier(.2,.8,.2,1) both}
  .dlitem:hover{background:rgba(102,200,244,.12)}
  .dlitem .mc-dot{width:7px;height:7px;border-radius:50%;background:#36d39a;box-shadow:0 0 8px #36d39a;flex:none}
  .dlitem .tag{margin-left:auto;font-size:10.5px;font-weight:700;padding:3px 9px;border-radius:999px;background:rgba(54,211,154,.14);color:#36d39a}
  .dlitem.soon{color:#5f6d87;cursor:not-allowed}
  .dlitem.soon .mc-dot{background:#3a4660;box-shadow:none}
  .dlitem.soon .tag{background:rgba(120,135,160,.12);color:#78879c}
  .dlitem.soon:hover{background:transparent}
  @keyframes dlin{from{opacity:0;transform:translateX(-8px)}to{opacity:1;transform:none}}
  .locked{display:flex;flex-direction:column;align-items:flex-end;gap:10px;color:#90a0bb;text-align:right;max-width:280px}
  .locked .lk{display:flex;align-items:center;gap:9px;font-size:13.5px}
  .locked .lk svg{width:20px;height:20px;color:#5a6b89;flex:none}
  .lockbtn{display:inline-flex;align-items:center;gap:8px;padding:11px 18px;border:1px solid #2a3c5e;border-radius:11px;background:#0e1b2e;color:#cfe0f4;font-weight:700;cursor:pointer}
  .lockbtn:hover{border-color:#66c8f4;color:#fff}
  .fname{color:#5f6d87;font-size:12px;margin-top:2px}
  /* stat strip */
  .strip{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-top:16px}
  @media(max-width:760px){.strip{grid-template-columns:repeat(2,1fr)}.tabs button span{display:none}.acct .t{display:none}}
  .tile{background:#0b1322;border:1px solid #16233a;border-radius:15px;padding:15px 17px}
  .tile .k{display:flex;align-items:center;gap:8px;font-size:11px;letter-spacing:.08em;color:#64748f;font-weight:700;text-transform:uppercase}
  .tile .k svg{width:14px;height:14px;color:#66c8f4}
  .tile .v{font-family:"Chakra Petch",system-ui;font-weight:700;font-size:19px;margin-top:9px}
  /* changelog timeline + features */
  .sec{margin-top:26px}
  .sec h3{font-family:"Chakra Petch",system-ui;font-size:16px;margin:0 0 16px;display:flex;align-items:center;gap:9px}
  .sec h3 svg{width:17px;height:17px;color:#66c8f4}
  .tl{border-left:2px solid #182840;margin-left:7px;padding-left:22px}
  .tl .ver{position:relative;margin-bottom:20px}
  .tl .ver::before{content:"";position:absolute;left:-29px;top:4px;width:12px;height:12px;border-radius:50%;background:#66c8f4;box-shadow:0 0 0 4px #0a1322,0 0 12px rgba(102,200,244,.7)}
  .tl .vt{font-family:"Chakra Petch",system-ui;font-weight:700;font-size:15px;color:#dce6f5;margin-bottom:8px}
  .tl .note{display:flex;align-items:flex-start;gap:9px;padding:3px 0;font-size:13.5px;color:#aeb9cf}
  .tl .note svg{width:14px;height:14px;color:#36d39a;flex:none;margin-top:4px}
  .chips{display:flex;flex-wrap:wrap;gap:10px}
  .chip{display:inline-flex;align-items:center;gap:8px;background:#0c1526;border:1px solid #1a2a44;border-radius:999px;padding:9px 15px;font-size:13px;color:#c4d0e4;font-weight:600}
  .chip svg{width:14px;height:14px;color:#36d39a}
  /* forms */
  .card{background:#0b1322;border:1px solid #16233a;border-radius:18px;padding:24px;max-width:580px}
  .row{display:flex;align-items:center;justify-content:space-between;padding:8px 0;font-size:14px}
  .row .k{color:#8695af}
  .field{margin-top:16px}
  .field label{display:block;font-size:12.5px;color:#8695af;margin-bottom:8px;font-weight:600}
  .field input{width:100%;background:#070d18;border:1px solid #223450;border-radius:12px;color:#eef1fa;font:14px ui-monospace,Menlo,monospace;padding:14px 15px;outline:none}
  .field input:focus{border-color:#66c8f4;box-shadow:0 0 0 3px rgba(102,200,244,.14)}
  .btn{margin-top:16px;display:inline-flex;align-items:center;gap:9px;padding:13px 24px;border:0;border-radius:12px;background:linear-gradient(100deg,#66c8f4,#2e8fc4 55%,#156e97);color:#fff;font-weight:800;cursor:pointer}
  .btn:hover{filter:brightness(1.08)}.btn:disabled{opacity:.6;cursor:default}
  .btn.ghost{background:#0e1b2e;border:1px solid #2a3c5e;color:#cfe0f4}
  .btn.ghost:hover{border-color:#66c8f4;color:#fff}
  .note2{color:#8695af;font-size:13px;margin-top:14px}
  .msg{min-height:18px;font-size:13.5px;margin-top:12px;color:#ff6b6b}.msg.ok{color:#36d39a}
</style></head>
<body>
  <div class="bar">
    <a class="logo" href="/" title="Back to the website"><span class="mk"><svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m14 13-7.5 7.5c-.83.83-2.17.83-3 0a2.12 2.12 0 0 1 0-3L11 10"/><path d="m16 16 6-6"/><path d="m8 8 6-6"/><path d="m9 7 8 8"/><path d="m21 11-8-8"/></svg></span><b>LETHAL</b></a>
    <div class="tabs nav">
      <button class="on" data-pane="download"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v12"/><path d="m7 10 5 5 5-5"/><path d="M5 21h14"/></svg><span>Download</span></button>
      <button data-pane="redeem"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="7.5" cy="15.5" r="4.5"/><path d="m10.5 12.5 8-8"/><path d="m16 5 3 3"/><path d="m13 8 3 3"/></svg><span>Redeem Key</span></button>
      <button data-pane="billing"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/></svg><span>Billing</span></button>
    </div>
    <div class="acct">
      <div class="t"><div class="nm" id="meName">—</div><div class="pl" id="mePlan">—</div></div>
      <div class="av" id="meAv">L</div>
      <button class="out" title="Log out" onclick="logout()"><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/></svg></button>
    </div>
  </div>

  <main>
    <!-- DOWNLOAD -->
    <div class="pane on" id="p-download">
      <h1 class="hl">Download</h1>
      <p class="hls">Grab the latest Lethal Client for Minecraft 26.2.</p>

      <div class="hero">
        <span class="ico"><svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v12"/><path d="m7 10 5 5 5-5"/><path d="M5 21h14"/></svg></span>
        <div class="info"><div class="nm">Lethal Client <span class="pill lat" id="latestBadge" style="display:none"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Latest</span></div><div class="ver" id="verLine">Loading…</div></div>
        <div class="act" id="dlbox"></div>
      </div>

      <div class="strip">
        <div class="tile"><div class="k"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-6 8-6s8 2 8 6"/></svg>Account</div><div class="v" id="acUser">—</div></div>
        <div class="tile"><div class="k"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 10h18"/></svg>Plan</div><div class="v" id="acPlan" style="color:#36d39a">—</div></div>
        <div class="tile"><div class="k"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 12l2 2 4-4"/><circle cx="12" cy="12" r="9"/></svg>License</div><div class="v" id="acStatus">—</div></div>
        <div class="tile"><div class="k"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>Status</div><div class="v" id="stDl" style="font-size:15px;color:#36d39a">—</div></div>
      </div>

      <div class="sec">
        <h3><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg>Changelog</h3>
        <div class="tl" id="changelog"></div>
      </div>

      <div class="sec">
        <h3><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 3 2.5 5 5.5.8-4 3.9.9 5.5L12 16l-4.9 2.6.9-5.5-4-3.9 5.5-.8z"/></svg>What's included</h3>
        <div class="chips">
          <span class="chip"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Auction &amp; flip tracking</span>
          <span class="chip"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Sky lyrics &amp; Now Playing</span>
          <span class="chip"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Freelook, zoom &amp; FPS</span>
          <span class="chip"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Custom themes &amp; skies</span>
          <span class="chip"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Auto-updates, free forever</span>
        </div>
      </div>
    </div>

    <!-- REDEEM -->
    <div class="pane" id="p-redeem">
      <h1 class="hl">Redeem Key</h1>
      <p class="hls">Link the license key from your purchase email to unlock the download.</p>
      <div class="card">
        <div id="redeemState"></div>
        <div class="field"><label>License key</label><input id="keyInput" placeholder="LETHAL-XXXX-XXXX-XXXX" spellcheck="false" autocomplete="off"></div>
        <button class="btn" id="redeemBtn">Redeem key</button>
        <div class="note2">One key links to one account. Don't have a key yet? <a href="/" style="color:#66c8f4;font-weight:700">Get Lethal Client &rarr;</a></div>
        <div class="msg" id="redeemMsg"></div>
      </div>
    </div>

    <!-- BILLING -->
    <div class="pane" id="p-billing">
      <h1 class="hl">Billing</h1>
      <p class="hls">Manage your subscription — switch between Monthly and Yearly, or cancel any time.</p>
      <div class="card" id="billingCard"><p class="note2" style="margin:0">Loading…</p></div>
    </div>

  </main>

<script>
  var CHANGELOG=[
    {v:"1.0.69", notes:["Each Minecraft version now updates on its own track","Lyrics show for more songs again (the match filter was too strict)"]},
    {v:"1.0.68", notes:["Lyrics show for more songs again (the match filter was too strict)"]},
    {v:"1.0.67", notes:["Security &amp; anti-tamper hardening"]},
    {v:"1.0.66", notes:["Slurs are now masked in lyrics"]},
    {v:"1.0.65", notes:["Fixed wrong / foreign-language lyrics — only your actual song is matched now","Reads both Spotify and Apple Music off your PC (no login)","Lyrics lead a bit so they read on time, never behind"]},
    {v:"1.0.63", notes:["Lyrics lead a bit more so slow parts read on time too, never behind"]},
    {v:"1.0.62", notes:["Fixed lyrics showing twice","Lyrics now lead the vocal slightly so they read in time, never behind"]},
    {v:"1.0.61", notes:["Big lyrics sync fix — song position now stamped at read time, no pipe delay","Sky lyrics show in 2–4 word phrases (much easier to follow &amp; sync)"]},
    {v:"1.0.60", notes:["Removed the sync debug overlay","General music &amp; lyrics polish"]},
    {v:"1.0.59", notes:["Fake player name (hides your name on your own screen)","Live theme switching in the menu","Sky lyrics now instant &amp; scattered around you","Lots of nametag and sync fixes"]},
    {v:"1.0.4x", notes:["New sky presets with previews","Removed music hotkeys","Beat-pulse lyrics by default"]}
  ];
  // The Minecraft versions we support, newest first. Ones with a real build show "Available".
  var MC_VERSIONS=["26.2","1.21.11"];
  function el(id){return document.getElementById(id);}
  function fmtSize(b){ if(!b) return ""; return " · "+Math.round(b/1024)+" KB"; }
  document.querySelectorAll(".nav button").forEach(function(btn){
    btn.addEventListener("click",function(){
      document.querySelectorAll(".nav button").forEach(function(x){x.classList.remove("on");});
      document.querySelectorAll(".pane").forEach(function(x){x.classList.remove("on");});
      btn.classList.add("on");
      el("p-"+btn.getAttribute("data-pane")).classList.add("on");
      if(btn.getAttribute("data-pane")==="billing") loadBilling();
      window.scrollTo(0,0);
    });
  });
  function fmtDate(ms){ if(!ms) return ""; try{ return new Date(ms).toLocaleDateString(undefined,{year:"numeric",month:"long",day:"numeric"}); }catch(e){ return ""; } }
  async function loadBilling(){
    var c=el("billingCard"); c.innerHTML='<p class="note2" style="margin:0">Loading…</p>';
    var r,a; try{ r=await fetch("/api/billing"); a=await r.json(); }catch(e){ c.innerHTML='<p class="msg">Couldn\\'t load billing.</p>'; return; }
    if(r.status===401){ location.href="/login"; return; }
    if(!a.hasSub){
      if((a.plan||"")==="lifetime"){
        c.innerHTML='<div class="row"><span class="k">Plan</span><span style="font-weight:700;color:#36d39a">Lifetime</span></div><p class="note2">You own Lethal Client forever — there\\'s no subscription to manage. Enjoy every update, free.</p>';
      } else {
        c.innerHTML='<p class="note2" style="margin:0">No subscription on this account. If you bought a plan, <a href="#" onclick="goRedeem();return false" style="color:#66c8f4;font-weight:700">redeem your key</a> first. Lifetime purchases have nothing to manage here.</p>';
      }
      return;
    }
    var planName=a.plan==="yearly"?"Yearly ($9.99/yr)":"Monthly ($2.99/mo)";
    var otherName=a.canSwitchTo==="yearly"?"Yearly ($9.99/yr)":"Monthly ($2.99/mo)";
    var html='<div class="row"><span class="k">Current plan</span><span style="font-weight:700">'+planName+'</span></div>';
    html+='<div class="row"><span class="k">Status</span><span class="pill '+(a.status==="active"?"a":"b")+'">'+a.status+'</span></div>';
    if(a.cancelAtPeriodEnd){
      html+='<div class="row"><span class="k">Ends on</span><span style="font-weight:700;color:#ffb44d">'+fmtDate(a.renewsAt)+'</span></div>';
      html+='<p class="note2">Your subscription is set to cancel. You keep full access until the date above. Changed your mind?</p>';
      html+='<div style="display:flex;gap:10px;flex-wrap:wrap;margin-top:4px">';
      html+='<button class="btn" id="bResume">Resume subscription</button>';
      html+='<button class="btn ghost" id="bUpgrade">Upgrade to Lifetime ($14.99)</button>';
      html+='</div>';
    } else {
      html+='<div class="row"><span class="k">Renews on</span><span style="font-weight:700">'+fmtDate(a.renewsAt)+'</span></div>';
      html+='<div style="display:flex;gap:10px;flex-wrap:wrap;margin-top:18px">';
      html+='<button class="btn" id="bUpgrade">Upgrade to Lifetime ($14.99)</button>';
      html+='<button class="btn ghost" id="bSwitch">Switch to '+otherName+'</button>';
      html+='<button class="btn ghost" id="bCancel" style="border-color:#5e2a3c;color:#ff9fb0">Cancel subscription</button>';
      html+='</div>';
      html+='<p class="note2">Upgrading to Lifetime is a one-time $14.99 — it cancels your subscription and you own the client forever. Switching plans is prorated. Cancelling keeps your access until the renewal date.</p>';
    }
    html+='<div class="msg" id="billingMsg"></div>';
    c.innerHTML=html;
    var rb=el("bResume"); if(rb) rb.onclick=function(){ billingAction("/api/billing/resume",null,this,"Subscription resumed."); };
    var sw=el("bSwitch"); if(sw) sw.onclick=function(){ if(!confirm("Switch to "+otherName+"? You'll be charged the prorated difference now.")) return; billingAction("/api/billing/switch",{plan:a.canSwitchTo},this,"Plan changed to "+otherName+"."); };
    var cb=el("bCancel"); if(cb) cb.onclick=function(){ if(!confirm("Cancel your subscription? You'll keep access until "+fmtDate(a.renewsAt)+", then it won't renew.")) return; billingAction("/api/billing/cancel",null,this,"Subscription set to cancel at period end."); };
    var up=el("bUpgrade"); if(up) up.onclick=function(){ if(!confirm("Upgrade to Lifetime for a one-time $14.99? This cancels your subscription and you'll own the client forever.")) return; goUpgrade(this); };
  }
  async function goUpgrade(btn){
    btn.disabled=true; var old=btn.textContent; btn.textContent="Redirecting…";
    try{
      var r=await fetch("/api/billing/upgrade",{method:"POST"});
      var j=await r.json().catch(function(){return {};});
      if(r.ok&&j.url){ location.href=j.url; }
      else { btn.disabled=false; btn.textContent=old; var m=el("billingMsg"); if(m) m.textContent=j.error||"Couldn't start the upgrade."; }
    }catch(e){ btn.disabled=false; btn.textContent=old; var m2=el("billingMsg"); if(m2) m2.textContent="Network error — try again."; }
  }
  async function billingAction(path, body, btn, okMsg){
    btn.disabled=true; var old=btn.textContent; btn.textContent="Working…";
    try{
      var r=await fetch(path,{method:"POST",headers:body?{"content-type":"application/json"}:{},body:body?JSON.stringify(body):undefined});
      var j=await r.json().catch(function(){return {};});
      if(r.ok&&j.ok){ await loadBilling(); await load(); var m=el("billingMsg"); if(m){ m.className="msg ok"; m.textContent=okMsg; } }
      else { btn.disabled=false; btn.textContent=old; var m2=el("billingMsg"); if(m2) m2.textContent=j.error||"Something went wrong."; }
    }catch(e){ btn.disabled=false; btn.textContent=old; var m3=el("billingMsg"); if(m3) m3.textContent="Network error — try again."; }
  }
  async function logout(){ await fetch("/api/auth/logout",{method:"POST"}); location.href="/login"; }
  function render(a){
    el("meName").textContent=a.username; el("meAv").textContent=(a.username||"L").charAt(0).toUpperCase();
    el("mePlan").textContent=a.linked?a.plan:"No key";
    el("acUser").textContent=a.username;
    el("acPlan").textContent=a.linked?a.plan:"—";
    el("acStatus").innerHTML=a.linked?('<span class="pill '+(a.status==="active"?"a":"b")+'">'+a.status+'</span>'):'<span class="pill b">none</span>';
    el("stDl").textContent=a.hasJar?"Operational":"Preparing";
    el("verLine").textContent=a.hasJar?("Version "+(a.version||"—")):"No build uploaded yet";
    el("latestBadge").style.display=a.hasJar?"inline-flex":"none";
    var box=el("dlbox");
    if(!a.hasJar){
      box.innerHTML='<div class="locked"><div class="lk"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/></svg>Coming soon</div></div>';
    } else if(a.canDownload){
      var avail=(a.mcAvailable||[]);
      var done={}; (a.downloaded||[]).forEach(function(m){done[m]=1;});
      box.innerHTML=''
        +'<div class="dlpick">'
          +'<button class="dlbtn" id="dlTrigger" type="button"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v12"/><path d="m7 10 5 5 5-5"/><path d="M5 21h14"/></svg>Download<span class="caret">&#9662;</span></button>'
          +'<div class="dlmenu" id="dlMenu"><div class="dlmenu-h">Choose your Minecraft version</div><div id="dlList"></div></div>'
        +'</div>'
        +'<div class="fname">Pick your Minecraft version to download</div>';
      var list=el("dlList");
      // Only the versions that have a build uploaded show up; one you already grabbed is disabled.
      list.innerHTML=avail.map(function(v,i){
        var dl=!!done[v];
        return '<div class="dlitem'+(dl?' soon':'')+'" data-mc="'+v+'" data-done="'+(dl?1:0)+'" style="animation-delay:'+(i*0.022)+'s"><span class="mc-dot"></span>Minecraft '+v+'<span class="tag">'+(dl?'Downloaded':'Download')+'</span></div>';
      }).join("");
      var trg=el("dlTrigger"), menu=el("dlMenu");
      function openMenu(o){ menu.classList.toggle("open",o); trg.classList.toggle("open",o); }
      trg.addEventListener("click",function(e){ e.stopPropagation(); openMenu(!menu.classList.contains("open")); });
      document.addEventListener("click",function(){ openMenu(false); });
      menu.addEventListener("click",function(e){ e.stopPropagation(); });
      list.querySelectorAll(".dlitem").forEach(function(it){
        it.addEventListener("click",function(){
          if(it.getAttribute("data-done")==="1") return;   // already downloaded — only once each
          openMenu(false);
          window.location.href="/api/download?mc="+encodeURIComponent(it.getAttribute("data-mc"));
          setTimeout(load, 1800);                           // refresh so this version shows as Downloaded
        });
      });
    } else {
      box.innerHTML='<div class="locked"><div class="lk"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/></svg>'+(a.status==="active"?"Redeem your key to unlock":"Your key is "+a.status)+'</div><button class="lockbtn" onclick="goRedeem()"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="7.5" cy="15.5" r="4.5"/><path d="m10.5 12.5 8-8"/><path d="m16 5 3 3"/></svg>Redeem a key</button></div>';
    }
    el("redeemState").innerHTML = a.linked
      ? '<div class="row"><span class="k">Linked key</span><span style="font-family:ui-monospace,monospace;font-weight:700">'+a.key+'</span></div><div class="row"><span class="k">Status</span><span class="pill '+(a.status==="active"?"a":"b")+'">'+a.status+'</span></div><p class="note2">You can replace it by redeeming a different key below.</p>'
      : '<p class="note2" style="margin-top:0">No key linked yet. Paste your key below to unlock the download.</p>';
  }
  function goRedeem(){ document.querySelector('.nav button[data-pane="redeem"]').click(); }
  async function load(){
    var r=await fetch("/api/account"); if(r.status===401){location.href="/login";return;}
    var a=await r.json(); render(a);
    el("changelog").innerHTML=CHANGELOG.map(function(c){
      return '<div class="ver"><div class="vt">v'+c.v+'</div>'+c.notes.map(function(n){
        return '<div class="note"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>'+n+'</div>';
      }).join("")+'</div>';
    }).join("");
  }
  el("redeemBtn").addEventListener("click", async function(){
    var b=el("redeemBtn"); b.disabled=true; el("redeemMsg").className="msg"; el("redeemMsg").textContent="";
    var r=await fetch("/api/redeem",{method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({key:el("keyInput").value.trim()})});
    var j=await r.json().catch(function(){return {};});
    if(r.ok&&j.ok){ el("redeemMsg").className="msg ok"; el("redeemMsg").textContent="Key redeemed — download unlocked!"; await load(); document.querySelector('.nav button[data-pane="download"]').click(); }
    else { el("redeemMsg").textContent=j.error||"Could not redeem."; }
    b.disabled=false;
  });
  load();
</script>
</body></html>`;

const COUNTDOWN_HTML = `<!doctype html><html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">
<title>Lethal Client — Coming Soon</title>
<link rel="icon" type="image/svg+xml" href="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA2NCA2NCI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCIgeTE9IjAiIHgyPSIwIiB5Mj0iMSI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjNjZjOGY0Ii8+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjMTU2ZTk3Ii8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZD0iTTMyIDYgNTQgMTR2MTZjMCAxNC05IDI0LTIyIDI4QzE5IDU0IDEwIDQ0IDEwIDMwVjE0eiIgZmlsbD0idXJsKCNnKSIvPjxwYXRoIGQ9Ik0yMyAzMmw2IDYgMTItMTMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4=">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@600;700&family=Manrope:wght@500;600;700&family=JetBrains+Mono:wght@600&display=swap">
<style>
  *{box-sizing:border-box}
  html,body{height:100%}
  body{margin:0;background:radial-gradient(1200px 700px at 50% -10%,#10263a 0%,#0a1420 55%,#06070e 100%);
    color:#eef1fa;font:16px/1.6 Manrope,ui-sans-serif,system-ui,sans-serif;overflow:hidden;
    display:grid;place-items:center;min-height:100vh;text-align:center;padding:24px}
  canvas#bg{position:fixed;inset:0;width:100%;height:100%;z-index:0}
  .wrap{position:relative;z-index:1;max-width:760px;width:100%}
  .kicker{letter-spacing:.42em;text-transform:uppercase;font-size:12px;color:#8b93ad;font-weight:700;margin-bottom:14px}
  h1{font-family:"Chakra Petch",system-ui;font-weight:700;font-size:clamp(40px,9vw,88px);line-height:1;margin:0 0 6px}
  .brand{background:linear-gradient(100deg,#66c8f4,#2e8fc4 45%,#156e97);-webkit-background-clip:text;background-clip:text;color:transparent}
  .sub{color:#9aa2ba;font-size:clamp(14px,2.4vw,18px);margin:0 0 34px}
  .cd{display:flex;gap:clamp(10px,2.5vw,22px);justify-content:center;flex-wrap:wrap}
  .unit{min-width:clamp(74px,19vw,118px);background:linear-gradient(170deg,rgba(23,28,48,.9),rgba(12,15,30,.9));
    border:1px solid #222a44;border-radius:18px;padding:18px 10px 12px;box-shadow:0 30px 80px -40px #000}
  .num{font-family:"JetBrains Mono",monospace;font-weight:600;font-size:clamp(30px,8vw,54px);line-height:1;
    background:linear-gradient(180deg,#fff,#aeb8dd);-webkit-background-clip:text;background-clip:text;color:transparent}
  .lbl{margin-top:8px;font-size:11px;letter-spacing:.22em;text-transform:uppercase;color:#7b84a0;font-weight:700}
  .live{margin-top:30px;color:#6f7a99;font-size:13px}
  .dot{display:inline-block;width:8px;height:8px;border-radius:50%;background:#36d39a;margin-right:7px;vertical-align:middle;box-shadow:0 0 0 0 rgba(54,211,154,.6);animation:p 2s infinite}
  @keyframes p{0%{box-shadow:0 0 0 0 rgba(54,211,154,.5)}70%{box-shadow:0 0 0 10px rgba(54,211,154,0)}100%{box-shadow:0 0 0 0 rgba(54,211,154,0)}}
  /* key access, bottom-right */
  .keybtn{position:fixed;right:16px;bottom:16px;z-index:5;width:44px;height:44px;border-radius:13px;display:grid;place-items:center;
    cursor:pointer;background:rgba(14,17,32,.7);border:1px solid #20263c;color:#8b93ad;font-size:18px;backdrop-filter:blur(8px);
    transition:.18s}
  .keybtn:hover{color:#cfd6ee;border-color:#39456e;transform:translateY(-1px)}
  .panel{position:fixed;right:16px;bottom:70px;z-index:6;width:260px;background:rgba(14,17,32,.96);border:1px solid #222a44;
    border-radius:14px;padding:14px;box-shadow:0 30px 80px -30px #000;display:none;backdrop-filter:blur(10px)}
  .panel.open{display:block}
  .panel label{display:block;font-size:11px;letter-spacing:.18em;text-transform:uppercase;color:#7b84a0;font-weight:700;margin-bottom:8px}
  .panel input{width:100%;background:#070a13;border:1px solid #222a44;border-radius:9px;color:#fff;padding:10px 11px;
    font:14px "JetBrains Mono",monospace;outline:none}
  .panel input:focus{border-color:#66c8f4}
  .panel input.bad{border-color:#ff5f6e;animation:shake .35s}
  @keyframes shake{0%,100%{transform:translateX(0)}25%{transform:translateX(-6px)}75%{transform:translateX(6px)}}
  .panel button{margin-top:10px;width:100%;background:linear-gradient(100deg,#66c8f4,#2e8fc4);color:#fff;border:0;border-radius:9px;
    padding:10px;font-weight:700;cursor:pointer;font-family:Manrope}
</style></head>
<body>
<canvas id="bg"></canvas>
<div class="wrap">
  <div class="kicker">Lethal Client</div>
  <h1>Launching <span class="brand">Soon</span></h1>
  <p class="sub">The wait is almost over. Minecraft, upgraded.</p>
  <div style="display:inline-flex;align-items:center;gap:8px;margin:0 auto 22px;padding:8px 16px;border-radius:999px;background:rgba(102,200,244,.08);border:1px solid rgba(102,200,244,.28);color:#9fd8f4;font-size:13px;font-weight:600"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>Compatible with all clients</div>
  <div class="cd" id="cd">
    <div class="unit"><div class="num" id="d">00</div><div class="lbl">Days</div></div>
    <div class="unit"><div class="num" id="h">00</div><div class="lbl">Hours</div></div>
    <div class="unit"><div class="num" id="m">00</div><div class="lbl">Minutes</div></div>
    <div class="unit"><div class="num" id="s">00</div><div class="lbl">Seconds</div></div>
  </div>
  <div class="live"><span class="dot"></span>Doors open the moment the timer hits zero.</div>
</div>

<div class="keybtn" id="keybtn" title="Early access"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3 20 6v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/><path d="m9 12 2 2 4-4"/></svg></div>
<div class="panel" id="panel">
  <label>Early access key</label>
  <input id="keyin" type="password" placeholder="Enter key" autocomplete="off" spellcheck="false">
  <button id="go">Unlock</button>
</div>

<script>
  var LAUNCH_TARGET = ${LAUNCH_TS};
  var d=document.getElementById("d"),h=document.getElementById("h"),m=document.getElementById("m"),s=document.getElementById("s");
  function pad(n){return (n<10?"0":"")+n;}
  function tick(){
    var left = LAUNCH_TARGET - Date.now();
    if (isNaN(LAUNCH_TARGET) || left <= 0){ location.reload(); return; }
    var sec=Math.floor(left/1000);
    d.textContent=pad(Math.floor(sec/86400));
    h.textContent=pad(Math.floor(sec/3600)%24);
    m.textContent=pad(Math.floor(sec/60)%60);
    s.textContent=pad(sec%60);
  }
  tick(); setInterval(tick,1000);

  // If this browser already unlocked once, re-establish access automatically so a reload or a
  // return visit keeps you in the site (not bounced back to the countdown). Tries once per tab to
  // avoid any loop if the cookie can't be set.
  (function(){
    try{
      var saved = localStorage.getItem("lc_key");
      if (saved && sessionStorage.getItem("lc_tried") !== "1") {
        sessionStorage.setItem("lc_tried","1");
        fetch("/api/access",{method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({key:saved})})
          .then(function(r){return r.json();}).then(function(j){ if(j && j.ok) location.reload(); }).catch(function(){});
      }
    }catch(e){}
  })();

  // key access
  var panel=document.getElementById("panel"), keybtn=document.getElementById("keybtn"),
      keyin=document.getElementById("keyin"), go=document.getElementById("go");
  keybtn.addEventListener("click",function(){ panel.classList.toggle("open"); if(panel.classList.contains("open")) keyin.focus(); });
  async function submit(){
    keyin.classList.remove("bad");
    try{
      var r=await fetch("/api/access",{method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({key:keyin.value})});
      var j=await r.json().catch(function(){return{};});
      if(j && j.ok){ try{ localStorage.setItem("lc_key", keyin.value); }catch(e){} location.reload(); return; }
    }catch(e){}
    keyin.classList.add("bad"); keyin.value=""; keyin.focus();
  }
  go.addEventListener("click",submit);
  keyin.addEventListener("keydown",function(e){ if(e.key==="Enter") submit(); });

  // subtle starfield
  (function(){
    var c=document.getElementById("bg"),x=c.getContext("2d"),W,H,dpr=Math.min(2,devicePixelRatio||1),st=[];
    function size(){W=c.width=innerWidth*dpr;H=c.height=innerHeight*dpr;}
    size();addEventListener("resize",size);
    for(var i=0;i<120;i++) st.push({x:Math.random(),y:Math.random(),z:Math.random()*0.8+0.2,t:Math.random()*6});
    function frame(){
      x.clearRect(0,0,W,H);
      for(var i=0;i<st.length;i++){var p=st[i];p.t+=0.02;
        var a=(0.35+0.4*Math.sin(p.t))*p.z, r=p.z*1.6*dpr;
        x.fillStyle="rgba("+(140+Math.floor(80*p.z))+","+(160+Math.floor(60*p.z))+",255,"+a.toFixed(3)+")";
        x.beginPath();x.arc(p.x*W,p.y*H,r,0,6.283);x.fill();}
      requestAnimationFrame(frame);
    }
    frame();
  })();
</script>
</body></html>`;

const STORE_HTML = `<title>Lethal Client</title>
<link rel="icon" type="image/svg+xml" href="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA2NCA2NCI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJnIiB4MT0iMCIgeTE9IjAiIHgyPSIwIiB5Mj0iMSI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjNjZjOGY0Ii8+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjMTU2ZTk3Ii8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZD0iTTMyIDYgNTQgMTR2MTZjMCAxNC05IDI0LTIyIDI4QzE5IDU0IDEwIDQ0IDEwIDMwVjE0eiIgZmlsbD0idXJsKCNnKSIvPjxwYXRoIGQ9Ik0yMyAzMmw2IDYgMTItMTMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4=">
<meta name="claude:viewport-fit-sides" content="cover">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@500;600;700&family=Manrope:wght@400;500;600;700&family=JetBrains+Mono:wght@500&display=swap">
<style>
  /* Layout concept: one dark editorial scroll, full-bleed bands, a glass hero card,
     a feature bento, two pricing tiers. Committed single dark "neon client" world. */
  :root {
    --bg:#06070e; --bg-soft:#0a0c17; --surface:#0e1120; --surface-2:#141829;
    --line:#20263c; --line-soft:#171c2d;
    --fg:#eef1fa; --muted:#9aa2ba; --faint:#6b7291;
    --blue:#66c8f4; --violet:#2e8fc4; --pink:#156e97;
    --grad:linear-gradient(100deg,#66c8f4 0%,#2e8fc4 52%,#156e97 100%);
    --grad-soft:linear-gradient(100deg,rgba(77,139,255,.16),rgba(255,95,162,.16));
    --ok:#36d39a;
    --radius:18px; --radius-sm:12px;
    --display:"Chakra Petch",ui-sans-serif,system-ui,sans-serif;
    --body:"Manrope",ui-sans-serif,system-ui,sans-serif;
    --mono:"JetBrains Mono",ui-monospace,Menlo,monospace;
    color-scheme:dark;
  }
  * { box-sizing:border-box; }
  body {
    margin:0; background:var(--bg); color:var(--fg);
    font-family:var(--body); font-size:16px; line-height:1.6;
    -webkit-font-smoothing:antialiased; overflow-x:hidden;
  }
  #petals { position:fixed; inset:0; width:100%; height:100%; z-index:0; pointer-events:none; opacity:.6; }
  .glow { position:fixed; z-index:0; pointer-events:none; filter:blur(90px); opacity:.5; border-radius:50%; }
  .glow.a { top:-180px; left:-120px; width:520px; height:520px; background:radial-gradient(circle,#2b9fe0,transparent 70%); }
  .glow.b { top:220px; right:-160px; width:480px; height:480px; background:radial-gradient(circle,#1f86c0,transparent 70%); opacity:.35; }
  .wrap { position:relative; z-index:1; width:100%; max-width:1120px; margin:0 auto;
    padding-left:max(20px,env(safe-area-inset-left,0px)); padding-right:max(20px,env(safe-area-inset-right,0px)); }

  h1,h2,h3 { font-family:var(--display); font-weight:700; line-height:1.08; text-wrap:balance; margin:0; letter-spacing:-.01em; }
  a { color:inherit; text-decoration:none; }
  .gt { background:var(--grad); -webkit-background-clip:text; background-clip:text; color:transparent; }
  .eyebrow { font-family:var(--display); font-weight:600; font-size:12.5px; letter-spacing:.18em; text-transform:uppercase; color:var(--blue); }

  /* ---- nav ---- */
  header { position:sticky; top:env(safe-area-inset-top,0px); z-index:20;
    backdrop-filter:blur(14px); background:rgba(6,7,14,.72); border-bottom:1px solid var(--line-soft); }
  body::before { content:""; position:fixed; inset:0 0 auto; height:env(safe-area-inset-top,0px); z-index:21; background:rgba(6,7,14,.9); }
  .nav { display:flex; align-items:center; justify-content:space-between; height:66px; }
  .brand { display:flex; align-items:center; gap:11px; font-family:var(--display); font-weight:700; font-size:18px; letter-spacing:-.01em; }
  .mark { width:26px; height:26px; border-radius:8px; background:var(--grad); box-shadow:0 0 18px rgba(50,150,210,.5); flex:none;
    display:grid; place-items:center; transform:rotate(0deg); }
  .mark span { width:9px; height:9px; background:#06070e; border-radius:2px; transform:rotate(45deg); }
  .nav-links { display:flex; align-items:center; gap:28px; }
  .nav-links a.navlink { color:var(--muted); font-weight:600; font-size:14.5px; }
  .nav-links a.navlink:hover { color:var(--fg); }
  .btn { display:inline-flex; align-items:center; justify-content:center; gap:8px; font-family:var(--display); font-weight:600;
    border-radius:11px; padding:11px 18px; font-size:14.5px; cursor:pointer; border:1px solid transparent; transition:transform .12s ease, box-shadow .2s ease, background .2s ease; white-space:nowrap; }
  .btn-primary { background:var(--grad); color:#fff; border:0; box-shadow:0 8px 26px -8px rgba(50,150,210,.7); }
  .btn-primary:hover { transform:translateY(-1px); box-shadow:0 12px 30px -8px rgba(50,150,210,.85); }
  .btn-ghost { background:rgba(255,255,255,.03); color:var(--fg); border-color:var(--line); }
  .btn-ghost:hover { border-color:#39406080; background:rgba(255,255,255,.06); }
  .btn-lg { padding:14px 24px; font-size:15.5px; border-radius:13px; }
  .nav .btn { padding:9px 16px; }

  /* ---- hero ---- */
  .hero { display:grid; grid-template-columns:1.05fr .95fr; gap:52px; align-items:center; padding-top:76px; padding-bottom:70px; }
  .hero-copy .eyebrow { margin-bottom:18px; display:inline-block; }
  .hero h1 { font-size:clamp(38px,6vw,62px); }
  .hero p.lead { color:var(--muted); font-size:18px; max-width:40ch; margin:22px 0 32px; }
  .cta-row { display:flex; gap:14px; flex-wrap:wrap; }
  .meta-row { display:flex; gap:22px; flex-wrap:wrap; margin-top:30px; color:var(--faint); font-size:13.5px; font-weight:600; }
  .meta-row span { display:inline-flex; align-items:center; gap:7px; }
  .dot { width:6px; height:6px; border-radius:50%; background:var(--ok); box-shadow:0 0 8px var(--ok); }

  /* hero glass card */
  .card3d { position:relative; }
  .glass { background:linear-gradient(160deg,rgba(20,24,41,.9),rgba(10,12,23,.9)); border:1px solid var(--line);
    border-radius:22px; padding:22px; box-shadow:0 30px 80px -30px rgba(0,0,0,.8), inset 0 1px 0 rgba(255,255,255,.04); }
  .lic-top { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
  .lic-top .who { display:flex; align-items:center; gap:10px; font-family:var(--display); font-weight:600; font-size:14px; }
  .avatar { width:30px; height:30px; border-radius:8px; background:linear-gradient(135deg,#caa26a,#8a5a3b); flex:none; }
  .chip { font-family:var(--display); font-size:11px; font-weight:600; letter-spacing:.08em; text-transform:uppercase;
    padding:5px 10px; border-radius:999px; background:rgba(54,211,154,.12); color:var(--ok); border:1px solid rgba(54,211,154,.3); }
  .keyfield { font-family:var(--mono); font-size:14px; letter-spacing:.02em; background:#070a13; border:1px solid var(--line);
    border-radius:11px; padding:13px 14px; color:#cdd6ea; display:flex; align-items:center; justify-content:space-between; }
  .keyfield b { color:#fff; font-weight:500; }
  .mini-grid { display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-top:14px; }
  .stat { background:#0b0e1a; border:1px solid var(--line-soft); border-radius:12px; padding:12px 13px; }
  .stat .l { font-size:11px; color:var(--faint); font-weight:600; letter-spacing:.06em; text-transform:uppercase; }
  .stat .v { font-family:var(--display); font-weight:700; font-size:20px; margin-top:3px; }
  .stat .v.pos { color:var(--ok); }
  .float-badge { position:absolute; right:-14px; bottom:34px; background:var(--surface-2); border:1px solid var(--line);
    border-radius:13px; padding:10px 14px; display:flex; align-items:center; gap:9px; box-shadow:0 18px 40px -16px rgba(0,0,0,.8);
    font-family:var(--display); font-weight:600; font-size:13px; }

  /* ---- section frame ---- */
  section { position:relative; z-index:1; }
  .band { padding-block:84px; }
  .band.alt { background:linear-gradient(180deg,transparent,var(--bg-soft) 12%,var(--bg-soft) 88%,transparent); }
  .sec-head { max-width:620px; margin-bottom:46px; }
  .sec-head h2 { font-size:clamp(28px,4vw,42px); }
  .sec-head p { color:var(--muted); font-size:17px; margin:14px 0 0; }

  /* ---- features bento ---- */
  .bento { display:grid; grid-template-columns:repeat(3,1fr); gap:16px; }
  .feat { background:var(--surface); border:1px solid var(--line-soft); border-radius:var(--radius); padding:22px;
    transition:border-color .2s ease, transform .2s ease, background .2s ease; }
  .feat:hover { border-color:#2e3660; transform:translateY(-3px); background:var(--surface-2); }
  .feat.wide { grid-column:span 2; }
  .ico { width:42px; height:42px; border-radius:11px; background:var(--grad-soft); border:1px solid var(--line);
    display:grid; place-items:center; margin-bottom:16px; }
  .ico svg { width:21px; height:21px; stroke:#cbd4ff; fill:none; stroke-width:1.7; }
  .feat h3 { font-size:17px; margin-bottom:7px; }
  .feat p { color:var(--muted); font-size:14.5px; margin:0; }

  /* ---- pricing ---- */
  .prices { display:grid; grid-template-columns:1fr 1fr 1fr; gap:18px; max-width:1000px; margin:0 auto; }
  .price { position:relative; background:var(--surface); border:1px solid var(--line); border-radius:22px; padding:30px; display:flex; flex-direction:column; }
  .price.feat-plan { background:linear-gradient(170deg,rgba(29,24,55,.6),var(--surface)); border-color:transparent;
    box-shadow:0 0 0 1.5px transparent; }
  .price.feat-plan::before { content:""; position:absolute; inset:-1.5px; border-radius:23px; padding:1.5px; background:var(--grad);
    -webkit-mask:linear-gradient(#000 0 0) content-box,linear-gradient(#000 0 0); -webkit-mask-composite:xor; mask-composite:exclude; pointer-events:none; }
  .ribbon { position:absolute; top:22px; right:22px; font-family:var(--display); font-size:11px; font-weight:600; letter-spacing:.1em;
    text-transform:uppercase; background:var(--grad); color:#fff; padding:5px 11px; border-radius:999px; }
  .plan-name { font-family:var(--display); font-weight:600; font-size:15px; color:var(--muted); letter-spacing:.04em; text-transform:uppercase; }
  .price .amt { display:flex; align-items:baseline; gap:4px; margin:16px 0 4px; }
  .price .amt .big { font-family:var(--display); font-weight:700; font-size:52px; letter-spacing:-.02em; }
  .price .amt .per { color:var(--faint); font-weight:600; font-size:16px; }
  .price .tag { color:var(--muted); font-size:14px; min-height:20px; }
  .plan-list { list-style:none; padding:0; margin:22px 0 26px; display:flex; flex-direction:column; gap:12px; }
  .plan-list li { display:flex; gap:11px; align-items:flex-start; font-size:14.5px; color:#d4daea; }
  .plan-list svg { width:18px; height:18px; flex:none; margin-top:2px; stroke:var(--blue); fill:none; stroke-width:2.2; }
  .price.feat-plan .plan-list svg { stroke:var(--pink); }
  .price .btn { margin-top:auto; width:100%; }
  .guarantee { text-align:center; color:var(--faint); font-size:13.5px; margin-top:22px; }

  /* ---- steps ---- */
  .steps { display:grid; grid-template-columns:repeat(3,1fr); gap:18px; counter-reset:s; }
  .step { background:var(--surface); border:1px solid var(--line-soft); border-radius:var(--radius); padding:24px; position:relative; }
  .step .num { font-family:var(--display); font-weight:700; font-size:14px; width:30px; height:30px; border-radius:9px;
    display:grid; place-items:center; background:var(--grad-soft); border:1px solid var(--line); color:#cbd4ff; margin-bottom:15px; }
  .step h3 { font-size:16.5px; margin-bottom:7px; }
  .step p { color:var(--muted); font-size:14px; margin:0; }

  /* ---- faq ---- */
  .faq { max-width:760px; margin:0 auto; display:flex; flex-direction:column; gap:12px; }
  details { background:var(--surface); border:1px solid var(--line-soft); border-radius:var(--radius-sm); overflow:hidden; }
  details[open] { border-color:var(--line); }
  summary { cursor:pointer; list-style:none; padding:18px 20px; font-family:var(--display); font-weight:600; font-size:16px;
    display:flex; align-items:center; justify-content:space-between; gap:16px; }
  summary::-webkit-details-marker { display:none; }
  summary .pm { width:20px; height:20px; flex:none; position:relative; }
  summary .pm::before,summary .pm::after { content:""; position:absolute; background:var(--muted); border-radius:2px; transition:transform .2s ease; }
  summary .pm::before { top:9px; left:2px; width:16px; height:2px; }
  summary .pm::after { top:2px; left:9px; width:2px; height:16px; }
  details[open] summary .pm::after { transform:rotate(90deg); opacity:0; }
  details .body { padding:0 20px 20px; color:var(--muted); font-size:15px; }

  /* ---- cta banner ---- */
  .final { text-align:center; background:linear-gradient(170deg,var(--surface-2),var(--surface)); border:1px solid var(--line);
    border-radius:26px; padding:56px 32px; position:relative; overflow:hidden; }
  .final::after { content:""; position:absolute; inset:0; background:var(--grad-soft); opacity:.5; pointer-events:none; }
  .final > * { position:relative; z-index:1; }
  .final h2 { font-size:clamp(28px,4vw,40px); }
  .final p { color:var(--muted); font-size:17px; margin:14px auto 28px; max-width:46ch; }

  /* ---- footer ---- */
  footer { border-top:1px solid var(--line-soft); padding-block:34px; margin-top:12px; }
  .foot { display:flex; align-items:center; justify-content:space-between; gap:20px; flex-wrap:wrap; color:var(--faint); font-size:13px; }
  .foot .brand { font-size:16px; }
  .disclaimer { color:var(--faint); font-size:12px; max-width:62ch; margin-top:14px; line-height:1.5; }

  @media (max-width:860px) {
    .hero { grid-template-columns:1fr; gap:38px; padding-top:54px; }
    .card3d { max-width:440px; }
    .bento { grid-template-columns:1fr 1fr; }
    .feat.wide { grid-column:span 2; }
  }
  @media (max-width:620px) {
    .nav-links .navlink { display:none; }
    .bento { grid-template-columns:1fr; }
    .feat.wide { grid-column:span 1; }
    .prices { grid-template-columns:1fr; }
    .steps { grid-template-columns:1fr; }
    .band { padding-block:62px; }
    .float-badge { display:none; }
  }
  @media (prefers-reduced-motion:reduce) { #petals { display:none; } * { transition:none !important; } }
</style>

<canvas id="petals"></canvas>
<div class="glow a"></div>
<div class="glow b"></div>

<header>
  <div class="wrap nav">
    <a class="brand" href="#top"><span class="mark"><span></span></span>Lethal Client</a>
    <nav class="nav-links">
      <a class="navlink" href="#features">Features</a>
      <a class="navlink" href="#faq">FAQ</a>
      <a class="btn btn-primary" href="/download">Download</a>
    </nav>
  </div>
</header>

<a id="top"></a>
<section>
  <div class="wrap hero">
    <div class="hero-copy">
      <span class="eyebrow">DonutSMP · Minecraft 26.2</span>
      <h1>Flip the auction house.<br><span class="gt">Keep every edge.</span></h1>
      <p class="lead">Lethal Client is the all-in-one auction and quality-of-life client for DonutSMP — live flip tracking, a profit HUD, freelook, smooth zoom and a title screen that's actually yours.</p>
      <div class="cta-row">
        <a class="btn btn-primary btn-lg" href="/download">Download — it's free</a>
        <a class="btn btn-ghost btn-lg" href="#features">What's inside</a>
      </div>
      <div class="meta-row">
        <span><span class="dot"></span> 100% free</span>
        <span><span class="dot"></span> Windows</span>
        <span><span class="dot"></span> Free updates forever</span>
      </div>
    </div>

    <div class="card3d">
      <div class="glass">
        <div class="lic-top">
          <div class="who"><span class="avatar"></span> Player615</div>
          <span class="chip">Active · Free</span>
        </div>
        <div class="keyfield"><b>LETHAL-7F2K-QP4M-9XJ-R3BN</b> <span style="color:var(--ok)">✓</span></div>
        <div class="mini-grid">
          <div class="stat"><div class="l">Session profit</div><div class="v pos">+41.8M</div></div>
          <div class="stat"><div class="l">Flips tracked</div><div class="v">126</div></div>
          <div class="stat"><div class="l">FPS</div><div class="v">244</div></div>
          <div class="stat"><div class="l">Ping</div><div class="v">22<span style="font-size:13px;color:var(--faint)">ms</span></div></div>
        </div>
      </div>
      <div class="float-badge"><span class="dot"></span> One PC · move anytime</div>
    </div>
  </div>
</section>

<section class="band alt" id="features">
  <div class="wrap">
    <div class="sec-head">
      <span class="eyebrow">Everything in one client</span>
      <h2>Built for the grind, tuned for speed.</h2>
      <p>One lightweight Fabric mod. Toggle what you want, move every HUD, bind every key — no bloat.</p>
    </div>
    <div class="bento">
      <div class="feat wide">
        <div class="ico"><svg viewBox="0 0 24 24"><path d="M3 7l9-4 9 4-9 4-9-4z"/><path d="M3 12l9 4 9-4"/><path d="M3 17l9 4 9-4"/></svg></div>
        <h3>Auction House tools</h3>
        <p>Track flips as they happen, watch recent sales, and keep a running list of what's worth sniping — all without alt-tabbing out of the game.</p>
      </div>
      <div class="feat">
        <div class="ico"><svg viewBox="0 0 24 24"><path d="M4 18V9m5 9V4m5 14v-6m5 6V7"/></svg></div>
        <h3>Profit HUD</h3>
        <p>A live transactions overlay that tallies your coins. Drag it anywhere, scale it to taste.</p>
      </div>
      <div class="feat">
        <div class="ico"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M12 5c-5 0-9 4.5-10 7 1 2.5 5 7 10 7s9-4.5 10-7c-1-2.5-5-7-10-7z"/></svg></div>
        <h3>Freelook</h3>
        <p>Look around without turning your player. Hold or toggle — your call, your keybind.</p>
      </div>
      <div class="feat">
        <div class="ico"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4-4"/><path d="M11 8v6M8 11h6"/></svg></div>
        <h3>Smooth zoom</h3>
        <p>Spyglass-grade zoom with slowed aim, scroll-to-adjust and an optional click sound.</p>
      </div>
      <div class="feat">
        <div class="ico"><svg viewBox="0 0 24 24"><path d="M3 12h4l2 6 4-14 2 8h6"/></svg></div>
        <h3>FPS &amp; ping</h3>
        <p>A clean, movable readout with color thresholds so you always know your frames.</p>
      </div>
      <div class="feat">
        <div class="ico"><svg viewBox="0 0 24 24"><path d="M3 15a4 4 0 014-4 5 5 0 019.6-1.5A3.5 3.5 0 1118 18H7a4 4 0 01-4-3z"/></svg></div>
        <h3>Custom sky</h3>
        <p>Replace the overworld sky with aurora, galaxy or deep-ocean presets and shooting stars.</p>
      </div>
      <div class="feat wide">
        <div class="ico"><svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="14" rx="2"/><path d="M3 9h18M8 18v2m8-2v2"/></svg></div>
        <h3>A title screen that's yours</h3>
        <p>An animated cherry-grove menu with falling petals, time-of-day and your name front and center — toggle it on or off from a single panel.</p>
      </div>
      <div class="feat">
        <div class="ico"><svg viewBox="0 0 24 24"><path d="M12 3l2 5 5 .5-3.8 3.4 1.2 5.1L12 14l-4.6 3 1.2-5.1L4.8 8.5 10 8z"/></svg></div>
        <h3>Capes, cosmetics &amp; ranks</h3>
        <p>Upload a cape, wear cosmetics and show a rank badge — from Member up to Creator and beyond.</p>
      </div>
    </div>
  </div>
</section>

<section class="band" id="explore">
  <style>
  /* ---- Faithful in-game client replica ---- */
  .lc-win{--acc:var(--acc);--acc2:var(--acc2);max-width:1060px;margin:34px auto 0;background:linear-gradient(180deg,#0c1626,#0a1220);border:1px solid #1b2b45;border-radius:18px;padding:12px;box-shadow:0 50px 120px -40px #000;display:grid;grid-template-columns:238px 1fr;gap:12px;height:632px}
  .lc-win *{box-sizing:border-box}
  /* sidebar */
  .lc-side{background:linear-gradient(180deg,#0e1a2d,#0b1526);border:1px solid #182942;border-radius:14px;display:flex;flex-direction:column;padding:16px 14px;min-height:0}
  .lc-logo{display:flex;align-items:center;gap:11px;padding:2px 4px 14px;border-bottom:1px solid #16263e;margin-bottom:12px}
  .lc-logo .mk{width:40px;height:40px;border-radius:11px;background:linear-gradient(145deg,#66c8f4,#156e97);display:flex;align-items:center;justify-content:center;flex:none;box-shadow:0 6px 18px -6px rgba(60,160,220,.7)}
  .lc-logo .mk svg{width:22px;height:22px}
  .lc-logo b{font-family:var(--display);font-weight:700;font-size:19px;letter-spacing:.14em;color:#eaf1fb}
  .lc-modlabel{font-family:var(--display);font-size:10.5px;letter-spacing:.18em;color:#566a86;padding:2px 8px 8px}
  .lc-mods{display:flex;flex-direction:column;gap:1px;overflow:hidden;flex:1;min-height:0;margin:0 -4px;padding:0 4px}
    .lc-mod{display:flex;align-items:center;gap:11px;background:transparent;border:0;border-radius:9px;padding:8px 10px;cursor:pointer;color:#8a9bb5;font-family:var(--body);font-weight:600;font-size:14px;text-align:left;position:relative;transition:.13s}
  .lc-mod svg{width:18px;height:18px;flex:none;stroke:currentColor}
  .lc-mod:hover{color:#cdd8ea;background:rgba(80,140,210,.07)}
  .lc-mod.is-on{color:var(--acc);background:linear-gradient(90deg,rgba(60,140,230,.16),rgba(60,140,230,.03))}
  .lc-mod.is-on::before{content:"";position:absolute;left:-4px;top:7px;bottom:7px;width:3px;border-radius:3px;background:var(--acc)}
  .lc-mod.locked{filter:blur(4.5px);opacity:.5;pointer-events:none;user-select:none}
  .lc-user{display:flex;align-items:center;gap:11px;background:#0b1626;border:1px solid #1a2c47;border-radius:11px;padding:9px 11px;margin-top:12px}
  .lc-user .pfp{width:34px;height:34px;border-radius:7px;background:linear-gradient(135deg,#caa472,#3a7a3a);flex:none;image-rendering:pixelated;background-size:cover}
  .lc-user b{display:block;font-family:var(--display);font-size:14px;color:#eaf1fb;line-height:1.2}
  .lc-user span{font-size:11.5px;color:#6f82a0}
  .lc-dot{width:9px;height:9px;border-radius:50%;background:#2fd36a;box-shadow:0 0 8px #2fd36a;margin-left:auto;flex:none}
  /* right column */
  .lc-right{display:flex;flex-direction:column;gap:12px;min-height:0}
  .lc-hello{background:linear-gradient(90deg,#0e1a2d,#0b1526);border:1px solid #182942;border-radius:13px;padding:17px 22px;font-family:var(--display);font-size:18px;color:#d7e2f2;flex:none}
  .lc-hello b{color:#fff;font-weight:700}
  .lc-content{background:linear-gradient(180deg,#0d1828,#0a1320);border:1px solid #182942;border-radius:14px;padding:20px;overflow-y:auto;flex:1;min-height:0}
  .lc-content::-webkit-scrollbar{width:8px}.lc-content::-webkit-scrollbar-thumb{background:#1d3250;border-radius:9px}
  .lc-pane{display:none}.lc-pane.is-on{display:block}
  .lc-phead{display:flex;align-items:center;gap:11px;margin-bottom:4px}
  .lc-phead .ic{color:var(--acc);display:flex}.lc-phead .ic svg{width:19px;height:19px;stroke:var(--acc)}
  .lc-phead h3{margin:0;font-family:var(--display);font-weight:700;font-size:17px;color:#eef3fb}
  .lc-badge{background:#15304e;color:#7fb6e8;font-family:var(--mono);font-size:12px;font-weight:700;padding:2px 10px;border-radius:999px}
  .lc-btn-r{margin-left:auto}
  .lc-sub{color:#6f82a0;font-size:12.5px;margin:0 0 16px}
  .lc-dark-btn{background:#0b1626;border:1px solid #223a5c;color:#aebdd4;border-radius:9px;padding:8px 14px;font-family:var(--body);font-weight:600;font-size:13px;cursor:pointer;transition:.13s}
  .lc-dark-btn:hover{border-color:#34567f;color:#dde7f5}
  /* THEME */
  .lc-themes{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-bottom:18px}
  .lc-theme{background:#0b1626;border:1px solid #1b2c47;border-radius:12px;padding:13px 14px;cursor:pointer;transition:.13s;position:relative}
  .lc-theme:hover{border-color:#2d486e}
  .lc-theme.is-on{border-color:var(--acc);box-shadow:0 0 0 1px var(--acc),0 0 22px -6px rgba(60,150,255,.6)}
  .lc-th-top{display:flex;align-items:center;justify-content:space-between;margin-bottom:11px}
  .lc-th-top span{font-family:var(--display);font-size:13.5px;color:#dbe6f5}
  .lc-radio{width:15px;height:15px;border-radius:50%;border:2px solid #2c466c;flex:none}
  .lc-theme.is-on .lc-radio{border-color:var(--acc);background:radial-gradient(circle,var(--acc) 42%,transparent 46%)}
  .lc-th-bars{display:flex;gap:0;height:34px;border-radius:7px;overflow:hidden}
  .lc-th-bars b{flex:1}
  .lc-fx-head{display:flex;align-items:center;gap:9px;margin-bottom:14px}
  .lc-fx-head .sp{color:var(--acc)}
  .lc-fx-head h4{margin:0;font-family:var(--display);font-size:12px;letter-spacing:.16em;color:#9fb0c8}
  .lc-row{display:flex;align-items:center;gap:16px;background:#0b1626;border:1px solid #182a44;border-radius:12px;padding:15px 17px;margin-bottom:11px}
  .lc-row .tx{flex:1}
  .lc-row h5{margin:0 0 3px;font-family:var(--display);font-weight:700;font-size:14.5px;color:#e9f0fa}
  .lc-row p{margin:0;color:#6f82a0;font-size:12.5px;line-height:1.4}
  .lc-tog{flex:none;width:44px;height:24px;border-radius:999px;background:#223150;position:relative;cursor:pointer;transition:.16s}
  .lc-tog::after{content:"";position:absolute;top:3px;left:3px;width:18px;height:18px;border-radius:50%;background:#9fb0cc;transition:.16s}
  .lc-tog.is-on{background:linear-gradient(90deg,var(--acc),var(--acc2))}
  .lc-tog.is-on::after{left:23px;background:#fff}
  .lc-reset-i{color:#4a5d78;cursor:pointer;flex:none;font-size:15px;line-height:1}
  .lc-reset-i:hover{color:#8fa4c2}
  /* TRANSACTIONS */
  .lc-stats{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-bottom:12px}
  .lc-stat{background:#0b1626;border:1px solid #182a44;border-radius:12px;padding:14px 16px}
  .lc-stat.hl{border-color:#1f6a7a;box-shadow:inset 0 0 0 1px rgba(40,170,190,.3)}
  .lc-stat .lab{display:flex;align-items:center;gap:8px;font-size:10.5px;letter-spacing:.12em;color:#64788f;font-family:var(--display);margin-bottom:8px}
  .lc-stat .lab .r{margin-left:auto}
  .lc-stat .val{font-family:var(--display);font-weight:800;font-size:24px}
  .lc-stat .val.pos{color:#33d17a}.lc-stat .val.neg{color:#ff6b6b}
  .lc-mini-btn{background:#0d1e33;border:1px solid #28456b;color:#bcd0ea;border-radius:7px;padding:4px 11px;font-size:11.5px;font-weight:600;cursor:pointer;font-family:var(--body)}
  .lc-profit{display:flex;align-items:center;gap:14px;background:#0b1626;border:1px solid #182a44;border-radius:12px;padding:15px 17px;margin-bottom:14px}
  .lc-profit .tx{flex:1}
  .lc-skin-lab{font-size:12.5px;color:#8a9bb5;font-weight:600}
  .lc-blue-btn{background:linear-gradient(90deg,var(--acc),var(--acc2));border:0;color:#fff;border-radius:8px;padding:8px 15px;font-weight:700;font-size:12.5px;cursor:pointer;font-family:var(--body)}
  .lc-filters{display:flex;align-items:center;gap:10px;flex-wrap:wrap;margin-bottom:14px}
  .lc-filt-lab{font-size:12.5px;color:#8a9bb5;font-weight:600}
  .lc-input{background:#0a1424;border:1px solid #213654;border-radius:9px;padding:9px 13px;color:#dbe6f5;font-family:var(--body);font-size:13px;outline:none}
  .lc-input::placeholder{color:#556883}
  .lc-input:focus{border-color:var(--acc)}
  .lc-chip{background:#0a1424;border:1px solid #213654;color:#8a9bb5;border-radius:999px;padding:7px 14px;font-size:12.5px;font-weight:600;cursor:pointer;font-family:var(--body);transition:.12s}
  .lc-chip:hover{color:#cdd8ea}
  .lc-chip.is-on{color:#fff;background:linear-gradient(90deg,var(--acc),var(--acc2));border-color:transparent}
  .lc-seg{display:inline-flex;background:#0a1424;border:1px solid #213654;border-radius:999px;overflow:hidden}
  .lc-seg button{background:transparent;border:0;color:#8a9bb5;padding:7px 15px;font-size:12.5px;font-weight:600;cursor:pointer;font-family:var(--body)}
  .lc-seg button.is-on{background:linear-gradient(90deg,var(--acc),var(--acc2));color:#fff}
  .lc-empty{background:#0a1424;border:1px solid #182a44;border-radius:12px;padding:46px 20px;text-align:center}
  .lc-empty b{display:block;font-family:var(--display);font-size:16px;color:#cdd8ea;margin-bottom:6px}
  .lc-empty span{color:#64788f;font-size:13px}
  /* RECENT AUCTIONS */
  .lc-auc{display:flex;align-items:center;gap:16px;background:#0b1626;border:1px solid #182a44;border-radius:12px;padding:12px 15px;margin-bottom:10px}
  .lc-auc .thumb{width:46px;height:46px;border-radius:9px;background:#0a1424;border:1px solid #1d3150;display:flex;align-items:center;justify-content:center;flex:none;font-size:22px}
  .lc-auc .nm{min-width:150px}
  .lc-auc .nm b{display:block;font-family:var(--display);font-size:15px;color:#eaf1fb}
  .lc-auc .nm span{font-size:12px;color:#6f82a0}
  .lc-col{min-width:92px}
  .lc-col .k{font-size:9.5px;letter-spacing:.1em;color:#5b6e88;font-family:var(--display)}
  .lc-col .v{font-family:var(--display);font-weight:700;font-size:15px;color:#e4edf8}
  .lc-redo{background:linear-gradient(90deg,var(--acc),var(--acc2));border:0;color:#fff;border-radius:8px;padding:8px 18px;font-weight:700;font-size:13px;cursor:pointer;font-family:var(--body)}
  .lc-sold{background:transparent;border:1px solid #2a6b3f;color:#4fd17a;border-radius:8px;padding:8px 18px;font-weight:700;font-size:13px;cursor:pointer;font-family:var(--body)}
  .lc-x{background:#0d1e33;border:1px solid #28456b;color:#8a9bb5;border-radius:8px;width:30px;height:30px;cursor:pointer;flex:none}
  .lc-auc .acts{margin-left:auto;display:flex;align-items:center;gap:9px}
  /* AUCTION */
  .lc-auction{display:grid;grid-template-columns:1fr 320px;gap:16px;height:100%}
  .lc-auc-left{display:flex;flex-direction:column;min-height:0}
  .lc-items-head{display:flex;align-items:center;gap:10px;margin-bottom:12px}
  .lc-items-head .d{width:8px;height:8px;border-radius:50%;background:var(--acc);box-shadow:0 0 8px var(--acc)}
  .lc-items-head b{font-family:var(--display);font-weight:700;font-size:16px;color:#eef3fb}
  .lc-items-head .ct{margin-left:auto;font-size:12.5px;color:#6f82a0}
  .lc-search{width:100%;margin-bottom:12px}
  .lc-grid{display:grid;grid-template-columns:repeat(9,1fr);gap:7px;overflow-y:auto;align-content:start;flex:1;min-height:0;padding-right:4px}
  .lc-grid::-webkit-scrollbar{width:8px}.lc-grid::-webkit-scrollbar-thumb{background:#1d3250;border-radius:9px}
  .lc-item{aspect-ratio:1;border-radius:7px;background:#0a1424;border:1px solid #16273f;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:.1s}
  .lc-item:hover{border-color:var(--acc);transform:translateY(-1px)}
  .lc-item.is-on{border-color:var(--acc);box-shadow:0 0 0 1px var(--acc)}
  .lc-item i{width:60%;height:60%;border-radius:4px;display:block}
  .lc-auc-right{background:#0b1626;border:1px solid #182a44;border-radius:12px;padding:16px;display:flex;flex-direction:column;gap:13px;overflow-y:auto}
  .lc-pick{display:flex;align-items:center;gap:12px}
  .lc-pick .box{width:52px;height:52px;border-radius:9px;background:#0a1424;border:1px solid #1d3150;flex:none;display:flex;align-items:center;justify-content:center}
  .lc-pick .box i{width:60%;height:60%;border-radius:4px}
  .lc-pick .t b{display:block;font-family:var(--display);font-size:14px;color:#dbe6f5}
  .lc-pick .t span{font-size:11.5px;color:#6f82a0}
  .lc-2nd{margin-left:auto;background:#0d1e33;border:1px solid #28456b;color:#bcd0ea;border-radius:8px;padding:7px 12px;font-size:12px;font-weight:600;cursor:pointer;font-family:var(--body);flex:none}
  .lc-fld label{display:block;font-family:var(--display);font-size:12px;color:#9fb0c8;margin:0 0 6px}
  .lc-fld input{width:100%}
  .lc-two{display:grid;grid-template-columns:1fr 1fr;gap:11px}
  .lc-start{background:#16243b;border:1px solid #223a5c;color:#64788f;border-radius:9px;padding:12px;font-weight:700;font-size:14px;cursor:not-allowed;font-family:var(--body)}
  .lc-cancel{background:#0a1424;border:1px solid #223a5c;color:#aebdd4;border-radius:9px;padding:12px;font-weight:700;font-size:14px;cursor:pointer;font-family:var(--body)}
  /* generic simple rows reused for small tabs */
  @media(max-width:860px){.lc-win{grid-template-columns:1fr;height:auto}.lc-side{flex-direction:column}.lc-mods{flex-direction:row;flex-wrap:wrap;overflow:visible}.lc-themes{grid-template-columns:repeat(2,1fr)}.lc-stats{grid-template-columns:1fr}.lc-auction{grid-template-columns:1fr}.lc-grid{grid-template-columns:repeat(6,1fr)}.lc-content{max-height:none}}
  </style>
  <div class="wrap">
    <div class="sec-head">
      <span class="eyebrow">See it in action</span>
      <h2>Click through the client.</h2>
      <p>This is the real in-game menu — pick a module, switch themes, flip toggles, build an auction. It works right here in your browser.</p>
    </div>

    <div class="lc-win" id="lcwin">
      <!-- ============ SIDEBAR ============ -->
      <aside class="lc-side">
        <div class="lc-logo">
          <span class="mk"><svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m14 13-7.5 7.5c-.83.83-2.17.83-3 0 0 0 0 0 0 0a2.12 2.12 0 0 1 0-3L11 10"/><path d="m16 16 6-6"/><path d="m8 8 6-6"/><path d="m9 7 8 8"/><path d="m21 11-8-8"/></svg></span>
          <b>LETHAL</b>
        </div>
        <div class="lc-modlabel">MODULES</div>
        <div class="lc-mods" id="lcmods">
          <button class="lc-mod" data-tab="auction"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="m14 13-7.5 7.5c-.83.83-2.17.83-3 0a2.12 2.12 0 0 1 0-3L11 10"/><path d="m16 16 6-6"/><path d="m8 8 6-6"/><path d="m9 7 8 8"/><path d="m21 11-8-8"/></svg>Auction</button>
          <button class="lc-mod" data-tab="recent"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>Recent Auctions</button>
          <button class="lc-mod" data-tab="transactions"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 8h14l-3-3"/><path d="M21 16H7l3 3"/></svg>Transactions</button>
          <button class="lc-mod is-on" data-tab="theme"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="13.5" cy="6.5" r="1.5"/><circle cx="17.5" cy="10.5" r="1.5"/><circle cx="8.5" cy="7.5" r="1.5"/><circle cx="6.5" cy="12.5" r="1.5"/><path d="M12 2a10 10 0 1 0 0 20c.6 0 1-.4 1-1 0-.5-.2-.8-.5-1.1-.3-.3-.5-.6-.5-1.1 0-.6.4-1 1-1h1.8a4 4 0 0 0 4-4C21.3 6 17 2 12 2z"/></svg>Theme</button>
          <button class="lc-mod locked" data-tab="skins"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-6 8-6s8 2 8 6"/></svg>Skins</button>
          <button class="lc-mod locked" data-tab="cape"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 3h12l-1 5a5 5 0 0 1-10 0z"/><path d="M7 9v11l5-2 5 2V9"/></svg>Cape</button>
          <button class="lc-mod locked" data-tab="cosmetics"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 7l4-3 4 2 4-2 4 3-3 3v8H7v-8z"/></svg>Cosmetics</button>
          <button class="lc-mod locked" data-tab="music"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/><path d="M9 18V5l12-2v13"/></svg>Music</button>
          <button class="lc-mod locked" data-tab="utilities"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>Utilities</button>
          <button class="lc-mod locked" data-tab="scoreboard"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 6h3M4 12h3M4 18h3"/><path d="M10 6h10M10 12h10M10 18h7"/></svg>Scoreboard</button>
          <button class="lc-mod locked" data-tab="sky"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M17.5 19a4.5 4.5 0 1 0 0-9 6 6 0 0 0-11.6-1.6A4 4 0 0 0 6 19z"/></svg>Sky</button>
        </div>
        <div class="lc-user">
          <span class="pfp"></span>
          <div><b>LethalLukeYT</b><span>Owner</span></div>
          <span class="lc-dot"></span>
        </div>
      </aside>

      <!-- ============ RIGHT ============ -->
      <div class="lc-right">
        <div class="lc-hello">Hello, <b>LethalLukeYT</b></div>
        <div class="lc-content" id="lccontent">

          <!-- ===== THEME ===== -->
          <div class="lc-pane is-on" data-pane="theme">
            <div class="lc-themes" id="lcthemes">
              <div class="lc-theme" data-th data-acc="#39e07d" data-acc2="#16a85a"><div class="lc-th-top"><span>Limelight</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#aadf00"></b><b style="background:#cdf25a"></b><b style="background:#2fe07d"></b><b style="background:#141f09"></b></div></div>
              <div class="lc-theme" data-th data-acc="#ff5a2c" data-acc2="#d1431f"><div class="lc-th-top"><span>Sunset</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#ff1f66"></b><b style="background:#ff6a2c"></b><b style="background:#9b43f0"></b><b style="background:#1a0a1e"></b></div></div>
              <div class="lc-theme is-on" data-th data-acc="#3f9bff" data-acc2="#1f6fd0"><div class="lc-th-top"><span>Electric</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#2f7bff"></b><b style="background:#28c4ff"></b><b style="background:#5f8dff"></b><b style="background:#0a1430"></b></div></div>
              <div class="lc-theme" data-th data-acc="#9d5bff" data-acc2="#5c34c4"><div class="lc-th-top"><span>Nebula</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#9d5bff"></b><b style="background:#c396ff"></b><b style="background:#5c34c4"></b><b style="background:#14082a"></b></div></div>
              <div class="lc-theme" data-th data-acc="#ffb300" data-acc2="#d18600"><div class="lc-th-top"><span>Goldrush</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#ffb300"></b><b style="background:#ffe08a"></b><b style="background:#ff9000"></b><b style="background:#1e1405"></b></div></div>
              <div class="lc-theme" data-th data-acc="#16d98a" data-acc2="#0fa268"><div class="lc-th-top"><span>Emerald</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#16d98a"></b><b style="background:#58efad"></b><b style="background:#0fa268"></b><b style="background:#07140d"></b></div></div>
              <div class="lc-theme" data-th data-acc="#ff28b4" data-acc2="#b01c7e"><div class="lc-th-top"><span>Plasma</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#ff28b4"></b><b style="background:#7b52ff"></b><b style="background:#1fe0ff"></b><b style="background:#14032a"></b></div></div>
              <div class="lc-theme" data-th data-acc="#ff4a4a" data-acc2="#c21616"><div class="lc-th-top"><span>Crimson</span><i class="lc-radio"></i></div><div class="lc-th-bars"><b style="background:#ff4a4a"></b><b style="background:#ff7d6a"></b><b style="background:#d41616"></b><b style="background:#1a0505"></b></div></div>
            </div>
            <div class="lc-fx-head"><span class="sp">&#10022;</span><h4>EFFECTS</h4><button class="lc-dark-btn lc-btn-r">Reset</button></div>
            <div class="lc-row"><div class="tx"><h5>See-Through GUI</h5><p>Allow background blur and transparency.</p></div><span class="lc-tog is-on" data-tog></span><span class="lc-reset-i">&#8635;</span></div>
            <div class="lc-row"><div class="tx"><h5>Frosted Blur</h5><p>Apply frosted glass blur to surfaces.</p></div><span class="lc-tog is-on" data-tog></span><span class="lc-reset-i">&#8635;</span></div>
            <div class="lc-row"><div class="tx"><h5>Ambient Background</h5><p>Enable the ambient gradient background.</p></div><span class="lc-tog is-on" data-tog></span><span class="lc-reset-i">&#8635;</span></div>
          </div>

          <!-- ===== TRANSACTIONS ===== -->
          <div class="lc-pane" data-pane="transactions">
            <div class="lc-phead"><span class="ic"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 8h14l-3-3"/><path d="M21 16H7l3 3"/></svg></span><h3>Transactions</h3><span class="lc-badge">0</span><button class="lc-dark-btn lc-btn-r">Clear History</button></div>
            <p class="lc-sub">Money you got and sent in chat. Set a minimum to only see the big ones.</p>
            <div class="lc-stats">
              <div class="lc-stat"><div class="lab">RECEIVED</div><div class="val pos">+$0</div></div>
              <div class="lc-stat"><div class="lab">SENT</div><div class="val neg">-$0</div></div>
              <div class="lc-stat hl"><div class="lab">SESSION · 6 h ago<button class="lc-mini-btn r">Reset</button></div><div class="val pos">+$0</div></div>
            </div>
            <div class="lc-profit"><div class="tx"><h5>Profit display</h5><p>Shows this session's profit on screen with your skin. Red when you're down.</p></div><span class="lc-skin-lab">Skin</span><span class="lc-tog is-on" data-tog></span><button class="lc-blue-btn">Edit position</button><span class="lc-tog" data-tog></span></div>
            <div class="lc-filters">
              <span class="lc-filt-lab">Minimum</span>
              <input class="lc-input" style="width:150px" placeholder="Any, e.g. 5m">
              <button class="lc-chip is-on">Any</button><button class="lc-chip">100k+</button><button class="lc-chip">1m+</button><button class="lc-chip">5m+</button><button class="lc-chip">10m+</button>
              <span class="lc-seg"><button class="is-on">All</button><button>Received</button><button>Sent</button></span>
              <input class="lc-input" style="width:120px" placeholder="Player">
            </div>
            <div class="lc-empty"><b>No transactions yet</b><span>When someone pays you, or you pay someone, it shows up here.</span></div>
          </div>

          <!-- ===== RECENT AUCTIONS ===== -->
          <div class="lc-pane" data-pane="recent">
            <div class="lc-phead"><span class="ic"><svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></span><h3>Recent Auctions</h3><span class="lc-badge">2</span><button class="lc-dark-btn lc-btn-r">Clear History</button></div>
            <p class="lc-sub">Your auction history. Press Redo to run one again.</p>
            <div id="lcrecent"></div>
          </div>

          <!-- ===== AUCTION ===== -->
          <div class="lc-pane" data-pane="auction">
            <div class="lc-auction">
              <div class="lc-auc-left">
                <div class="lc-items-head"><span class="d"></span><b>New Auction</b><span class="ct" id="lcitemct">1536 items</span></div>
                <input class="lc-input lc-search" id="lcsearch" placeholder="Search items...">
                <div class="lc-grid" id="lcgrid"></div>
              </div>
              <div class="lc-auc-right">
                <div class="lc-pick"><div class="box" id="lcpickbox"></div><div class="t" id="lcpicktxt"><b>No item yet</b><span>Pick one on the left</span></div><button class="lc-2nd">+ 2nd item</button></div>
                <div class="lc-fld"><label>Minimum bid</label><input class="lc-input" placeholder="e.g. 10k"></div>
                <div class="lc-two"><div class="lc-fld"><label>Quantity</label><input class="lc-input" value="1"></div><div class="lc-fld"><label>Timer (sec)</label><input class="lc-input" value="60"></div></div>
                <div class="lc-fld"><label>Worth of one item</label><input class="lc-input" placeholder="Optional"></div>
                <div class="lc-fld"><label>Note on the auction card</label><input class="lc-input" value="15 Seconds To Make Order"></div>
                <button class="lc-start">Start Auction</button>
                <button class="lc-cancel">Cancel</button>
              </div>
            </div>
          </div>

          <!-- ===== small tabs ===== -->
          <div class="lc-pane" data-pane="skins">
            <div class="lc-phead"><h3>Skins</h3></div><p class="lc-sub">Swap your in-game skin from a saved library — only you see the change until you apply it.</p>
            <div class="lc-row"><div class="tx"><h5>Skin library</h5><p>Save and switch between your favourite skins instantly.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Slim arms</h5><p>Use the 3px (Alex) model.</p></div><span class="lc-tog" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="cape">
            <div class="lc-phead"><h3>Cape</h3></div><p class="lc-sub">Upload your own cape, visible to every Lethal player.</p>
            <div class="lc-row"><div class="tx"><h5>Custom cape</h5><p>Shown on your back to everyone using the client.</p></div><button class="lc-blue-btn">Upload</button></div>
            <div class="lc-row"><div class="tx"><h5>Physics</h5><p>The cape waves as you move.</p></div><span class="lc-tog is-on" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="cosmetics">
            <div class="lc-phead"><h3>Cosmetics</h3></div><p class="lc-sub">Little extras others can see — headphones, a top hat, and your accent colour.</p>
            <div class="lc-row"><div class="tx"><h5>Headphones</h5><p>Appear on your head while music plays — others see them too.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Top hat</h5><p>A little class never hurt.</p></div><span class="lc-tog" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="music">
            <div class="lc-phead"><h3>Music</h3></div><p class="lc-sub">The Now Playing card and floating sky lyrics, synced to Spotify or Apple Music.</p>
            <div class="lc-row"><div class="tx"><h5>Now Playing card</h5><p>Album art, song and artist in the corner.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Sky lyrics</h5><p>The words float around you, scattered, synced to the beat.</p></div><span class="lc-tog is-on" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="crosshair">
            <div class="lc-phead"><h3>Crosshair</h3></div><p class="lc-sub">A custom crosshair that replaces the vanilla one.</p>
            <div class="lc-row"><div class="tx"><h5>Custom crosshair</h5><p>Shape, size and colour, your way.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Dot centre</h5><p>Add a centre dot.</p></div><span class="lc-tog" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="utilities">
            <div class="lc-phead"><h3>Utilities</h3></div><p class="lc-sub">Quality-of-life tools for everyday play.</p>
            <div class="lc-row"><div class="tx"><h5>Freelook</h5><p>Look around without turning your player.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Zoom</h5><p>Spyglass-grade zoom with scroll-to-adjust.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Fullbright</h5><p>See in the dark without night vision.</p></div><span class="lc-tog" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>FPS &amp; ping</h5><p>A clean, movable readout with colour thresholds.</p></div><span class="lc-tog is-on" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="scoreboard">
            <div class="lc-phead"><h3>Scoreboard</h3></div><p class="lc-sub">A cleaner sidebar scoreboard with your own styling.</p>
            <div class="lc-row"><div class="tx"><h5>Custom scoreboard</h5><p>Replace the server scoreboard look.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-row"><div class="tx"><h5>Hide numbers</h5><p>Drop the red side numbers.</p></div><span class="lc-tog is-on" data-tog></span></div>
          </div>
          <div class="lc-pane" data-pane="sky">
            <div class="lc-phead"><h3>Sky</h3></div><p class="lc-sub">Replace the overworld sky with a preset — only on your screen.</p>
            <div class="lc-row"><div class="tx"><h5>Custom sky</h5><p>Pick a preset below.</p></div><span class="lc-tog is-on" data-tog></span></div>
            <div class="lc-card lc-row" style="display:block"><h5 style="margin-bottom:10px">Preset</h5><div class="lc-filters" style="margin:0">
              <button class="lc-chip is-on">Northern Lights</button><button class="lc-chip">Galaxy</button><button class="lc-chip">Blood Moon</button><button class="lc-chip">Nebula</button><button class="lc-chip">Deep Ocean</button><button class="lc-chip">Vaporwave</button><button class="lc-chip">Sunset Dream</button><button class="lc-chip">Emerald Veil</button>
            </div></div>
          </div>
          <div class="lc-pane" data-pane="settings">
            <div class="lc-phead"><h3>Settings</h3></div><p class="lc-sub">Client-wide options.</p>
            <div class="lc-row"><div class="tx"><h5>Open menu key</h5><p>Right Shift</p></div><span class="lc-reset-i">&#8635;</span></div>
            <div class="lc-row"><div class="tx"><h5>Click sounds</h5><p>Play a click when you open tabs and flip toggles.</p></div><span class="lc-tog is-on" data-tog></span></div>
          </div>

        </div>
      </div>
    </div>
  </div>

  <script>
  (function(){
    var root=document.getElementById("lcwin"); if(!root) return;
    var content=document.getElementById("lccontent");
    // --- click sound (soft blip via WebAudio; no asset needed) ---
    var AC=window.AudioContext||window.webkitAudioContext, actx=null;
    function blip(){try{if(!actx)actx=new AC();var o=actx.createOscillator(),g=actx.createGain();o.type="sine";o.frequency.value=620;g.gain.value=.05;o.connect(g);g.connect(actx.destination);var t=actx.currentTime;g.gain.setValueAtTime(.05,t);g.gain.exponentialRampToValueAtTime(.0001,t+.08);o.start(t);o.stop(t+.09);}catch(e){}}
    // --- tab switching ---
    root.querySelectorAll(".lc-mod").forEach(function(m){
      m.addEventListener("click",function(){
        blip();
        root.querySelectorAll(".lc-mod").forEach(function(x){x.classList.remove("is-on");});
        content.querySelectorAll(".lc-pane").forEach(function(x){x.classList.remove("is-on");});
        m.classList.add("is-on");
        var p=content.querySelector('.lc-pane[data-pane="'+m.getAttribute("data-tab")+'"]');
        if(p){p.classList.add("is-on");content.scrollTop=0;}
      });
    });
    // --- toggles / chips / segments / themes ---
    root.addEventListener("click",function(e){
      var tog=e.target.closest("[data-tog]"); if(tog){blip();tog.classList.toggle("is-on");return;}
      var th=e.target.closest(".lc-theme"); if(th){blip();th.parentNode.querySelectorAll(".lc-theme").forEach(function(x){x.classList.remove("is-on");});th.classList.add("is-on");var a1=th.getAttribute("data-acc"),a2=th.getAttribute("data-acc2");if(a1){root.style.setProperty("--acc",a1);root.style.setProperty("--acc2",a2);}return;}
      var chip=e.target.closest(".lc-chip"); if(chip){blip();chip.parentNode.querySelectorAll(".lc-chip").forEach(function(x){x.classList.remove("is-on");});chip.classList.add("is-on");return;}
      var seg=e.target.closest(".lc-seg button"); if(seg){blip();seg.parentNode.querySelectorAll("button").forEach(function(x){x.classList.remove("is-on");});seg.classList.add("is-on");return;}
    });
    // --- recent auctions list ---
    var rec=[
      ["Elytra","&#129442;","x1 · won by drdonutt","$150m","$520m","$544m"],
      ["Elytra","&#129442;","x1 · won by jettism","$400m","$844m","$544m"]
    ];
    var rc=document.getElementById("lcrecent");
    if(rc){rc.innerHTML=rec.map(function(r){
      return '<div class="lc-auc"><div class="thumb">'+r[1]+'</div><div class="nm"><b>'+r[0]+'</b><span>'+r[2]+'</span></div>'
        +'<div class="lc-col"><div class="k">MIN BID</div><div class="v">'+r[3]+'</div></div>'
        +'<div class="lc-col"><div class="k">TOP BID</div><div class="v">'+r[4]+'</div></div>'
        +'<div class="lc-col"><div class="k">WORTH EACH</div><div class="v">'+r[5]+'</div></div>'
        +'<div class="acts"><button class="lc-redo">Redo</button><button class="lc-sold">Sold</button><button class="lc-x">&times;</button></div></div>';
    }).join("");}
    // --- auction item grid ---
    var names=["Stone","Granite","Polished Granite","Diorite","Andesite","Cobblestone","Deepslate","Cobbled Deepslate","Tuff","Calcite","Dripstone","Grass Block","Dirt","Coarse Dirt","Podzol","Sand","Red Sand","Gravel","Gold Ore","Iron Ore","Coal Ore","Copper Ore","Diamond Ore","Emerald Ore","Lapis Ore","Redstone Ore","Oak Log","Birch Log","Spruce Log","Dark Oak Log","Acacia Log","Oak Planks","Glass","Sponge","Wool","White Wool","Red Wool","Blue Wool","Green Wool","TNT","Bookshelf","Obsidian","Diamond Block","Gold Block","Iron Block","Emerald Block","Netherite Block","Beacon","Elytra","Dragon Head","Totem","Netherite Sword","Enchanted Apple","Shulker Box","Ender Chest","Crafting Table","Furnace","Anvil","Brewing Stand","Cauldron","Hopper","Dispenser","Observer","Piston","Redstone Lamp","Sea Lantern","Glowstone","Honeycomb","Amethyst","Prismarine","Quartz Block","Nether Star"];
    var cols=["#8a8f99","#a06b57","#b07b63","#d9d4cc","#9aa0a6","#6e737b","#3a3f47","#44484f","#6b6f66","#e8e4da","#c7b9a8","#5fa74a","#7a5a3c","#6b5436","#4a3a28","#e6d9a8","#c76a42","#7c7c7c","#e6c558","#d9b48a","#2b2b2b","#c47b4a","#6fe0e6","#2fd17a","#2f5fd1","#d14545","#8a6a3c","#e8dcc0","#c9a87a","#4a3620","#9a6a3c","#c9a060","#bfe4ff","#e6d84a","#e0e0e0","#f0f0f0","#d14545","#3f7bd1","#3fb060","#e04a2f","#9a6a3c","#2a1f3a","#6fe0e6","#e6c558","#d9d4cc","#2fd17a","#2a2a30","#8ae6d0","#c9c2e6","#2a2a30","#e8d84a","#8a8f99","#e04a9a","#2a2a30","#9a6a3c","#8a8f99","#6e737b","#3a3f47","#c9a8e6","#6b6f76","#8a8f99","#6e737b","#44484f","#8a8f99","#d14545","#8ae6d0","#e6d84a","#e6c558","#c9a05a","#2fd1b0","#9ad0e6","#f0ece0","#f5e08a"];
    var grid=document.getElementById("lcgrid"),pickbox=document.getElementById("lcpickbox"),picktxt=document.getElementById("lcpicktxt");
    function emoji(n){if(n=="Elytra")return"&#129442;";if(n=="Dragon Head")return"";if(n=="Totem")return"";return"";}
    function build(filter){
      if(!grid)return;grid.innerHTML="";
      names.forEach(function(n,i){
        if(filter && n.toLowerCase().indexOf(filter.toLowerCase())<0)return;
        var d=document.createElement("div");d.className="lc-item";d.title=n;
        d.innerHTML='<i style="background:'+cols[i%cols.length]+'"></i>';
        d.addEventListener("click",function(){
          blip();
          grid.querySelectorAll(".lc-item").forEach(function(x){x.classList.remove("is-on");});
          d.classList.add("is-on");
          if(pickbox)pickbox.innerHTML='<i style="background:'+cols[i%cols.length]+'"></i>';
          if(picktxt)picktxt.innerHTML='<b>'+n+'</b><span>Ready to auction</span>';
        });
        grid.appendChild(d);
      });
    }
    build("");
    var sb=document.getElementById("lcsearch");
    if(sb)sb.addEventListener("input",function(){build(sb.value.trim());});
  })();
  </script>
</section>

<section class="band" id="pricing">
  <div class="wrap">
    <div class="sec-head" style="margin-left:auto;margin-right:auto;text-align:center;">
      <span class="eyebrow">Now 100% free</span>
      <h2>Download Lethal Client — free.</h2>
      <p style="margin-left:auto;margin-right:auto;">The whole client, every feature, every future update. No account, no payment — just download and play.</p>
    </div>
    <div style="text-align:center;margin-top:30px;">
      <div id="dlPick"><a class="btn btn-primary btn-lg" href="/download" style="font-size:18px;padding:16px 44px;">Download — it's free</a></div>
      <p class="guarantee" style="margin-top:18px;">No account · Windows · drop the .jar in your mods folder and play.</p>
    </div>
    <div style="display:flex;gap:18px;justify-content:center;flex-wrap:wrap;margin-top:28px;">
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:22px;padding:26px 40px;min-width:200px;text-align:center;">
        <div id="dl26" style="font-family:var(--display);font-weight:700;font-size:44px;letter-spacing:-.02em;line-height:1;">—</div>
        <div style="color:var(--muted);font-size:14px;margin-top:10px;">26.2 Downloads</div>
      </div>
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:22px;padding:26px 40px;min-width:200px;text-align:center;">
        <div id="dl1211" style="font-family:var(--display);font-weight:700;font-size:44px;letter-spacing:-.02em;line-height:1;">—</div>
        <div style="color:var(--muted);font-size:14px;margin-top:10px;">1.21.11 Downloads</div>
      </div>
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:22px;padding:26px 40px;min-width:200px;text-align:center;">
        <div id="dlTotal" style="font-family:var(--display);font-weight:700;font-size:44px;letter-spacing:-.02em;line-height:1;">—</div>
        <div style="color:var(--muted);font-size:14px;margin-top:10px;">Total Downloads</div>
      </div>
    </div>
  </div>
</section>

<section class="band alt">
  <div class="wrap">
    <div class="sec-head">
      <span class="eyebrow">Three steps</span>
      <h2>From download to in-game in a minute.</h2>
    </div>
    <div class="steps">
      <div class="step"><div class="num">1</div><h3>Hit download</h3><p>Grab the latest .jar — no account, no payment, nothing to buy.</p></div>
      <div class="step"><div class="num">2</div><h3>Drop it in your mods folder</h3><p>Put the jar next to Fabric API in your Minecraft mods folder.</p></div>
      <div class="step"><div class="num">3</div><h3>Launch and play</h3><p>Open Minecraft 26.2 with Fabric and you're in.</p></div>
    </div>
  </div>
</section>

<section class="band" id="faq">
  <div class="wrap">
    <div class="sec-head" style="margin-left:auto;margin-right:auto;text-align:center;">
      <span class="eyebrow">Questions</span>
      <h2>Good to know.</h2>
    </div>
    <div class="faq">
      <details open><summary>Which version does it run on?<span class="pm"></span></summary><div class="body">Lethal Client is a Fabric mod for Minecraft 26.2. Drop the jar in your mods folder alongside Fabric API and launch.</div></details>
      <details><summary>How much does it cost?<span class="pm"></span></summary><div class="body">Nothing — the whole client is free. Just make a free account and download it. Optional cosmetics may be available to buy later, but every feature is free.</div></details>
      <details><summary>Do I need an account?<span class="pm"></span></summary><div class="body">Nope. No account, no login, no payment — just click download and drop the jar in your mods folder.</div></details>
      <details><summary>Do updates cost extra?<span class="pm"></span></summary><div class="body">No. New features and fixes download themselves the next time you launch the game — always free.</div></details>
    </div>
  </div>
</section>

<section class="band">
  <div class="wrap">
    <div class="final">
      <h2>Ready when you are.</h2>
      <p>The whole client, completely free. No account, no payment — just download.</p>
      <a class="btn btn-primary btn-lg" href="/download">Download — it's free</a>
    </div>
  </div>
</section>

<footer>
  <div class="wrap">
    <div style="text-align:center;padding:4px 0 28px;border-bottom:1px solid var(--line);margin-bottom:22px;">
      <div style="color:var(--muted);font-size:14px;margin-bottom:14px;">Creator of the client · <a href="https://youtube.com/@lethallukeyt" target="_blank" rel="noopener" style="color:#eef1fa;font-weight:700;text-decoration:none;">@lethallukeyt</a></div>
      <div style="display:flex;gap:12px;justify-content:center;flex-wrap:wrap;">
        <a href="https://youtube.com/@lethallukeyt" target="_blank" rel="noopener" title="YouTube" style="display:inline-flex;align-items:center;gap:8px;padding:10px 18px;border-radius:12px;background:#FF0000;color:#fff;font-weight:700;font-size:14px;text-decoration:none;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M23 7.5a3 3 0 0 0-2.1-2.1C19 5 12 5 12 5s-7 0-8.9.4A3 3 0 0 0 1 7.5 31 31 0 0 0 .7 12 31 31 0 0 0 1 16.5a3 3 0 0 0 2.1 2.1C5 19 12 19 12 19s7 0 8.9-.4a3 3 0 0 0 2.1-2.1A31 31 0 0 0 23.3 12 31 31 0 0 0 23 7.5zM9.75 15.5v-7l6 3.5z"/></svg>YouTube</a>
        <a href="https://twitch.tv/lethallukeyt" target="_blank" rel="noopener" title="Twitch" style="display:inline-flex;align-items:center;gap:8px;padding:10px 18px;border-radius:12px;background:#9146FF;color:#fff;font-weight:700;font-size:14px;text-decoration:none;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M4 2 2.5 6v14H7v2h3l2-2h3l4-4V2zm15 11-2 2h-3l-2 2v-2H6V4h13zM13 7h1.5v4H13zm-4 0h1.5v4H9z"/></svg>Twitch</a>
        <a href="https://tiktok.com/@lethallukeyt" target="_blank" rel="noopener" title="TikTok" style="display:inline-flex;align-items:center;gap:8px;padding:10px 18px;border-radius:12px;background:#111;color:#fff;font-weight:700;font-size:14px;text-decoration:none;border:1px solid #2a2f45;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M16 3c.3 2 1.6 3.6 3.6 3.8v2.6c-1.3 0-2.5-.4-3.6-1.1v5.9a5.4 5.4 0 1 1-5.4-5.4c.3 0 .6 0 .9.1v2.7a2.8 2.8 0 1 0 2 2.6V3z"/></svg>TikTok</a>
      </div>
    </div>
    <div class="foot">
      <a class="brand" href="#top"><span class="mark"><span></span></span>Lethal Client</a>
      <div>© <span id="yr">2026</span> Lethal Client · All rights reserved</div>
    </div>
    <p class="disclaimer">Lethal Client is an independent project and is not affiliated with, endorsed by, or associated with Mojang, Microsoft or DonutSMP. Minecraft is a trademark of Mojang Synergies AB.</p>
  </div>
</footer>

<script>
  document.getElementById("yr").textContent = new Date().getFullYear();

  // Live download counters (per Minecraft version + total) and the version picker — refresh on their own.
  (function(){
    var cur = { "26.2":0, "1.21.11":0, total:0 };
    var pickKey = "";
    function animate(el, from, to){
      if(from===to){ el.textContent = to.toLocaleString(); return; }
      var start = null, dur = 700;
      function step(ts){ if(!start) start = ts; var k = Math.min(1,(ts-start)/dur);
        el.textContent = Math.round(from + (to-from)*(1-Math.pow(1-k,3))).toLocaleString();
        if(k<1) requestAnimationFrame(step); }
      requestAnimationFrame(step);
    }
    function label(v){ return v==="26.2" ? "26.2" : v; }
    function buildPicker(avail){
      var key = (avail||[]).join(",");
      if(key===pickKey) return;                   // only rebuild when the available set changes
      pickKey = key;
      var pick=document.getElementById("dlPick");
      if(!pick) return;
      if(!avail || avail.length===0){
        pick.innerHTML='<button class="btn btn-primary btn-lg" style="font-size:18px;padding:16px 44px;opacity:.6;cursor:not-allowed" disabled>Coming soon</button>';
        return;
      }
      pick.innerHTML = avail.map(function(v){
        return '<a class="btn btn-primary btn-lg" href="/download?mc='+encodeURIComponent(v)+'" style="font-size:18px;padding:16px 34px;margin:6px;">Download for '+label(v)+'</a>';
      }).join("");
    }
    function refresh(){
      fetch("/api/stats",{cache:"no-store"}).then(function(r){return r.json();}).then(function(d){
        var c26=d["26.2"]||(d.counts&&d.counts["26.2"])||0;
        var c1211=d["1.21.11"]||(d.counts&&d.counts["1.21.11"])||0;
        var na=d.total||0;
        var e26=document.getElementById("dl26"), e1211=document.getElementById("dl1211"), a=document.getElementById("dlTotal");
        if(e26) animate(e26, cur["26.2"], c26);
        if(e1211) animate(e1211, cur["1.21.11"], c1211);
        if(a) animate(a, cur.total, na);
        cur = { "26.2":c26, "1.21.11":c1211, total:na };
        buildPicker(d.available||[]);
      }).catch(function(){
        ["dl26","dl1211","dlTotal"].forEach(function(id){ var x=document.getElementById(id); if(x&&x.textContent==="—") x.textContent="0"; });
      });
    }
    refresh();
    setInterval(refresh, 12000);                 // every 12s
    document.addEventListener("visibilitychange", function(){ if(!document.hidden) refresh(); });
  })();

  // Checkout: ask the server for a Stripe checkout URL, then go there.
  async function buy(plan) {
    var btns = document.querySelectorAll("[data-plan]");
    btns.forEach(function (b) { b.style.pointerEvents = "none"; b.style.opacity = "0.55"; });
    try {
      var r = await fetch("/api/checkout", { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ plan: plan }) });
      var d = await r.json().catch(function () { return {}; });
      if (d && d.url) { location.href = d.url; return; }
      alert(d && d.error ? d.error : "Checkout isn't set up yet.");
    } catch (e) {
      alert("Couldn't start checkout. Please try again.");
    }
    btns.forEach(function (b) { b.style.pointerEvents = ""; b.style.opacity = ""; });
  }
  document.querySelectorAll("[data-plan]").forEach(function (b) {
    b.addEventListener("click", function (e) { e.preventDefault(); buy(b.getAttribute("data-plan")); });
  });

  // Ambient cherry petals — ties the page to the client's title screen.
  (function () {
    var reduce = matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (reduce) return;
    var c = document.getElementById("petals"), x = c.getContext("2d"), W, H, dpr = Math.min(2, devicePixelRatio || 1);
    var cols = ["#ff8fc0", "#ffb6d5", "#ffd1e6", "#c9a6ff"], P = [];
    function size() { W = c.width = innerWidth * dpr; H = c.height = innerHeight * dpr; }
    size(); addEventListener("resize", size);
    var N = Math.min(46, Math.round(innerWidth / 26));
    for (var i = 0; i < N; i++) P.push(reset({}, true));
    function reset(p, init) {
      p.x = Math.random() * W; p.y = init ? Math.random() * H : -20 * dpr;
      p.s = (2 + Math.random() * 3) * dpr; p.sp = (10 + Math.random() * 24) * dpr / 60;
      p.sway = 0.4 + Math.random() * 1.1; p.ph = Math.random() * 6.28; p.rot = Math.random() * 6.28;
      p.rs = (Math.random() - 0.5) * 0.04; p.col = cols[(Math.random() * cols.length) | 0]; p.a = 0.5 + Math.random() * 0.4;
      return p;
    }
    var t = 0;
    function frame() {
      t += 0.016; x.clearRect(0, 0, W, H);
      for (var i = 0; i < P.length; i++) {
        var p = P[i]; p.y += p.sp; p.x += Math.sin(t * p.sway + p.ph) * 0.4 * dpr; p.rot += p.rs;
        if (p.y > H + 20 * dpr) reset(p, false);
        x.save(); x.translate(p.x, p.y); x.rotate(p.rot); x.globalAlpha = p.a; x.fillStyle = p.col;
        x.beginPath(); x.moveTo(0, -p.s); x.lineTo(p.s, 0); x.lineTo(0, p.s); x.lineTo(-p.s, 0); x.closePath(); x.fill();
        x.restore();
      }
      requestAnimationFrame(frame);
    }
    frame();
  })();

  // Smooth in-page anchor scrolling.
  document.querySelectorAll('a[href^="#"]').forEach(function (a) {
    a.addEventListener("click", function (e) {
      var id = a.getAttribute("href"); if (id.length < 2) return;
      var el = document.querySelector(id); if (!el) return;
      e.preventDefault(); el.scrollIntoView({ behavior: "smooth", block: "start" });
    });
  });
</script>
{{LOGOUT}}
<a href="/admin" title="Lethal Admin" aria-label="Lethal Admin" style="position:fixed;right:16px;bottom:16px;z-index:9999;width:44px;height:44px;border-radius:13px;display:grid;place-items:center;background:rgba(14,17,32,.72);border:1px solid #20263c;color:#8b93ad;text-decoration:none;backdrop-filter:blur(8px);transition:.18s" onmouseover="this.style.color='#cfd6ee';this.style.borderColor='#39456e'" onmouseout="this.style.color='#8b93ad';this.style.borderColor='#20263c'"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3 20 6v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/><path d="m9 12 2 2 4-4"/></svg></a>
`;

// Shown in the top-right only when you got in with the access key before launch, so you can
// go back to the countdown. Logging out clears the cookie and the saved key, so the countdown
// stays locked until you type the key again. After launch there is no gate, so it is never shown.
const LOGOUT_BUTTON = `<button id="lc-logout" title="Log out" aria-label="Log out" onclick="(function(){try{localStorage.removeItem('lc_key');sessionStorage.removeItem('lc_tried');}catch(e){}fetch('/api/logout',{method:'POST'}).catch(function(){}).finally(function(){location.href='/';});})()" style="position:fixed;left:16px;bottom:16px;z-index:9999;width:44px;height:44px;border-radius:13px;display:grid;place-items:center;background:rgba(14,17,32,.72);border:1px solid #20263c;color:#8b93ad;cursor:pointer;backdrop-filter:blur(8px);transition:.18s" onmouseover="this.style.color='#cfd6ee';this.style.borderColor='#39456e'" onmouseout="this.style.color='#8b93ad';this.style.borderColor='#20263c'"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/></svg></button>`;

// ---------- admin website ----------

const ADMIN_HTML = `<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Lethal Admin</title>
<link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%233b82f6'%3E%3Cpath d='M12 2 4 5v6c0 5 3.4 8.5 8 11 4.6-2.5 8-6 8-11V5l-8-3z'/%3E%3C/svg%3E">
<style>
  :root { --bg:#0a0e1a; --card:#111626; --card2:#0d1120; --border:#1e2740; --text:#e8ecf5; --muted:#8b93ad; --blue:#3b82f6; --blue2:#2563eb; --green:#22c55e; --red:#ef4444; --amber:#f59e0b; }
  * { box-sizing:border-box; }
  body { margin:0; background:var(--bg); color:var(--text); font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Helvetica,Arial,sans-serif; -webkit-font-smoothing:antialiased; }
  .wrap { max-width:1040px; margin:0 auto; padding:24px 16px 64px; }
  h1 { font-size:26px; font-weight:800; margin:0; letter-spacing:-.5px; }
  h1 span { color:var(--blue); }
  .sub { color:var(--muted); font-size:14px; margin:6px 0 0; }
  .card { background:var(--card); border:1px solid var(--border); border-radius:16px; padding:18px; margin-top:16px; }
  .row { display:flex; gap:12px; align-items:center; flex-wrap:wrap; }
  input, select { background:var(--card2); border:1px solid var(--border); color:var(--text); border-radius:10px; padding:11px 13px; font-size:14px; outline:none; }
  input:focus, select:focus { border-color:var(--blue); }
  button { cursor:pointer; border:1px solid var(--border); background:var(--card2); color:var(--text); border-radius:10px; padding:10px 15px; font-size:14px; font-weight:600; transition:.15s; }
  button:hover { border-color:#39456e; }
  button.primary { background:var(--blue); border-color:var(--blue); color:#fff; }
  button.primary:hover { background:var(--blue2); }
  button.danger { color:#fca5a5; }
  button.danger:hover { border-color:var(--red); color:#fff; background:rgba(239,68,68,.15); }
  button:disabled { opacity:.5; cursor:default; }
  .err { color:#fca5a5; font-size:13px; min-height:18px; margin-top:10px; }
  table { width:100%; border-collapse:collapse; }
  th { text-align:left; color:var(--muted); font-size:12px; font-weight:600; padding:8px 8px; text-transform:uppercase; letter-spacing:.4px; }
  td { padding:10px 8px; border-top:1px solid var(--border); font-size:14px; vertical-align:middle; }
  .muted { color:var(--muted); }
  .pill { display:inline-block; padding:3px 9px; border-radius:999px; font-size:12px; font-weight:700; }
  .pill.active { background:rgba(34,197,94,.15); color:#4ade80; }
  .pill.unused { background:rgba(59,130,246,.15); color:#60a5fa; }
  .pill.revoked { background:rgba(239,68,68,.15); color:#f87171; }
  .pill.expired { background:rgba(245,158,11,.15); color:#fbbf24; }
  .avatar { width:28px; height:28px; border-radius:6px; image-rendering:pixelated; background:#1a2138; }
  .mono { font-family:ui-monospace,SFMono-Regular,Menlo,Consolas,monospace; }
  .center { text-align:center; }
  .login-wrap { max-width:400px; margin:12vh auto 0; }
  .switch { position:relative; width:46px; height:26px; border-radius:999px; background:#2a3350; border:none; padding:0; }
  .switch::after { content:""; position:absolute; top:3px; left:3px; width:20px; height:20px; border-radius:50%; background:#fff; transition:.18s; }
  .switch.on { background:var(--green); }
  .switch.on::after { left:23px; }
  #toast { position:fixed; left:50%; bottom:26px; transform:translateX(-50%) translateY(20px); background:#11182b; border:1px solid var(--border); color:var(--text); padding:11px 18px; border-radius:12px; font-size:14px; font-weight:600; opacity:0; pointer-events:none; transition:.2s; z-index:50; }
  #toast.show { opacity:1; transform:translateX(-50%) translateY(0); }
  #toast.bad { border-color:var(--red); color:#fca5a5; }
  .head { display:flex; justify-content:space-between; align-items:flex-start; gap:16px; margin-bottom:4px; }
  .grow { flex:1 1 200px; }
  @media (max-width:640px){ .hide-sm{ display:none; } }
</style>
</head>
<body>
<div class="wrap">

  <div id="login" class="login-wrap">
    <div class="card">
      <h1>Lethal <span>Admin</span></h1>
      <p class="sub">Enter your admin password.</p>
      <div style="margin-top:14px"><input id="pw" type="password" autocomplete="off" placeholder="Admin password" style="width:100%"></div>
      <div style="margin-top:10px"><button id="loginBtn" class="primary" style="width:100%">Log in</button></div>
      <div class="err" id="loginErr"></div>
    </div>
  </div>

  <div id="app" style="display:none">
    <div class="head">
      <div>
        <h1>Lethal <span>Admin</span></h1>
        <p class="sub">Everyone running the client, with their Minecraft name. Pick a role to give them a nametag badge.</p>
      </div>
      <button id="logoutBtn">Log out</button>
    </div>

    <div class="card">
      <div class="row" style="justify-content:space-between;flex-wrap:nowrap">
        <div><b>Maintenance mode</b><div class="muted" id="maintText">Off — the mod works normally for everyone.</div></div>
        <button id="maint" class="switch" title="Turn maintenance on or off"></button>
      </div>
    </div>

    <div class="card">
      <div><b>Update the mod</b><div class="muted" id="updText">No update uploaded yet.</div>
        <div class="muted" style="margin-top:4px;font-size:12px">Upload each Minecraft version's jar (26.2 and 1.21.11) — it's filed under its own version automatically, so just upload them one at a time.</div></div>
      <div class="row" style="margin-top:12px">
        <input id="jar" type="file" accept=".jar" class="grow">
        <label class="muted row" style="gap:6px"><input id="req" type="checkbox" checked style="width:auto"> Make everyone update</label>
        <button id="uploadBtn" class="primary">Upload</button>
      </div>
    </div>

    <div class="card">
      <div><b>Send a build to specific people</b><div class="muted">Only the players you list auto-update to this jar — everyone else keeps the public version. Great for testing a build first.</div></div>
      <div class="row" style="margin-top:12px">
        <input id="tjar" type="file" accept=".jar" class="grow">
        <button id="tUploadBtn" class="primary">Send to people</button>
      </div>
      <input id="tNames" class="grow" style="margin-top:8px" placeholder="Minecraft names or UUIDs, comma-separated (e.g. LethalLukeYT, Notch)">
      <div id="targetsBox" class="muted" style="margin-top:10px">No targeted builds.</div>
    </div>

    <div class="card">
      <div><b>Minimum version</b><div class="muted" id="verText">Any version can be used.</div></div>
      <div class="row" style="margin-top:12px">
        <input id="reqVer" class="grow" placeholder="Minimum version, e.g. 1.0.2 (blank = any)">
        <button id="verBtn" class="primary">Save</button>
      </div>
    </div>

    <div class="card">
      <div class="row" style="justify-content:space-between;margin-bottom:10px">
        <b>Players <span class="muted" id="pCount"></span></b>
        <input id="pSearch" placeholder="Search name…" style="max-width:220px">
      </div>
      <div id="playersBox" class="muted">Loading players…</div>
    </div>
  </div>

</div>
<div id="toast"></div>

<script>
var $ = function (id) { return document.getElementById(id); };
function ls(k, v) { try { if (v === undefined) return localStorage.getItem(k); if (v === null) localStorage.removeItem(k); else localStorage.setItem(k, v); } catch (e) { return null; } }
var pw = ls("lc_admin_pw") || "";
var players = [], settings = {};

function toast(msg, bad) { var t = $("toast"); t.textContent = msg; t.className = "show" + (bad ? " bad" : ""); clearTimeout(toast.t); toast.t = setTimeout(function () { t.className = ""; }, 2400); }
function esc(s) { return String(s == null ? "" : s).replace(/[&<>"']/g, function (c) { return "&#" + c.charCodeAt(0) + ";"; }); }
function ago(t) { if (!t) return "never"; var s = (Date.now() - t) / 1000; if (s < 60) return "just now"; if (s < 3600) return Math.floor(s / 60) + " min ago"; if (s < 86400) return Math.floor(s / 3600) + " h ago"; return Math.floor(s / 86400) + " d ago"; }

async function api(action, body) {
  var r = await fetch("/api/admin/" + action, {
    method: "POST",
    headers: { authorization: "Bearer " + pw, "content-type": "application/json" },
    body: JSON.stringify(body || {}),
  });
  if (r.status === 401) { showLogin("Wrong password."); throw new Error("auth"); }
  var data = await r.json().catch(function () { return {}; });
  if (!r.ok) { toast(data.error || "Something went wrong", true); throw new Error(data.error || "err"); }
  return data;
}

function showLogin(msg) { pw = ""; ls("lc_admin_pw", null); $("app").style.display = "none"; $("login").style.display = ""; $("loginErr").textContent = msg || ""; }
function showApp() { $("login").style.display = "none"; $("app").style.display = ""; }

var busy = false;
async function doLogin() {
  if (busy) return; busy = true;
  var err = $("loginErr");
  try {
    pw = ($("pw").value || "").trim();
    if (!pw) { err.textContent = "Type your password first."; return; }
    err.textContent = "Signing in…";
    var r;
    try {
      r = await fetch("/api/admin/list", { method: "POST", headers: { authorization: "Bearer " + pw, "content-type": "application/json" }, body: "{}" });
    } catch (e) { err.textContent = "Can't reach the server (" + (e.message || e) + ")."; return; }
    if (r.status === 401) { err.textContent = "Wrong password."; return; }
    if (!r.ok) { err.textContent = "Server error " + r.status + ". Try again."; return; }
    var data = await r.json().catch(function () { return { keys: [] }; });
    ls("lc_admin_pw", pw);
    err.textContent = "";
    showApp();
    applyList(data);
    loadPlayers();
  } finally { busy = false; }
}

async function loadAll() {
  var data = await api("list");
  showApp();
  applyList(data);
  loadPlayers();
}

function applyList(data) {
  settings = data.settings || {};
  renderSettings();
}

function renderSettings() {
  var on = !!settings.maintenance;
  $("maint").className = "switch" + (on ? " on" : "");
  $("maintText").textContent = on ? "ON — everyone sees the maintenance message." : "Off — the mod works normally for everyone.";
  var v = settings.requiredVersion || "";
  $("verText").textContent = v ? ("Players must be on " + v + " or newer.") : "Any version can be used.";
  if (document.activeElement !== $("reqVer")) $("reqVer").value = v;
  var up = settings.update;
  var ml = settings.mcLatest || {};
  var mcs = Object.keys(ml).filter(function (k) { return ml[k] && ml[k].version; }).sort();
  if (mcs.length) {
    $("updText").innerHTML = mcs.map(function (mc) {
      return 'MC ' + esc(mc) + ': <b>v' + esc(ml[mc].version) + '</b>';
    }).join(' &nbsp;·&nbsp; ');
  } else {
    $("updText").textContent = up && up.version ? ("Current upload: " + up.version) : "No update uploaded yet.";
  }
  renderTargets();
}

async function loadPlayers() {
  try { var d = await api("players"); players = d.players || []; renderPlayers(); } catch (e) {}
}
function verCell(p) {
  var ver = p.ver || "";
  var mc = p.mc || "";
  var mcTag = mc ? '<span class="muted" style="font-size:11px">MC ' + esc(mc) + '</span>' : '';
  if (!ver) return '<span class="muted">unknown</span>' + (mcTag ? ' ' + mcTag : '');
  var label = 'v' + esc(ver);
  var badge = '';
  if (p.uptodate === true) {
    badge = '<span style="background:#11351f;color:#4ade80;border:1px solid #1e5c37;border-radius:6px;padding:1px 7px;font-size:11px;font-weight:600">Up to date</span>';
  } else if (p.uptodate === false) {
    badge = '<span style="background:#3a1414;color:#f87171;border:1px solid #6b2020;border-radius:6px;padding:1px 7px;font-size:11px;font-weight:600">Outdated' + (p.latest ? ' → v' + esc(p.latest) : '') + '</span>';
  }
  return '<div class="row" style="gap:6px;flex-wrap:wrap"><b>' + label + '</b> ' + badge + (mcTag ? ' ' + mcTag : '') + '</div>';
}
function renderPlayers() {
  var q = (($("pSearch") && $("pSearch").value) || "").toLowerCase();
  var list = players.filter(function (p) { return !q || (p.name || "").toLowerCase().indexOf(q) >= 0; });
  $("pCount").textContent = "· " + players.length;
  if (!list.length) { $("playersBox").innerHTML = '<div class="muted" style="padding:8px 0">No players yet — they appear here the first time they run the client.</div>'; return; }
  var roles = ["member", "vip", "creator", "owner"];
  $("playersBox").innerHTML = '<table><thead><tr><th>Player</th><th>Version</th><th>Last seen</th><th>Role</th></tr></thead><tbody>' +
    list.map(function (p) {
      return '<tr><td><div class="row" style="gap:10px"><img class="avatar" src="https://mc-heads.net/avatar/' + encodeURIComponent(p.uuid || p.name) + '/28" onerror="this.style.visibility=\\'hidden\\'"><b>' + esc(p.name || "?") + '</b></div></td>' +
        '<td>' + verCell(p) + '</td>' +
        '<td class="muted">' + ago(p.seen) + '</td>' +
        '<td><select class="roleSel" data-uuid="' + esc(p.uuid) + '">' +
        roles.map(function (r) { return '<option value="' + r + '"' + (p.role === r ? " selected" : "") + '>' + r.charAt(0).toUpperCase() + r.slice(1) + '</option>'; }).join("") +
        '</select></td></tr>';
    }).join("") + '</tbody></table>';
  Array.prototype.forEach.call(document.querySelectorAll(".roleSel"), function (sel) {
    sel.onchange = function () {
      var uuid = sel.getAttribute("data-uuid");
      var p = players.find(function (x) { return x.uuid === uuid; });
      if (p) p.role = sel.value;
      api("setrole", { uuid: uuid, role: sel.value }).then(function () { toast("Role updated"); }).catch(function () {});
    };
  });
}

// ---- events ----
$("loginBtn").addEventListener("click", function () { doLogin(); });
$("pw").addEventListener("keydown", function (e) { if (e.key === "Enter") { e.preventDefault(); doLogin(); } });
$("logoutBtn").addEventListener("click", function () { showLogin(""); });
$("pSearch").addEventListener("input", renderPlayers);

$("maint").addEventListener("click", function () {
  var next = !settings.maintenance;
  api("settings", { maintenance: next }).then(function (d) { settings = d.settings || settings; renderSettings(); toast(next ? "Maintenance ON" : "Maintenance off"); }).catch(function () {});
});
$("verBtn").addEventListener("click", function () {
  api("settings", { requiredVersion: $("reqVer").value.trim() }).then(function (d) { settings = d.settings || settings; renderSettings(); toast("Saved"); }).catch(function () {});
});
$("uploadBtn").addEventListener("click", async function () {
  var file = $("jar").files[0];
  if (!file) { toast("Pick the new .jar first", true); return; }
  var b = $("uploadBtn"); b.disabled = true; b.textContent = "Uploading…";
  try {
    var r = await fetch("/api/admin/upload", { method: "POST", headers: { authorization: "Bearer " + pw, "x-require": $("req").checked ? "1" : "0" }, body: file });
    var data = await r.json().catch(function () { return {}; });
    if (!r.ok) { toast(data.error || "Upload failed", true); return; }
    settings = data.settings || settings; renderSettings(); $("jar").value = "";
    toast("Uploaded " + (data.settings && data.settings.update ? data.settings.update.version : "") + " — games get it within a minute");
  } finally { b.disabled = false; b.textContent = "Upload"; }
});

$("tUploadBtn").addEventListener("click", async function () {
  var file = $("tjar").files[0];
  var names = ($("tNames").value || "").trim();
  if (!file) { toast("Pick the .jar first", true); return; }
  if (!names) { toast("List at least one player name", true); return; }
  var b = $("tUploadBtn"); b.disabled = true; b.textContent = "Sending…";
  try {
    var r = await fetch("/api/admin/upload", { method: "POST", headers: { authorization: "Bearer " + pw, "x-target": names }, body: file });
    var data = await r.json().catch(function () { return {}; });
    if (!r.ok) { toast(data.error || "Upload failed", true); return; }
    settings = data.settings || settings; renderSettings(); $("tjar").value = ""; $("tNames").value = "";
    var n = data.targeted ? data.targeted.count : 0;
    toast("Sent to " + n + " player" + (n === 1 ? "" : "s") + " — they update within a minute");
  } finally { b.disabled = false; b.textContent = "Send to people"; }
});

function renderTargets() {
  var box = $("targetsBox");
  if (!box) return;
  var t = (settings && settings.targets) || [];
  if (!t.length) { box.innerHTML = "No targeted builds."; return; }
  box.innerHTML = t.map(function (x) {
    var who = ((x.names || []).concat(x.uuids || [])).map(esc).join(", ");
    var mcTag = x.mc ? ' <span class="muted" style="font-size:11px">MC ' + esc(x.mc) + ' only</span>' : '';
    return '<div class="row" style="justify-content:space-between;gap:10px;padding:6px 0;border-top:1px solid var(--line)">' +
      '<div><b>v' + esc(x.version) + '</b>' + mcTag + ' <span class="muted">→ ' + who + '</span></div>' +
      '<button class="roleSel" data-ver="' + esc(x.version) + '" style="cursor:pointer">Remove</button></div>';
  }).join("");
  Array.prototype.forEach.call(box.querySelectorAll("button[data-ver]"), function (btn) {
    btn.onclick = function () {
      api("untarget", { version: btn.getAttribute("data-ver") }).then(function (d) {
        if (d && d.settings) { settings = d.settings; renderSettings(); }
        toast("Removed");
      }).catch(function () {});
    };
  });
}

// Refresh players + last-seen every 20s while logged in.
setInterval(function () { if (pw && $("app").style.display !== "none") { loadPlayers(); api("list").then(applyList).catch(function () {}); } }, 20000);

// ---- start ----
if (pw) { loadAll().catch(function () { showLogin(""); }); } else { showLogin(""); }
</script>
</body>
</html>`;
