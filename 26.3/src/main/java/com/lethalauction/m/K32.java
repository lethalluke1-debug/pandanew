/*
 * Decompiled with CFR 0.152.
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K20;
import com.lethalauction.m.K9;

public final class K32 {
    static final int a = -2096899;
    static final int b = -5893637;

    private K32() {
    }

    public static int a() {
        return 0xFF000000 | K20.a(K9.b, 0);
    }

    public static int b() {
        return K9.b >= 0 ? K32.a() : -5893637;
    }

    public static int c() {
        return K18.a(K32.a(), -16777216, 0.55f);
    }

    public static int d() {
        return K18.a(K32.a(), -1, 0.65f);
    }
}

