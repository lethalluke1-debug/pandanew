/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 */
package com.lethalauction.m;

import com.mojang.blaze3d.platform.NativeImage;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class K57 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
    private static final int b = 400;
    private static final AtomicInteger c = new AtomicInteger();
    private static final Map<String, C107> d = new ConcurrentHashMap<String, C107>();

    private K57() {
    }

    public static Identifier a(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        C107 t = d.get(url);
        if (t == null) {
            if (d.size() >= 400) {
                return null;
            }
            t = new C107();
            C107 prev = d.putIfAbsent(url, t);
            if (prev == null) {
                K57.a(url, t);
            } else {
                t = prev;
            }
        }
        return t.e ? null : t.a;
    }

    public static boolean b(String url) {
        C107 t = url == null ? null : d.get(url);
        return t != null && t.e;
    }

    public static int c(String url) {
        C107 t = d.get(url);
        return t == null ? 1 : t.b;
    }

    public static int d(String url) {
        C107 t = d.get(url);
        return t == null ? 1 : t.c;
    }

    private static void a(String url, C107 t) {
        t.d = true;
        Thread th = new Thread(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(12L)).header("User-Agent", "LethalClient").GET().build();
                HttpResponse<byte[]> res = a.send(req, HttpResponse.BodyHandlers.ofByteArray());
                if (res.statusCode() != 200 || res.body() == null || res.body().length == 0) {
                    t.e = true;
                    return;
                }
                NativeImage img = K57.a(res.body());
                Minecraft.getInstance().execute(() -> K57.a(url, t, img));
            }
            catch (Exception e) {
                t.e = true;
            }
        }, "lethal-chat-asset");
        th.setDaemon(true);
        th.start();
    }

    private static NativeImage a(byte[] bytes) throws Exception {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
        if (bi == null) {
            throw new IOException("unsupported image");
        }
        int w = bi.getWidth();
        int h = bi.getHeight();
        NativeImage img = new NativeImage(w, h, true);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                img.setPixel(x, y, bi.getRGB(x, y));
            }
        }
        return img;
    }

    private static void a(String url, C107 t, NativeImage img) {
        try {
            Identifier loc = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)("chat/asset_" + c.incrementAndGet()));
            DynamicTexture tex = new DynamicTexture(() -> "Lethal chat asset", img);
            Minecraft.getInstance().getTextureManager().register(loc, (AbstractTexture)tex);
            t.b = img.getWidth();
            t.c = img.getHeight();
            t.a = loc;
            t.d = false;
        }
        catch (Exception e) {
            t.e = true;
        }
    }

    private static final class C107 {
        volatile Identifier a;
        volatile int b = 1;
        volatile int c = 1;
        volatile boolean d;
        volatile boolean e;

        private C107() {
        }
    }
}

