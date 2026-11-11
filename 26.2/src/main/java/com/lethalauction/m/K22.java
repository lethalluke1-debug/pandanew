/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.m.K18;
import com.lethalauction.m.K31;
import com.lethalauction.m.K34;
import com.lethalauction.m.K37;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public class K22
extends Screen {
    static final Identifier a = K18.a("icons_extra");
    static final int b = 60058;
    static final int c = 60656;
    static final int d = 60103;
    static final int e = 60130;
    static final int f = 59998;
    static final int g = 59910;
    static final int h = 61431;
    static final int i = -14715649;
    static final int j = -13992976;
    static final int k = -15916217;
    static final int l = -200997109;
    static final int m = -789258;
    static final int n = -6577748;
    static final int o = -10656654;
    static final int p = -42401;
    static final int q = -13315969;
    private static final long r = 1700L;
    private static final long s = 2300L;
    private static final long t = 15000L;
    private static final long u = 3000L;
    private static final long v = 1500L;
    private static final long w = 800L;
    private static final long x = 380L;
    private final Screen y;
    private final Supplier<Screen> z;
    private C84 A = C84.LOADING;
    private long B = System.currentTimeMillis();
    private final long C = System.currentTimeMillis();
    private final boolean D = !K37.p();
    private CompletableFuture<K37.C91> E;
    private boolean F;
    private final K31 G = new K31("", 40, c -> c >= 97 && c <= 122 || c >= 65 && c <= 90 || c >= 48 && c <= 57 || c == 45);
    private boolean H;
    private boolean I;
    private String J = "";
    private long K;
    private float L = 1.0f;
    private float M;
    private float N;
    private float O;
    private float P;
    private float Q;
    private float R;
    private final List<C85> S = new ArrayList<C85>();

    public K22(Screen parent, Supplier<Screen> onSuccess) {
        super((Component)Component.literal((String)"Lethal Client License"));
        this.y = parent;
        this.z = onSuccess;
    }

    protected void init() {
        if (this.E == null && this.A == C84.LOADING) {
            this.E = K37.t();
        }
    }

    public boolean shouldCloseOnEsc() {
        return this.A == C84.KEY && !this.I && !(this.y instanceof TitleScreen);
    }

    public void onClose() {
        this.minecraft.gui.setScreen(this.y);
    }

    public boolean isPauseScreen() {
        return false;
    }

    private long a() {
        return System.currentTimeMillis() - this.B;
    }

    private void a(C84 next) {
        this.A = next;
        this.B = System.currentTimeMillis();
    }

    public void tick() {
        long t = this.a();
        switch (this.A.ordinal()) {
            case 0: {
                boolean done = this.E == null || this.E.isDone();
                if (t < (this.D ? 2300L : 1700L) || !done && t <= 15000L) break;
                if (this.D) {
                    this.a(C84.INTRO);
                    break;
                }
                if (K37.a()) {
                    this.F = true;
                    this.a(C84.SUCCESS);
                    break;
                }
                this.b();
                break;
            }
            case 1: {
                if (t < 3000L) break;
                K37.q();
                if (K37.a()) {
                    this.a(C84.SUCCESS);
                    break;
                }
                this.b();
                break;
            }
            case 3: {
                if (t < (this.F ? 800L : 1500L)) break;
                this.a(C84.EXIT);
                break;
            }
            case 4: {
                if (t < 380L) break;
                this.minecraft.gui.setScreen(this.z != null ? this.z.get() : this.y);
                break;
            }
        }
    }

    private void b() {
        if (K37.g()) {
            this.minecraft.gui.setScreen((Screen)new K34());
            return;
        }
        K37.C92 s = K37.l();
        this.J = s == K37.C92.NONE || s == K37.C92.CHECKING ? "" : K37.m();
        this.G.b = K37.n();
        this.a(C84.KEY);
    }

    private void c() {
        if (this.I) {
            return;
        }
        this.I = true;
        this.J = "";
        K37.a(this.G.b).thenAccept(r -> this.minecraft.execute(() -> {
            this.I = false;
            if (r.a()) {
                K37.q();
                this.a(C84.SUCCESS);
            } else if (r.b() == K37.C92.OUTDATED) {
                this.minecraft.gui.setScreen((Screen)new K34());
            } else {
                this.J = r.c();
                this.K = System.currentTimeMillis();
            }
        }));
    }

    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float partialTick) {
        this.S.clear();
        float menuScale = Math.min((float)this.width * 0.564f / 1036.0f, (float)this.height * 0.94f / 518.0f);
        this.O = 1036.0f * menuScale;
        this.P = 518.0f * menuScale;
        this.M = ((float)this.width - this.O) / 2.0f;
        this.N = ((float)this.height - this.P) / 2.0f;
        this.L = Math.min(this.O / 620.0f, this.P / 330.0f);
        this.Q = ((float)mx - (float)this.width / 2.0f) / this.L;
        this.R = ((float)my - (float)this.height / 2.0f) / this.L;
        long t = this.a();
        float alpha = 1.0f;
        if (this.A == C84.EXIT) {
            alpha = 1.0f - K22.a(Math.min(1.0f, (float)t / 380.0f));
        }
        float open = K22.a(Math.min(1.0f, (float)(System.currentTimeMillis() - this.C) / 450.0f));
        K18.h = alpha * open;
        this.a(g);
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate((float)this.width / 2.0f, (float)this.height / 2.0f);
        p.scale(this.L);
        switch (this.A.ordinal()) {
            case 0: {
                this.a(g, t);
                break;
            }
            case 1: {
                this.b(g, t);
                break;
            }
            case 2: {
                this.c(g, t);
                break;
            }
            case 3: 
            case 4: {
                this.d(g, this.A == C84.EXIT ? 10000L : t);
            }
        }
        p.popMatrix();
        K18.h = 1.0f;
    }

    private void a(GuiGraphicsExtractor g) {
        int x = Math.round(this.M);
        int y = Math.round(this.N);
        int w = Math.round(this.O);
        int h = Math.round(this.P);
        int r = Math.max(4, Math.round(14.0f * this.O / 1036.0f));
        float pulse = this.A == C84.INTRO ? 0.5f + 0.5f * (float)Math.sin((double)this.a() / 260.0) : 0.0f;
        for (int i = 4; i >= 1; --i) {
            float a = (0.22f + 0.18f * pulse) / (float)i;
            K18.a(g, x - i, y - i, w + 2 * i, h + 2 * i, r + i, 0, K18.a(-14715649, a));
        }
        K18.a(g, x, y, w, h, r, -200997109, K18.a(-14715649, -1, 0.1f * pulse));
    }

    private void a(GuiGraphicsExtractor g, long t) {
        float in = K22.a(Math.min(1.0f, (float)t / 500.0f));
        K22.a(g, 0.0f, -26.0f, 22.0f, 1.0f);
        float rise = (1.0f - in) * 6.0f;
        float a = K18.h;
        K18.h = a * in;
        K18.b(g, "Hello", 0.0f, 38.0f + rise, 11.5f, -6577748, K18.b);
        K18.b(g, this.d(), 0.0f, 54.0f + rise, 11.5f, -13992976, K18.c);
        K18.h = a;
    }

    private void b(GuiGraphicsExtractor g, long t) {
        float a = K18.h;
        float out = K22.a(Math.min(1.0f, (float)t / 350.0f));
        if (out < 1.0f) {
            K18.h = a * (1.0f - out);
            K22.a(g, 0.0f, -26.0f, 22.0f * (1.0f - out * 0.6f), 1.0f);
            K18.b(g, "Hello", 0.0f, 38.0f, 11.5f, -6577748, K18.b);
            K18.b(g, this.d(), 0.0f, 54.0f, 11.5f, -13992976, K18.c);
        }
        float leave = K22.a(K22.b((float)(t - 2550L) / 450.0f));
        float lift = -leave * 30.0f;
        float fade = 1.0f - leave;
        String logo = "LETHAL CLIENT";
        float size = 22.0f;
        float total = K18.a(logo, size, K18.d);
        float x = -total / 2.0f;
        for (int i = 0; i < logo.length(); ++i) {
            String ch = logo.substring(i, i + 1);
            float k = K22.a(K22.b((float)(t - 300L - (long)i * 55L) / 380.0f));
            K18.h = a * k * fade;
            K18.a(g, ch, x, -14.0f + (1.0f - k) * -14.0f + lift, size, -789258, K18.d);
            x += K18.a(ch, size, K18.d);
        }
        float line = K22.a(K22.b((float)(t - 1000L) / 600.0f));
        if (line > 0.0f) {
            int half = Math.round(total / 2.0f * line);
            K18.h = a * fade;
            K18.d(g, -half, Math.round(8.0f + lift), half, 2, K18.a(-14715649, 0.0f), -14715649);
            K18.d(g, 0, Math.round(8.0f + lift), half, 2, -14715649, K18.a(-14715649, 0.0f));
            float sweep = K22.b((float)(t - 1000L) / 900.0f);
            float sx = -total / 2.0f + total * sweep;
            K18.h = a * fade * (1.0f - sweep);
            K18.a(g, sx, 9.0f + lift, 3.0f, -4204801);
        }
        float sub = K22.a(K22.b((float)(t - 1450L) / 500.0f));
        K18.h = a * sub * fade;
        K18.b(g, "Welcome, " + this.d(), 0.0f, 30.0f + (1.0f - sub) * 6.0f + lift, 11.5f, -6577748, K18.b);
        K18.h = a;
    }

    private void c(GuiGraphicsExtractor g, long t) {
        boolean hover;
        float a = K18.h;
        float in = K22.a(Math.min(1.0f, (float)t / 450.0f));
        K18.h = a * in;
        float dy = (1.0f - in) * 24.0f;
        int top = Math.round(-98.0f + dy);
        K18.a(g, 0.0f, top + 18, 18.0f, K18.a(-14715649, 0.16f));
        K18.b(g, K22.a(60103), 0.0f, (float)(top + 18), 18.0f, -13992976, K22.a);
        K18.b(g, "Enter your license key", 0.0f, (float)(top + 56), 15.0f, -789258, K18.c);
        K18.b(g, "You only have to do this once on this PC.", 0.0f, (float)(top + 74), 10.5f, -6577748, K18.a);
        int shake = 0;
        long since = System.currentTimeMillis() - this.K;
        if (since < 420L) {
            shake = Math.round((float)Math.sin((double)since / 22.0) * 7.0f * (1.0f - (float)since / 420.0f));
        }
        int fw = 300;
        int fx = -fw / 2 + shake;
        int fy = top + 92;
        boolean bad = !this.J.isEmpty();
        K22.a(g, this.G, fx, fy, fw, 34, this.H, true, bad ? -42401 : -14715649, "LETHAL-XXXX-XXXX-XXXX-XXXX", () -> {
            this.H = !this.H;
        }, this.S);
        int by = fy + 46;
        boolean bl = hover = this.Q >= (float)(-fw / 2) && this.Q < (float)(fw / 2) && this.R >= (float)by && this.R < (float)(by + 32);
        int fill = this.I ? K18.a(-14715649, -16777216, 0.35f) : (hover ? K18.a(-14715649, -1, 0.12f) : -14715649);
        K18.a(g, -fw / 2, by, fw, 32, 8, fill);
        if (this.I) {
            K22.a(g, -40.0f, by + 16, 6.0f, 1.0f, -1, 0x30FFFFFF);
            K18.a(g, "Checking...", -28.0f, (float)by + 16.5f, 12.5f, -789258, K18.c);
        } else {
            K18.b(g, "Activate", 0.0f, (float)by + 16.5f, 12.5f, -789258, K18.c);
            this.S.add(new C85(-fw / 2, by, fw, 32, this::c));
        }
        if (bad) {
            float w = K18.a(this.J, 10.5f, K18.b);
            K18.b(g, K22.a(59910), -w / 2.0f - 9.0f, (float)(by + 52), 11.0f, -42401, K22.a);
            K18.a(g, this.J, -w / 2.0f + 2.0f, (float)(by + 52), 10.5f, -42401, K18.b);
        }
        float bottom = this.P / 2.0f / this.L - 22.0f;
        boolean title = this.y instanceof TitleScreen;
        String quit = title ? "Quit Game" : "Back to game";
        float qw = K18.a(quit, 10.5f, K18.b);
        boolean qh = Math.abs(this.Q) < qw / 2.0f + 4.0f && Math.abs(this.R - bottom) < 9.0f;
        K18.b(g, quit, 0.0f, bottom, 10.5f, qh ? -789258 : -10656654, K18.b);
        this.S.add(new C85(Math.round(-qw / 2.0f - 4.0f), Math.round(bottom - 9.0f), Math.round(qw + 8.0f), 18, () -> {
            if (title) {
                this.minecraft.stop();
            } else if (!this.I) {
                this.onClose();
            }
        }));
        K18.h = a;
    }

    private void d(GuiGraphicsExtractor g, long t) {
        float a = K18.h;
        float pop = K22.b((float)t / 420.0f);
        float s = pop < 0.7f ? K22.a(pop / 0.7f) * 1.12f : 1.12f - 0.12f * K22.a((pop - 0.7f) / 0.3f);
        float ring = K22.b((float)t / 700.0f);
        K18.h = a * (1.0f - ring) * 0.6f;
        K18.a(g, 0.0f, -20.0f, 24.0f + ring * 26.0f, K18.a(-14715649, 0.35f));
        K18.h = a;
        K18.a(g, 0.0f, -20.0f, 24.0f * s, -14715649);
        K18.b(g, K22.a(59998), 0.0f, -20.0f, 26.0f * s, -789258, K22.a);
        float txt = K22.a(K22.b((float)(t - 200L) / 400.0f));
        K18.h = a * txt;
        K18.b(g, this.F ? "Welcome back" : "Access granted", 0.0f, 24.0f + (1.0f - txt) * 6.0f, 14.0f, -789258, K18.c);
        K18.b(g, this.d(), 0.0f, 42.0f + (1.0f - txt) * 6.0f, 11.5f, -13992976, K18.c);
        K18.h = a;
    }

    static void a(GuiGraphicsExtractor g, K31 box, int x, int y, int w, int h, boolean visible, boolean focused, int accent, String placeholder, Runnable toggleEye, List<C85> spots) {
        String shown;
        K18.a(g, x, y, w, h, 8, -16250094, focused ? accent : -14735560);
        float cy = (float)y + (float)h / 2.0f;
        float maxW = w - 50;
        if (box.b.isEmpty()) {
            K18.a(g, placeholder, (float)(x + 12), cy, 12.0f, -10656654, K18.b);
        }
        String string = shown = visible ? box.b : "\u2022".repeat(box.b.length());
        while (!shown.isEmpty() && K18.a(shown, 13.0f, K18.b) > maxW) {
            shown = shown.substring(1);
        }
        if (focused && box.c && !shown.isEmpty()) {
            K18.a(g, x + 10, Math.round(cy - 9.0f), Math.round(K18.a(shown, 13.0f, K18.b) + 4.0f), 18, 3, K18.a(accent, 0.45f));
        }
        if (!shown.isEmpty()) {
            K18.a(g, shown, (float)(x + 12), cy, 13.0f, -789258, K18.b);
        }
        if (focused && !box.c && System.currentTimeMillis() / 500L % 2L == 0L) {
            int cx = Math.round((float)(x + 13) + K18.a(shown, 13.0f, K18.b));
            K18.a(g, cx, Math.round(cy - 7.0f), 1, 14, -789258);
        }
        int ex = x + w - 22;
        K18.b(g, K22.a(visible ? 60656 : 60058), (float)ex, cy, 15.0f, visible ? accent : -6577748, a);
        spots.add(new C85(ex - 14, y, 28, h, toggleEye));
    }

    static void a(GuiGraphicsExtractor g, float cx, float cy, float r, float alpha) {
        K22.a(g, cx, cy, r, alpha, -14715649, -15916217);
    }

    static void a(GuiGraphicsExtractor g, float cx, float cy, float r, float alpha, int color, int track) {
        float dot = Math.max(1.2f, r * 0.13f);
        int steps = Math.max(24, Math.round(r * 4.0f));
        for (int i = 0; i < steps; ++i) {
            double ang = Math.PI * 2 * (double)i / (double)steps;
            K18.a(g, cx + (float)Math.cos(ang) * r, cy + (float)Math.sin(ang) * r, dot, K18.a(track, alpha));
        }
        double head = (double)(System.currentTimeMillis() % 950L) / 950.0 * Math.PI * 2.0 - 1.5707963267948966;
        int arc = Math.round((float)steps * 0.3f);
        for (int i = 0; i < arc; ++i) {
            double ang = head - Math.PI * 2 * (double)i / (double)steps;
            float k = 1.0f - (float)i / (float)arc;
            K18.a(g, cx + (float)Math.cos(ang) * r, cy + (float)Math.sin(ang) * r, dot, K18.a(color, alpha * (0.15f + 0.85f * k)));
        }
    }

    static String a(int codepoint) {
        return new String(Character.toChars(codepoint));
    }

    private String d() {
        return this.minecraft.getUser().getName();
    }

    private static float a(float k) {
        return 1.0f - (float)Math.pow(1.0f - k, 3.0);
    }

    private static float b(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return true;
        }
        float x = (float)((event.x() - (double)((float)this.width / 2.0f)) / (double)this.L);
        float y = (float)((event.y() - (double)((float)this.height / 2.0f)) / (double)this.L);
        for (C85 s : List.copyOf(this.S)) {
            if (!s.a(x, y)) continue;
            s.e().run();
            LethalAuctionClient.playClick();
            return true;
        }
        return true;
    }

    public boolean charTyped(CharacterEvent event) {
        if (this.A == C84.KEY && !this.I && this.G.a(event.codepointAsString())) {
            this.J = "";
        }
        return true;
    }

    public boolean keyPressed(KeyEvent event) {
        if (this.A != C84.KEY || this.I) {
            return true;
        }
        if (event.isEscape()) {
            return super.keyPressed(event);
        }
        int key = event.key();
        if (event.isConfirmation()) {
            this.c();
        } else if (key == 259) {
            this.G.a(event.hasControlDown());
        } else if (key == 261) {
            if (this.G.c) {
                this.G.a(true);
            }
        } else if (event.isSelectAll()) {
            this.G.c = !this.G.b.isEmpty();
        } else if (event.isPaste()) {
            this.G.a(this.minecraft.keyboardHandler.getClipboard().trim());
            this.J = "";
        } else if (!(event.isCopy() || event.isCut() || key != 263 && key != 262 && key != 268 && key != 269)) {
            this.G.c = false;
        }
        return true;
    }

    private static enum C84 {
        LOADING,
        INTRO,
        KEY,
        SUCCESS,
        EXIT;

    }

    static final class C85 {
        private final int a;
        private final int b;
        private final int c;
        private final int d;
        private final Runnable e;
        C85(int a, int b, int c, int d, Runnable e) { this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; }
        public int a() { return this.a; }
        public int b() { return this.b; }
        public int c() { return this.c; }
        public int d() { return this.d; }
        public Runnable e() { return this.e; }
        boolean a(float px, float py) {
            return px >= (float)this.a && px < (float)(this.a + this.c) && py >= (float)this.b && py < (float)(this.b + this.d);
        }



    }
}

