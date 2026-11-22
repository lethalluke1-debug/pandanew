/*
 * Decompiled with CFR 0.152.
 */
package com.lethalauction.m;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class K41 {
    private static final Pattern a = Pattern.compile("n+[i1!|\u00edy]+[gq]+[ae3r@4]+", 2);
    private static final Set<String> b = Set.of("niger", "nigeria", "nigerian", "nigerians", "nigerien", "nigeriens");

    private K41() {
    }

    public static String a(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        String squished = word.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9!|@\u00edy]", "");
        if (b.contains(squished)) {
            return word;
        }
        if (a.matcher(squished).find()) {
            int n = Math.max(3, word.length());
            return "*".repeat(n);
        }
        return word;
    }
}

