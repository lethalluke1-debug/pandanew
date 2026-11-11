/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.lethalauction.m;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

final class K11 {
    static final Identifier a = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"textures/figure/parachute.png");
    private static final String b = "/assets/lethalauction/figure/parachute.obj";
    private static float[] c;
    private static float[] d;
    private static float[] e;
    private static float[] f;
    private static float[] g;
    private static int[] h;
    private static boolean i;
    private static boolean j;

    private K11() {
    }

    static boolean a() {
        return j;
    }

    static void b() {
        if (i || j) {
            return;
        }
        try {
            K11.c();
            i = true;
        }
        catch (Throwable t) {
            j = true;
        }
    }

    private static void c() throws Exception {
        int i;
        ArrayList<float[]> verts = new ArrayList<float[]>();
        ArrayList<float[]> uvs = new ArrayList<float[]>();
        ArrayList<int[]> faces = new ArrayList<int[]>();
        try (InputStream in = K11.class.getResourceAsStream(b);
             BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));){
            String line;
            while ((line = r.readLine()) != null) {
                int i2;
                String[] s;
                if (line.startsWith("v ")) {
                    s = line.trim().split("\\s+");
                    verts.add(new float[]{Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])});
                    continue;
                }
                if (line.startsWith("vt ")) {
                    s = line.trim().split("\\s+");
                    uvs.add(new float[]{Float.parseFloat(s[1]), 1.0f - Float.parseFloat(s[2])});
                    continue;
                }
                if (!line.startsWith("f ")) continue;
                s = line.trim().split("\\s+");
                int n = s.length - 1;
                int[] vi = new int[n];
                int[] ui = new int[n];
                for (i2 = 0; i2 < n; ++i2) {
                    String[] parts = s[i2 + 1].split("/");
                    vi[i2] = Integer.parseInt(parts[0]) - 1;
                    ui[i2] = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) - 1 : 0;
                }
                for (i2 = 1; i2 < n - 1; ++i2) {
                    faces.add(new int[]{vi[0], ui[0], vi[i2], ui[i2], vi[i2 + 1], ui[i2 + 1]});
                }
            }
        }
        c = new float[verts.size()];
        d = new float[verts.size()];
        e = new float[verts.size()];
        for (i = 0; i < verts.size(); ++i) {
            K11.c[i] = ((float[])verts.get(i))[0];
            K11.d[i] = ((float[])verts.get(i))[1];
            K11.e[i] = ((float[])verts.get(i))[2];
        }
        f = new float[uvs.size()];
        g = new float[uvs.size()];
        for (i = 0; i < uvs.size(); ++i) {
            K11.f[i] = ((float[])uvs.get(i))[0];
            K11.g[i] = ((float[])uvs.get(i))[1];
        }
        h = new int[faces.size() * 6];
        for (i = 0; i < faces.size(); ++i) {
            System.arraycopy(faces.get(i), 0, h, i * 6, 6);
        }
    }

    static void a(Matrix4f m, VertexConsumer vc) {
        K11.b();
        if (!i || h == null) {
            return;
        }
        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        Vector3f c = new Vector3f();
        Vector3f e1 = new Vector3f();
        Vector3f e2 = new Vector3f();
        Vector3f nrm = new Vector3f();
        for (int i = 0; i < h.length; i += 6) {
            int va = h[i];
            int ua = h[i + 1];
            int vb = h[i + 2];
            int ub = h[i + 3];
            int vcc = h[i + 4];
            int uc = h[i + 5];
            if (va >= K11.c.length || vb >= K11.c.length || vcc >= K11.c.length) continue;
            m.transformPosition(K11.c[va], d[va], e[va], a);
            m.transformPosition(K11.c[vb], d[vb], e[vb], b);
            m.transformPosition(K11.c[vcc], d[vcc], e[vcc], c);
            b.sub((Vector3fc)a, e1);
            c.sub((Vector3fc)a, e2);
            e1.cross((Vector3fc)e2, nrm);
            if (nrm.lengthSquared() > 1.0E-8f) {
                nrm.normalize();
            } else {
                nrm.set(0.0f, 1.0f, 0.0f);
            }
            K11.a(vc, a, f[ua], g[ua], nrm);
            K11.a(vc, b, f[ub], g[ub], nrm);
            K11.a(vc, c, f[uc], g[uc], nrm);
        }
    }

    private static void a(VertexConsumer vc, Vector3f p, float u, float v, Vector3f n) {
        vc.addVertex(p.x, p.y, p.z).setColor(255, 255, 255, 255).setUv(u, v).setUv1(0, 10).setUv2(240, 240).setNormal(n.x, n.y, n.z);
    }
}

