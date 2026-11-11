/*
 * Decompiled with CFR 0.152.
 */
package com.lethalauction.m;

import com.lethalauction.m.K9;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.LocalDate;
import java.util.List;

public final class K8 {
    public static final List<C77> a = List.of(new C77("halloween", "Halloween", "\ue020", false), new C77("xmas", "Christmas Tree", "\ue021", false), new C77("pumpkin", "Pumpkin", "\ue022", false), new C77("heart", "Heart", "\ue023", false), new C77("sixseven", "67", "\ue024", false), new C77("star", "Star", "\ue025", false), new C77("skull", "Skull", "\ue026", false), new C77("fire", "Fire", "\ue027", false), new C77("diamond", "Diamond", "\ue028", false));

    private K8() {
    }

    public static C77 a(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (C77 s : a) {
            if (!s.a.equals(id)) continue;
            return s;
        }
        return null;
    }

    public static C77 a(int i) {
        return i >= 0 && i < a.size() ? a.get(i) : null;
    }

    public static int b(String id) {
        for (int i = 0; i < a.size(); ++i) {
            if (!K8.a.get((int)i).a.equals(id)) continue;
            return i;
        }
        return -1;
    }

    public static C77 a() {
        return K8.a(K9.aH);
    }

    public static String b() {
        int i = K8.b(K9.aH);
        return i < 0 ? "" : "sticker:" + i;
    }

    public static C77 c(String cos) {
        if (cos == null || cos.isEmpty()) {
            return null;
        }
        for (String part : cos.split(",")) {
            if (!part.startsWith("sticker:")) continue;
            try {
                return K8.a(Integer.parseInt(part.substring("sticker:".length())));
            }
            catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static boolean a(C77 s) {
        if (s == null || !s.d) {
            return true;
        }
        LocalDate d = LocalDate.now();
        if (s.a.equals("halloween")) {
            return d.getMonthValue() == 10 || d.getMonthValue() == 11 && d.getDayOfMonth() <= 7;
        }
        return true;
    }

    public static void d(String id) {
        K9.aH = id.equals(K9.aH) ? "" : id;
        K9.b();
    }

    public static final class C77 {
        private final String a;
        private final String b;
        private final String c;
        private final boolean d;
        C77(String a, String b, String c, boolean d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public String c() { return this.c; }
        public boolean d() { return this.d; }


    }
}

