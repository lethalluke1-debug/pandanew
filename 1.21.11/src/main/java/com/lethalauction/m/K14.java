/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11719
 *  net.minecraft.class_11719$class_11721
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  net.minecraft.class_5481
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K8;
import com.lethalauction.m.K9;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_11719;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import net.minecraft.class_638;
import net.minecraft.class_640;

public final class K14 {
    private static final class_2960 g = class_2960.method_60655((String)"lethalauction", (String)"nametag");
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
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null && mc.field_1724.method_5667().equals(id)) {
            return K37.h();
        }
        return K38.c(id);
    }

    public static int b(UUID id) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null && mc.field_1724.method_5667().equals(id)) {
            return K37.i();
        }
        return K38.d(id);
    }

    public static class_2960 c() {
        return g;
    }

    public static String b(int rank) {
        return h[Math.max(0, Math.min(h.length - 1, rank))];
    }

    public static String c(int rank) {
        return K14.b(rank);
    }

    public static String c(UUID id) {
        class_310 mc = class_310.method_1551();
        K8.C76 s = mc.field_1724 != null && mc.field_1724.method_5667().equals(id) ? K8.a() : K8.c(K38.a(id));
        return s == null ? "" : s.c();
    }

    public static class_5250 b(String glyphs) {
        return class_2561.method_43470((String)glyphs).method_27694(st -> st.method_27704((class_11719)new class_11719.class_11721(g)).method_36139(0xFFFFFF));
    }

    public static class_2561 a(class_2561 name, UUID id) {
        String badge = K14.b(K14.a(id));
        int r2 = K14.b(id);
        String badge2 = r2 > 0 ? K14.b(r2) : "";
        String sticker = K14.c(id);
        class_2561 donut = K14.d(id);
        class_5250 out = class_2561.method_43473();
        if (!badge.isEmpty()) {
            out.method_10852((class_2561)K14.b(badge));
            if (!badge2.isEmpty()) {
                out.method_10852((class_2561)K14.b(badge2));
            }
            out.method_27693(" ");
        }
        if (donut != null) {
            out.method_10852(donut).method_27693(" ");
        }
        out.method_10852(name);
        if (!sticker.isEmpty()) {
            out.method_27693(" ").method_10852((class_2561)K14.b(sticker));
        }
        return out;
    }

    public static class_2561 d(UUID id) {
        try {
            class_640 info;
            class_5250 tag;
            class_269 sb;
            class_268 team;
            String name;
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 == null) {
                return null;
            }
            class_1657 p = mc.field_1687.method_18470(id);
            String string = name = p != null ? p.method_5820() : null;
            if (name != null && (team = (sb = mc.field_1687.method_8428()).method_1164(name)) != null && !(tag = class_268.method_1142((class_270)team, (class_2561)class_2561.method_43473())).getString().trim().isEmpty()) {
                return tag;
            }
            class_640 class_6402 = info = mc.method_1562() == null ? null : mc.method_1562().method_2871(id);
            if (info != null && info.method_2971() != null) {
                String nm = name == null ? "" : name;
                class_5250 pre = class_2561.method_43473();
                boolean[] done = new boolean[]{false};
                info.method_2971().method_27658((style, text) -> {
                    int idx;
                    if (done[0]) {
                        return Optional.empty();
                    }
                    int n = idx = nm.isEmpty() ? -1 : text.indexOf(nm);
                    if (idx >= 0) {
                        if (idx > 0) {
                            pre.method_10852((class_2561)class_2561.method_43470((String)text.substring(0, idx)).method_10862(style));
                        }
                        done[0] = true;
                    } else {
                        pre.method_10852((class_2561)class_2561.method_43470((String)text).method_10862(style));
                    }
                    return Optional.empty();
                }, class_2583.field_24360);
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

    public static boolean a(class_1657 player) {
        return player != null && K14.a() && (player == class_310.method_1551().field_1724 || K38.e(player.method_5667()));
    }

    public static class_1657 a(class_1297 carrier) {
        for (class_1297 e = carrier.method_5854(); e != null; e = e.method_5854()) {
            if (!(e instanceof class_1657)) continue;
            class_1657 p = (class_1657)e;
            return p;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return null;
        }
        class_243 pos = carrier.method_73189();
        for (class_1657 p : level.method_18456()) {
            double dx = pos.field_1352 - p.method_23317();
            double dz = pos.field_1350 - p.method_23321();
            double dy = pos.field_1351 - p.method_23318();
            if (!(dx * dx + dz * dz < 0.36) || !(dy > (double)p.method_17682() - 0.4) || !(dy < (double)p.method_17682() + 1.6)) continue;
            return p;
        }
        return null;
    }

    public static class_5481 e(UUID id) {
        String badge = K14.b(K14.a(id));
        if (badge.isEmpty()) {
            return class_5481.field_26385;
        }
        return class_5481.method_30742((class_5481)K14.b(badge).method_30937(), (class_5481)class_2561.method_43470((String)" ").method_30937());
    }

    public static class_5481 f(UUID id) {
        String sticker = K14.c(id);
        if (sticker.isEmpty()) {
            return class_5481.field_26385;
        }
        return class_5481.method_30742((class_5481)class_2561.method_43470((String)" ").method_30937(), (class_5481)K14.b(sticker).method_30937());
    }

    public static String a(class_5481 seq) {
        StringBuilder sb = new StringBuilder();
        seq.accept((index, style, codepoint) -> {
            sb.appendCodePoint(codepoint);
            return true;
        });
        return sb.toString();
    }
}

