package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.List;
import org.joml.Vector4f;

/**
 * Custom aurora skybox. Minecraft 26.3 replaced the GPU render pipeline with a
 * new backend (renderpearl), so the custom sky shader is disabled on this
 * version - the theme list and UI stay, but no custom sky is drawn (vanilla sky
 * renders). The 26.2 and 1.21.11 builds keep the full effect.
 */
public final class K50 {
    public static final List<C100> a = List.of(new C100("Northern Lights", 198932, 729651, 4063136, 9395199, 1.0f, 1.0f, 0.0f, 1.0f), new C100("Aurora Storm", 66828, 666158, 4980602, 3134463, 1.6f, 0.8f, 0.0f, 1.2f), new C100("Galaxy", 459546, 2297919, 13134847, 5213183, 0.0f, 1.6f, 1.0f, 1.0f), new C100("Crimson Aurora", 721680, 2755100, 16727406, 16757575, 1.2f, 1.0f, 0.0f, 1.0f), new C100("Sunset Dream", 1708358, 15231339, 16743129, 16761451, 0.5f, 0.5f, 0.0f, 0.8f), new C100("Deep Ocean", 68888, 670542, 58879, 4029439, 0.9f, 1.1f, 0.25f, 1.0f), new C100("Starfall", 131850, 857392, 12175103, 8031487, 0.0f, 2.0f, 0.4f, 3.0f), new C100("Cotton Candy", 2299471, 6962828, 16748512, 0x8FD8FF, 0.9f, 0.9f, 0.35f, 1.0f), new C100("Blood Moon", 655876, 2819592, 0xFF2A2A, 16742972, 0.8f, 0.7f, 0.0f, 0.6f), new C100("Nebula", 656160, 1707066, 16735200, 5999871, 0.0f, 1.8f, 1.6f, 1.0f), new C100("Emerald Veil", 135178, 467994, 2883486, 10288987, 1.5f, 1.0f, 0.0f, 1.0f), new C100("Golden Hour", 2364992, 15239756, 16761164, 16740272, 0.5f, 0.4f, 0.0f, 0.7f), new C100("Ice Veil", 199188, 664630, 8382719, 12560639, 1.3f, 1.2f, 0.2f, 1.0f), new C100("Vaporwave", 1706554, 16740272, 58879, 16743129, 0.7f, 0.6f, 0.4f, 0.8f));

    private K50() {
    }

    public static boolean a() {
        // Custom sky rendering is unavailable on 26.3's new render backend.
        return false;
    }

    public static C100 b() {
        int i = K9.p;
        return a.get(i >= 0 && i < a.size() ? i : 0);
    }

    public static Vector4f c() {
        int c = K50.b().c();
        return new Vector4f((float)(c >> 16 & 0xFF) / 255.0f, (float)(c >> 8 & 0xFF) / 255.0f, (float)(c & 0xFF) / 255.0f, 1.0f);
    }

    public static void a(RenderTarget target) {
        // no-op: custom sky disabled on 26.3
    }

    public static final class C100 {
        private final String a;
        private final int b;
        private final int c;
        private final int d;
        private final int e;
        private final float f;
        private final float g;
        private final float h;
        private final float i;
        C100(String a, int b, int c, int d, int e, float f, float g, float h, float i) { this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; this.f = f; this.g = g; this.h = h; this.i = i; }
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
