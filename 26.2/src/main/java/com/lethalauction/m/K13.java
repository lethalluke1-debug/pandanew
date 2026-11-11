/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.Team
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package com.lethalauction.m;

import com.lethalauction.m.K51;
import com.lethalauction.m.K54;
import java.util.Optional;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

public final class K13 {
    private static final K51 a = new K51();

    private K13() {
    }

    public static void a(LevelRenderContext ctx) {
        try {
            K13.b(ctx);
        }
        catch (Throwable throwable) {
        }
        finally {
            try {
                a.a(ctx.submitNodeCollector());
            }
            catch (Throwable throwable) {}
        }
    }

    private static void b(LevelRenderContext ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.gui.hud.isHidden()) {
            return;
        }
        if (mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        Component rank = K13.a(mc);
        if (rank == null) {
            return;
        }
        float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        double px = Mth.lerp((double)pt, (double)mc.player.xo, (double)mc.player.getX());
        double py = Mth.lerp((double)pt, (double)mc.player.yo, (double)mc.player.getY());
        double pz = Mth.lerp((double)pt, (double)mc.player.zo, (double)mc.player.getZ());
        double topY = py + (double)mc.player.getBbHeight() + 0.6;
        Matrix4f m = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
        m.translate((float)(px - cam.x), (float)(topY - cam.y), (float)(pz - cam.z));
        m.rotate((Quaternionfc)mc.gameRenderer.mainCamera().rotation());
        m.scale(-0.025f, -0.025f, 0.025f);
        Font font = mc.font;
        float w = (float)font.width((FormattedText)rank) / 2.0f;
        K54.a(font, rank, -w + 0.6f, 0.6f, 0x50000000, false, m, a, Font.DisplayMode.NORMAL, 0xF000F0);
        K54.a(font, rank, -w, 0.0f, -1, false, m, a, Font.DisplayMode.NORMAL, 0xF000F0);
    }

    public static Component a(Minecraft mc) {
        PlayerInfo info;
        MutableComponent tag;
        if (mc.player == null || mc.level == null) {
            return null;
        }
        String name = mc.player.getScoreboardName();
        Scoreboard sb = mc.level.getScoreboard();
        PlayerTeam team = sb.getPlayersTeam(name);
        if (team != null && !(tag = PlayerTeam.formatNameForTeam((Team)team, (Component)Component.empty())).getString().trim().isEmpty()) {
            return tag;
        }
        PlayerInfo playerInfo = info = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(mc.player.getUUID());
        if (info != null && info.getTabListDisplayName() != null) {
            Component tab = info.getTabListDisplayName();
            MutableComponent pre = Component.empty();
            boolean[] done = new boolean[]{false};
            tab.visit((style, text) -> {
                int idx;
                if (done[0]) {
                    return Optional.empty();
                }
                int n = idx = name.isEmpty() ? -1 : text.indexOf(name);
                if (idx >= 0) {
                    if (idx > 0) {
                        pre.append((Component)Component.literal((String)text.substring(0, idx)).setStyle(style));
                    }
                    done[0] = true;
                } else {
                    pre.append((Component)Component.literal((String)text).setStyle(style));
                }
                return Optional.empty();
            }, Style.EMPTY);
            if (!pre.getString().trim().isEmpty()) {
                return pre;
            }
            if (!tab.getString().trim().equals(name)) {
                return tab;
            }
        }
        return null;
    }
}

