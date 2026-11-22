/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.scores.Objective
 *  net.minecraft.world.scores.PlayerScoreEntry
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.Team
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import com.lethalauction.mixin.TabOverlayAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public final class K17 {
    private static final int a = 31996;
    private static final int b = 50677;
    private static final int[] c = new int[]{65280, 10814460, 0xFF0000, 16545539, 42751, 16770560};
    private static final String[] d = new String[]{"Money", "Shards", "Kills", "Deaths", "", "Playtime"};

    private K17() {
    }

    public static boolean a() {
        return K37.b() && K9.aV;
    }

    public static boolean a(GuiGraphicsExtractor g, Objective obj) {
        try {
            int titleW;
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            Scoreboard sb = obj.getScoreboard();
            List<PlayerScoreEntry> entries = new ArrayList<PlayerScoreEntry>(sb.listPlayerScores(obj));
            entries.removeIf(PlayerScoreEntry::isHidden);
            entries.sort(Comparator.comparingInt(PlayerScoreEntry::value).reversed());
            if (entries.size() > 15) {
                entries = entries.subList(0, 15);
            }
            ArrayList<Component> body = new ArrayList<Component>();
            body.add(Component.literal((String)" "));
            for (PlayerScoreEntry e : entries) {
                PlayerTeam team = sb.getPlayersTeam(e.owner());
                Component name = team != null ? PlayerTeam.formatNameForTeam((Team)team, (Component)e.ownerName()) : e.ownerName();
                String string = name.getString();
                if (K17.a(string.toLowerCase()) || string.trim().isEmpty()) continue;
                body.add(K17.a((Component)name));
            }
            body.add(Component.literal((String)" "));
            body.add(K17.b());
            Objects.requireNonNull(font);
            int lineH = 9;
            int maxW = titleW = K17.a(font);
            for (Component component : body) {
                maxW = Math.max(maxW, font.width((FormattedText)component));
            }
            int pad = 4;
            int n = maxW + pad * 2;
            int right = g.guiWidth();
            int left = right - n;
            int totalH = (body.size() + 1) * lineH + 4;
            int top = (g.guiHeight() - totalH) / 2;
            g.fill(left, top, right, top + lineH + 2, 0x70000000);
            g.fill(left, top + lineH + 2, right, top + totalH, 0x50000000);
            K17.a(g, font, (left + right) / 2, top + 2);
            int y = top + lineH + 4;
            for (Component component : body) {
                g.text(font, component.getVisualOrderText(), left + pad, y, -1, false);
                y += lineH;
            }
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static Component a(Component line) {
        ArrayList texts = new ArrayList();
        ArrayList styles = new ArrayList();
        line.visit((style, text) -> {
            texts.add(text);
            styles.add(style);
            return Optional.empty();
        }, Style.EMPTY);
        int iconRun = -1;
        int statColor = 0xFFFFFF;
        String label = null;
        long bestD = 9000L;
        for (int i = 0; i < styles.size(); ++i) {
            if (((String)texts.get(i)).trim().isEmpty() || ((Style)styles.get(i)).getColor() == null) continue;
            int c = ((Style)styles.get(i)).getColor().getValue() & 0xFFFFFF;
            for (int k = 0; k < K17.c.length; ++k) {
                long d = K17.a(c, K17.c[k]);
                if (d >= bestD) continue;
                bestD = d;
                iconRun = i;
                statColor = K17.c[k];
                label = K17.d[k].isEmpty() ? (line.getString().contains("(") ? "Team" : "Keyall") : K17.d[k];
            }
        }
        if (label == null) {
            return line;
        }
        boolean hasLabel = line.getString().toLowerCase().contains(label.toLowerCase());
        MutableComponent out = Component.empty();
        for (int i = 0; i < texts.size(); ++i) {
            if (i > iconRun) {
                out.append((Component)Component.literal((String)((String)texts.get(i))).withStyle(Style.EMPTY.withColor(statColor)));
            } else {
                out.append((Component)Component.literal((String)((String)texts.get(i))).setStyle((Style)styles.get(i)));
            }
            if (i != iconRun || hasLabel) continue;
            String sep = ((String)texts.get(i)).endsWith(" ") ? "" : " ";
            out.append((Component)Component.literal((String)(sep + label + " ")).withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        }
        return out;
    }

    private static boolean a(String low) {
        if (low.matches(".*\\d\\s*ms.*")) {
            return true;
        }
        return low.contains("na east") || low.contains("na west") || low.contains("eu ") || low.contains("asia") || low.contains("ocean");
    }

    private static Component b() {
        int ping = K17.c();
        Style gray = Style.EMPTY.withColor(10133678);
        Style blue = Style.EMPTY.withColor(42751);
        return Component.literal((String)"Ping ").withStyle(gray).append((Component)Component.literal((String)"(").withStyle(gray)).append((Component)Component.literal((String)(ping + "ms")).withStyle(blue)).append((Component)Component.literal((String)")").withStyle(gray));
    }

    private static int c() {
        try {
            Integer p;
            Minecraft mc = Minecraft.getInstance();
            PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (info != null && info.getTabListDisplayName() != null && (p = K17.b(info.getTabListDisplayName().getString())) != null) {
                return p;
            }
            TabOverlayAccessor tab = (TabOverlayAccessor)mc.gui.hud.getTabList();
            if (tab.lethalauction$footer() != null && (p = K17.b(tab.lethalauction$footer().getString())) != null) {
                return p;
            }
            if (tab.lethalauction$header() != null && (p = K17.b(tab.lethalauction$header().getString())) != null) {
                return p;
            }
            if (info != null && info.getLatency() > 1) {
                return info.getLatency();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }

    private static Integer b(String s) {
        if (s == null) {
            return null;
        }
        Matcher m = Pattern.compile("(\\d+)\\s*ms").matcher(s);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return null;
    }

    private static long a(int a, int b) {
        long dr = (a >> 16 & 0xFF) - (b >> 16 & 0xFF);
        long dg = (a >> 8 & 0xFF) - (b >> 8 & 0xFF);
        long db = (a & 0xFF) - (b & 0xFF);
        return dr * dr + dg * dg + db * db;
    }

    private static int a(Font font) {
        String s = "Donut SMP";
        int w = 0;
        for (int i = 0; i < s.length(); ++i) {
            w += font.width(K17.a(s.charAt(i), 0xFFFFFF));
        }
        return w;
    }

    private static void a(GuiGraphicsExtractor g, Font font, int cx, int y) {
        String s = "Donut SMP";
        int x = cx - K17.a(font) / 2;
        int n = s.length();
        for (int i = 0; i < n; ++i) {
            float f = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
            int col = 0xFF000000 | K17.a(31996, 50677, f);
            FormattedCharSequence seq = K17.a(s.charAt(i), col);
            g.text(font, seq, x, y, col, false);
            x += font.width(seq);
        }
    }

    private static FormattedCharSequence a(char c, int color) {
        return Component.literal((String)String.valueOf(c)).withStyle(Style.EMPTY.withBold(Boolean.valueOf(true)).withColor(color & 0xFFFFFF)).getVisualOrderText();
    }

    private static int a(int a, int b, float t) {
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        return Math.round((float)ar + (float)(br - ar) * t) << 16 | Math.round((float)ag + (float)(bg - ag) * t) << 8 | Math.round((float)ab + (float)(bb - ab) * t);
    }
}

