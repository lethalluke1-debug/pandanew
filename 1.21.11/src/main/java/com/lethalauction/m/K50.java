/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10789
 *  net.minecraft.class_1937
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_9799
 *  net.minecraft.class_9801
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.class_10789;
import net.minecraft.class_1937;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_9799;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public final class K50 {
    public static final List<C99> a = List.of(new C99("Northern Lights", 198932, 729651, 4063136, 9395199, 1.0f, 1.0f, 0.0f, 1.0f), new C99("Aurora Storm", 66828, 666158, 4980602, 3134463, 1.6f, 0.8f, 0.0f, 1.2f), new C99("Galaxy", 459546, 2297919, 13134847, 5213183, 0.0f, 1.6f, 1.0f, 1.0f), new C99("Crimson Aurora", 721680, 2755100, 16727406, 16757575, 1.2f, 1.0f, 0.0f, 1.0f), new C99("Sunset Dream", 1708358, 15231339, 16743129, 16761451, 0.5f, 0.5f, 0.0f, 0.8f), new C99("Deep Ocean", 68888, 670542, 58879, 4029439, 0.9f, 1.1f, 0.25f, 1.0f), new C99("Starfall", 131850, 857392, 12175103, 8031487, 0.0f, 2.0f, 0.4f, 3.0f), new C99("Cotton Candy", 2299471, 6962828, 16748512, 0x8FD8FF, 0.9f, 0.9f, 0.35f, 1.0f), new C99("Blood Moon", 655876, 2819592, 0xFF2A2A, 16742972, 0.8f, 0.7f, 0.0f, 0.6f), new C99("Nebula", 656160, 1707066, 16735200, 5999871, 0.0f, 1.8f, 1.6f, 1.0f), new C99("Emerald Veil", 135178, 467994, 2883486, 10288987, 1.5f, 1.0f, 0.0f, 1.0f), new C99("Golden Hour", 2364992, 15239756, 16761164, 16740272, 0.5f, 0.4f, 0.0f, 0.7f), new C99("Ice Veil", 199188, 664630, 8382719, 12560639, 1.3f, 1.2f, 0.2f, 1.0f), new C99("Vaporwave", 1706554, 16740272, 58879, 16743129, 0.7f, 0.6f, 0.4f, 0.8f));
    private static final RenderPipeline b = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withUniform("DynamicTransforms", class_10789.field_60031).withUniform("Projection", class_10789.field_60031).withLocation(class_2960.method_60655((String)"lethalauction", (String)"pipeline/sky")).withVertexShader(class_2960.method_60655((String)"lethalauction", (String)"core/lethal_sky")).withFragmentShader(class_2960.method_60655((String)"lethalauction", (String)"core/lethal_sky")).withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27382).build();
    private static GpuBuffer c;

    private K50() {
    }

    public static boolean a() {
        if (!K9.o || !K37.b()) {
            return false;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return false;
        }
        class_5321 dim = level.method_27983();
        return dim == class_1937.field_25179 || dim == class_1937.field_25180 || dim == class_1937.field_25181;
    }

    public static C99 b() {
        int i = K9.p;
        return a.get(i >= 0 && i < a.size() ? i : 0);
    }

    public static Vector4f c() {
        int c = K50.b().c();
        return new Vector4f((float)(c >> 16 & 0xFF) / 255.0f, (float)(c >> 8 & 0xFF) / 255.0f, (float)(c & 0xFF) / 255.0f, 1.0f);
    }

    public static void a(class_276 target) {
        if (c == null) {
            c = K50.d();
        }
        C99 ps = K50.b();
        float shooting = K9.q ? ps.i() : 0.0f;
        Matrix4f params = new Matrix4f(K50.a(ps.b()), K50.b(ps.b()), K50.c(ps.b()), ps.f(), K50.a(ps.c()), K50.b(ps.c()), K50.c(ps.c()), ps.g(), K50.a(ps.d()), K50.b(ps.d()), K50.c(ps.d()), ps.h(), K50.a(ps.e()), K50.b(ps.e()), K50.c(ps.e()), shooting);
        float time = (float)(System.currentTimeMillis() % 3600000L) / 1000.0f;
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)new Matrix4f((Matrix4fc)RenderSystem.getModelViewMatrix()), (Vector4fc)new Vector4f(time, 0.0f, 0.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)params);
        RenderSystem.class_5590 indices = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
        GpuBuffer indexBuffer = indices.method_68274(36);
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Lethal Client sky", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());){
            pass.setPipeline(b);
            RenderSystem.bindDefaultUniforms((RenderPass)pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setVertexBuffer(0, c);
            pass.setIndexBuffer(indexBuffer, indices.method_31924());
            pass.drawIndexed(0, 0, 36, 1);
        }
    }

    private static float a(int c) {
        return (float)(c >> 16 & 0xFF) / 255.0f;
    }

    private static float b(int c) {
        return (float)(c >> 8 & 0xFF) / 255.0f;
    }

    private static float c(int c) {
        return (float)(c & 0xFF) / 255.0f;
    }

    private static GpuBuffer d() {
        try (class_9799 bytes = class_9799.method_72201((int)(24 * class_290.field_1592.getVertexSize()));){
            GpuBuffer gpuBuffer;
            block20: {
                class_287 builder = new class_287(bytes, VertexFormat.class_5596.field_27382, class_290.field_1592);
                for (int i = 0; i < 6; ++i) {
                    Matrix4f pose = new Matrix4f();
                    switch (i) {
                        case 1: {
                            pose.rotationX(1.5707964f);
                            break;
                        }
                        case 2: {
                            pose.rotationX(-1.5707964f);
                            break;
                        }
                        case 3: {
                            pose.rotationX((float)Math.PI);
                            break;
                        }
                        case 4: {
                            pose.rotationZ(1.5707964f);
                            break;
                        }
                        case 5: {
                            pose.rotationZ(-1.5707964f);
                            break;
                        }
                    }
                    builder.method_22918((Matrix4fc)pose, -100.0f, -100.0f, -100.0f);
                    builder.method_22918((Matrix4fc)pose, -100.0f, -100.0f, 100.0f);
                    builder.method_22918((Matrix4fc)pose, 100.0f, -100.0f, 100.0f);
                    builder.method_22918((Matrix4fc)pose, 100.0f, -100.0f, -100.0f);
                }
                class_9801 mesh = builder.method_60800();
                try {
                    gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Lethal Client sky", 40, mesh.method_60818());
                    if (mesh == null) break block20;
                }
                catch (Throwable throwable) {
                    if (mesh != null) {
                        try {
                            mesh.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                mesh.close();
            }
            return gpuBuffer;
        }
    }

    public static final class C99 {
        private final String a;
        private final int b;
        private final int c;
        private final int d;
        private final int e;
        private final float f;
        private final float g;
        private final float h;
        private final float i;
        C99(String a, int b, int c, int d, int e, float f, float g, float h, float i) { this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; this.f = f; this.g = g; this.h = h; this.i = i; }
        public String a() { return this.a; }
        public int b() { return this.b; }
        public int c() { return this.c; }
        public int d() { return this.d; }
        public int e() { return this.e; }
        public float f() { return this.f; }
        public float g() { return this.g; }
        public float h() { return this.h; }
        public float i() { return this.i; }


    }
}

