/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.class_12249
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1890
 *  net.minecraft.class_1893
 *  net.minecraft.class_1921
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_3532
 *  net.minecraft.class_3965
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  net.minecraft.class_5602
 *  net.minecraft.class_591
 *  net.minecraft.class_6880
 *  net.minecraft.class_6880$class_6883
 *  net.minecraft.class_746
 *  net.minecraft.class_7833
 *  net.minecraft.class_7923
 *  net.minecraft.class_7924
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.lethalauction.m;

import com.lethalauction.m.K10;
import com.lethalauction.m.K11;
import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K51;
import com.lethalauction.m.K9;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.class_12249;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1921;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_5602;
import net.minecraft.class_591;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_7833;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class K12 {
    public static final List<C79> a = List.of(new C79("creator", "Creator Pack", List.of(new C78("dorzeey", "Dorzeey", K12.e("dorzeey")), new C78("speedingduck", "SpeedingDuck", K12.e("speedingduck")), new C78("sixxpain", "6ixxpain", K12.e("sixxpain")), new C78("agentbtw", "AgentBtw", K12.e("agentbtw")))), new C79("donut", "Donut Pack", List.of(new C78("drdonut", "DrDonut", K12.e("drdonut")), new C78("farex", "Farex", K12.e("farex")), new C78("jettism", "Jettism", K12.e("jettism")), new C78("w1zox", "W1zox", K12.e("w1zox")), new C78("jestic", "Jestic", K12.e("jestic")), new C78("nolimitdash", "NoLimitDash", K12.e("nolimitdash")), new C78("truezay", "TrueZay", K12.e("truezay")), new C78("lethallukeyt", "LethalLukeYT", K12.e("lethallukeyt")))), new C79("pokemon", "Pokemon Pack", List.of(new C78("pikachu", "Pikachu", K12.e("pikachu")), new C78("charmander", "Charmander", K12.e("charmander")), new C78("bulbasaur", "Bulbasaur", K12.e("bulbasaur")), new C78("squirtle", "Squirtle", K12.e("squirtle")), new C78("snorlax", "Snorlax", K12.e("snorlax")), new C78("ashketchum", "Ash Ketchum", K12.e("ashketchum")), new C78("gengar", "Gengar", K12.e("gengar")), new C78("mew", "Mew", K12.e("mew")))), new C79("steamer", "Steamer Pack", List.of(new C78("stableronaldo", "StableRonaldo", K12.e("stableronaldo")), new C78("jynxzi", "Jynxzi", K12.e("jynxzi")), new C78("marlon", "Marlon", K12.e("marlon")), new C78("lacy", "Lacy", K12.e("lacy")), new C78("jasontheween", "JasonTheWeen", K12.e("jasontheween")), new C78("ishowspeed", "IShowSpeed", K12.e("ishowspeed")), new C78("lospollos", "LosPollos", K12.e("lospollos")), new C78("adapt", "Adapt", K12.e("adapt")))), new C79("nintendo", "Nintendo Pack", List.of(new C78("mario", "Mario", K12.e("mario")), new C78("luigi", "Luigi", K12.e("luigi")), new C78("bowser", "Bowser", K12.e("bowser")), new C78("rosalina", "Rosalina", K12.e("rosalina")), new C78("yoshi", "Yoshi", K12.e("yoshi")), new C78("shyguy", "ShyGuy", K12.e("shyguy")), new C78("kirby", "Kirby", K12.e("kirby")), new C78("zelda", "Zelda", K12.e("zelda")))), new C79("sml", "SML Pack", List.of(new C78("jeffy", "Jeffy", K12.e("jeffy")), new C78("marvin", "Marvin", K12.e("marvin")), new C78("cody", "Cody", K12.e("cody")), new C78("joseph", "Joseph", K12.e("joseph")), new C78("brooklynguy", "Brooklyn Guy", K12.e("brooklynguy")), new C78("junior", "Junior", K12.e("junior")), new C78("mrgoodman", "Mr GoodMen", K12.e("mrgoodman")), new C78("chefpp", "Chef PP", K12.e("chefpp")))), new C79("horror", "Horror Pack", List.of(new C78("freddy", "Freddy", K12.e("freddy")), new C78("ghostface", "GhostFace", K12.e("ghostface")), new C78("helloneighbor", "Hello Neighbor", K12.e("helloneighbor")), new C78("granny", "Granny", K12.e("granny")), new C78("jason", "Jason", K12.e("jason")), new C78("chucky", "Chucky", K12.e("chucky")), new C78("michaelmyers", "Michael Myers", K12.e("michaelmyers")), new C78("bendy", "Bendy", K12.e("bendy")))), new C79("movie", "Movie Pack", List.of(new C78("harrypotter", "Harry Potter", K12.e("harrypotter")), new C78("darthvader", "Darth Vader", K12.e("darthvader")), new C78("patrickjane", "Patrick Jane", K12.e("patrickjane")), new C78("lightningmcqueen", "Lightning McQueen", K12.e("lightningmcqueen")), new C78("shrek", "Shrek", K12.e("shrek")), new C78("spongebob", "SpongeBob", K12.e("spongebob")), new C78("dextermorgan", "Dexter Morgan", K12.e("dextermorgan")), new C78("angrybird", "Angry Bird", K12.e("angrybird")))), new C79("sports", "Sports Pack", List.of(new C78("cristianoronaldo", "Cristiano Ronaldo", K12.e("cristianoronaldo")), new C78("lionelmessi", "Lionel Messi", K12.e("lionelmessi")), new C78("lebronjames", "LeBron James", K12.e("lebronjames")), new C78("kobebryant", "Kobe Bryant", K12.e("kobebryant")), new C78("harrykane", "Harry Kane", K12.e("harrykane")), new C78("michaeljordan", "Michael Jordan", K12.e("michaeljordan")), new C78("connormcdavid", "Connor McDavid", K12.e("connormcdavid")), new C78("stephcurry", "Steph Curry", K12.e("stephcurry")), new C78("tombrady", "Tom Brady", K12.e("tombrady")), new C78("waynegretzky", "Wayne Gretzky", K12.e("waynegretzky")))), new C79("animal", "Animal Pack", List.of(new C78("dog", "Dog", K12.e("dog")), new C78("cat", "Cat", K12.e("cat")), new C78("wolf", "Wolf", K12.e("wolf")), new C78("bee", "Bee", K12.e("bee")), new C78("dolphin", "Dolphin", K12.e("dolphin")), new C78("turtle", "Turtle", K12.e("turtle")), new C78("chick", "Chick", K12.e("chick")), new C78("mouse", "Mouse", K12.e("mouse")))));
    public static final List<C78> b = a.stream().flatMap(p -> p.c().stream()).toList();
    public static final List<C76> cars = List.of(
        new C76("kart", "Go-Kart", 0xE53935, 0xFFEB3B, 0x222222, 0),
        new C76("sport", "Sports Car", 0x1E88E5, 0xFFFFFF, 0x15151A, 1),
        new C76("truck", "Monster Truck", 0x43A047, 0xFF7043, 0x2E2E2E, 2),
        new C76("retro", "Convertible", 0xEC407A, 0xFFF176, 0x5D4037, 3),
        new C76("racer", "Flame Racer", 0xFF6F00, 0x212121, 0x111111, 1),
        new C76("police", "Police Cruiser", 0x1565C0, 0xFFFFFF, 0x0D0D0D, 1));
    private static final float e = 0.55f;
    private static final double f = 1.3;
    private static final double g = 0.18;
    private static final double h = 1.2;
    private static final double i = 2.5;
    private static final long j = 480L;
    private static final float k = 432.0f;
    private static final long l = 8000L;
    private static final long m = 1500L;
    private static final long n = 15000L;
    private static final long o = 700L;
    private static final double p = 2.5;
    private static final float q = 1.0f;
    private static final float r = 1.6f;
    private static final float s = 0.09f;
    private static final float t = -0.1f;
    private static final K51 u = new K51();
    private static class_591 v;
    private static long w;
    private static final C77 x;
    private static final Map<UUID, C77> y;
    private static class_2338 z;
    public static final int[] c;
    public static final boolean d = false;
    private static final long A = 1900L;
    private static final long B = 450L;
    private static final float C = 0.06f;
    private static final int[][] D;
    private static final int[][] E;
    private static final float[][] F;

    private static class_2960 e(String file) {
        return class_2960.method_60655((String)"lethalauction", (String)("textures/figure/" + file + ".png"));
    }

    public static C79 a(String figureId) {
        for (C79 p : a) {
            for (C78 f : p.c()) {
                if (!f.a().equals(figureId)) continue;
                return p;
            }
        }
        return null;
    }

    private K12() {
    }

    public static C78 b(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (C78 f : b) {
            if (!f.a.equals(id)) continue;
            return f;
        }
        return null;
    }

    public static C78 a() {
        return K12.b(K9.aI);
    }

    public static C76 car() {
        if (K9.car == null || K9.car.isEmpty()) {
            return null;
        }
        for (C76 c : cars) {
            if (!c.a().equals(K9.car)) continue;
            return c;
        }
        return null;
    }

    public static void setCar(String id) {
        K9.car = id.equals(K9.car) ? "" : id;
        K9.b();
    }

    public static void c(String id) {
        K9.aI = id.equals(K9.aI) ? "" : id;
        K9.b();
        K12.x.a = null;
        K12.x.b = null;
        K12.x.n = 0;
        K12.x.t = 0.0f;
        K12.x.s = 0.0f;
        K12.x.H = 432.0f;
        K12.x.I = 0L;
        K12.x.J = 0L;
        K12.x.M = 0.0;
        K12.x.L = 0.0;
        K12.x.K = 0.0;
        K12.x.N = 0L;
        K12.x.O = false;
        K12.x.U = "";
        K12.x.W = 0L;
    }

    public static void d(String id) {
        if (K12.a() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        K12.x.U = id;
        K12.x.V = now;
        K12.x.W = now + 8000L;
        K12.x.B = 0;
        K12.x.A = now;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null) {
            K12.x.x = mc.field_1724.method_23317();
            K12.x.z = mc.field_1724.method_23321();
        }
    }

    public static boolean clearWaypoint() {
        if (z != null) {
            z = null;
            return true;
        }
        return false;
    }

    public static class_2338 b() {
        return z;
    }

    public static void a(class_310 mc) {
        class_3965 bhr;
        if (z != null) {
            z = null;
            return;
        }
        if (K12.a() == null || mc.field_1724 == null) {
            return;
        }
        class_239 class_2392 = mc.field_1765;
        if (class_2392 instanceof class_3965 && (bhr = (class_3965)class_2392).method_17783() != class_239.class_240.field_1333) {
            class_2338 bp = bhr.method_17777();
            double surf = K12.d((class_1937)mc.field_1687, (double)bp.method_10263() + 0.5, (double)bp.method_10260() + 0.5, bp.method_10264() + 2);
            z = new class_2338(bp.method_10263(), (int)Math.floor(surf), bp.method_10260());
            K12.x.B = 0;
            K12.x.A = System.currentTimeMillis();
        }
    }

    public static boolean c() {
        return !K12.x.U.isEmpty() && System.currentTimeMillis() < K12.x.W;
    }

    public static boolean b(class_310 mc) {
        return false;
    }

    public static boolean c(class_310 mc) {
        return false;
    }

    private static void a(class_310 mc, long now) {
        boolean crit;
        class_746 p = mc.field_1724;
        float scale = p.method_7261(0.5f);
        p.method_7350();
        boolean charged = scale > 0.85f;
        boolean sprint = p.method_5624();
        boolean bl = crit = charged && K12.b((class_1657)p);
        float dmg = charged ? K12.a((class_1657)p) * (crit ? 1.5f : 1.0f) : 0.3f;
        K12.x.H -= dmg;
        K12.x.B = 0;
        K12.x.A = now;
        K12.x.S = now;
        K12.x.N = now + 500L;
        p.method_6104(class_1268.field_5808);
        double kx = K12.x.a.field_1352 - p.method_23317();
        double kz = K12.x.a.field_1350 - p.method_23321();
        double kl = Math.sqrt(kx * kx + kz * kz);
        if (kl < 1.0E-4) {
            kx = -Math.sin(Math.toRadians(p.method_36454()));
            kz = Math.cos(Math.toRadians(p.method_36454()));
            kl = 1.0;
        }
        float power = Math.max(0.25f, scale);
        double kb = (crit ? 0.58 : (sprint ? 0.54 : 0.38)) * (double)power;
        K12.x.K = kx / kl * kb;
        K12.x.L = kz / kl * kb;
        K12.x.M = (crit ? 0.34f : 0.24f) * Math.max(0.35f, scale);
        K12.x.T = K12.x.a.field_1351;
        K12.x.J = now + 2500L;
        try {
            class_3414 atk = crit ? class_3417.field_15016 : (!charged ? class_3417.field_14625 : (sprint || kb > 0.5 ? class_3417.field_14999 : class_3417.field_14840));
            mc.field_1687.method_8486(p.method_23317(), p.method_23318(), p.method_23321(), atk, class_3419.field_15248, 1.0f, 1.0f, false);
            mc.field_1687.method_8486(K12.x.a.field_1352, K12.x.a.field_1351 + 0.6, K12.x.a.field_1350, class_3417.field_15115, class_3419.field_15248, 1.0f, 1.0f, false);
        }
        catch (Throwable atk) {
            // empty catch block
        }
        if (crit) {
            try {
                int i;
                double cx = K12.x.a.field_1352;
                double cy = K12.x.a.field_1351 + 0.9;
                double cz = K12.x.a.field_1350;
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                for (i = 0; i < 12; ++i) {
                    mc.field_1687.method_8406((class_2394)class_2398.field_11205, cx + (((Random)rnd).nextDouble() - 0.5) * 0.7, cy + ((Random)rnd).nextDouble() * 0.7, cz + (((Random)rnd).nextDouble() - 0.5) * 0.7, (((Random)rnd).nextDouble() - 0.5) * 0.3, ((Random)rnd).nextDouble() * 0.3, (((Random)rnd).nextDouble() - 0.5) * 0.3);
                }
                for (i = 0; i < 6; ++i) {
                    mc.field_1687.method_8406((class_2394)class_2398.field_11209, cx + (((Random)rnd).nextDouble() - 0.5) * 0.5, cy + (((Random)rnd).nextDouble() - 0.5) * 0.5, cz + (((Random)rnd).nextDouble() - 0.5) * 0.5, (((Random)rnd).nextDouble() - 0.5) * 0.2, 0.1 + ((Random)rnd).nextDouble() * 0.2, (((Random)rnd).nextDouble() - 0.5) * 0.2);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (K12.x.H <= 0.0f) {
            K12.x.I = now + 8000L;
        }
    }

    private static float a(class_1657 p) {
        class_1799 s = p.method_6047();
        float base = 1.0f;
        try {
            int sharp;
            int mat;
            String id = class_7923.field_41178.method_10221(s.method_7909()).method_12832();
            mat = id.contains("netherite") ? 6 : (id.contains("diamond") ? 5 : (id.contains("iron") ? 4 : (id.contains("stone") ? 3 : (id.contains("gold") ? 2 : (id.contains("wood") ? 1 : 0)))));
            if (mat > 0) {
                if (id.endsWith("sword")) {
                    base = (new float[]{0.0f, 4.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f})[mat];
                } else if (id.endsWith("axe")) {
                    base = (new float[]{0.0f, 7.0f, 7.0f, 9.0f, 9.0f, 9.0f, 10.0f})[mat];
                } else if (id.endsWith("shovel")) {
                    base = (new float[]{0.0f, 2.5f, 2.5f, 3.5f, 4.5f, 5.5f, 6.5f})[mat];
                } else if (id.endsWith("hoe")) {
                    base = (new float[]{0.0f, 1.0f, 1.0f, 2.0f, 3.0f, 4.0f, 4.0f})[mat];
                }
            }
            if ((sharp = K12.a(p, s)) > 0) {
                base += 0.5f * (float)sharp + 0.5f;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return base;
    }

    private static int a(class_1657 p, class_1799 s) {
        try {
            class_6880.class_6883 holder = p.method_73183().method_30349().method_30530(class_7924.field_41265).method_46747(class_1893.field_9118);
            return class_1890.method_8225((class_6880)holder, (class_1799)s);
        }
        catch (Throwable t) {
            return 0;
        }
    }

    private static int a(C77 b) {
        return System.currentTimeMillis() < b.N ? class_4608.method_23624((float)0.0f, (boolean)true) : class_4608.field_21444;
    }

    private static boolean b(class_1657 p) {
        return !p.method_24828() && p.method_18798().field_1351 < 0.0 && !p.method_5799() && !p.method_5765();
    }

    public static void d(class_310 mc) {
        if (mc.field_1724 == null || mc.field_1687 == null || !K37.e()) {
            K12.x.b = null;
            K12.x.a = null;
            y.clear();
            return;
        }
        C78 mine = K12.a();
        if (mine != null) {
            K12.x.w = mine.a();
            try {
                K12.a(x, (class_1657)mc.field_1724, (class_1937)mc.field_1687);
            }
            catch (Throwable t) {
                K12.x.b = null;
                K12.x.a = null;
            }
        } else {
            K12.x.b = null;
            K12.x.a = null;
        }
        if (!K9.aK) {
            y.clear();
            return;
        }
        UUID me = mc.field_1724.method_5667();
        HashSet<UUID> activeNow = new HashSet<UUID>();
        for (class_1657 p : mc.field_1687.method_18456()) {
            String figId;
            UUID id2 = p.method_5667();
            if (id2 == null || id2.equals(me) || (figId = K38.b(id2)) == null || figId.isEmpty() || K12.b(figId) == null) continue;
            activeNow.add(id2);
            C77 b = y.get(id2);
            if (b == null) {
                b = new C77();
                y.put(id2, b);
            }
            if (!figId.equals(b.w)) {
                b.w = figId;
                b.b = null;
                b.a = null;
            }
            try {
                K12.a(b, p, (class_1937)mc.field_1687);
            }
            catch (Throwable t) {
                b.b = null;
                b.a = null;
            }
        }
        y.keySet().removeIf(id -> !activeNow.contains(id));
    }

    private static void a(C77 b, class_1657 p, class_1937 lvl) {
        boolean onGround;
        double pvz;
        boolean frozen;
        boolean airborne;
        boolean ko;
        boolean selfWp;
        double px = p.method_23317();
        double py = p.method_23318();
        double pz = p.method_23321();
        long now = System.currentTimeMillis();
        if (b.a == null) {
            b.b = b.a = new class_243(px + 0.7, py, pz);
            b.e = b.f = p.field_6283;
            b.n = 0;
            b.t = 0.0f;
            b.s = 0.0f;
            b.v = now + 480L;
        }
        double playerStep = Math.abs(px - p.field_6038) + Math.abs(py - p.field_5971) + Math.abs(pz - p.field_5989);
        boolean bl = selfWp = b == x && z != null;
        if (selfWp) {
            px = (double)z.method_10263() + 0.5;
            py = z.method_10264();
            pz = (double)z.method_10260() + 0.5;
            playerStep = 0.0;
        }
        double awayNow = Math.sqrt((px - b.a.field_1352) * (px - b.a.field_1352) + (pz - b.a.field_1350) * (pz - b.a.field_1350));
        if (playerStep > 12.0 || awayNow > 18.0) {
            double gy = K12.d(lvl, px + 0.7, pz, py);
            b.b = b.a = new class_243(px + 0.7, gy, pz);
            b.n = 0;
            b.t = 0.0f;
            b.s = 0.0f;
            b.v = now + 480L;
        }
        boolean bl2 = ko = now < b.I;
        if (b.H <= 0.0f && !ko) {
            b.H = 432.0f;
        }
        boolean bl3 = airborne = b.M != 0.0 || Math.abs(b.K) + Math.abs(b.L) > 0.01;
        if (airborne && !ko) {
            double nkx = b.a.field_1352;
            double nkz = b.a.field_1350;
            double total = Math.abs(b.K) + Math.abs(b.L);
            int sub = Math.max(1, Math.min(10, (int)Math.ceil(total / 0.1)));
            double sx = b.K / (double)sub;
            double sz = b.L / (double)sub;
            for (int i = 0; i < sub; ++i) {
                if (K12.b(lvl, nkx + sx, b.a.field_1351, nkz + sz)) {
                    nkx += sx;
                    nkz += sz;
                    continue;
                }
                if (K12.b(lvl, nkx + sx, b.a.field_1351, nkz)) {
                    nkx += sx;
                    b.L = 0.0;
                    continue;
                }
                if (K12.b(lvl, nkx, b.a.field_1351, nkz + sz)) {
                    nkz += sz;
                    b.K = 0.0;
                    continue;
                }
                b.K = 0.0;
                b.L = 0.0;
                break;
            }
            b.M -= 0.08;
            double nky = b.a.field_1351 + b.M;
            double landY = K12.d(lvl, nkx, nkz, b.a.field_1351);
            if (b.M <= 0.0 && nky <= landY) {
                nky = landY;
                b.M = 0.0;
            }
            if (nky < landY) {
                nky = landY;
                b.M = 0.0;
            }
            b.b = b.a;
            b.f = b.e;
            b.h = b.g;
            b.a = new class_243(nkx, nky, nkz);
            b.K *= 0.86;
            b.L *= 0.86;
            b.i += (0.0f - b.i) * 0.3f;
            b.O = true;
            b.Q = b.P;
            b.P += 0.8f;
            if (b.M == 0.0 && Math.abs(b.K) + Math.abs(b.L) < 0.02) {
                b.K = 0.0;
                b.L = 0.0;
            }
            b.u = now;
            b.e = class_3532.method_17821((float)0.15f, (float)b.e, (float)((float)Math.toDegrees(Math.atan2(-(px - b.a.field_1352), pz - b.a.field_1350))));
            return;
        }
        if (ko) {
            b.b = b.a;
            b.f = b.e;
            b.h = b.g;
            double gy = K12.d(lvl, b.a.field_1352, b.a.field_1350, b.a.field_1351);
            b.a = new class_243(b.a.field_1352, b.a.field_1351 + (gy - b.a.field_1351) * 0.4, b.a.field_1350);
            b.i += (0.0f - b.i) * 0.3f;
            return;
        }
        boolean bl4 = frozen = now < b.J;
        if (now < b.W) {
            b.b = b.a;
            b.f = b.e;
            b.h = b.g;
            b.t = b.s;
            double gy = K12.d(lvl, b.a.field_1352, b.a.field_1350, b.a.field_1351);
            b.a = new class_243(b.a.field_1352, b.a.field_1351 + (gy - b.a.field_1351) * 0.4, b.a.field_1350);
            b.i += (0.0f - b.i) * 0.3f;
            b.e = class_3532.method_17821((float)0.15f, (float)b.e, (float)((float)Math.toDegrees(Math.atan2(-(px - b.a.field_1352), pz - b.a.field_1350))));
            b.n = 0;
            b.s += (0.0f - b.s) * 0.3f;
            return;
        }
        if (Math.abs(px - b.x) > 2.5 || Math.abs(pz - b.z) > 2.5 || py < b.y - 2.0) {
            b.x = px;
            b.y = py;
            b.z = pz;
            b.A = now;
            if (b.B == 1 || b.B == 2) {
                b.B = 3;
                b.C = now;
            }
        }
        if (b.B == 0 && now - b.A >= 15000L && !frozen) {
            b.B = 1;
            b.C = now;
            b.D = b.a.field_1352;
            b.E = b.a.field_1350;
            b.F = K12.d(lvl, b.a.field_1352, b.a.field_1350, b.a.field_1351);
            b.G = (float)Math.toDegrees(Math.atan2(-(px - b.a.field_1352), pz - b.a.field_1350));
        }
        if (b.B == 1 && now - b.C > 700L) {
            b.B = 2;
        }
        if (b.B == 3 && now - b.C > 700L) {
            b.B = 0;
        }
        if (b.B != 0) {
            b.b = b.a;
            b.f = b.e;
            b.h = b.g;
            b.t = b.s;
            b.a = new class_243(b.D, b.F, b.E);
            b.i += (0.0f - b.i) * 0.3f;
            b.e = b.G;
            b.n = 0;
            b.s += (0.0f - b.s) * 0.3f;
            return;
        }
        b.b = b.a;
        b.f = b.e;
        double pvx = p.method_23317() - p.field_6038;
        double pspeed = Math.sqrt(pvx * pvx + (pvz = p.method_23321() - p.field_5989) * pvz);
        if (pspeed > 0.03) {
            double ndx = -pvx / pspeed;
            double ndz = -pvz / pspeed;
            b.c += (ndx - b.c) * 0.25;
            b.d += (ndz - b.d) * 0.25;
            double tl = Math.sqrt(b.c * b.c + b.d * b.d);
            if (tl > 1.0E-4) {
                b.c /= tl;
                b.d /= tl;
            }
        }
        double restX = px + b.c * 1.3;
        double restZ = pz + b.d * 1.3;
        if (selfWp) {
            restX = px;
            restZ = pz;
        }
        double toRest = Math.sqrt((restX - b.a.field_1352) * (restX - b.a.field_1352) + (restZ - b.a.field_1350) * (restZ - b.a.field_1350));
        double nx = b.a.field_1352;
        double nz = b.a.field_1350;
        if (toRest > 0.18 && b.n == 0 && !frozen) {
            double[] veer;
            double MAXSTEP = 0.28;
            double ease = 0.14;
            double dirX = restX - b.a.field_1352;
            double dirZ = restZ - b.a.field_1350;
            double dlen = Math.sqrt(dirX * dirX + dirZ * dirZ);
            double ux = dirX / dlen;
            double uz = dirZ / dlen;
            double stepLen = Math.min(dlen * ease, 0.28);
            boolean stepped = false;
            for (double a : veer = new double[]{0.0, 18.0, -18.0, 36.0, -36.0, 55.0, -55.0, 75.0, -75.0}) {
                double vz;
                double cnz;
                double s;
                double r = Math.toRadians(a);
                double c = Math.cos(r);
                double vx = ux * c - uz * (s = Math.sin(r));
                double cnx = b.a.field_1352 + vx * stepLen;
                if (!K12.c(lvl, cnx, b.a.field_1351, cnz = b.a.field_1350 + (vz = ux * s + uz * c) * stepLen)) continue;
                nx = cnx;
                nz = cnz;
                stepped = true;
                break;
            }
            if (!stepped) {
                double cnx = b.a.field_1352 + ux * stepLen;
                double cnz = b.a.field_1350 + uz * stepLen;
                if (K12.c(lvl, cnx, b.a.field_1351, b.a.field_1350)) {
                    nx = cnx;
                }
                if (K12.c(lvl, b.a.field_1352, b.a.field_1351, cnz)) {
                    nz = cnz;
                }
            }
        }
        double groundY = K12.d(lvl, nx, nz, b.a.field_1351);
        double hdx = px - nx;
        double hdz = pz - nz;
        double hdist = Math.sqrt(hdx * hdx + hdz * hdz);
        if (p.method_24828() && hdist < 2.6 && groundY < py - 1.5) {
            groundY = py;
        }
        if (groundY > b.a.field_1351 + 0.45 && b.n == 0) {
            b.j = now + 420L;
        }
        double heightAbove = b.a.field_1351 - groundY;
        if (b.n == 0 && heightAbove >= 2.5) {
            b.n = 2;
            b.l = b.a.field_1351;
        }
        double ny = b.n == 2 ? b.a.field_1351 + (groundY - b.a.field_1351) * 0.1 : b.a.field_1351 + (groundY - b.a.field_1351) * 0.5;
        boolean bl5 = onGround = Math.abs(ny - groundY) < 0.14;
        if (b.m && !onGround) {
            b.l = b.k;
        }
        if (!b.m && onGround) {
            if (b.n == 2) {
                b.n = 0;
            } else if (b.l - groundY >= 0.5) {
                b.o = now + 220L;
            }
        }
        if (onGround) {
            b.k = groundY;
        }
        b.m = onGround;
        b.a = new class_243(nx, ny, nz);
        b.O = false;
        double figMoved = Math.sqrt((b.a.field_1352 - b.b.field_1352) * (b.a.field_1352 - b.b.field_1352) + (b.a.field_1350 - b.b.field_1350) * (b.a.field_1350 - b.b.field_1350));
        double distP = Math.sqrt((px - b.a.field_1352) * (px - b.a.field_1352) + (pz - b.a.field_1350) * (pz - b.a.field_1350));
        if (b.n != 0 || distP < 2.0 || figMoved > 0.02 || frozen) {
            b.u = now;
        }
        if (b.n == 0 && !frozen && distP > 2.0 && now - b.u > 900L) {
            double gy = K12.d(lvl, px + 0.7, pz, py);
            b.a = b.b = new class_243(px + 0.7, gy, pz);
            b.u = now;
        }
        b.t = b.s;
        b.s = b.s + ((b.n == 2 ? 1.0f : 0.0f) - b.s) * 0.3f;
        double moved = Math.sqrt((b.a.field_1352 - b.b.field_1352) * (b.a.field_1352 - b.b.field_1352) + (b.a.field_1350 - b.b.field_1350) * (b.a.field_1350 - b.b.field_1350));
        float targetMove = (float)Math.min(moved * 7.0, 1.0);
        b.i += (targetMove - b.i) * 0.3f;
        b.h = b.g;
        b.g += (float)moved * 2.1f;
        double ddx = b.a.field_1352 - b.b.field_1352;
        double ddz = b.a.field_1350 - b.b.field_1350;
        float targetYaw = moved > 0.015 ? (float)Math.toDegrees(Math.atan2(-ddx, ddz)) : (float)Math.toDegrees(Math.atan2(-(px - b.a.field_1352), pz - b.a.field_1350));
        b.e = class_3532.method_17821((float)(moved > 0.015 ? 0.22f : 0.08f), (float)b.e, (float)targetYaw);
    }

    private static void a(C77 b, class_1657 p, class_1937 lvl, long now) {
        class_243 from;
        double px = p.method_23317();
        double py = p.method_23318();
        double pz = p.method_23321();
        float yaw = p.field_6283;
        double yr = Math.toRadians(yaw);
        double rightX = Math.cos(yr);
        double rightZ = Math.sin(yr);
        double sX = px + rightX * 0.3;
        double sZ = pz + rightZ * 0.3;
        double sY = py + 1.02;
        b.b = b.a;
        b.f = b.e;
        b.h = b.g;
        b.t = b.s;
        b.i += (0.0f - b.i) * 0.4f;
        b.n = 0;
        b.s += (0.0f - b.s) * 0.4f;
        class_243 class_2432 = from = b.r != null ? b.r : b.a;
        if (b.p == 1) {
            float prog = Math.min(1.0f, (float)(now - b.q) / 1900.0f);
            float e = prog * prog * (3.0f - 2.0f * prog);
            float slip = (float)(0.14 * Math.sin((double)prog * Math.PI * 3.0) * (1.0 - (double)prog));
            float frac = Math.max(0.0f, Math.min(1.0f, e - slip));
            b.a = new class_243(from.field_1352 + (sX - from.field_1352) * (double)frac, from.field_1351 + (sY - from.field_1351) * (double)frac, from.field_1350 + (sZ - from.field_1350) * (double)frac);
            b.e = yaw + 180.0f;
            if (prog >= 1.0f) {
                b.p = 2;
                b.q = now;
            }
        } else if (b.p == 2) {
            double sway = Math.sin((double)now / 900.0) * 0.012;
            b.a = new class_243(sX, sY + sway, sZ);
            b.e = class_3532.method_17821((float)0.3f, (float)b.e, (float)(yaw + 180.0f));
        } else if (b.p == 3) {
            float prog = Math.min(1.0f, (float)(now - b.q) / 450.0f);
            double gx = px + rightX * 0.75;
            double gz = pz + rightZ * 0.75;
            double gy = K12.d(lvl, gx, gz, py);
            double y = from.field_1351 + (gy - from.field_1351) * (double)prog + Math.sin((double)prog * Math.PI) * 0.22;
            b.a = new class_243(from.field_1352 + (gx - from.field_1352) * (double)prog, y, from.field_1350 + (gz - from.field_1350) * (double)prog);
            b.e = class_3532.method_17821((float)0.3f, (float)b.e, (float)yaw);
            if (prog >= 1.0f) {
                b.p = 0;
                b.a = new class_243(gx, gy, gz);
                b.o = now + 220L;
            }
        }
    }

    private static boolean a(class_1937 lvl, double x, double y, double z) {
        try {
            return lvl.method_18026(new class_238(x - 0.17, y + 0.1, z - 0.17, x + 0.17, y + 1.0, z + 0.17));
        }
        catch (Throwable t) {
            return true;
        }
    }

    private static boolean b(class_1937 lvl, double x, double y, double z) {
        return K12.a(lvl, x, y, z) && K12.d(lvl, x, z, y) <= y + 0.3;
    }

    private static class_243 a(class_1937 lvl, class_243 pos) {
        for (double r = 0.3; r <= 1.8; r += 0.3) {
            for (int a = 0; a < 12; ++a) {
                double z;
                double ang = (double)a * Math.PI / 6.0;
                double x = pos.field_1352 + Math.cos(ang) * r;
                if (!K12.a(lvl, x, pos.field_1351, z = pos.field_1350 + Math.sin(ang) * r) || !K12.c(lvl, x, pos.field_1351, z)) continue;
                return new class_243(x, K12.d(lvl, x, z, pos.field_1351), z);
            }
        }
        return null;
    }

    private static boolean c(class_1937 lvl, double x, double curY, double z) {
        try {
            double gy = K12.d(lvl, x, z, curY);
            if (gy - curY > 1.2) {
                return false;
            }
            return lvl.method_18026(new class_238(x - 0.17, gy + 0.1, z - 0.17, x + 0.17, gy + 1.0, z + 0.17));
        }
        catch (Throwable t) {
            return true;
        }
    }

    private static double d(class_1937 lvl, double x, double z, double aroundY) {
        try {
            int base = class_3532.method_15357((double)aroundY);
            for (int i = 1; i >= -256; --i) {
                boolean solid;
                int by = base + i;
                boolean bl = solid = !lvl.method_18026(new class_238(x - 0.15, (double)by + 0.02, z - 0.15, x + 0.15, (double)by + 0.98, z + 0.15));
                if (!solid) continue;
                double top = (double)by + 1.0;
                try {
                    class_2338 pos = new class_2338(class_3532.method_15357((double)x), by, class_3532.method_15357((double)z));
                    class_265 sh = lvl.method_8320(pos).method_26220((class_1922)lvl, pos);
                    if (!sh.method_1110()) {
                        top = (double)by + sh.method_1105(class_2350.class_2351.field_11052);
                    }
                }
                catch (Throwable pos) {
                    // empty catch block
                }
                boolean clearAbove = lvl.method_18026(new class_238(x - 0.15, top + 0.1, z - 0.15, x + 0.15, top + 1.0, z + 0.15));
                if (!clearAbove) continue;
                return top;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return aroundY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void a(WorldRenderContext ctx) {
        try {
            C78 mine;
            u.a(ctx.consumers());
            class_310 mc = class_310.method_1551();
            if (mc.field_1724 == null) return;
            if (!K37.e()) {
                return;
            }
            long now = System.currentTimeMillis();
            if (v == null) {
                if (now < w) {
                    return;
                }
                try {
                    v = new class_591(mc.method_31974().method_32072(class_5602.field_27577), false);
                }
                catch (Throwable t) {
                    v = null;
                    w = now + 1500L;
                    try {
                        u.b(ctx.consumers());
                        return;
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return;
                }
            }
            if ((mine = K12.a()) != null && K12.x.a != null && K12.x.b != null) {
                K12.a(ctx, mc, x, mine);
            }
            Iterator<C77> iterator = y.values().iterator();
            while (iterator.hasNext()) {
                C77 b = iterator.next();
                C78 f = K12.b(b.w);
                if (f == null || b.a == null || b.b == null) continue;
                K12.a(ctx, mc, b, f);
            }
            return;
        }
        catch (Throwable throwable) {
            return;
        }
        finally {
            try {
                u.b(ctx.consumers());
            }
            catch (Throwable throwable) {}
        }
    }

    // ===== Rideable car (local figure only) =====
    private static void renderCar(WorldRenderContext ctx, class_310 mc, C77 b, C78 figure, double rx, double ry, double rz, float yaw, float pt) {
        C76 car = K12.car();
        if (car == null) {
            return;
        }
        long nowMs = System.currentTimeMillis();
        float dYaw = b.e - b.f;
        while (dYaw > 180.0f) dYaw -= 360.0f;
        while (dYaw < -180.0f) dYaw += 360.0f;
        float steerTarget = dYaw * 0.07f;
        if (steerTarget > 0.55f) steerTarget = 0.55f;
        if (steerTarget < -0.55f) steerTarget = -0.55f;
        b.X += (steerTarget - b.X) * 0.2f;
        float steer = b.X;
        float rphase = class_3532.method_16439((float)pt, (float)b.h, (float)b.g);
        float speed = (float)b.i;
        if (speed < 0.0f) speed = 0.0f;
        if (speed > 1.0f) speed = 1.0f;
        float bob = (float)Math.sin((double)rphase * 2.0) * 0.016f * speed + (float)Math.sin((double)nowMs / 500.0) * 0.004f;
        float pitch = (float)Math.sin((double)rphase) * 0.03f * speed;
        float bank = -steer * 0.5f;
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double dx = rx - cam.field_1352;
        double dyBase = ry - cam.field_1351;
        double dz = rz - cam.field_1350;
        double dy = dyBase + (double)bob;
        boolean thirdPerson = !mc.field_1690.method_31044().method_31034();
        double midDy = ry + 0.4 - cam.field_1351;
        if (thirdPerson && dx * dx + midDy * midDy + dz * dz < 0.9) {
            return;
        }
        float yawRad = (float)Math.toRadians(180.0 - (double)yaw);
        Matrix4f base = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
        base.translate((float)dx, (float)dy, (float)dz);
        base.rotateY(yawRad);
        base.rotateZ(bank);
        base.rotateX(pitch);
        class_4588 vc = u.method_73477(class_12249.method_75990((class_2960)K10.a));
        K12.carBody(vc, base, car);
        float wr = 0.155f;
        float wx = 0.27f;
        float wzF = 0.30f;
        float wzR = -0.30f;
        K12.carWheel(vc, base, -wx, wr, wzF, steer, rphase);
        K12.carWheel(vc, base, wx, wr, wzF, steer, rphase);
        K12.carWheel(vc, base, -wx, wr, wzR, 0.0f, rphase);
        K12.carWheel(vc, base, wx, wr, wzR, 0.0f, rphase);
        K12.carSteer(vc, base, steer);
        v.method_63514();
        float hs = 1.7f;
        K12.v.field_3398.field_37939 = K12.v.field_3398.field_37940 = hs;
        K12.v.field_3398.field_37938 = K12.v.field_3398.field_37940;
        K12.v.field_3394.field_37940 = 1.0f;
        K12.v.field_3394.field_37939 = 1.0f;
        K12.v.field_3394.field_37938 = 1.0f;
        float grip = (float)Math.sin((double)nowMs / 170.0) * 0.05f * speed;
        K12.v.field_3392.field_3654 = -1.3f;
        K12.v.field_3397.field_3654 = -1.3f;
        K12.v.field_3392.field_3674 = 0.08f;
        K12.v.field_3397.field_3674 = -0.08f;
        K12.v.field_3401.field_3654 = -1.0f - 0.12f * Math.abs(steer) + grip;
        K12.v.field_27433.field_3654 = -1.0f - 0.12f * Math.abs(steer) - grip;
        K12.v.field_3401.field_3674 = 0.13f + steer * 0.4f;
        K12.v.field_27433.field_3674 = -0.13f + steer * 0.4f;
        K12.v.field_3398.field_3675 = steer * 0.4f;
        K12.v.field_3398.field_3654 = 0.08f;
        float appear = 1.0f;
        if (nowMs < b.v) {
            float t = 1.0f - (float)(b.v - nowMs) / 480.0f;
            float s = t - 1.0f;
            float c1 = 1.70158f;
            appear = Math.max(0.0f, 1.0f + (c1 + 1.0f) * s * s * s + c1 * s * s);
        }
        float fsc = 0.46f * appear;
        class_4587 ps = ctx.matrices();
        ps.method_22903();
        ps.method_22904(dx, dy + 0.02, dz);
        ps.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - yaw));
        ps.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)Math.toDegrees((double)bank)));
        ps.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees((float)Math.toDegrees((double)pitch)));
        ps.method_22905(fsc, fsc, fsc);
        ps.method_22905(-1.0f, -1.0f, 1.0f);
        ps.method_46416(0.0f, -1.501f, 0.0f);
        v.method_62100(ps, u.method_73477(class_12249.method_75990((class_2960)figure.c())), 0xF000F0, class_4608.field_21444, -1);
        ps.method_22909();
    }

    private static void carBody(class_4588 vc, Matrix4f m, C76 car) {
        int body = car.body();
        int accent = car.accent();
        int roof = car.roof();
        int style = car.style();
        int dark = 0x15151A;
        int glass = 0x2A3A4A;
        int light = 0xFFF2C0;
        int tail = 0xE03030;
        K12.a(vc, m, -0.30f, 0.10f, -0.44f, 0.30f, 0.18f, 0.44f, dark);
        K12.a(vc, m, -0.28f, 0.16f, -0.40f, 0.28f, 0.34f, 0.40f, body);
        K12.a(vc, m, -0.26f, 0.16f, 0.24f, 0.26f, 0.30f, 0.46f, body);
        K12.a(vc, m, -0.26f, 0.16f, -0.46f, 0.26f, 0.30f, -0.24f, body);
        K12.a(vc, m, -0.295f, 0.22f, -0.34f, -0.285f, 0.27f, 0.34f, accent);
        K12.a(vc, m, 0.285f, 0.22f, -0.34f, 0.295f, 0.27f, 0.34f, accent);
        K12.a(vc, m, -0.18f, 0.30f, -0.20f, 0.18f, 0.345f, 0.08f, dark);
        K12.a(vc, m, -0.18f, 0.30f, -0.22f, 0.18f, 0.58f, -0.16f, style == 3 ? accent : dark);
        K12.a(vc, m, -0.22f, 0.22f, 0.45f, -0.12f, 0.30f, 0.47f, light);
        K12.a(vc, m, 0.12f, 0.22f, 0.45f, 0.22f, 0.30f, 0.47f, light);
        K12.a(vc, m, -0.22f, 0.24f, -0.47f, -0.12f, 0.30f, -0.455f, tail);
        K12.a(vc, m, 0.12f, 0.24f, -0.47f, 0.22f, 0.30f, -0.455f, tail);
        K12.a(vc, m, -0.28f, 0.14f, 0.46f, 0.28f, 0.22f, 0.48f, dark);
        K12.a(vc, m, -0.28f, 0.14f, -0.48f, 0.28f, 0.22f, -0.46f, dark);
        if (style == 1) {
            K12.a(vc, m, -0.17f, 0.345f, 0.06f, 0.17f, 0.45f, 0.10f, glass);
        } else if (style == 2) {
            K12.a(vc, m, -0.28f, 0.34f, -0.12f, 0.28f, 0.62f, 0.14f, body);
            K12.a(vc, m, -0.24f, 0.42f, 0.12f, 0.24f, 0.58f, 0.145f, glass);
            K12.a(vc, m, -0.30f, 0.30f, -0.46f, 0.30f, 0.44f, -0.14f, body);
            K12.a(vc, m, -0.26f, 0.30f, -0.44f, 0.26f, 0.34f, -0.16f, dark);
            K12.a(vc, m, -0.32f, 0.02f, 0.26f, -0.20f, 0.18f, 0.46f, roof);
            K12.a(vc, m, 0.20f, 0.02f, 0.26f, 0.32f, 0.18f, 0.46f, roof);
            K12.a(vc, m, -0.32f, 0.02f, -0.46f, -0.20f, 0.18f, -0.26f, roof);
            K12.a(vc, m, 0.20f, 0.02f, -0.46f, 0.32f, 0.18f, -0.26f, roof);
        } else if (style == 3) {
            K12.a(vc, m, -0.18f, 0.345f, 0.08f, 0.18f, 0.44f, 0.11f, glass);
            K12.a(vc, m, -0.19f, 0.345f, 0.07f, -0.16f, 0.46f, 0.12f, roof);
            K12.a(vc, m, 0.16f, 0.345f, 0.07f, 0.19f, 0.46f, 0.12f, roof);
        } else {
            K12.a(vc, m, -0.16f, 0.34f, -0.18f, -0.12f, 0.56f, -0.14f, dark);
            K12.a(vc, m, 0.12f, 0.34f, -0.18f, 0.16f, 0.56f, -0.14f, dark);
            K12.a(vc, m, -0.16f, 0.52f, -0.18f, 0.16f, 0.56f, -0.14f, dark);
        }
    }

    private static void carWheel(class_4588 vc, Matrix4f base, float x, float y, float z, float steer, float roll) {
        Matrix4f m = new Matrix4f((Matrix4fc)base);
        m.translate(x, y, z);
        if (steer != 0.0f) {
            m.rotateY(steer);
        }
        m.rotateX(roll);
        float tw = 0.055f;
        float r = 0.155f;
        float e1 = 0.004f;
        K12.a(vc, m, -tw, -r, -r, tw, r, r, 0x15151A);
        float bar = 0.03f;
        float hub = r * 0.72f;
        K12.a(vc, m, -tw - e1, -bar, -hub, tw + e1, bar, hub, 0xB8B8C0);
        K12.a(vc, m, -tw - e1, -hub, -bar, tw + e1, hub, bar, 0xB8B8C0);
        K12.a(vc, m, -tw - 2.0f * e1, -0.04f, -0.04f, tw + 2.0f * e1, 0.04f, 0.04f, 0x6A6A74);
    }

    private static void carSteer(class_4588 vc, Matrix4f base, float steer) {
        Matrix4f m = new Matrix4f((Matrix4fc)base);
        m.translate(0.0f, 0.44f, 0.17f);
        m.rotateX(-0.5f);
        m.rotateY(steer * 2.0f);
        float rw = 0.075f;
        int col = 0x202024;
        int rim = 0x303036;
        K12.a(vc, m, -rw, -0.012f, -0.012f, rw, 0.012f, 0.012f, col);
        K12.a(vc, m, -0.012f, -rw, -0.012f, 0.012f, rw * 0.4f, 0.012f, col);
        K12.a(vc, m, -rw, -0.02f, -0.016f, -rw + 0.022f, 0.02f, 0.016f, rim);
        K12.a(vc, m, rw - 0.022f, -0.02f, -0.016f, rw, 0.02f, 0.016f, rim);
        K12.a(vc, m, -0.02f, rw - 0.022f, -0.016f, 0.02f, rw, 0.016f, rim);
        K12.a(vc, m, -0.02f, -rw, -0.016f, 0.02f, -rw + 0.022f, 0.016f, rim);
    }

    private static void a(WorldRenderContext ctx, class_310 mc, C77 b, C78 figure) {
        float h;
        boolean thirdPerson;
        boolean chuting;
        float pt = mc.method_61966().method_60637(false);
        double rx = class_3532.method_16436((double)pt, (double)b.b.field_1352, (double)b.a.field_1352);
        double ry = class_3532.method_16436((double)pt, (double)b.b.field_1351, (double)b.a.field_1351);
        double rz = class_3532.method_16436((double)pt, (double)b.b.field_1350, (double)b.a.field_1350);
        float yaw = class_3532.method_17821((float)pt, (float)b.f, (float)b.e);
        if (System.currentTimeMillis() < b.I) {
            K12.b(ctx, mc, b, figure, rx, ry, rz);
            return;
        }
        if (System.currentTimeMillis() < b.W && !b.U.isEmpty()) {
            K12.a(ctx, mc, b, figure, rx, ry, rz, yaw);
            return;
        }
        if (b.B != 0) {
            K12.a(ctx, mc, b, figure, rx, ry, rz);
            return;
        }
        long nowMs = System.currentTimeMillis();
        float rphase = class_3532.method_16439((float)pt, (float)b.h, (float)b.g);
        float legSwing = (float)Math.sin(rphase) * b.i * 0.55f;
        float idle = (float)Math.sin((double)nowMs / 650.0) * 0.018f;
        float walkHop = Math.abs((float)Math.sin(rphase)) * b.i * 0.08f;
        float climb = 0.0f;
        float climbTuck = 0.0f;
        if (nowMs < b.j) {
            float prog = (float)(b.j - nowMs) / 420.0f;
            climb = (float)Math.sin((double)prog * Math.PI) * 0.04f;
            climbTuck = (float)Math.sin((double)prog * Math.PI) * 0.3f;
        }
        float squash = 0.0f;
        if (nowMs < b.o) {
            squash = (float)Math.sin((double)((float)(b.o - nowMs) / 220.0f) * Math.PI);
        }
        float bob = walkHop + climb + idle * (1.0f - b.i) - squash * 0.12f;
        float chuteAmt = class_3532.method_16439((float)pt, (float)b.t, (float)b.s);
        boolean bl = chuting = chuteAmt > 0.1f;
        if (chuting) {
            bob = 0.0f;
        }
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double dx = rx - cam.field_1352;
        double dy = ry - cam.field_1351 + (double)bob;
        double dz = rz - cam.field_1350;
        double midDy = ry + 0.55 - cam.field_1351;
        boolean bl2 = thirdPerson = !mc.field_1690.method_31044().method_31034();
        if (thirdPerson && dx * dx + midDy * midDy + dz * dz < 1.3224999999999998) {
            return;
        }
        if (b.p != 0 && !thirdPerson) {
            return;
        }
        v.method_63514();
        K12.v.field_3398.field_37939 = K12.v.field_3398.field_37940 = (h = 1.7f);
        K12.v.field_3398.field_37938 = K12.v.field_3398.field_37940;
        K12.v.field_3394.field_37940 = 1.0f;
        K12.v.field_3394.field_37939 = 1.0f;
        K12.v.field_3394.field_37938 = 1.0f;
        K12.v.field_3392.field_3654 = legSwing - climbTuck + squash * 0.45f;
        K12.v.field_3397.field_3654 = -legSwing - climbTuck * 0.7f - squash * 0.45f;
        K12.v.field_3401.field_3654 = -legSwing * 0.5f - climbTuck * 0.6f;
        K12.v.field_27433.field_3654 = legSwing * 0.5f - climbTuck * 0.6f;
        K12.v.field_3401.field_3674 = 0.0f;
        K12.v.field_27433.field_3674 = 0.0f;
        if (chuting) {
            K12.v.field_3401.field_3654 = 2.8902652f;
            K12.v.field_27433.field_3654 = 2.8902652f;
            K12.v.field_3401.field_3674 = -0.35f;
            K12.v.field_27433.field_3674 = 0.35f;
            K12.v.field_3392.field_3654 = 0.2f;
            K12.v.field_3397.field_3654 = -0.2f;
        } else if (b.O) {
            float since = (float)(nowMs - b.S) / 1000.0f;
            float env = since < 0.1f ? since / 0.1f : Math.max(0.0f, 1.0f - (since - 0.1f) / 0.55f);
            env = Math.max(0.0f, Math.min(1.0f, env));
            float j = (float)Math.sin(class_3532.method_16439((float)pt, (float)b.Q, (float)b.P));
            K12.v.field_3401.field_3654 = (-1.3f + j * 0.12f) * env;
            K12.v.field_27433.field_3654 = (-1.3f - j * 0.12f) * env;
            K12.v.field_3401.field_3674 = -0.3f * env;
            K12.v.field_27433.field_3674 = 0.3f * env;
            K12.v.field_3392.field_3654 = 0.22f * env;
            K12.v.field_3397.field_3654 = -0.22f * env;
            K12.v.field_3398.field_3654 = -0.12f * env;
        } else if (b.p == 1) {
            float cph = (float)nowMs / 110.0f;
            float s = (float)Math.sin(cph);
            K12.v.field_3401.field_3654 = -2.35f + s * 0.55f;
            K12.v.field_27433.field_3654 = -2.35f - s * 0.55f;
            K12.v.field_3401.field_3674 = -0.28f;
            K12.v.field_27433.field_3674 = 0.28f;
            K12.v.field_3392.field_3654 = 0.55f - s * 0.55f;
            K12.v.field_3397.field_3654 = 0.55f + s * 0.55f;
            K12.v.field_3398.field_3654 = -0.3f;
            K12.v.field_3398.field_3674 = (float)Math.sin((double)nowMs / 42.0) * 0.05f;
        } else if (b.p == 2) {
            float swing = (float)Math.sin((double)nowMs / 470.0) * 0.22f;
            K12.v.field_3392.field_3654 = 0.4f + swing;
            K12.v.field_3397.field_3654 = 0.4f - swing;
            K12.v.field_3392.field_3674 = 0.05f;
            K12.v.field_3397.field_3674 = -0.05f;
            K12.v.field_3401.field_3654 = 0.25f;
            K12.v.field_27433.field_3654 = 0.25f;
            K12.v.field_3401.field_3674 = -0.12f;
            K12.v.field_27433.field_3674 = 0.12f;
            K12.v.field_3398.field_3675 = (float)Math.sin((double)nowMs / 1300.0) * 0.22f;
        }
        float appear = 1.0f;
        if (nowMs < b.v) {
            float t = 1.0f - (float)(b.v - nowMs) / 480.0f;
            float s = t - 1.0f;
            float c1 = 1.70158f;
            appear = Math.max(0.0f, 1.0f + (c1 + 1.0f) * s * s * s + c1 * s * s);
        }
        float sc = 0.55f * appear * (b.p != 0 ? 0.66f : 1.0f);
        class_4587 ps = ctx.matrices();
        ps.method_22903();
        ps.method_22904(dx, dy, dz);
        ps.method_22905(sc, sc, sc);
        ps.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - yaw));
        ps.method_22905(-1.0f, -1.0f, 1.0f);
        ps.method_46416(0.0f, -1.501f, 0.0f);
        class_1921 type = class_12249.method_75990((class_2960)figure.c());
        v.method_62100(ps, u.method_73477(type), 0xF000F0, K12.a(b), -1);
        ps.method_22909();
        if (chuteAmt > 0.02f && !K11.a()) {
            Matrix4f m = new Matrix4f((Matrix4fc)ps.method_23760().method_23761());
            m.translate((float)dx, (float)(dy + 0.9), (float)dz);
            m.rotateY((float)Math.toRadians(180.0 - (double)yaw));
            float cs = chuteAmt * 0.2f;
            m.scale(cs);
            K11.a(m, u.method_73477(class_12249.method_75990((class_2960)K11.a)));
        }
    }

    private static void a(WorldRenderContext ctx, class_310 mc, C77 b, C78 figure, double rx, double ry, double rz, float yaw) {
        float h;
        long now = System.currentTimeMillis();
        float t = (float)(now - b.V) / 1000.0f;
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double dx = rx - cam.field_1352;
        double dy = ry - cam.field_1351;
        double dz = rz - cam.field_1350;
        float reach = (float)(Math.sin((double)t * 2.8) + 1.0) * 0.5f;
        float chew = (float)Math.sin((double)t * 9.0);
        float sway = (float)Math.sin((double)t * 1.7);
        float rock = (float)Math.sin((double)t * 1.1);
        float yawRad = (float)Math.toRadians(180.0 - (double)yaw);
        float seatH = 0.4f;
        class_4588 prop = u.method_73477(class_12249.method_75990((class_2960)K10.a));
        v.method_63514();
        K12.v.field_3398.field_37939 = K12.v.field_3398.field_37940 = (h = 1.7f);
        K12.v.field_3398.field_37938 = K12.v.field_3398.field_37940;
        K12.v.field_3394.field_37940 = 1.0f;
        K12.v.field_3394.field_37939 = 1.0f;
        K12.v.field_3394.field_37938 = 1.0f;
        K12.v.field_3392.field_3654 = -1.5f + rock * 0.1f;
        K12.v.field_3397.field_3654 = -1.5f - rock * 0.1f;
        K12.v.field_3392.field_3674 = 0.12f + sway * 0.06f;
        K12.v.field_3397.field_3674 = -0.12f + sway * 0.06f;
        K12.v.field_27433.field_3654 = -1.0f;
        K12.v.field_27433.field_3674 = -0.3f + sway * 0.05f;
        K12.v.field_3401.field_3654 = class_3532.method_16439((float)reach, (float)-0.85f, (float)-2.2f);
        K12.v.field_3401.field_3674 = class_3532.method_16439((float)reach, (float)0.25f, (float)0.62f);
        K12.v.field_3398.field_3654 = 0.14f + (reach > 0.75f ? Math.abs(chew) * 0.08f : 0.0f);
        K12.v.field_3398.field_3675 = sway * 0.2f;
        Matrix4f cm = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
        cm.translate((float)dx, (float)dy, (float)dz);
        cm.rotateY(yawRad);
        K12.a(prop, cm);
        class_4587 ps = ctx.matrices();
        ps.method_22903();
        ps.method_22904(dx, dy + (double)seatH + (double)sway * 0.015, dz);
        ps.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - yaw));
        ps.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(sway * 4.0f));
        ps.method_22905(0.55f, 0.55f, 0.55f);
        ps.method_22905(-1.0f, -1.0f, 1.0f);
        ps.method_46416(0.0f, -1.501f, 0.0f);
        v.method_62100(ps, u.method_73477(class_12249.method_75990((class_2960)figure.c())), 0xF000F0, class_4608.field_21444, -1);
        ps.method_22909();
        Matrix4f pm = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
        pm.translate((float)dx, (float)(dy + (double)seatH + 0.1), (float)dz);
        pm.rotateY(yawRad);
        pm.translate(0.14f + sway * 0.02f, 0.0f, -0.28f);
        K12.b(prop, pm);
        int[] cornCols = new int[]{15983528, 15390336, 16248512};
        for (int i = 0; i < 11; ++i) {
            float life = (t * 1.25f + (float)i * 0.31f) % 1.0f;
            float ang = (float)i * 2.0f + (float)(i % 2) * 0.7f;
            float spd = 0.7f + (float)(i % 3) * 0.35f;
            float kx = (float)Math.cos(ang) * life * spd * 0.7f;
            float kz = (float)Math.sin(ang) * life * spd * 0.7f;
            float ky = 0.62f + life * 1.3f - life * life * 2.1f;
            Matrix4f km = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
            km.translate((float)dx, (float)dy, (float)dz);
            km.rotateY(yawRad);
            km.translate(0.12f + kx, ky, -0.24f + kz);
            km.rotateY(life * 13.0f);
            km.rotateX(life * 10.0f);
            float s = 0.035f;
            K12.a(prop, km, -s, -s, -s * 0.7f, s, s, s * 0.7f, cornCols[i % 3]);
        }
    }

    private static void a(class_4588 vc, Matrix4f m) {
        float[] edge;
        int wood = 8281655;
        int dark = 5915176;
        float sw = 0.33f;
        float seatBot = 0.34f;
        float seatTop = 0.42f;
        K12.a(vc, m, -sw, seatBot, -sw, sw, seatTop, sw, wood);
        float lt = 0.045f;
        for (float lx : edge = new float[]{-(sw - lt), sw - lt}) {
            for (float lz : edge) {
                K12.a(vc, m, lx - lt, 0.0f, lz - lt, lx + lt, seatBot, lz + lt, dark);
            }
        }
        K12.a(vc, m, -sw, seatTop, sw - 0.07f, sw, seatTop + 0.62f, sw, wood);
        K12.a(vc, m, -sw, seatTop + 0.5f, sw - 0.09f, sw, seatTop + 0.62f, sw + 0.02f, dark);
        K12.a(vc, m, -sw - 0.02f, seatTop, -sw, -sw + 0.06f, seatTop + 0.2f, sw - 0.06f, dark);
        K12.a(vc, m, sw - 0.06f, seatTop, -sw, sw + 0.02f, seatTop + 0.2f, sw - 0.06f, dark);
    }

    private static void b(class_4588 vc, Matrix4f m) {
        int red = 0xCF0000;
        int white = 0xF2F2F2;
        int corn = 15983528;
        int corn2 = 15390336;
        float r = 0.1f;
        float hgt = 0.22f;
        float e = 0.001f;
        K12.a(vc, m, -r, 0.0f, -r, r, hgt, r, red);
        K12.a(vc, m, -0.028f, 0.01f, -r - e, 0.028f, hgt - 0.02f, -r - e, white);
        K12.a(vc, m, -0.028f, 0.01f, r, 0.028f, hgt - 0.02f, r + e, white);
        K12.a(vc, m, -r - e, 0.01f, -0.028f, -r - e, hgt - 0.02f, 0.028f, white);
        K12.a(vc, m, r, 0.01f, -0.028f, r + e, hgt - 0.02f, 0.028f, white);
        K12.a(vc, m, -r - 0.006f, hgt - 0.025f, -r - 0.006f, r + 0.006f, hgt, r + 0.006f, white);
        K12.a(vc, m, -r * 0.85f, hgt, -r * 0.85f, r * 0.85f, hgt + 0.07f, r * 0.85f, corn);
        K12.a(vc, m, -0.055f, hgt + 0.05f, -0.03f, 0.055f, hgt + 0.12f, 0.055f, corn2);
        K12.a(vc, m, 0.02f, hgt + 0.04f, -0.07f, 0.09f, hgt + 0.11f, 0.02f, corn);
        K12.a(vc, m, -0.08f, hgt + 0.04f, -0.01f, -0.01f, hgt + 0.1f, 0.06f, corn2);
    }

    private static void a(WorldRenderContext ctx, class_310 mc, C77 b, C78 figure, double rx, double ry, double rz) {
        float h;
        long nowMs = System.currentTimeMillis();
        float rise = b.B == 1 ? Math.min(1.0f, (float)(nowMs - b.C) / 700.0f) : (b.B == 3 ? Math.max(0.0f, 1.0f - (float)(nowMs - b.C) / 700.0f) : 1.0f);
        float ease = rise * rise * (3.0f - 2.0f * rise);
        double tsec = (double)nowMs / 1000.0;
        float scroll = (float)Math.sin(tsec * 3.2) * 0.09f;
        float nod = (float)Math.sin(tsec * 0.8) * 0.04f;
        float yaw = b.G;
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double dx = rx - cam.field_1352;
        double dy = ry - cam.field_1351;
        double dz = rz - cam.field_1350;
        v.method_63514();
        K12.v.field_3398.field_37939 = K12.v.field_3398.field_37940 = (h = 1.7f);
        K12.v.field_3398.field_37938 = K12.v.field_3398.field_37940;
        K12.v.field_3394.field_37940 = 1.0f;
        K12.v.field_3394.field_37939 = 1.0f;
        K12.v.field_3394.field_37938 = 1.0f;
        K12.v.field_3398.field_3654 = 0.32f * ease + nod;
        K12.v.field_3392.field_3654 = -1.5f * ease;
        K12.v.field_3397.field_3654 = -1.5f * ease;
        K12.v.field_3392.field_3674 = 0.1f * ease;
        K12.v.field_3397.field_3674 = -0.1f * ease;
        K12.v.field_3401.field_3654 = -1.85f * ease;
        K12.v.field_27433.field_3654 = -1.85f * ease + scroll;
        K12.v.field_3401.field_3674 = 0.18f * ease;
        K12.v.field_27433.field_3674 = -0.18f * ease;
        class_4587 ps = ctx.matrices();
        ps.method_22903();
        ps.method_22904(dx, dy, dz);
        ps.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - yaw));
        ps.method_46416(0.0f, -0.3f * ease, 0.0f);
        ps.method_22903();
        ps.method_22905(0.55f, 0.55f, 0.55f);
        ps.method_22905(-1.0f, -1.0f, 1.0f);
        ps.method_46416(0.0f, -1.501f, 0.0f);
        v.method_62100(ps, u.method_73477(class_12249.method_75990((class_2960)figure.c())), 0xF000F0, class_4608.field_21444, -1);
        ps.method_22909();
        if (rise > 0.4f) {
            Matrix4f pm = new Matrix4f((Matrix4fc)ps.method_23760().method_23761());
            pm.translate(0.0f, 0.78f, -0.34f);
            pm.rotateX(2.35f);
            K12.a(pm, scroll);
        }
        ps.method_22909();
    }

    private static void b(WorldRenderContext ctx, class_310 mc, C77 b, C78 figure, double rx, double ry, double rz) {
        float h;
        long now = System.currentTimeMillis();
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double dx = rx - cam.field_1352;
        double dy = ry - cam.field_1351;
        double dz = rz - cam.field_1350;
        v.method_63514();
        K12.v.field_3398.field_37939 = K12.v.field_3398.field_37940 = (h = 1.7f);
        K12.v.field_3398.field_37938 = K12.v.field_3398.field_37940;
        K12.v.field_3394.field_37940 = 1.0f;
        K12.v.field_3394.field_37939 = 1.0f;
        K12.v.field_3394.field_37938 = 1.0f;
        float tw = (float)Math.sin((double)now / 120.0) * 0.05f;
        K12.v.field_3401.field_3674 = 1.3f + tw;
        K12.v.field_27433.field_3674 = -1.3f - tw;
        K12.v.field_3392.field_3674 = 0.25f;
        K12.v.field_3397.field_3674 = -0.25f;
        class_4587 ps = ctx.matrices();
        ps.method_22903();
        ps.method_22904(dx, dy, dz);
        ps.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - b.e));
        ps.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
        ps.method_22905(0.55f, 0.55f, 0.55f);
        ps.method_22905(-1.0f, -1.0f, 1.0f);
        ps.method_46416(0.0f, -1.501f, 0.0f);
        v.method_62100(ps, u.method_73477(class_12249.method_75990((class_2960)figure.c())), 0xF000F0, K12.a(b), -1);
        ps.method_22909();
        double fr = Math.toRadians(b.e);
        double fwdX = -Math.sin(fr);
        double fwdZ = Math.cos(fr);
        double headX = rx - fwdX * 0.7;
        double headZ = rz - fwdZ * 0.7;
        double hy = ry + 0.82;
        float spin = (float)(now % 1600L) / 1600.0f * 6.2832f;
        int stars = 5;
        for (int i = 0; i < stars; ++i) {
            float a = spin + (float)i * 6.2832f / (float)stars;
            double sx = headX + Math.cos(a) * 0.26 - cam.field_1352;
            double sy = hy + Math.sin((double)now / 250.0 + (double)i) * 0.03 - cam.field_1351;
            double sz = headZ + Math.sin(a) * 0.26 - cam.field_1350;
            Matrix4f sm = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
            sm.translate((float)sx, (float)sy, (float)sz);
            sm.rotateY(a);
            K12.c(u.method_73477(class_12249.method_75990((class_2960)K10.a)), sm);
        }
    }

    private static void a(WorldRenderContext ctx, class_310 mc, int col) {
        class_243 cam = mc.field_1773.method_19418().method_71156();
        Matrix4f m = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
        m.translate((float)((double)z.method_10263() - cam.field_1352), (float)((double)z.method_10264() - cam.field_1351) + 0.06f, (float)((double)z.method_10260() - cam.field_1350));
        class_4588 vc = u.method_73477(class_12249.method_75990((class_2960)K10.a));
        int r = col >> 16 & 0xFF;
        int g = col >> 8 & 0xFF;
        int b = col & 0xFF;
        float lo = 0.16f;
        float hi = 0.84f;
        float hw = 0.06f;
        K12.a(vc, m, lo, lo, hi, hi, hw, r, g, b);
        K12.a(vc, m, lo, hi, hi, lo, hw, r, g, b);
    }

    private static void a(class_4588 vc, Matrix4f m, float ax, float az, float bx, float bz, float hw, int r, int g, int b) {
        float dx = bx - ax;
        float dz = bz - az;
        float len = (float)Math.sqrt(dx * dx + dz * dz);
        float ux = dx / len;
        float uz = dz / len;
        float px = -uz * hw;
        float pz = ux * hw;
        float[][] c = new float[][]{{ax + px, 0.0f, az + pz}, {bx + px, 0.0f, bz + pz}, {bx - px, 0.0f, bz - pz}, {ax - px, 0.0f, az - pz}};
        int[][] tris = new int[][]{{0, 1, 2}, {0, 2, 3}};
        Vector3f p = new Vector3f();
        int[][] nArrayArray = tris;
        int n = nArrayArray.length;
        for (int i = 0; i < n; ++i) {
            int[] tri;
            for (int k : tri = nArrayArray[i]) {
                m.transformPosition(c[k][0], c[k][1], c[k][2], p);
                vc.method_22912(p.x, p.y, p.z).method_1336(r, g, b, 255).method_22913(0.5f, 0.5f).method_60796(0, 10).method_22921(240, 240).method_22914(0.0f, 1.0f, 0.0f);
            }
        }
    }

    private static void c(class_4588 vc, Matrix4f m) {
        K12.a(vc, m, -0.055f, -0.018f, -0.006f, 0.055f, 0.018f, 0.006f, 16769594);
        K12.a(vc, m, -0.018f, -0.055f, -0.006f, 0.018f, 0.055f, 0.006f, 16769594);
    }

    private static void a(Matrix4f m, float scroll) {
        class_4588 vc = u.method_73477(class_12249.method_75990((class_2960)K10.a));
        float w = 0.08f;
        float hh = 0.14f;
        float t = 0.028f;
        K12.a(vc, m, -w, -hh, -t, w, hh, t, 0x17171C);
        float s = 0.012f;
        float f = -t;
        long now = System.currentTimeMillis();
        int n = D.length;
        int vid = (int)(now / 3000L % (long)n);
        float phase = (float)(now % 3000L) / 3000.0f;
        float swipe = phase < 0.12f ? (0.12f - phase) / 0.12f : 0.0f;
        int bg = D[vid][0];
        int subj = D[vid][1];
        float x0 = -w + s;
        float x1 = w - s;
        float y0 = -hh + s;
        float y1 = hh - s;
        K12.a(vc, m, x0, y0, f - 0.0015f, x1, y1, f, bg);
        float cy = (float)Math.sin((double)now / 320.0) * 0.006f - swipe * (y1 - y0);
        K12.a(vc, m, -0.032f, cy - 0.04f, f - 0.003f, 0.032f, cy + 0.04f, f - 0.0015f, subj);
        for (int i = 0; i < 3; ++i) {
            float dy = -0.038f + (float)i * 0.034f;
            K12.a(vc, m, x1 - 0.02f, dy - 0.009f, f - 0.003f, x1 - 0.006f, dy + 0.009f, f - 0.0015f, 0xF2F2F2);
        }
    }

    private static void a(class_4588 vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int bl = rgb & 0xFF;
        float[][] c = new float[][]{{x0, y0, z0}, {x0, y0, z1}, {x0, y1, z0}, {x0, y1, z1}, {x1, y0, z0}, {x1, y0, z1}, {x1, y1, z0}, {x1, y1, z1}};
        Vector3f p = new Vector3f();
        Vector3f n = new Vector3f();
        for (int f = 0; f < 6; ++f) {
            int[][] t;
            int[] q = E[f];
            m.transformDirection(F[f][0], F[f][1], F[f][2], n);
            n.normalize();
            for (int[] tri : t = new int[][]{{q[0], q[1], q[2]}, {q[0], q[2], q[3]}, {q[0], q[2], q[1]}, {q[0], q[3], q[2]}}) {
                for (int k = 0; k < 3; ++k) {
                    float[] v = c[tri[k]];
                    m.transformPosition(v[0], v[1], v[2], p);
                    vc.method_22912(p.x, p.y, p.z).method_1336(r, g, bl, 255).method_22913(0.5f, 0.5f).method_60796(0, 10).method_22921(240, 240).method_22914(n.x, n.y, n.z);
                }
            }
        }
    }

    static {
        x = new C77();
        y = new HashMap<UUID, C77>();
        c = new int[]{4186367, 0xFF4D4D, 5111658, 16765498, 12083711, 0xFFFFFF, 16747069};
        D = new int[][]{{1994588, 15909198}, {6958192, 15760446}, {2112099, 14830154}, {2829634, 5032432}};
        E = new int[][]{{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {1, 5, 7, 3}, {0, 2, 6, 4}};
        F = new float[][]{{-1.0f, 0.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};
    }

    public static final class C79 {
        private final String a;
        private final String b;
        private final List<C78> c;
        C79(String a, String b, List<C78> c) { this.a = a; this.b = b; this.c = c; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public List<C78> c() { return this.c; }


    }

    public static final class C78 {
        private final String a;
        private final String b;
        private final class_2960 c;
        C78(String a, String b, class_2960 c) { this.a = a; this.b = b; this.c = c; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public class_2960 c() { return this.c; }


    }

    public static final class C76 {
        private final String a;
        private final String b;
        private final int d;
        private final int e;
        private final int f;
        private final int g;
        C76(String a, String b, int body, int accent, int roof, int style) {
            this.a = a; this.b = b; this.d = body; this.e = accent; this.f = roof; this.g = style;
        }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public int body() { return this.d; }
        public int accent() { return this.e; }
        public int roof() { return this.f; }
        public int style() { return this.g; }
    }

    private static final class C77 {
        class_243 a;
        class_243 b;
        double c = 0.0;
        double d = 1.0;
        float e;
        float f;
        float g;
        float h;
        float i;
        long j;
        double k;
        double l;
        boolean m = true;
        int n;
        long o;
        int p;
        long q;
        class_243 r;
        float s;
        float t;
        long u;
        long v;
        String w = "";
        double x;
        double y;
        double z;
        long A;
        int B;
        long C;
        double D;
        double E;
        double F;
        float G;
        float H = 432.0f;
        long I;
        long J;
        double K;
        double L;
        double M;
        long N;
        boolean O;
        float P;
        float Q;
        long R;
        long S;
        double T;
        String U = "";
        long V;
        long W;
        float X;

        private C77() {
        }
    }
}

