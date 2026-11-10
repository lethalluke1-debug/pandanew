/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.lethalauction.m;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.m.K54;
import com.lethalauction.m.K9;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

public final class K57 {
    private static final String[] f = new String[0];
    private static final C103[] g = new C103[0];
    public static final boolean a = false;
    public static final int b = 0;
    public static final int c = 1;
    public static final int d = 2;
    public static final int e = 3;
    private static final int h = 200;
    private static final HttpClient i = HttpClient.newHttpClient();
    private static final Deque<C104> j = new ArrayDeque<C104>();
    private static volatile long k = -1L;
    private static volatile long l = -1L;
    private static volatile long m = -1L;
    private static volatile int n;
    private static volatile int o;
    private static volatile int p;
    private static volatile int q;
    private static volatile String r;
    private static volatile WebSocket s;
    private static final AtomicBoolean t;
    private static volatile String u;
    private static volatile String v;
    private static long w;
    private static int x;
    private static final StringBuilder y;

    private K57() {
    }

    public static int a() {
        return q;
    }

    public static String b() {
        return r;
    }

    public static long c() {
        return k;
    }

    public static long d() {
        return l;
    }

    public static long e() {
        return m;
    }

    public static int f() {
        return n;
    }

    public static int g() {
        return o;
    }

    public static int h() {
        return p;
    }

