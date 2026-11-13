/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.model.player.PlayerModel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemInstance
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class K12 {
    public static final List<C80> a = List.of(new C80("creator", "Creator Pack", List.of(new C79("dorzeey", "Dorzeey", K12.e("dorzeey")), new C79("speedingduck", "SpeedingDuck", K12.e("speedingduck")), new C79("sixxpain", "6ixxpain", K12.e("sixxpain")), new C79("agentbtw", "AgentBtw", K12.e("agentbtw")))), new C80("donut", "Donut Pack", List.of(new C79("drdonut", "DrDonut", K12.e("drdonut")), new C79("farex", "Farex", K12.e("farex")), new C79("jettism", "Jettism", K12.e("jettism")), new C79("w1zox", "W1zox", K12.e("w1zox")), new C79("jestic", "Jestic", K12.e("jestic")), new C79("nolimitdash", "NoLimitDash", K12.e("nolimitdash")), new C79("truezay", "TrueZay", K12.e("truezay")), new C79("lethallukeyt", "LethalLukeYT", K12.e("lethallukeyt")))), new C80("pokemon", "Pokemon Pack", List.of(new C79("pikachu", "Pikachu", K12.e("pikachu")), new C79("charmander", "Charmander", K12.e("charmander")), new C79("bulbasaur", "Bulbasaur", K12.e("bulbasaur")), new C79("squirtle", "Squirtle", K12.e("squirtle")), new C79("snorlax", "Snorlax", K12.e("snorlax")), new C79("ashketchum", "Ash Ketchum", K12.e("ashketchum")), new C79("gengar", "Gengar", K12.e("gengar")), new C79("mew", "Mew", K12.e("mew")))), new C80("steamer", "Steamer Pack", List.of(new C79("stableronaldo", "StableRonaldo", K12.e("stableronaldo")), new C79("jynxzi", "Jynxzi", K12.e("jynxzi")), new C79("marlon", "Marlon", K12.e("marlon")), new C79("lacy", "Lacy", K12.e("lacy")), new C79("jasontheween", "JasonTheWeen", K12.e("jasontheween")), new C79("ishowspeed", "IShowSpeed", K12.e("ishowspeed")), new C79("lospollos", "LosPollos", K12.e("lospollos")), new C79("adapt", "Adapt", K12.e("adapt")))), new C80("nintendo", "Nintendo Pack", List.of(new C79("mario", "Mario", K12.e("mario")), new C79("luigi", "Luigi", K12.e("luigi")), new C79("bowser", "Bowser", K12.e("bowser")), new C79("rosalina", "Rosalina", K12.e("rosalina")), new C79("yoshi", "Yoshi", K12.e("yoshi")), new C79("shyguy", "ShyGuy", K12.e("shyguy")), new C79("kirby", "Kirby", K12.e("kirby")), new C79("zelda", "Zelda", K12.e("zelda")))), new C80("sml", "SML Pack", List.of(new C79("jeffy", "Jeffy", K12.e("jeffy")), new C79("marvin", "Marvin", K12.e("marvin")), new C79("cody", "Cody", K12.e("cody")), new C79("joseph", "Joseph", K12.e("joseph")), new C79("brooklynguy", "Brooklyn Guy", K12.e("brooklynguy")), new C79("junior", "Junior", K12.e("junior")), new C79("mrgoodman", "Mr GoodMen", K12.e("mrgoodman")), new C79("chefpp", "Chef PP", K12.e("chefpp")))), new C80("horror", "Horror Pack", List.of(new C79("freddy", "Freddy", K12.e("freddy")), new C79("ghostface", "GhostFace", K12.e("ghostface")), new C79("helloneighbor", "Hello Neighbor", K12.e("helloneighbor")), new C79("granny", "Granny", K12.e("granny")), new C79("jason", "Jason", K12.e("jason")), new C79("chucky", "Chucky", K12.e("chucky")), new C79("michaelmyers", "Michael Myers", K12.e("michaelmyers")), new C79("bendy", "Bendy", K12.e("bendy")))), new C80("movie", "Movie Pack", List.of(new C79("harrypotter", "Harry Potter", K12.e("harrypotter")), new C79("darthvader", "Darth Vader", K12.e("darthvader")), new C79("patrickjane", "Patrick Jane", K12.e("patrickjane")), new C79("lightningmcqueen", "Lightning McQueen", K12.e("lightningmcqueen")), new C79("shrek", "Shrek", K12.e("shrek")), new C79("spongebob", "SpongeBob", K12.e("spongebob")), new C79("dextermorgan", "Dexter Morgan", K12.e("dextermorgan")), new C79("angrybird", "Angry Bird", K12.e("angrybird")))), new C80("sports", "Sports Pack", List.of(new C79("cristianoronaldo", "Cristiano Ronaldo", K12.e("cristianoronaldo")), new C79("lionelmessi", "Lionel Messi", K12.e("lionelmessi")), new C79("lebronjames", "LeBron James", K12.e("lebronjames")), new C79("kobebryant", "Kobe Bryant", K12.e("kobebryant")), new C79("harrykane", "Harry Kane", K12.e("harrykane")), new C79("michaeljordan", "Michael Jordan", K12.e("michaeljordan")), new C79("connormcdavid", "Connor McDavid", K12.e("connormcdavid")), new C79("stephcurry", "Steph Curry", K12.e("stephcurry")), new C79("tombrady", "Tom Brady", K12.e("tombrady")), new C79("waynegretzky", "Wayne Gretzky", K12.e("waynegretzky")))), new C80("animal", "Animal Pack", List.of(new C79("dog", "Dog", K12.e("dog")), new C79("cat", "Cat", K12.e("cat")), new C79("wolf", "Wolf", K12.e("wolf")), new C79("bee", "Bee", K12.e("bee")), new C79("dolphin", "Dolphin", K12.e("dolphin")), new C79("turtle", "Turtle", K12.e("turtle")), new C79("chick", "Chick", K12.e("chick")), new C79("mouse", "Mouse", K12.e("mouse")))));
    public static final List<C79> b = a.stream().flatMap(p -> p.c().stream()).toList();
    public static final List<C77> cars = List.of(
        new C77("kart", "Go-Kart", 0xE53935, 0xFFEB3B, 0x222222, 0),
        new C77("sport", "Sports Car", 0x1E88E5, 0xFFFFFF, 0x15151A, 1),
        new C77("truck", "Monster Truck", 0x43A047, 0xFF7043, 0x2E2E2E, 2),
        new C77("retro", "Convertible", 0xEC407A, 0xFFF176, 0x5D4037, 3),
        new C77("racer", "Flame Racer", 0xFF6F00, 0x212121, 0x111111, 1),
        new C77("police", "Police Cruiser", 0x1565C0, 0xFFFFFF, 0x0D0D0D, 1));
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
    private static PlayerModel v;
    private static long w;
    private static final C78 x;
    private static final Map<UUID, C78> y;
    private static BlockPos z;
    public static final int[] c;
    public static final boolean d = false;
    private static final long A = 1900L;
    private static final long B = 450L;
    private static final float C = 0.06f;
    private static final int[][] D;
    private static final int[][] E;
    private static final float[][] F;

    private static Identifier e(String file) {
        return Identifier.fromNamespaceAndPath((String)"lethalauction", (String)("textures/figure/" + file + ".png"));
    }

    public static C80 a(String figureId) {
        for (C80 p : a) {
            for (C79 f : p.c()) {
                if (!f.a().equals(figureId)) continue;
                return p;
            }
        }
        return null;
    }

    private K12() {
    }

    public static C79 b(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (C79 f : b) {
            if (!f.a.equals(id)) continue;
            return f;
        }
        return null;
    }

    public static C79 a() {
        return K12.b(K9.aI);
    }

    public static C77 car() {
        if (K9.car == null || K9.car.isEmpty()) {
            return null;
        }
        for (C77 c : cars) {
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
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            K12.x.x = mc.player.getX();
            K12.x.z = mc.player.getZ();
        }
    }

    public static BlockPos b() {
        return z;
    }

    public static void a(Minecraft mc) {
        BlockHitResult bhr;
        if (z != null) {
            z = null;
            return;
        }
        if (K12.a() == null || mc.player == null) {
            return;
        }
        HitResult hitResult = mc.hitResult;
        if (hitResult instanceof BlockHitResult && (bhr = (BlockHitResult)hitResult).getType() != HitResult.Type.MISS) {
            BlockPos bp = bhr.getBlockPos();
            double surf = K12.d((Level)mc.level, (double)bp.getX() + 0.5, (double)bp.getZ() + 0.5, bp.getY() + 2);
            z = new BlockPos(bp.getX(), (int)Math.floor(surf), bp.getZ());
            K12.x.B = 0;
            K12.x.A = System.currentTimeMillis();
        }
    }

    public static boolean c() {
        return !K12.x.U.isEmpty() && System.currentTimeMillis() < K12.x.W;
    }

    public static boolean b(Minecraft mc) {
        return false;
    }

    public static boolean c(Minecraft mc) {
        return false;
    }

    private static void a(Minecraft mc, long now) {
        boolean crit;
        LocalPlayer p = mc.player;
        float scale = p.getAttackStrengthScale(0.5f);
        p.resetAttackStrengthTicker();
        boolean charged = scale > 0.85f;
        boolean sprint = p.isSprinting();
        boolean bl = crit = charged && K12.b((Player)p);
        float dmg = charged ? K12.a((Player)p) * (crit ? 1.5f : 1.0f) : 0.3f;
        K12.x.H -= dmg;
        K12.x.B = 0;
        K12.x.A = now;
        K12.x.S = now;
        K12.x.N = now + 500L;
        p.swing(InteractionHand.MAIN_HAND);
        double kx = K12.x.a.x - p.getX();
        double kz = K12.x.a.z - p.getZ();
        double kl = Math.sqrt(kx * kx + kz * kz);
        if (kl < 1.0E-4) {
            kx = -Math.sin(Math.toRadians(p.getYRot()));
            kz = Math.cos(Math.toRadians(p.getYRot()));
            kl = 1.0;
        }
        float power = Math.max(0.25f, scale);
        double kb = (crit ? 0.58 : (sprint ? 0.54 : 0.38)) * (double)power;
        K12.x.K = kx / kl * kb;
        K12.x.L = kz / kl * kb;
        K12.x.M = (crit ? 0.34f : 0.24f) * Math.max(0.35f, scale);
        K12.x.T = K12.x.a.y;
        K12.x.J = now + 2500L;
        try {
            SoundEvent atk = crit ? SoundEvents.PLAYER_ATTACK_CRIT : (!charged ? SoundEvents.PLAYER_ATTACK_WEAK : (sprint || kb > 0.5 ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_STRONG));
            mc.level.playLocalSound(p.getX(), p.getY(), p.getZ(), atk, SoundSource.PLAYERS, 1.0f, 1.0f, false);
            mc.level.playLocalSound(K12.x.a.x, K12.x.a.y + 0.6, K12.x.a.z, SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }
        catch (Throwable atk) {
            // empty catch block
        }
        if (crit) {
            try {
                int i;
                double cx = K12.x.a.x;
                double cy = K12.x.a.y + 0.9;
                double cz = K12.x.a.z;
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                for (i = 0; i < 12; ++i) {
                    mc.level.addParticle((ParticleOptions)ParticleTypes.CRIT, cx + (((Random)rnd).nextDouble() - 0.5) * 0.7, cy + ((Random)rnd).nextDouble() * 0.7, cz + (((Random)rnd).nextDouble() - 0.5) * 0.7, (((Random)rnd).nextDouble() - 0.5) * 0.3, ((Random)rnd).nextDouble() * 0.3, (((Random)rnd).nextDouble() - 0.5) * 0.3);
                }
                for (i = 0; i < 6; ++i) {
                    mc.level.addParticle((ParticleOptions)ParticleTypes.DAMAGE_INDICATOR, cx + (((Random)rnd).nextDouble() - 0.5) * 0.5, cy + (((Random)rnd).nextDouble() - 0.5) * 0.5, cz + (((Random)rnd).nextDouble() - 0.5) * 0.5, (((Random)rnd).nextDouble() - 0.5) * 0.2, 0.1 + ((Random)rnd).nextDouble() * 0.2, (((Random)rnd).nextDouble() - 0.5) * 0.2);
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

    private static float a(Player p) {
        ItemStack s = p.getMainHandItem();
        float base = 1.0f;
        try {
            int sharp;
            int mat;
            String id = BuiltInRegistries.ITEM.getKey(s.getItem()).getPath();
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

    private static int a(Player p, ItemStack s) {
        try {
            Holder.Reference holder = p.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);
            return EnchantmentHelper.getItemEnchantmentLevel((Holder)holder, (ItemInstance)s);
        }
        catch (Throwable t) {
            return 0;
        }
    }

    private static int a(C78 b) {
        return System.currentTimeMillis() < b.N ? OverlayTexture.pack((float)0.0f, (boolean)true) : OverlayTexture.NO_OVERLAY;
    }

    private static boolean b(Player p) {
        return !p.onGround() && p.getDeltaMovement().y < 0.0 && !p.isInWater() && !p.isPassenger();
    }

    public static void d(Minecraft mc) {
        if (mc.player == null || mc.level == null || !K37.e()) {
            K12.x.b = null;
            K12.x.a = null;
            y.clear();
            return;
        }
        C79 mine = K12.a();
        if (mine != null) {
            K12.x.w = mine.a();
            try {
                K12.a(x, (Player)mc.player, (Level)mc.level);
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
        UUID me = mc.player.getUUID();
        HashSet<UUID> activeNow = new HashSet<UUID>();
        for (Player p : mc.level.players()) {
            String figId;
            UUID id2 = p.getUUID();
            if (id2 == null || id2.equals(me) || (figId = K38.b(id2)) == null || figId.isEmpty() || K12.b(figId) == null) continue;
            activeNow.add(id2);
            C78 b = y.get(id2);
            if (b == null) {
                b = new C78();
                y.put(id2, b);
            }
            if (!figId.equals(b.w)) {
                b.w = figId;
                b.b = null;
                b.a = null;
            }
            try {
                K12.a(b, p, (Level)mc.level);
            }
            catch (Throwable t) {
                b.b = null;
                b.a = null;
            }
        }
        y.keySet().removeIf(id -> !activeNow.contains(id));
    }

    private static void a(C78 b, Player p, Level lvl) {
        boolean onGround;
        double pvz;
        boolean frozen;
        boolean airborne;
        boolean ko;
        boolean selfWp;
        double px = p.getX();
        double py = p.getY();
        double pz = p.getZ();
        long now = System.currentTimeMillis();
        if (b.a == null) {
            b.b = b.a = new Vec3(px + 0.7, py, pz);
            b.e = b.f = p.yBodyRot;
            b.n = 0;
            b.t = 0.0f;
            b.s = 0.0f;
            b.v = now + 480L;
        }
        double playerStep = Math.abs(px - p.xOld) + Math.abs(py - p.yOld) + Math.abs(pz - p.zOld);
        boolean bl = selfWp = b == x && z != null;
        if (selfWp) {
            px = (double)z.getX() + 0.5;
            py = z.getY();
            pz = (double)z.getZ() + 0.5;
            playerStep = 0.0;
        }
        double awayNow = Math.sqrt((px - b.a.x) * (px - b.a.x) + (pz - b.a.z) * (pz - b.a.z));
        if (playerStep > 12.0 || awayNow > 18.0) {
            double gy = K12.d(lvl, px + 0.7, pz, py);
            b.b = b.a = new Vec3(px + 0.7, gy, pz);
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
            double nkx = b.a.x;
            double nkz = b.a.z;
            double total = Math.abs(b.K) + Math.abs(b.L);
            int sub = Math.max(1, Math.min(10, (int)Math.ceil(total / 0.1)));
            double sx = b.K / (double)sub;
            double sz = b.L / (double)sub;
            for (int i = 0; i < sub; ++i) {
                if (K12.b(lvl, nkx + sx, b.a.y, nkz + sz)) {
                    nkx += sx;
                    nkz += sz;
                    continue;
                }
                if (K12.b(lvl, nkx + sx, b.a.y, nkz)) {
                    nkx += sx;
                    b.L = 0.0;
                    continue;
                }
                if (K12.b(lvl, nkx, b.a.y, nkz + sz)) {
                    nkz += sz;
                    b.K = 0.0;
                    continue;
                }
                b.K = 0.0;
                b.L = 0.0;
                break;
            }
            b.M -= 0.08;
            double nky = b.a.y + b.M;
            double landY = K12.d(lvl, nkx, nkz, b.a.y);
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
            b.a = new Vec3(nkx, nky, nkz);
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
            b.e = Mth.rotLerp((float)0.15f, (float)b.e, (float)((float)Math.toDegrees(Math.atan2(-(px - b.a.x), pz - b.a.z))));
            return;
        }
        if (ko) {
            b.b = b.a;
            b.f = b.e;
            b.h = b.g;
            double gy = K12.d(lvl, b.a.x, b.a.z, b.a.y);
            b.a = new Vec3(b.a.x, b.a.y + (gy - b.a.y) * 0.4, b.a.z);
            b.i += (0.0f - b.i) * 0.3f;
            return;
        }
        boolean bl4 = frozen = now < b.J;
        if (now < b.W) {
            b.b = b.a;
            b.f = b.e;
            b.h = b.g;
            b.t = b.s;
            double gy = K12.d(lvl, b.a.x, b.a.z, b.a.y);
            b.a = new Vec3(b.a.x, b.a.y + (gy - b.a.y) * 0.4, b.a.z);
            b.i += (0.0f - b.i) * 0.3f;
            b.e = Mth.rotLerp((float)0.15f, (float)b.e, (float)((float)Math.toDegrees(Math.atan2(-(px - b.a.x), pz - b.a.z))));
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
            b.D = b.a.x;
            b.E = b.a.z;
            b.F = K12.d(lvl, b.a.x, b.a.z, b.a.y);
            b.G = (float)Math.toDegrees(Math.atan2(-(px - b.a.x), pz - b.a.z));
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
            b.a = new Vec3(b.D, b.F, b.E);
            b.i += (0.0f - b.i) * 0.3f;
            b.e = b.G;
            b.n = 0;
            b.s += (0.0f - b.s) * 0.3f;
            return;
        }
        b.b = b.a;
        b.f = b.e;
        double pvx = p.getX() - p.xOld;
        double pspeed = Math.sqrt(pvx * pvx + (pvz = p.getZ() - p.zOld) * pvz);
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
        double toRest = Math.sqrt((restX - b.a.x) * (restX - b.a.x) + (restZ - b.a.z) * (restZ - b.a.z));
        double nx = b.a.x;
        double nz = b.a.z;
        if (toRest > 0.18 && b.n == 0 && !frozen) {
            double[] veer;
            double MAXSTEP = 0.28;
            double ease = 0.14;
            double dirX = restX - b.a.x;
            double dirZ = restZ - b.a.z;
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
                double cnx = b.a.x + vx * stepLen;
                if (!K12.c(lvl, cnx, b.a.y, cnz = b.a.z + (vz = ux * s + uz * c) * stepLen)) continue;
                nx = cnx;
                nz = cnz;
                stepped = true;
                break;
            }
            if (!stepped) {
                double cnx = b.a.x + ux * stepLen;
                double cnz = b.a.z + uz * stepLen;
                if (K12.c(lvl, cnx, b.a.y, b.a.z)) {
                    nx = cnx;
                }
                if (K12.c(lvl, b.a.x, b.a.y, cnz)) {
                    nz = cnz;
                }
            }
        }
        double groundY = K12.d(lvl, nx, nz, b.a.y);
        double hdx = px - nx;
        double hdz = pz - nz;
        double hdist = Math.sqrt(hdx * hdx + hdz * hdz);
        if (p.onGround() && hdist < 2.6 && groundY < py - 1.5) {
            groundY = py;
        }
        if (groundY > b.a.y + 0.45 && b.n == 0) {
            b.j = now + 420L;
        }
        double heightAbove = b.a.y - groundY;
        if (b.n == 0 && heightAbove >= 2.5) {
            b.n = 2;
            b.l = b.a.y;
        }
        double ny = b.n == 2 ? b.a.y + (groundY - b.a.y) * 0.1 : b.a.y + (groundY - b.a.y) * 0.5;
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
        b.a = new Vec3(nx, ny, nz);
        b.O = false;
        double figMoved = Math.sqrt((b.a.x - b.b.x) * (b.a.x - b.b.x) + (b.a.z - b.b.z) * (b.a.z - b.b.z));
        double distP = Math.sqrt((px - b.a.x) * (px - b.a.x) + (pz - b.a.z) * (pz - b.a.z));
        if (b.n != 0 || distP < 2.0 || figMoved > 0.02 || frozen) {
            b.u = now;
        }
        if (b.n == 0 && !frozen && distP > 2.0 && now - b.u > 900L) {
            double gy = K12.d(lvl, px + 0.7, pz, py);
            b.a = b.b = new Vec3(px + 0.7, gy, pz);
            b.u = now;
        }
        b.t = b.s;
        b.s = b.s + ((b.n == 2 ? 1.0f : 0.0f) - b.s) * 0.3f;
        double moved = Math.sqrt((b.a.x - b.b.x) * (b.a.x - b.b.x) + (b.a.z - b.b.z) * (b.a.z - b.b.z));
        float targetMove = (float)Math.min(moved * 7.0, 1.0);
        b.i += (targetMove - b.i) * 0.3f;
        b.h = b.g;
        b.g += (float)moved * 2.1f;
        double ddx = b.a.x - b.b.x;
        double ddz = b.a.z - b.b.z;
        float targetYaw = moved > 0.015 ? (float)Math.toDegrees(Math.atan2(-ddx, ddz)) : (float)Math.toDegrees(Math.atan2(-(px - b.a.x), pz - b.a.z));
        b.e = Mth.rotLerp((float)(moved > 0.015 ? 0.22f : 0.08f), (float)b.e, (float)targetYaw);
    }

    private static void a(C78 b, Player p, Level lvl, long now) {
        Vec3 from;
        double px = p.getX();
        double py = p.getY();
        double pz = p.getZ();
        float yaw = p.yBodyRot;
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
        Vec3 vec3 = from = b.r != null ? b.r : b.a;
        if (b.p == 1) {
            float prog = Math.min(1.0f, (float)(now - b.q) / 1900.0f);
            float e = prog * prog * (3.0f - 2.0f * prog);
            float slip = (float)(0.14 * Math.sin((double)prog * Math.PI * 3.0) * (1.0 - (double)prog));
            float frac = Math.max(0.0f, Math.min(1.0f, e - slip));
            b.a = new Vec3(from.x + (sX - from.x) * (double)frac, from.y + (sY - from.y) * (double)frac, from.z + (sZ - from.z) * (double)frac);
            b.e = yaw + 180.0f;
            if (prog >= 1.0f) {
                b.p = 2;
                b.q = now;
            }
        } else if (b.p == 2) {
            double sway = Math.sin((double)now / 900.0) * 0.012;
            b.a = new Vec3(sX, sY + sway, sZ);
            b.e = Mth.rotLerp((float)0.3f, (float)b.e, (float)(yaw + 180.0f));
        } else if (b.p == 3) {
            float prog = Math.min(1.0f, (float)(now - b.q) / 450.0f);
            double gx = px + rightX * 0.75;
            double gz = pz + rightZ * 0.75;
            double gy = K12.d(lvl, gx, gz, py);
            double y = from.y + (gy - from.y) * (double)prog + Math.sin((double)prog * Math.PI) * 0.22;
            b.a = new Vec3(from.x + (gx - from.x) * (double)prog, y, from.z + (gz - from.z) * (double)prog);
            b.e = Mth.rotLerp((float)0.3f, (float)b.e, (float)yaw);
            if (prog >= 1.0f) {
                b.p = 0;
                b.a = new Vec3(gx, gy, gz);
                b.o = now + 220L;
            }
        }
    }

    private static boolean a(Level lvl, double x, double y, double z) {
        try {
            return lvl.noCollision(new AABB(x - 0.17, y + 0.1, z - 0.17, x + 0.17, y + 1.0, z + 0.17));
        }
        catch (Throwable t) {
            return true;
        }
    }

    private static boolean b(Level lvl, double x, double y, double z) {
        return K12.a(lvl, x, y, z) && K12.d(lvl, x, z, y) <= y + 0.3;
    }

    private static Vec3 a(Level lvl, Vec3 pos) {
        for (double r = 0.3; r <= 1.8; r += 0.3) {
            for (int a = 0; a < 12; ++a) {
                double z;
                double ang = (double)a * Math.PI / 6.0;
                double x = pos.x + Math.cos(ang) * r;
                if (!K12.a(lvl, x, pos.y, z = pos.z + Math.sin(ang) * r) || !K12.c(lvl, x, pos.y, z)) continue;
                return new Vec3(x, K12.d(lvl, x, z, pos.y), z);
            }
        }
        return null;
    }

    private static boolean c(Level lvl, double x, double curY, double z) {
        try {
            double gy = K12.d(lvl, x, z, curY);
            if (gy - curY > 1.2) {
                return false;
            }
            return lvl.noCollision(new AABB(x - 0.17, gy + 0.1, z - 0.17, x + 0.17, gy + 1.0, z + 0.17));
        }
        catch (Throwable t) {
            return true;
        }
    }

    private static double d(Level lvl, double x, double z, double aroundY) {
        try {
            int base = Mth.floor((double)aroundY);
            for (int i = 1; i >= -256; --i) {
                boolean solid;
                int by = base + i;
                boolean bl = solid = !lvl.noCollision(new AABB(x - 0.15, (double)by + 0.02, z - 0.15, x + 0.15, (double)by + 0.98, z + 0.15));
                if (!solid) continue;
                double top = (double)by + 1.0;
                try {
                    BlockPos pos = new BlockPos(Mth.floor((double)x), by, Mth.floor((double)z));
                    VoxelShape sh = lvl.getBlockState(pos).getCollisionShape((BlockGetter)lvl, pos);
                    if (!sh.isEmpty()) {
                        top = (double)by + sh.max(Direction.Axis.Y);
                    }
                }
                catch (Throwable pos) {
                    // empty catch block
                }
                boolean clearAbove = lvl.noCollision(new AABB(x - 0.15, top + 0.1, z - 0.15, x + 0.15, top + 1.0, z + 0.15));
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
    public static void a(LevelRenderContext ctx) {
        try {
            C79 mine;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (!K37.e()) {
                return;
            }
            long now = System.currentTimeMillis();
            if (v == null) {
                if (now < w) {
                    return;
                }
                try {
                    v = new PlayerModel(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
                }
                catch (Throwable t) {
                    v = null;
                    w = now + 1500L;
                    try {
                        u.a(ctx.submitNodeCollector());
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
            Iterator<C78> iterator = y.values().iterator();
            while (iterator.hasNext()) {
                C78 b = iterator.next();
                C79 f = K12.b(b.w);
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
                u.a(ctx.submitNodeCollector());
            }
            catch (Throwable throwable) {}
        }
    }

    // ===== Rideable car (local figure only) =====
    private static void renderCar(LevelRenderContext ctx, Minecraft mc, C78 b, C79 figure, double rx, double ry, double rz, float yaw, float pt) {
        C77 car = K12.car();
        if (car == null) {
            return;
        }
        long nowMs = System.currentTimeMillis();
        // steering: smoothed per-tick yaw change
        float dYaw = (float)Mth.wrapDegrees((double)(b.e - b.f));
        float steerTarget = Mth.clamp((float)dYaw * 0.07f, -0.55f, 0.55f);
        b.X += (steerTarget - b.X) * 0.2f;
        float steer = b.X;
        float rphase = Mth.lerp((float)pt, (float)b.h, (float)b.g);
        float speed = Mth.clamp((float)b.i, 0.0f, 1.0f);
        float bob = (float)Math.sin((double)rphase * 2.0) * 0.016f * speed + (float)Math.sin((double)nowMs / 500.0) * 0.004f;
        float pitch = (float)Math.sin((double)rphase) * 0.03f * speed;
        float bank = -steer * 0.5f;
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        double dx = rx - cam.x;
        double dyBase = ry - cam.y;
        double dz = rz - cam.z;
        double dy = dyBase + (double)bob;
        boolean thirdPerson = !mc.options.getCameraType().isFirstPerson();
        double midDy = ry + 0.4 - cam.y;
        if (thirdPerson && dx * dx + midDy * midDy + dz * dz < 0.9) {
            return;
        }
        float yawRad = (float)Math.toRadians(180.0 - (double)yaw);
        // ---- car body + wheels (world space, camera-relative) ----
        Matrix4f base = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
        base.translate((float)dx, (float)dy, (float)dz);
        base.rotateY(yawRad);
        base.rotateZ(bank);
        base.rotateX(pitch);
        VertexConsumer vc = u.a(RenderTypes.entityCutout((Identifier)K10.a));
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
        // ---- seated figure ----
        v.resetPose();
        float hs = 1.7f;
        K12.v.head.yScale = K12.v.head.zScale = hs;
        K12.v.head.xScale = K12.v.head.zScale;
        K12.v.hat.zScale = 1.0f;
        K12.v.hat.yScale = 1.0f;
        K12.v.hat.xScale = 1.0f;
        float grip = (float)Math.sin((double)nowMs / 170.0) * 0.05f * speed;
        K12.v.rightLeg.xRot = -1.45f;
        K12.v.leftLeg.xRot = -1.45f;
        K12.v.rightLeg.zRot = 0.12f;
        K12.v.leftLeg.zRot = -0.12f;
        K12.v.rightArm.xRot = -1.12f - 0.12f * Math.abs(steer) + grip;
        K12.v.leftArm.xRot = -1.12f - 0.12f * Math.abs(steer) - grip;
        K12.v.rightArm.zRot = 0.34f + steer * 0.45f;
        K12.v.leftArm.zRot = -0.34f + steer * 0.45f;
        K12.v.head.yRot = steer * 0.5f;
        K12.v.head.xRot = 0.06f;
        float appear = 1.0f;
        if (nowMs < b.v) {
            float t = 1.0f - (float)(b.v - nowMs) / 480.0f;
            float s = t - 1.0f;
            float c1 = 1.70158f;
            appear = Math.max(0.0f, 1.0f + (c1 + 1.0f) * s * s * s + c1 * s * s);
        }
        float fsc = 0.5f * appear;
        PoseStack ps = ctx.poseStack();
        ps.pushPose();
        ps.translate(dx, dy + 0.30, dz);
        ps.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - yaw));
        ps.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)Math.toDegrees((double)bank)));
        ps.mulPose((Quaternionfc)Axis.XP.rotationDegrees((float)Math.toDegrees((double)pitch)));
        ps.scale(fsc, fsc, fsc);
        ps.scale(-1.0f, -1.0f, 1.0f);
        ps.translate(0.0f, -1.501f, 0.0f);
        v.renderToBuffer(ps, u.a(RenderTypes.entityCutout((Identifier)figure.c())), 0xF000F0, OverlayTexture.NO_OVERLAY, -1);
        ps.popPose();
    }

    private static void carBody(VertexConsumer vc, Matrix4f m, C77 car) {
        int body = car.body();
        int accent = car.accent();
        int roof = car.roof();
        int style = car.style();
        int dark = 0x15151A;
        int glass = 0x2A3A4A;
        int light = 0xFFF2C0;
        int tail = 0xE03030;
        // chassis
        K12.a(vc, m, -0.30f, 0.10f, -0.44f, 0.30f, 0.18f, 0.44f, dark);
        // main hull
        K12.a(vc, m, -0.28f, 0.16f, -0.40f, 0.28f, 0.34f, 0.40f, body);
        // front hood (+z)
        K12.a(vc, m, -0.26f, 0.16f, 0.24f, 0.26f, 0.30f, 0.46f, body);
        // rear deck (-z)
        K12.a(vc, m, -0.26f, 0.16f, -0.46f, 0.26f, 0.30f, -0.24f, body);
        // side accent stripes
        K12.a(vc, m, -0.295f, 0.22f, -0.34f, -0.285f, 0.27f, 0.34f, accent);
        K12.a(vc, m, 0.285f, 0.22f, -0.34f, 0.295f, 0.27f, 0.34f, accent);
        // seat cushion + back
        K12.a(vc, m, -0.18f, 0.30f, -0.20f, 0.18f, 0.345f, 0.08f, dark);
        K12.a(vc, m, -0.18f, 0.30f, -0.22f, 0.18f, 0.58f, -0.16f, style == 3 ? accent : dark);
        // headlights / taillights
        K12.a(vc, m, -0.22f, 0.22f, 0.45f, -0.12f, 0.30f, 0.47f, light);
        K12.a(vc, m, 0.12f, 0.22f, 0.45f, 0.22f, 0.30f, 0.47f, light);
        K12.a(vc, m, -0.22f, 0.24f, -0.47f, -0.12f, 0.30f, -0.455f, tail);
        K12.a(vc, m, 0.12f, 0.24f, -0.47f, 0.22f, 0.30f, -0.455f, tail);
        // bumpers
        K12.a(vc, m, -0.28f, 0.14f, 0.46f, 0.28f, 0.22f, 0.48f, dark);
        K12.a(vc, m, -0.28f, 0.14f, -0.48f, 0.28f, 0.22f, -0.46f, dark);
        if (style == 1) {
            // sports: windshield
            K12.a(vc, m, -0.17f, 0.345f, 0.06f, 0.17f, 0.45f, 0.10f, glass);
        } else if (style == 2) {
            // truck: raised body + cabin + bed
            K12.a(vc, m, -0.28f, 0.34f, -0.12f, 0.28f, 0.62f, 0.14f, body);
            K12.a(vc, m, -0.24f, 0.42f, 0.12f, 0.24f, 0.58f, 0.145f, glass);
            K12.a(vc, m, -0.30f, 0.30f, -0.46f, 0.30f, 0.44f, -0.14f, body);
            K12.a(vc, m, -0.26f, 0.30f, -0.44f, 0.26f, 0.34f, -0.16f, dark);
            K12.a(vc, m, -0.32f, 0.02f, 0.26f, -0.20f, 0.18f, 0.46f, roof);
            K12.a(vc, m, 0.20f, 0.02f, 0.26f, 0.32f, 0.18f, 0.46f, roof);
            K12.a(vc, m, -0.32f, 0.02f, -0.46f, -0.20f, 0.18f, -0.26f, roof);
            K12.a(vc, m, 0.20f, 0.02f, -0.46f, 0.32f, 0.18f, -0.26f, roof);
        } else if (style == 3) {
            // convertible: small windshield frame
            K12.a(vc, m, -0.18f, 0.345f, 0.08f, 0.18f, 0.44f, 0.11f, glass);
            K12.a(vc, m, -0.19f, 0.345f, 0.07f, -0.16f, 0.46f, 0.12f, roof);
            K12.a(vc, m, 0.16f, 0.345f, 0.07f, 0.19f, 0.46f, 0.12f, roof);
        } else {
            // kart: roll bar
            K12.a(vc, m, -0.16f, 0.34f, -0.18f, -0.12f, 0.56f, -0.14f, dark);
            K12.a(vc, m, 0.12f, 0.34f, -0.18f, 0.16f, 0.56f, -0.14f, dark);
            K12.a(vc, m, -0.16f, 0.52f, -0.18f, 0.16f, 0.56f, -0.14f, dark);
        }
    }

    private static void carWheel(VertexConsumer vc, Matrix4f base, float x, float y, float z, float steer, float roll) {
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

    private static void carSteer(VertexConsumer vc, Matrix4f base, float steer) {
        Matrix4f m = new Matrix4f((Matrix4fc)base);
        m.translate(0.0f, 0.40f, 0.14f);
        m.rotateX(-0.6f);
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

    private static void a(LevelRenderContext ctx, Minecraft mc, C78 b, C79 figure) {
        float h;
        boolean thirdPerson;
        boolean chuting;
        float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        double rx = Mth.lerp((double)pt, (double)b.b.x, (double)b.a.x);
        double ry = Mth.lerp((double)pt, (double)b.b.y, (double)b.a.y);
        double rz = Mth.lerp((double)pt, (double)b.b.z, (double)b.a.z);
        float yaw = Mth.rotLerp((float)pt, (float)b.f, (float)b.e);
        if (b == x && !K9.car.isEmpty() && K12.car() != null) {
            K12.renderCar(ctx, mc, b, figure, rx, ry, rz, yaw, pt);
            return;
        }
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
        float rphase = Mth.lerp((float)pt, (float)b.h, (float)b.g);
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
        float chuteAmt = Mth.lerp((float)pt, (float)b.t, (float)b.s);
        boolean bl = chuting = chuteAmt > 0.1f;
        if (chuting) {
            bob = 0.0f;
        }
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        double dx = rx - cam.x;
        double dy = ry - cam.y + (double)bob;
        double dz = rz - cam.z;
        double midDy = ry + 0.55 - cam.y;
        boolean bl2 = thirdPerson = !mc.options.getCameraType().isFirstPerson();
        if (thirdPerson && dx * dx + midDy * midDy + dz * dz < 1.3224999999999998) {
            return;
        }
        if (b.p != 0 && !thirdPerson) {
            return;
        }
        v.resetPose();
        K12.v.head.yScale = K12.v.head.zScale = (h = 1.7f);
        K12.v.head.xScale = K12.v.head.zScale;
        K12.v.hat.zScale = 1.0f;
        K12.v.hat.yScale = 1.0f;
        K12.v.hat.xScale = 1.0f;
        K12.v.rightLeg.xRot = legSwing - climbTuck + squash * 0.45f;
        K12.v.leftLeg.xRot = -legSwing - climbTuck * 0.7f - squash * 0.45f;
        K12.v.rightArm.xRot = -legSwing * 0.5f - climbTuck * 0.6f;
        K12.v.leftArm.xRot = legSwing * 0.5f - climbTuck * 0.6f;
        K12.v.rightArm.zRot = 0.0f;
        K12.v.leftArm.zRot = 0.0f;
        if (chuting) {
            K12.v.rightArm.xRot = 2.8902652f;
            K12.v.leftArm.xRot = 2.8902652f;
            K12.v.rightArm.zRot = -0.35f;
            K12.v.leftArm.zRot = 0.35f;
            K12.v.rightLeg.xRot = 0.2f;
            K12.v.leftLeg.xRot = -0.2f;
        } else if (b.O) {
            float since = (float)(nowMs - b.S) / 1000.0f;
            float env = since < 0.1f ? since / 0.1f : Math.max(0.0f, 1.0f - (since - 0.1f) / 0.55f);
            env = Math.max(0.0f, Math.min(1.0f, env));
            float j = (float)Math.sin(Mth.lerp((float)pt, (float)b.Q, (float)b.P));
            K12.v.rightArm.xRot = (-1.3f + j * 0.12f) * env;
            K12.v.leftArm.xRot = (-1.3f - j * 0.12f) * env;
            K12.v.rightArm.zRot = -0.3f * env;
            K12.v.leftArm.zRot = 0.3f * env;
            K12.v.rightLeg.xRot = 0.22f * env;
            K12.v.leftLeg.xRot = -0.22f * env;
            K12.v.head.xRot = -0.12f * env;
        } else if (b.p == 1) {
            float cph = (float)nowMs / 110.0f;
            float s = (float)Math.sin(cph);
            K12.v.rightArm.xRot = -2.35f + s * 0.55f;
            K12.v.leftArm.xRot = -2.35f - s * 0.55f;
            K12.v.rightArm.zRot = -0.28f;
            K12.v.leftArm.zRot = 0.28f;
            K12.v.rightLeg.xRot = 0.55f - s * 0.55f;
            K12.v.leftLeg.xRot = 0.55f + s * 0.55f;
            K12.v.head.xRot = -0.3f;
            K12.v.head.zRot = (float)Math.sin((double)nowMs / 42.0) * 0.05f;
        } else if (b.p == 2) {
            float swing = (float)Math.sin((double)nowMs / 470.0) * 0.22f;
            K12.v.rightLeg.xRot = 0.4f + swing;
            K12.v.leftLeg.xRot = 0.4f - swing;
            K12.v.rightLeg.zRot = 0.05f;
            K12.v.leftLeg.zRot = -0.05f;
            K12.v.rightArm.xRot = 0.25f;
            K12.v.leftArm.xRot = 0.25f;
            K12.v.rightArm.zRot = -0.12f;
            K12.v.leftArm.zRot = 0.12f;
            K12.v.head.yRot = (float)Math.sin((double)nowMs / 1300.0) * 0.22f;
        }
        float appear = 1.0f;
        if (nowMs < b.v) {
            float t = 1.0f - (float)(b.v - nowMs) / 480.0f;
            float s = t - 1.0f;
            float c1 = 1.70158f;
            appear = Math.max(0.0f, 1.0f + (c1 + 1.0f) * s * s * s + c1 * s * s);
        }
        float sc = 0.55f * appear * (b.p != 0 ? 0.66f : 1.0f);
        PoseStack ps = ctx.poseStack();
        ps.pushPose();
        ps.translate(dx, dy, dz);
        ps.scale(sc, sc, sc);
        ps.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - yaw));
        ps.scale(-1.0f, -1.0f, 1.0f);
        ps.translate(0.0f, -1.501f, 0.0f);
        RenderType type = RenderTypes.entityCutout((Identifier)figure.c());
        v.renderToBuffer(ps, u.a(type), 0xF000F0, K12.a(b), -1);
        ps.popPose();
        if (chuteAmt > 0.02f && !K11.a()) {
            Matrix4f m = new Matrix4f((Matrix4fc)ps.last().pose());
            m.translate((float)dx, (float)(dy + 0.9), (float)dz);
            m.rotateY((float)Math.toRadians(180.0 - (double)yaw));
            float cs = chuteAmt * 0.2f;
            m.scale(cs);
            K11.a(m, u.a(RenderTypes.entityCutout((Identifier)K11.a)));
        }
    }

    private static void a(LevelRenderContext ctx, Minecraft mc, C78 b, C79 figure, double rx, double ry, double rz, float yaw) {
        float h;
        long now = System.currentTimeMillis();
        float t = (float)(now - b.V) / 1000.0f;
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        double dx = rx - cam.x;
        double dy = ry - cam.y;
        double dz = rz - cam.z;
        float reach = (float)(Math.sin((double)t * 2.8) + 1.0) * 0.5f;
        float chew = (float)Math.sin((double)t * 9.0);
        float sway = (float)Math.sin((double)t * 1.7);
        float rock = (float)Math.sin((double)t * 1.1);
        float yawRad = (float)Math.toRadians(180.0 - (double)yaw);
        float seatH = 0.4f;
        VertexConsumer prop = u.a(RenderTypes.entityCutout((Identifier)K10.a));
        v.resetPose();
        K12.v.head.yScale = K12.v.head.zScale = (h = 1.7f);
        K12.v.head.xScale = K12.v.head.zScale;
        K12.v.hat.zScale = 1.0f;
        K12.v.hat.yScale = 1.0f;
        K12.v.hat.xScale = 1.0f;
        K12.v.rightLeg.xRot = -1.5f + rock * 0.1f;
        K12.v.leftLeg.xRot = -1.5f - rock * 0.1f;
        K12.v.rightLeg.zRot = 0.12f + sway * 0.06f;
        K12.v.leftLeg.zRot = -0.12f + sway * 0.06f;
        K12.v.leftArm.xRot = -1.0f;
        K12.v.leftArm.zRot = -0.3f + sway * 0.05f;
        K12.v.rightArm.xRot = Mth.lerp((float)reach, (float)-0.85f, (float)-2.2f);
        K12.v.rightArm.zRot = Mth.lerp((float)reach, (float)0.25f, (float)0.62f);
        K12.v.head.xRot = 0.14f + (reach > 0.75f ? Math.abs(chew) * 0.08f : 0.0f);
        K12.v.head.yRot = sway * 0.2f;
        Matrix4f cm = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
        cm.translate((float)dx, (float)dy, (float)dz);
        cm.rotateY(yawRad);
        K12.a(prop, cm);
        PoseStack ps = ctx.poseStack();
        ps.pushPose();
        ps.translate(dx, dy + (double)seatH + (double)sway * 0.015, dz);
        ps.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - yaw));
        ps.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(sway * 4.0f));
        ps.scale(0.55f, 0.55f, 0.55f);
        ps.scale(-1.0f, -1.0f, 1.0f);
        ps.translate(0.0f, -1.501f, 0.0f);
        v.renderToBuffer(ps, u.a(RenderTypes.entityCutout((Identifier)figure.c())), 0xF000F0, OverlayTexture.NO_OVERLAY, -1);
        ps.popPose();
        Matrix4f pm = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
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
            Matrix4f km = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
            km.translate((float)dx, (float)dy, (float)dz);
            km.rotateY(yawRad);
            km.translate(0.12f + kx, ky, -0.24f + kz);
            km.rotateY(life * 13.0f);
            km.rotateX(life * 10.0f);
            float s = 0.035f;
            K12.a(prop, km, -s, -s, -s * 0.7f, s, s, s * 0.7f, cornCols[i % 3]);
        }
    }

    private static void a(VertexConsumer vc, Matrix4f m) {
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

    private static void b(VertexConsumer vc, Matrix4f m) {
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

    private static void a(LevelRenderContext ctx, Minecraft mc, C78 b, C79 figure, double rx, double ry, double rz) {
        float h;
        long nowMs = System.currentTimeMillis();
        float rise = b.B == 1 ? Math.min(1.0f, (float)(nowMs - b.C) / 700.0f) : (b.B == 3 ? Math.max(0.0f, 1.0f - (float)(nowMs - b.C) / 700.0f) : 1.0f);
        float ease = rise * rise * (3.0f - 2.0f * rise);
        double tsec = (double)nowMs / 1000.0;
        float scroll = (float)Math.sin(tsec * 3.2) * 0.09f;
        float nod = (float)Math.sin(tsec * 0.8) * 0.04f;
        float yaw = b.G;
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        double dx = rx - cam.x;
        double dy = ry - cam.y;
        double dz = rz - cam.z;
        v.resetPose();
        K12.v.head.yScale = K12.v.head.zScale = (h = 1.7f);
        K12.v.head.xScale = K12.v.head.zScale;
        K12.v.hat.zScale = 1.0f;
        K12.v.hat.yScale = 1.0f;
        K12.v.hat.xScale = 1.0f;
        K12.v.head.xRot = 0.32f * ease + nod;
        K12.v.rightLeg.xRot = -1.5f * ease;
        K12.v.leftLeg.xRot = -1.5f * ease;
        K12.v.rightLeg.zRot = 0.1f * ease;
        K12.v.leftLeg.zRot = -0.1f * ease;
        K12.v.rightArm.xRot = -1.85f * ease;
        K12.v.leftArm.xRot = -1.85f * ease + scroll;
        K12.v.rightArm.zRot = 0.18f * ease;
        K12.v.leftArm.zRot = -0.18f * ease;
        PoseStack ps = ctx.poseStack();
        ps.pushPose();
        ps.translate(dx, dy, dz);
        ps.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - yaw));
        ps.translate(0.0f, -0.3f * ease, 0.0f);
        ps.pushPose();
        ps.scale(0.55f, 0.55f, 0.55f);
        ps.scale(-1.0f, -1.0f, 1.0f);
        ps.translate(0.0f, -1.501f, 0.0f);
        v.renderToBuffer(ps, u.a(RenderTypes.entityCutout((Identifier)figure.c())), 0xF000F0, OverlayTexture.NO_OVERLAY, -1);
        ps.popPose();
        if (rise > 0.4f) {
            Matrix4f pm = new Matrix4f((Matrix4fc)ps.last().pose());
            pm.translate(0.0f, 0.78f, -0.34f);
            pm.rotateX(2.35f);
            K12.a(pm, scroll);
        }
        ps.popPose();
    }

    private static void b(LevelRenderContext ctx, Minecraft mc, C78 b, C79 figure, double rx, double ry, double rz) {
        float h;
        long now = System.currentTimeMillis();
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        double dx = rx - cam.x;
        double dy = ry - cam.y;
        double dz = rz - cam.z;
        v.resetPose();
        K12.v.head.yScale = K12.v.head.zScale = (h = 1.7f);
        K12.v.head.xScale = K12.v.head.zScale;
        K12.v.hat.zScale = 1.0f;
        K12.v.hat.yScale = 1.0f;
        K12.v.hat.xScale = 1.0f;
        float tw = (float)Math.sin((double)now / 120.0) * 0.05f;
        K12.v.rightArm.zRot = 1.3f + tw;
        K12.v.leftArm.zRot = -1.3f - tw;
        K12.v.rightLeg.zRot = 0.25f;
        K12.v.leftLeg.zRot = -0.25f;
        PoseStack ps = ctx.poseStack();
        ps.pushPose();
        ps.translate(dx, dy, dz);
        ps.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - b.e));
        ps.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0f));
        ps.scale(0.55f, 0.55f, 0.55f);
        ps.scale(-1.0f, -1.0f, 1.0f);
        ps.translate(0.0f, -1.501f, 0.0f);
        v.renderToBuffer(ps, u.a(RenderTypes.entityCutout((Identifier)figure.c())), 0xF000F0, K12.a(b), -1);
        ps.popPose();
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
            double sx = headX + Math.cos(a) * 0.26 - cam.x;
            double sy = hy + Math.sin((double)now / 250.0 + (double)i) * 0.03 - cam.y;
            double sz = headZ + Math.sin(a) * 0.26 - cam.z;
            Matrix4f sm = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
            sm.translate((float)sx, (float)sy, (float)sz);
            sm.rotateY(a);
            K12.c(u.a(RenderTypes.entityCutout((Identifier)K10.a)), sm);
        }
    }

    private static void a(LevelRenderContext ctx, Minecraft mc, int col) {
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        Matrix4f m = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
        m.translate((float)((double)z.getX() - cam.x), (float)((double)z.getY() - cam.y) + 0.06f, (float)((double)z.getZ() - cam.z));
        VertexConsumer vc = u.a(RenderTypes.entityCutout((Identifier)K10.a));
        int r = col >> 16 & 0xFF;
        int g = col >> 8 & 0xFF;
        int b = col & 0xFF;
        float lo = 0.16f;
        float hi = 0.84f;
        float hw = 0.06f;
        K12.a(vc, m, lo, lo, hi, hi, hw, r, g, b);
        K12.a(vc, m, lo, hi, hi, lo, hw, r, g, b);
    }

    private static void a(VertexConsumer vc, Matrix4f m, float ax, float az, float bx, float bz, float hw, int r, int g, int b) {
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
                vc.addVertex(p.x, p.y, p.z).setColor(r, g, b, 255).setUv(0.5f, 0.5f).setUv1(0, 10).setUv2(240, 240).setNormal(0.0f, 1.0f, 0.0f);
            }
        }
    }

    private static void c(VertexConsumer vc, Matrix4f m) {
        K12.a(vc, m, -0.055f, -0.018f, -0.006f, 0.055f, 0.018f, 0.006f, 16769594);
        K12.a(vc, m, -0.018f, -0.055f, -0.006f, 0.018f, 0.055f, 0.006f, 16769594);
    }

    private static void a(Matrix4f m, float scroll) {
        VertexConsumer vc = u.a(RenderTypes.entityCutout((Identifier)K10.a));
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

    private static void a(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
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
                    vc.addVertex(p.x, p.y, p.z).setColor(r, g, bl, 255).setUv(0.5f, 0.5f).setUv1(0, 10).setUv2(240, 240).setNormal(n.x, n.y, n.z);
                }
            }
        }
    }

    static {
        x = new C78();
        y = new HashMap<UUID, C78>();
        c = new int[]{4186367, 0xFF4D4D, 5111658, 16765498, 12083711, 0xFFFFFF, 16747069};
        D = new int[][]{{1994588, 15909198}, {6958192, 15760446}, {2112099, 14830154}, {2829634, 5032432}};
        E = new int[][]{{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {1, 5, 7, 3}, {0, 2, 6, 4}};
        F = new float[][]{{-1.0f, 0.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};
    }

    public static final class C80 {
        private final String a;
        private final String b;
        private final List<C79> c;
        C80(String a, String b, List<C79> c) { this.a = a; this.b = b; this.c = c; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public List<C79> c() { return this.c; }


    }

    public static final class C79 {
        private final String a;
        private final String b;
        private final Identifier c;
        C79(String a, String b, Identifier c) { this.a = a; this.b = b; this.c = c; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public Identifier c() { return this.c; }


    }

    public static final class C77 {
        private final String a;
        private final String b;
        private final int d;
        private final int e;
        private final int f;
        private final int g;
        C77(String a, String b, int body, int accent, int roof, int style) {
            this.a = a; this.b = b; this.d = body; this.e = accent; this.f = roof; this.g = style;
        }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public int body() { return this.d; }
        public int accent() { return this.e; }
        public int roof() { return this.f; }
        public int style() { return this.g; }
    }

    private static final class C78 {
        Vec3 a;
        Vec3 b;
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
        Vec3 r;
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

        private C78() {
        }
    }
}

