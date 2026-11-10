/*
 * Decompiled with CFR 0.152.
 */
package com.lethalauction.m;

import java.util.function.IntPredicate;

final class K31 {
    final int a;
    private final IntPredicate d;
    String b;
    boolean c;

    K31(String initial, int maxLength, IntPredicate allowed) {
        this.b = initial;
        this.a = maxLength;
        this.d = allowed;
    }

    static K31 a(int maxLength) {
        return new K31("", maxLength, c -> c >= 32 && c != 127);
    }

    static K31 a(String initial, int maxLength) {
        return new K31(initial, maxLength, c -> c >= 48 && c <= 57);
    }

    static K31 b(int maxLength) {
        return new K31("", maxLength, c -> c >= 48 && c <= 57 || c == 46 || c == 107 || c == 109 || c == 98 || c == 75 || c == 77 || c == 66);
    }

    boolean a(String text) {
        if (this.c) {
            this.b = "";
            this.c = false;
        }
        StringBuilder sb = new StringBuilder(this.b);
        text.codePoints().filter(this.d).forEach(c -> {
            if (sb.length() < this.a) {
                sb.appendCodePoint(c);
            }
        });
        boolean changed = !sb.toString().equals(this.b);
        this.b = sb.toString();
        return changed;
    }

    boolean a(boolean word) {
        if (this.b.isEmpty()) {
            this.c = false;
            return false;
        }
        if (!word && !this.c) {
            this.b = this.b.substring(0, this.b.offsetByCodePoints(this.b.length(), -1));
        } else {
            this.c = false;
            this.b = "";
        }
        return true;
    }
}