    public static long i() {
        long t = 0L;
        if (k >= 0L) {
            t += k;
        }
        if (l >= 0L) {
            t += l;
        }
        if (m >= 0L) {
            t += m;
        }
        return t;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static C104[] a(int n) {
        Deque<C104> deque = j;
        synchronized (deque) {
            int size = j.size();
            int take = Math.min(n, size);
            C104[] out = new C104[take];
            int skip = size - take;
            int i = 0;
            int k = 0;
            for (C104 l : j) {
                if (i++ < skip) continue;
                out[k++] = l;
            }
            return out;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int j() {
        Deque<C104> deque = j;
        synchronized (deque) {
            return j.size();
        }
    }

    public static void k() {
        long now;
        boolean on = false;
        String string = u = on ? K57.n() : "";
        if (!on) {
            if (s != null || q != 0) {
                K57.c("off");
            }
            return;
        }
        if (s != null && !u.equals(v)) {
            K57.c("reconnect");
        }
        if (s == null && !t.get() && (now = System.currentTimeMillis()) >= w) {
            K57.p();
        }
    }

    private static boolean m() {
        return !K9.ae.trim().isEmpty() || !K9.af.trim().isEmpty() || !K9.ag.trim().isEmpty();
    }

    private static String n() {
        return K9.ad + "|" + K9.ae.trim() + "|" + K9.af.trim() + "|" + K9.ag.trim();
    }

    private static URI o() {
        String base = K9.ad.trim();
        StringBuilder q = new StringBuilder();
        K57.a(q, "twitch", K9.ae);
        K57.a(q, "youtube", K9.af);
        K57.a(q, "tiktok", K9.ag);
        String url = base + (String)(q.length() > 0 ? "?" + String.valueOf(q) : "");
        return URI.create(url);
    }

    private static void a(StringBuilder q, String name, String val) {
        String string = val = val == null ? "" : val.trim();
        if (val.isEmpty()) {
            return;
        }
        if (q.length() > 0) {
            q.append('&');
        }
        q.append(name).append('=').append(K57.a(val));
    }

    private static String a(String s) {
        StringBuilder out = new StringBuilder();
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            int c = b & 0xFF;
            if (c >= 65 && c <= 90 || c >= 97 && c <= 122 || c >= 48 && c <= 57 || c == 45 || c == 95 || c == 46 || c == 126) {
                out.append((char)c);
                continue;
            }
            out.append('%').append(Character.forDigit(c >> 4, 16)).append(Character.forDigit(c & 0xF, 16));
        }
        return out.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void p() {
        URI uri;
        String openingKey;
        if (!t.compareAndSet(false, true)) {
            return;
        }
        v = openingKey = u;
        q = 1;
        r = "Connecting\u2026";
        K57.q();
        StringBuilder stringBuilder = y;
        synchronized (stringBuilder) {
            y.setLength(0);
        }
        try {
            uri = K57.o();
        }
        catch (Exception e) {
            t.set(false);
            K57.b("Bad server URL");
            return;
        }
        i.newWebSocketBuilder().buildAsync(uri, new C105()).whenComplete((ws, err) -> {
            t.set(false);
            if (err != null) {
                K57.b("Can't reach server");
                return;
            }
            if (!openingKey.equals(u) || !K9.ab) {
                try {
                    ws.sendClose(1000, "stale");
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return;
            }
            s = ws;
            q = 2;
            r = "Connected";
            x = 0;
        });
    }

    private static void b(String why) {
        q = 3;
        r = why;
        s = null;
        x = Math.min(30, x == 0 ? 3 : x * 2);
        w = System.currentTimeMillis() + (long)x * 1000L;
    }

    private static void c(String reason) {
        WebSocket ws = s;
        s = null;
        q = 0;
        r = "";
        K57.q();
        v = "";
        x = 0;
        w = 0L;
        if (ws != null) {
            try {
                ws.sendClose(1000, reason);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static void q() {
        m = -1L;
        l = -1L;
        k = -1L;
        p = 0;
        o = 0;
        n = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void l() {
        Deque<C104> deque = j;
        synchronized (deque) {
            j.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void d(String json) {
        String type;
        JsonObject o;
        try {
            o = JsonParser.parseString((String)json).getAsJsonObject();
        }
        catch (Exception e) {
            return;
        }
        block8 : switch (type = K57.a(o, "type")) {
            case "chat": {
                String platform = K57.a(o, "platform");
                String name = K57.a(o, "name");
                String text = K57.a(o, "text");
                if (text.isEmpty()) {
                    return;
                }
                int col = K57.f(K57.a(o, "color"));
                C104 line = new C104(platform, name, text, col);
                line.h = K57.a(o, "roomId");
                K57.a(o, platform, line);
                line.i = K57.e(K57.a(o, "emotes"));
                Deque<C104> deque = j;
                synchronized (deque) {
                    j.addLast(line);
                    while (j.size() > 200) {
                        j.removeFirst();
                    }
                }
                K54.a(platform, name, text);
                break;
            }
            case "viewers": {
                long count = o.has("count") && o.get("count").isJsonPrimitive() ? o.get("count").getAsLong() : -1L;
                switch (K57.a(o, "platform")) {
                    case "twitch": {
                        k = count;
                        break block8;
                    }
                    case "youtube": {
                        l = count;
                        break block8;
                    }
                    case "tiktok": {
                        m = count;
                        break block8;
                    }
                }
                break;
            }
            case "status": {
                int st = switch (K57.a(o, "state")) {
                    case "wait" -> 1;
                    case "on" -> 2;
                    case "err" -> 3;
                    default -> 0;
                };
                switch (K57.a(o, "platform")) {
                    case "twitch": {
                        n = st;
                        break block8;
                    }
                    case "youtube": {
                        K57.o = st;
                        break block8;
                    }
                    case "tiktok": {
                        p = st;
                        break block8;
                    }
                }
                break;
            }
        }
    }

    private static void a(JsonObject o, String platform, C104 line) {
        try {
            if ("twitch".equals(platform)) {
                String raw = K57.a(o, "badges");
                if (raw.isEmpty()) {
                    return;
                }
                String[] parts = raw.split(",");
                ArrayList<String> keys = new ArrayList<String>();
                ArrayList<String> roles = new ArrayList<String>();
                for (String p : parts) {
                    if ((p = p.trim()).isEmpty()) continue;
                    keys.add(p);
                    int slash = p.indexOf(47);
                    roles.add(slash > 0 ? p.substring(0, slash) : p);
                }
                line.g = keys.toArray(new String[0]);
                line.f = roles.toArray(new String[0]);
            } else if (o.has("badges") && o.get("badges").isJsonArray()) {
                JsonArray arr = o.getAsJsonArray("badges");
                ArrayList<String> roles = new ArrayList<String>();
                for (int i = 0; i < arr.size(); ++i) {
                    String b = arr.get(i).getAsString();
                    roles.add("mod".equalsIgnoreCase(b) ? "moderator" : ("sub".equalsIgnoreCase(b) ? "member" : b));
                }
                line.f = roles.toArray(new String[0]);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static C103[] e(String raw) {
        if (raw == null || raw.isEmpty()) {
            return g;
        }
        ArrayList<C103> out = new ArrayList<C103>();
        try {
            for (String grp : raw.split("/")) {
                int colon = grp.indexOf(58);
                if (colon <= 0) continue;
                String id = grp.substring(0, colon);
                for (String range : grp.substring(colon + 1).split(",")) {
                    int dash = range.indexOf(45);
                    if (dash <= 0) continue;
                    int a = Integer.parseInt(range.substring(0, dash).trim());
                    int b = Integer.parseInt(range.substring(dash + 1).trim());
                    if (b < a) continue;
                    out.add(new C103(a, b, id));
                }
            }
        }
        catch (Exception e) {
            return g;
        }
        out.sort((x, y) -> Integer.compare(x.a, y.a));
        return out.toArray(new C103[0]);
    }

    private static String a(JsonObject o, String k) {
        try {
            return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
        }
        catch (Exception e) {
            return "";
        }
    }

    private static int f(String s) {
        if (s == null) {
            return 0;
        }
        if ((s = s.trim()).startsWith("#")) {
            s = s.substring(1);
        }
        if (s.length() != 6) {
            return 0;
        }
        try {
            return 0xFF000000 | Integer.parseInt(s, 16);
        }
        catch (Exception e) {
            return 0;
        }
    }

    static {
        q = 0;
        r = "";
        t = new AtomicBoolean(false);
        u = "";
        v = "";
        y = new StringBuilder();
    }

    public static final class C104 {
        public final String a;
        public final String b;
        public final String c;
        public final int d;
        public long e;
        public String[] f;
        public String[] g;
        public String h = "";
        public C103[] i;

        C104(String platform, String name, String text, int nameColor) {
            this.a = platform;
            this.b = name;
            this.c = text;
            this.d = nameColor;
            this.e = System.currentTimeMillis();
        }
    }

    private static final class C105
    implements WebSocket.Listener {
        private C105() {
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1L);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            StringBuilder stringBuilder = y;
            synchronized (stringBuilder) {
                y.append(data);
                if (last) {
                    String whole = y.toString();
                    y.setLength(0);
                    for (String part : whole.split("\n")) {
                        String p = part.trim();
                        if (p.isEmpty()) continue;
                        K57.d(p);
                    }
                }
            }
            webSocket.request(1L);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            if (webSocket == s) {
                K57.b("Disconnected");
            }
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            if (webSocket == s) {
                K57.b("Connection lost");
            }
        }
    }

    public static final class C103 {
        public final int a;
        public final int b;
        public final String c;

        C103(int start, int end, String id) {
            this.a = start;
            this.b = end;
            this.c = id;
        }
    }
}

