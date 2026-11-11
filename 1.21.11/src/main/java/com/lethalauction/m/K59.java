/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.lethalauction.m;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class K59 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
    private static volatile Map<String, String> b;
    private static volatile long c;
    private static final long d = 60000L;
    private static final Map<String, Map<String, String>> e;
    private static final Map<String, Long> f;

    private K59() {
    }

    public static String a(String roomId, String key) {
        Map<String, String> g;
        if (key == null || key.isEmpty()) {
            return null;
        }
        K59.a();
        if (roomId != null && !roomId.isEmpty()) {
            String u;
            K59.a(roomId);
            Map<String, String> ch = e.get(roomId);
            if (ch != null && (u = ch.get(key)) != null) {
                return u;
            }
        }
        return (g = b) == null ? null : g.get(key);
    }

    private static void a() {
        long now = System.currentTimeMillis();
        if (b != null || now < c) {
            return;
        }
        c = now + 60000L;
        K59.a("https://badges.twitch.tv/v1/badges/global/display", (Map<String, String> m) -> {
            if (!m.isEmpty()) {
                b = m;
            }
        });
    }

    private static void a(String roomId) {
        if (e.containsKey(roomId)) {
            return;
        }
        long now = System.currentTimeMillis();
        Long next = f.get(roomId);
        if (next != null && now < next) {
            return;
        }
        f.put(roomId, now + 60000L);
        K59.a("https://badges.twitch.tv/v1/badges/channels/" + roomId + "/display", (Map<String, String> m) -> {
            if (!m.isEmpty()) {
                e.put(roomId, m);
            }
        });
    }

    private static void a(String url, C111 sink) {
        Thread t = new Thread(() -> {
            ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
            try {
                JsonObject root;
                JsonObject sets;
                HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(12L)).header("User-Agent", "LethalClient").GET().build();
                HttpResponse<String> res = a.send(req, HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() == 200 && (sets = (root = JsonParser.parseString((String)res.body()).getAsJsonObject()).getAsJsonObject("badge_sets")) != null) {
                    for (String set : sets.keySet()) {
                        JsonObject versions = sets.getAsJsonObject(set).getAsJsonObject("versions");
                        if (versions == null) continue;
                        for (String ver : versions.keySet()) {
                            JsonObject v = versions.getAsJsonObject(ver);
                            String img = K59.a(v, "image_url_2x", "image_url_1x", "image_url_4x");
                            if (img == null) continue;
                            map.put(set + "/" + ver, img);
                        }
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            sink.accept(map);
        }, "lethal-twitch-badges");
        t.setDaemon(true);
        t.start();
    }

    private static String a(JsonObject o, String ... keys) {
        for (String k : keys) {
            if (!o.has(k) || o.get(k).isJsonNull()) continue;
            return o.get(k).getAsString();
        }
        return null;
    }

    static {
        e = new ConcurrentHashMap<String, Map<String, String>>();
        f = new ConcurrentHashMap<String, Long>();
    }

    private static interface C111 {
        public void accept(Map<String, String> var1);
    }
}

