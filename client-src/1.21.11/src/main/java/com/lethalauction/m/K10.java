/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.lethalauction.m;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import net.minecraft.class_2960;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

final class K10 {
    static final class_2960 a = class_2960.method_60655((String)"lethalauction", (String)"textures/figure/white.png");
    private static final String c = "/assets/lethalauction/figure/chair.obj";
    static final float b = 0.42f;
    private static float[] d;
    private static float[] e;
    private static float[] f;
    private static int[] g;
    private static boolean h;
    private static boolean i;

    private K10() {
    }

    static boolean a() {
        return i;
    }

    static void b() {
        if (h || i) {
            return;
        }
        try {
            K10.c();
            h = true;
        }
        catch (Throwable t) {
            i = true;
        }
    }

    private static void c() throws Exception {
        int i;
        ArrayList<float[]> verts = new ArrayList<float[]>();
        ArrayList<int[]> faces = new ArrayList<int[]>();
        try (InputStream in = K10.class.getResourceAsStream(c);
             BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));){
            String line;
            while ((line = r.readLine()) != null) {
                String[] s;
                if (line.startsWith("v ")) {
                    s = line.trim().split("\\s+");
                    verts.add(new float[]{Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])});
                    continue;
                }
                if (!line.startsWith("f ")) continue;
                s = line.trim().split("\\s+");
                int a = Integer.parseInt(s[1]) - 1;
                int b = Integer.parseInt(s[2]) - 1;
                int c = Integer.parseInt(s[3]) - 1;
                int rgb = s.length > 4 ? Integer.parseInt(s[4], 16) : 0xFFFFFF;
                faces.add(new int[]{a, b, c, rgb});
            }
        }
        d = new float[verts.size()];
        e = new float[verts.size()];
        f = new float[verts.size()];
        for (i = 0; i < verts.size(); ++i) {
            K10.d[i] = ((float[])verts.get(i))[0];
            K10.e[i] = ((float[])verts.get(i))[1];
            K10.f[i] = ((float[])verts.get(i))[2];
        }
        g = new int[faces.size() * 5];
        for (i = 0; i < faces.size(); ++i) {
            int[] f = (int[])faces.get(i);
            float cy = (e[f[0]] + e[f[1]] + e[f[2]]) / 3.0f;
            K10.g[i * 5] = f[0];
            K10.g[i * 5 + 1] = f[1];
            K10.g[i * 5 + 2] = f[2];
            K10.g[i * 5 + 3] = f[3];
            K10.g[i * 5 + 4] = cy < 0.42f ? 0 : 1;
        }
    }

    static void a(Matrix4f world, float leanRad, class_4588 vc) {
        K10.b();
        if (!h || g == null) {
            return;
        }
        Matrix4f seat = new Matrix4f((Matrix4fc)world);
        seat.translate(0.0f, 0.42f, 0.0f).rotateX(leanRad).translate(0.0f, -0.42f, 0.0f);
        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        Vector3f c = new Vector3f();
        Vector3f e1 = new Vector3f();
        Vector3f e2 = new Vector3f();
        Vector3f nrm = new Vector3f();
        for (int i = 0; i < g.length; i += 5) {
            int va = g[i];
            int vb = g[i + 1];
            int vcc = g[i + 2];
            int rgb = g[i + 3];
            if (va >= d.length || vb >= d.length || vcc >= d.length) continue;
            Matrix4f m = g[i + 4] == 1 ? seat : world;
            m.transformPosition(d[va], e[va], f[va], a);
            m.transformPosition(d[vb], e[vb], f[vb], b);
            m.transformPosition(d[vcc], e[vcc], f[vcc], c);
            b.sub((Vector3fc)a, e1);
            c.sub((Vector3fc)a, e2);
            e1.cross((Vector3fc)e2, nrm);
            if (nrm.lengthSquared() > 1.0E-8f) {
                nrm.normalize();
            } else {
                nrm.set(0.0f, 1.0f, 0.0f);
            }
            int cr = rgb >> 16 & 0xFF;
            int cg = rgb >> 8 & 0xFF;
            int cb = rgb & 0xFF;
            K10.a(vc, a, cr, cg, cb, nrm);
            K10.a(vc, b, cr, cg, cb, nrm);
            K10.a(vc, c, cr, cg, cb, nrm);
        }
    }

    private static void a(class_4588 vc, Vector3f p, int r, int g, int b, Vector3f n) {
        vc.method_22912(p.x, p.y, p.z).method_1336(r, g, b, 255).method_22913(0.5f, 0.5f).method_60796(0, 10).method_22921(240, 240).method_22914(n.x, n.y, n.z);
    }
}

