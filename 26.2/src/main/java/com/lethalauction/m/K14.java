/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.Team
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K8;
import com.lethalauction.m.K9;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public final class K14 {
    private static final Identifier g = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"nametag");
    private static final String[] h = new String[]{"\ue000", "\ue001", "\ue004", "\ue005", "\ue002", "\ue003"};
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    public static final int d = 3;
    public static final int e = 4;
    public static final int f = 5;
    private static final String[] i = new String[]{"Member", "VIP", "Staff", "Manager", "Creator", "Owner"};

    private K14() {
    }

    public static boolean a() {
        return K37.e() && (K9.l || K37.h() < 4);
    }

    public static boolean b() {
        return K14.a() && K38.c();
    }

    public static int a(String rank) {
        return switch (rank == null ? "" : rank) {
            case "owner" -> 5;
            case "creator" -> 4;
            case "manager" -> 3;
            case "staff" -> 2;
            case "vip" -> 1;
            default -> 0;
        };
    }

    public static String a(int rank) {
        return i[Math.max(0, Math.min(i.length - 1, rank))];
    }

    public static int a(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(id)) {
            return K37.h();
        }
        return K38.c(id);
    }

    public static int b(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(id)) {
            return K37.i();
        }
        return K38.d(id);
    }

    public static Identifier c() {
        return g;
    }

    public static String b(int rank) {
        return h[Math.max(0, Math.min(h.length - 1, rank))];
    }

    public static String c(int rank) {
        return K14.b(rank);
    }

    public static String c(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        K8.C77 s = mc.player != null && mc.player.getUUID().equals(id) ? K8.a() : K8.c(K38.a(id));
        return s == null ? "" : s.c();
    }

    public static MutableComponent b(String glyphs) {
        return Component.literal((String)glyphs).withStyle(st -> st.withFont((FontDescription)new FontDescription.Resource(g)).withColor(0xFFFFFF));
    }

    public static Component a(Component name, UUID id) {
        String badge = K14.b(K14.a(id));
        int r2 = K14.b(id);
        String badge2 = r2 > 0 ? K14.b(r2) : "";
        String sticker = K14.c(id);
        Component donut = K14.d(id);
        MutableComponent out = Component.empty();
        if (!badge.isEmpty()) {
            out.append((Component)K14.b(badge));
            if (!badge2.isEmpty()) {
                out.append((Component)K14.b(badge2));
            }
            out.append(" ");
        }
        if (donut != null) {
            out.append(donut).append(" ");
        }
        out.append(name);
        if (!sticker.isEmpty()) {
            out.append(" ").append((Component)K14.b(sticker));
        }
        return out;
    }

    public static Component d(UUID id) {
        try {
            PlayerInfo info;
            MutableComponent tag;
            Scoreboard sb;
            PlayerTeam team;
            String name;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                return null;
            }
            Player p = mc.level.getPlayerByUUID(id);
            String string = name = p != null ? p.getScoreboardName() : null;
            if (name != null && (team = (sb = mc.level.getScoreboard()).getPlayersTeam(name)) != null && !(tag = PlayerTeam.formatNameForTeam((Team)team, (Component)Component.empty())).getString().trim().isEmpty()) {
                return tag;
            }
            PlayerInfo playerInfo = info = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(id);
            if (info != null && info.getTabListDisplayName() != null) {
                String nm = name == null ? "" : name;
                MutableComponent pre = Component.empty();
                boolean[] done = new boolean[]{false};
                info.getTabListDisplayName().visit((style, text) -> {
                    int idx;
                    if (done[0]) {
                        return Optional.empty();
                    }
                    int n = idx = nm.isEmpty() ? -1 : text.indexOf(nm);
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
            }
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static boolean a(Player player) {
        return player != null && K14.a() && (player == Minecraft.getInstance().player || K38.e(player.getUUID()));
    }

    public static Player a(Entity carrier) {
        for (Entity e = carrier.getVehicle(); e != null; e = e.getVehicle()) {
            if (!(e instanceof Player)) continue;
            Player p = (Player)e;
            return p;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }
        Vec3 pos = carrier.position();
        for (Player p : level.players()) {
            double dx = pos.x - p.getX();
            double dz = pos.z - p.getZ();
            double dy = pos.y - p.getY();
            if (!(dx * dx + dz * dz < 0.36) || !(dy > (double)p.getBbHeight() - 0.4) || !(dy < (double)p.getBbHeight() + 1.6)) continue;
            return p;
        }
        return null;
    }

    public static FormattedCharSequence e(UUID id) {
        String badge = K14.b(K14.a(id));
        if (badge.isEmpty()) {
            return FormattedCharSequence.EMPTY;
        }
        return FormattedCharSequence.composite((FormattedCharSequence)K14.b(badge).getVisualOrderText(), (FormattedCharSequence)Component.literal((String)" ").getVisualOrderText());
    }

    public static FormattedCharSequence f(UUID id) {
        String sticker = K14.c(id);
        if (sticker.isEmpty()) {
            return FormattedCharSequence.EMPTY;
        }
        return FormattedCharSequence.composite((FormattedCharSequence)Component.literal((String)" ").getVisualOrderText(), (FormattedCharSequence)K14.b(sticker).getVisualOrderText());
    }

    public static String a(FormattedCharSequence seq) {
        StringBuilder sb = new StringBuilder();
        seq.accept((index, style, codepoint) -> {
            sb.appendCodePoint(codepoint);
            return true;
        });
        return sb.toString();
    }
}

