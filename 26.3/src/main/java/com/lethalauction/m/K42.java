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
import com.lethalauction.m.K43;
import com.lethalauction.m.K47;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class K42 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
    private static volatile String b = "";

    private K42() {
    }

    static void a(String key, String title, String artist) {
        if (key == null || key.isEmpty() || title == null || title.isEmpty() || key.equals(b)) {
            return;
        }
        b = key;
        Thread t = new Thread(() -> K42.b(key, title, artist), "lethal-coverart");
        t.setDaemon(true);
        t.start();
    }

    private static void b(String key, String title, String artist) {
        try {
            String query = K42.b(artist) + " " + K42.b(title);
            String term = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create("https://itunes.apple.com/search?term=" + term + "&entity=song&limit=1");
            HttpRequest req = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10L)).header("User-Agent", "LethalClient").GET().build();
            HttpResponse<String> res = a.send(req, HttpResponse.BodyHandlers.ofString());
            K43.a("itunes search \"" + query + "\" -> HTTP " + res.statusCode());
            if (res.statusCode() != 200) {
                return;
            }
            JsonObject o = JsonParser.parseString((String)res.body()).getAsJsonObject();
            JsonArray results = o.getAsJsonArray("results");
            if (results == null || results.isEmpty()) {
                K43.a("no results for \"" + query + "\"");
                K47.a(key);
                return;
            }
            JsonObject first = results.get(0).getAsJsonObject();
            if (!first.has("artworkUrl100") || first.get("artworkUrl100").isJsonNull()) {
                K43.a("result had no artwork");
                K47.a(key);
                return;
            }
            String base = first.get("artworkUrl100").getAsString();
            byte[] body = K42.a(base.replace("100x100bb", "512x512bb"));
            if (body == null) {
                body = K42.a(base);
            }
            if (body != null && body.length > 0) {
                K43.a("got cover (" + body.length + " bytes) for \"" + query + "\"");
                K47.a(key, body);
            } else {
                K43.a("cover download empty for \"" + query + "\"");
                K47.a(key);
            }
        }
        catch (Exception e) {
            K43.a("itunes lookup error: " + String.valueOf(e));
        }
    }

    private static byte[] a(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(12L)).header("User-Agent", "LethalClient").GET().build();
            HttpResponse<byte[]> res = a.send(req, HttpResponse.BodyHandlers.ofByteArray());
            return res.statusCode() == 200 ? res.body() : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static String b(String s) {
        if (s == null) {
            return "";
        }
        s = s.replaceAll("(?i)\\((feat|ft|with)\\.?[^)]*\\)", " ");
        s = s.replaceAll("(?i)\\[[^]]*]", " ");
        s = s.replaceAll("(?i)\\b(feat|ft|with)\\.?\\b.*$", " ");
        s = s.replaceAll("(?i)\\s*-\\s*(single|ep|remastered.*|deluxe.*)$", " ");
        s = s.replace("&", " ");
        return s.replaceAll("\\s+", " ").trim();
    }
}

