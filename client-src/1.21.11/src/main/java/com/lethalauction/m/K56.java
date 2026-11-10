/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package com.lethalauction.m;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public final class K56 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
    private static final int b = 400;
    private static final AtomicInteger c = new AtomicInteger();
    private static final Map<String, C102> d = new ConcurrentHashMap<String, C102>();

    private K56() {
    }

    public static class_2960 a(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        C102 t = d.get(url);
        if (t == null) {
            if (d.size() >= 400) {
                return null;
            }
            t = new C102();
            C102 prev = d.putIfAbsent(url, t);
            if (prev == null) {
                K56.a(url, t);
            } else {
                t = prev;
            }
        }
        return t.e ? null : t.a;
    }

    public static boolean b(String url) {
        C102 t = url == null ? null : d.get(url);
        return t != null && t.e;
    }

    public static int c(String url) {
        C102 t = d.get(url);
        return t == null ? 1 : t.b;
    }

    public static int d(String url) {
        C102 t = d.get(url);
        return t == null ? 1 : t.c;
    }

    private static void a(String url, C102 t) {
        t.d = true;
        Thread th = new Thread(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(12L)).header("User-Agent", "LethalClient").GET().build();
                HttpResponse<byte[]> res = a.send(req, HttpResponse.BodyHandlers.ofByteArray());
                if (res.statusCode() != 200 || res.body() == null || res.body().length == 0) {
                    t.e = true;
                    return;
                }
                class_1011 img = K56.a(res.body());
                class_310.method_1551().execute(() -> K56.a(url, t, img));
            }
            catch (Exception e) {
                t.e = true;
            }
        }, "lethal-chat-asset");
        th.setDaemon(true);
        th.start();
    }

    private static class_1011 a(byte[] bytes) throws Exception {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
        if (bi == null) {
            throw new IOException("unsupported image");
        }
        int w = bi.getWidth();
        int h = bi.getHeight();
        class_1011 img = new class_1011(w, h, true);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                img.method_61941(x, y, bi.getRGB(x, y));
            }
        }
        return img;
    }

    private static void a(String url, C102 t, class_1011 img) {
        try {
            class_2960 loc = class_2960.method_60655((String)"lethalauction", (String)("chat/asset_" + c.incrementAndGet()));
            class_1043 tex = new class_1043(() -> "Lethal chat asset", img);
            class_310.method_1551().method_1531().method_4616(loc, (class_1044)tex);
            t.b = img.method_4307();
            t.c = img.method_4323();
            t.a = loc;
            t.d = false;
        }
        catch (Exception e) {
            t.e = true;
        }
    }

    private static final class C102 {
        volatile class_2960 a;
        volatile int b = 1;
        volatile int c = 1;
        volatile boolean d;
        volatile boolean e;

        private C102() {
        }
    }
}

