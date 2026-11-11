/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.rendertype.RenderType
 */
package com.lethalauction.m;

import com.lethalauction.m.K52;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class K51
implements K52.C103 {
    public static final K51 a = new K51();
    private final List<C102> b = new ArrayList<C102>();
    private final Map<RenderType, C101> c = new HashMap<RenderType, C101>();

    @Override
    public VertexConsumer a(RenderType type) {
        C101 rec = this.c.get(type);
        if (rec == null) {
            rec = new C101();
            this.c.put(type, rec);
            this.b.add(new C102(type, rec));
        }
        return rec;
    }

    @Override
    public void b(RenderType type) {
        this.c.remove(type);
    }

    public void a(SubmitNodeCollector collector) {
        PoseStack poseStack = new PoseStack();
        for (C102 seg : this.b) {
            if (seg.b().a == 0) continue;
            C101 rec = seg.b().a();
            collector.submitCustomGeometry(poseStack, seg.a(), (pose, vc) -> rec.a(vc));
        }
        this.b.clear();
        this.c.clear();
    }

    static final class C101
    implements VertexConsumer {
        int a = 0;
        int b = 0;
        float[] c = new float[704];
        int[] d = new int[192];

        C101() {
        }

        private int b() {
            return (this.a - 1) * 11;
        }

        private int c() {
            return (this.a - 1) * 3;
        }

        C101 a() {
            C101 r = new C101();
            r.a = this.a;
            r.b = this.b;
            r.c = Arrays.copyOf(this.c, this.a * 11);
            r.d = Arrays.copyOf(this.d, this.a * 3);
            return r;
        }

        void a(VertexConsumer vc) {
            for (int k = 0; k < this.a; ++k) {
                int a = k * 11;
                int b = k * 3;
                vc.addVertex(this.c[a], this.c[a + 1], this.c[a + 2]);
                if ((this.b & 2) != 0) {
                    vc.setColor(this.d[b]);
                }
                if ((this.b & 4) != 0) {
                    vc.setUv(this.c[a + 3], this.c[a + 4]);
                }
                if ((this.b & 8) != 0) {
                    vc.setUv1(this.d[b + 1] & 0xFFFF, this.d[b + 1] >>> 16);
                }
                if ((this.b & 0x10) != 0) {
                    vc.setUv2(this.d[b + 2] & 0xFFFF, this.d[b + 2] >>> 16);
                }
                if ((this.b & 0x20) != 0) {
                    vc.setNormal(this.c[a + 5], this.c[a + 6], this.c[a + 7]);
                }
                if ((this.b & 0x40) == 0) continue;
                vc.setLineWidth(this.c[a + 8]);
            }
        }

        public VertexConsumer addVertex(float x, float y, float z) {
            ++this.a;
            if (this.a * 11 > this.c.length) {
                this.c = Arrays.copyOf(this.c, this.c.length * 2);
            }
            if (this.a * 3 > this.d.length) {
                this.d = Arrays.copyOf(this.d, this.d.length * 2);
            }
            int o = this.b();
            this.c[o] = x;
            this.c[o + 1] = y;
            this.c[o + 2] = z;
            this.b |= 1;
            return this;
        }

        public VertexConsumer setColor(int r, int g, int b, int a) {
            return this.setColor((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
        }

        public VertexConsumer setColor(int argb) {
            this.d[this.c()] = argb;
            this.b |= 2;
            return this;
        }

        public VertexConsumer setUv(float u, float v) {
            int o = this.b();
            this.c[o + 3] = u;
            this.c[o + 4] = v;
            this.b |= 4;
            return this;
        }

        public VertexConsumer setUv1(int u, int v) {
            this.d[this.c() + 1] = u & 0xFFFF | v << 16;
            this.b |= 8;
            return this;
        }

        public VertexConsumer setUv2(int u, int v) {
            this.d[this.c() + 2] = u & 0xFFFF | v << 16;
            this.b |= 0x10;
            return this;
        }

        public VertexConsumer setNormal(float x, float y, float z) {
            int o = this.b();
            this.c[o + 5] = x;
            this.c[o + 6] = y;
            this.c[o + 7] = z;
            this.b |= 0x20;
            return this;
        }

        public VertexConsumer setLineWidth(float w) {
            this.c[this.b() + 8] = w;
            this.b |= 0x40;
            return this;
        }
    }

    private static final class C102 {
        private final RenderType a;
        private final C101 b;
        C102(RenderType a, C101 b) { this.a = a; this.b = b; }
        public RenderType a() { return this.a; }
        public C101 b() { return this.b; }


    }
}

