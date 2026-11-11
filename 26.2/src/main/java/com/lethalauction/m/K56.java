/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K55;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

final class K56 {
    private static final int a = -15986400;
    private static final int b = -15459282;
    private static final int c = -14536622;
    private static final int d = -1;
    private static final int e = -5327162;

    private K56() {
    }

    static void a(GuiGraphicsExtractor g, int game, List<K55.C106> e, int n, int winner, float p, boolean running, int ax, int ay, int aw, int ah) {
        switch (game) {
            case 1: {
                K56.a(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
            case 2: {
                K56.b(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
            case 3: {
                K56.a(g, e, n, winner, p, ax, ay, aw, ah, false, true);
                break;
            }
            case 4: {
                K56.a(g, e, n, winner, p, ax, ay, aw, ah, true, true);
                break;
            }
            case 5: {
                K56.c(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
            case 6: {
                K56.d(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
            case 7: {
                K56.e(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
            case 8: {
                K56.f(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
            case 9: {
                K56.g(g, e, n, winner, p, ax, ay, aw, ah);
                break;
            }
        }
    }

    private static float a(float p) {
        return 1.0f - (float)Math.pow(1.0f - p, 3.0);
    }

    private static float b(float p) {
        return p * p;
    }

    private static float c(float p) {
        return p < 0.5f ? 2.0f * p * p : 1.0f - (float)Math.pow(-2.0f * p + 2.0f, 2.0) / 2.0f;
    }

    private static float a(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static int a(int i) {
        return K55.a(i);
    }

    private static int[] a(int n, int winner, int max) {
        if (n <= max) {
            int[] v = new int[n];
            for (int i = 0; i < n; ++i) {
                v[i] = i;
            }
            return v;
        }
        int[] v = new int[max];
        for (int i = 0; i < max; ++i) {
            v[i] = i;
        }
        if (winner >= max) {
            v[max - 1] = winner;
        }
        return v;
    }

    private static int a(int[] vis, int winner) {
        for (int i = 0; i < vis.length; ++i) {
            if (vis[i] != winner) continue;
            return i;
        }
        return 0;
    }

    private static String a(List<K55.C106> e, int i) {
        return i >= 0 && i < e.size() ? e.get((int)i).a : "?";
    }

    private static String a(String s, float size, float maxW) {
        if (s == null) {
            return "";
        }
        if (K18.a(s, size, K18.c) <= maxW) {
            return s;
        }
        while (s.length() > 1 && K18.a(s + "\u2026", size, K18.c) > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "\u2026";
    }

    private static long a() {
        return System.currentTimeMillis();
    }

    private static void a(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        int cols = Math.min(n, 6);
        int[] vis = K56.a(n, winner, cols);
        int wp = K56.a(vis, winner);
        float top = ay + 16;
        float bottom = ay + ah - 30;
        float slotW = (float)aw / (float)cols;
        int ROWS = 6;
        float pegTop = top + 8.0f;
        float pegBot = bottom - 12.0f;
        for (int r = 0; r < ROWS; ++r) {
            int pc = 5 + r % 2;
            float py = pegTop + (float)r * ((pegBot - pegTop) / (float)(ROWS - 1));
            for (int c = 0; c < pc; ++c) {
                float px = (float)ax + (float)aw * ((float)c + (r % 2 == 0 ? 0.5f : 1.0f)) / (float)(pc + (r % 2 == 0 ? 0 : 1));
                K18.a(g, px, py, 2.6f, -14011312);
                K18.a(g, px, py, 2.0f, -9469264);
            }
        }
        for (int i = 0; i < cols; ++i) {
            boolean win = i == wp && p >= 1.0f;
            K18.a(g, Math.round((float)ax + (float)i * slotW + 1.0f), Math.round(bottom), Math.round(slotW - 2.0f), Math.round((float)(ay + ah - 2) - bottom), 2, win ? K56.a(vis[i]) : K18.a(K56.a(vis[i]), 0.45f));
        }
        float startX = (float)ax + (float)aw / 2.0f;
        float endX = (float)ax + slotW * ((float)wp + 0.5f);
        float amp = slotW * 0.55f;
        float[] wx = new float[ROWS + 1];
        wx[0] = startX;
        for (int k = 1; k <= ROWS; ++k) {
            float fr = (float)k / (float)ROWS;
            float base = K56.a(startX, endX, fr);
            int sign = (Math.abs(winner) * 31 + k * 13) % 2 == 0 ? 1 : -1;
            float zig = amp * (1.0f - fr) * (float)sign;
            wx[k] = Math.max((float)ax + 7.0f, Math.min((float)(ax + aw) - 7.0f, base + zig));
        }
        wx[ROWS] = endX;
        float fRow = p * (float)ROWS;
        int ri = Math.min(ROWS - 1, (int)fRow);
        float rf = fRow - (float)ri;
        float chipX = K56.a(wx[ri], wx[ri + 1], rf);
        float yFall = p * p * 0.35f + p * 0.65f;
        float hop = (float)Math.abs(Math.sin((double)rf * Math.PI)) * 3.2f * (1.0f - p * 0.7f);
        float chipY = K56.a(top, bottom - 5.0f, yFall) - hop;
        K18.a(g, chipX, chipY, 5.5f, -15724009);
        K18.a(g, chipX, chipY, 4.5f, -11701);
        if (p >= 1.0f) {
            K18.b(g, K56.a(K56.a(e, winner), 8.0f, slotW + 20.0f), (float)ax + slotW * ((float)wp + 0.5f), bottom - 12.0f, 8.0f, -1, K18.c);
        }
    }

    private static void b(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        float cx = (float)ax + (float)aw * 0.4f;
        float cy = (float)ay + (float)ah * 0.52f;
        float r = (float)Math.min(aw, ah) * 0.3f;
        K18.a(g, cx, cy, r + 2.0f, -14010800);
        K18.a(g, cx, cy, r, -15986400);
        float tubeX = (float)ax + (float)aw * 0.82f;
        float tubeTop = (float)ay + (float)ah * 0.14f;
        float tubeBot = cy;
        K18.a(g, Math.round(tubeX - 5.0f), Math.round(tubeTop), 10, Math.round(tubeBot - tubeTop), 4, -15196106);
        int[] vis = K56.a(n, winner, 7);
        float t = (float)K56.a() / 1000.0f;
        float rise = p > 0.76f ? Math.min(1.0f, (p - 0.76f) / 0.24f) : 0.0f;
        for (int i = 0; i < vis.length; ++i) {
            if (vis[i] == winner && rise > 0.0f) continue;
            float a = (float)i * 0.9f + t * (2.2f + (float)i * 0.3f);
            float rr = r * (0.3f + 0.5f * Math.abs((float)Math.sin(a * 0.7f)));
            float bx = cx + (float)Math.cos(a) * rr;
            float by = cy + (float)Math.sin(a * 1.3f) * rr;
            K18.a(g, bx, by, 3.6f, K56.a(vis[i]));
        }
        if (rise > 0.0f) {
            float by = K56.a(tubeBot - 6.0f, tubeTop + 6.0f, K56.a(rise));
            K18.a(g, tubeX, by, 4.2f, K56.a(winner));
            if (p >= 1.0f) {
                K18.b(g, K56.a(K56.a(e, winner), 8.5f, (float)aw * 0.5f), tubeX, tubeTop - 6.0f, 8.5f, -1, K18.c);
            }
        }
    }

    private static void a(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah, boolean drag, boolean horizontal) {
        int lanes = Math.min(n, 6);
        int[] vis = K56.a(n, winner, lanes);
        int wp = K56.a(vis, winner);
        float laneH = (float)(ah - 10) / (float)lanes;
        float startX = ax + 44;
        float finishX = ax + aw - 14;
        float runW = finishX - startX;
        float ep = drag ? K56.b(Math.min(1.0f, p * 1.05f)) : K56.a(p);
        K18.a(g, Math.round(finishX), ay + 4, 1, Math.round(ah - 8), 0x55FFFFFF);
        for (int i = 0; i < lanes; ++i) {
            float ly = (float)(ay + 6) + (float)i * laneH;
            K18.a(g, ax + 4, Math.round(ly), Math.round(aw - 8), Math.round(laneH - 4.0f), 4, -15459282);
            float base = vis[i] == winner ? ep : ep * (0.62f + 0.28f * K56.d((float)vis[i] * 0.37f));
            float jit = (float)Math.sin((float)K56.a() / 160.0f + (float)vis[i]) * (1.0f - p) * 0.03f;
            float prog = Math.max(0.0f, Math.min(1.0f, base + jit));
            float rx = startX + runW * prog;
            float ry = ly + (laneH - 4.0f) / 2.0f;
            K18.a(g, K56.a(K56.a(e, vis[i]), 7.5f, 36.0f), (float)(ax + 7), ry, 7.5f, vis[i] == winner && p >= 1.0f ? -1 : -5327162, K18.c);
            if (drag) {
                K18.a(g, Math.round(rx - 7.0f), Math.round(ry - 4.0f), 14, 8, 3, K56.a(vis[i]));
                continue;
            }
            K18.a(g, rx, ry, 5.0f, K56.a(vis[i]));
            K18.a(g, rx + 3.0f, ry - 2.0f, 1.4f, 0x66000000);
        }
    }

    private static void c(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        int lanes = Math.min(n, 6);
        int[] vis = K56.a(n, winner, lanes);
        float laneW = (float)(aw - 8) / (float)lanes;
        float startY = ay + 22;
        float finishY = ay + ah - 10;
        float runH = finishY - startY;
        float ep = K56.a(p);
        K18.a(g, ax + 4, Math.round(finishY), Math.round(aw - 8), 1, 0x55FFFFFF);
        for (int i = 0; i < lanes; ++i) {
            float lx = (float)(ax + 4) + (float)i * laneW;
            K18.a(g, Math.round(lx), ay + 6, Math.round(laneW - 3.0f), Math.round(ah - 12), 4, -15848902);
            float base = vis[i] == winner ? ep : ep * (0.6f + 0.3f * K56.d((float)vis[i] * 0.53f));
            float prog = Math.max(0.0f, Math.min(1.0f, base));
            float by = startY + runH * prog;
            float bx = lx + (laneW - 3.0f) / 2.0f;
            K18.a(g, Math.round(bx - 4.0f), Math.round(by - 6.0f), 8, 12, 3, K56.a(vis[i]));
            K18.a(g, K56.a(K56.a(e, vis[i]), 6.5f, laneW + 6.0f), lx, (float)(ay + 14), 6.5f, vis[i] == winner && p >= 1.0f ? -1 : -5327162, K18.c);
        }
    }

    private static void d(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        float cx = (float)ax + (float)aw / 2.0f;
        float cy = (float)ay + (float)ah / 2.0f;
        float R = (float)Math.min(aw, ah) * 0.38f;
        K18.a(g, cx, cy, R + 3.0f, -15797);
        K18.a(g, cx, cy, R, -15068666);
        K18.a(g, cx, cy, R - 3.0f, -15986400);
        int k = Math.min(n, 8);
        int[] vis = K56.a(n, winner, k);
        int survivors = Math.max(1, k);
        int eliminated = (int)(p * (float)(k - 1));
        int rank = 0;
        for (int i = 0; i < k; ++i) {
            boolean isWin = vis[i] == winner;
            double ang = (double)i / (double)k * 2.0 * Math.PI + (double)(p * 2.0f);
            float innerR = R * 0.55f * (1.0f - 0.6f * p * (float)(isWin ? 1 : 0));
            float bx = cx + (float)Math.cos(ang) * (isWin ? innerR : R * 0.55f);
            float by = cy + (float)Math.sin(ang) * (isWin ? innerR : R * 0.55f);
            if (!isWin) {
                int n2 = rank++;
                int myRank = n2;
                float te = (float)(myRank + 1) / (float)k;
                if (p > te) {
                    float out = Math.min(1.0f, (p - te) / 0.12f);
                    float fly = R * 1.6f * out;
                    bx = cx + (float)Math.cos(ang) * (R * 0.55f + fly);
                    by = cy + (float)Math.sin(ang) * (R * 0.55f + fly);
                    float prev = K18.h;
                    K18.h = prev * Math.max(0.0f, 1.0f - out);
                    K18.a(g, bx, by, 5.0f, K56.a(vis[i]));
                    K18.h = prev;
                    continue;
                }
            }
            float sz = isWin && p >= 1.0f ? 7.0f : 5.0f;
            K18.a(g, isWin ? K56.a(bx, cx, p) : bx, isWin ? K56.a(by, cy, p) : by, sz, K56.a(vis[i]));
        }
        if (p >= 1.0f) {
            K18.b(g, K56.a(K56.a(e, winner), 8.5f, (float)aw * 0.6f), cx, cy + R + 6.0f, 8.5f, -1, K18.c);
        }
    }

    private static void e(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        float[] stop = new float[]{0.45f, 0.68f, 0.9f};
        float reelW = (float)(aw - 20) / 3.0f;
        float reelH = (float)ah * 0.56f;
        float ry = (float)ay + (float)ah * 0.24f;
        float rowH = reelH / 3.0f;
        float nn = Math.max(1, n);
        K18.a(g, ax + 6, Math.round(ry + reelH / 2.0f - 11.0f), Math.round(aw - 12), 22, 5, K18.a(-45702, 0.14f));
        for (int r = 0; r < 3; ++r) {
            float rx = (float)(ax + 8) + (float)r * (reelW + 4.0f);
            K18.a(g, Math.round(rx), Math.round(ry), Math.round(reelW), Math.round(reelH), 5, -15986400);
            boolean stopped = p >= stop[r];
            float centerY = ry + reelH / 2.0f;
            float cx = rx + reelW / 2.0f;
            float sp = Math.min(1.0f, p / stop[r]);
            float eased = K56.a(sp);
            float totalRows = (1.0f - eased) * (nn * 1.5f + 26.0f);
            int baseIdx = (int)Math.floor(totalRows);
            float frac = totalRows - (float)baseIdx;
            for (int k = -1; k <= 1; ++k) {
                boolean mid;
                float yy = centerY + ((float)k - frac) * rowH;
                if (yy < ry + 4.0f || yy > ry + reelH - 4.0f) continue;
                int idx = stopped ? winner : (int)Math.floorMod((long)winner + (long)baseIdx - (long)k, (long)n);
                boolean bl = mid = Math.abs((float)k - frac) < 0.5f;
                int col = mid ? (stopped ? K56.a(winner) : -1) : -11181958;
                float sz = mid ? 8.5f : 7.0f;
                K18.b(g, K56.a(K56.a(e, idx), sz, reelW - 6.0f), cx, yy, sz, col, mid ? K18.c : K18.b);
            }
        }
        if (p >= 1.0f) {
            K18.b(g, "JACKPOT", (float)ax + (float)aw / 2.0f, ry + reelH + 10.0f, 8.0f, -11701, K18.c);
        }
    }

    private static void f(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        int cols = Math.min(n, 6);
        int[] vis = K56.a(n, winner, cols);
        int wp = K56.a(vis, winner);
        float slotW = (float)(aw - 16) / (float)cols;
        float capY = ay + ah - 26;
        float railY = ay + 12;
        K18.a(g, ax + 6, Math.round(railY), Math.round(aw - 12), 2, -14536622);
        float winX = (float)(ax + 8) + slotW * ((float)wp + 0.5f);
        float moveP = Math.min(1.0f, p / 0.45f);
        float clawX = K56.a((float)ax + (float)aw / 2.0f, winX, K56.a(moveP));
        float descend = p < 0.45f ? 0.0f : Math.min(1.0f, (p - 0.45f) / 0.25f);
        float lift = p < 0.7f ? 0.0f : Math.min(1.0f, (p - 0.7f) / 0.3f);
        float clawY = K56.a(railY, capY - 8.0f, descend);
        clawY = K56.a(clawY, railY + 4.0f, lift);
        for (int i = 0; i < cols; ++i) {
            float x = (float)(ax + 8) + slotW * ((float)i + 0.5f);
            float y = capY;
            if (i == wp && lift > 0.0f) {
                y = K56.a(capY, clawY + 10.0f, lift);
            }
            K18.a(g, Math.round(x - slotW / 2.0f + 3.0f), Math.round(y - 8.0f), Math.round(slotW - 6.0f), 16, 6, K56.a(vis[i]));
            K18.a(g, Math.round(x - slotW / 2.0f + 3.0f), Math.round(y - 8.0f), Math.round(slotW - 6.0f), 8, 6, K18.a(-1, 0.18f));
        }
        K18.a(g, Math.round(clawX), Math.round(railY), 2, Math.round(clawY - railY), -7695450);
        K18.a(g, Math.round(clawX - 6.0f), Math.round(clawY - 2.0f), 12, 5, 2, -4735284);
        float open = lift > 0.0f ? 0.0f : (1.0f - descend) * 3.0f;
        K18.a(g, Math.round(clawX - 5.0f - open), Math.round(clawY + 2.0f), 2, 6, -4735284);
        K18.a(g, Math.round(clawX + 3.0f + open), Math.round(clawY + 2.0f), 2, 6, -4735284);
        if (p >= 1.0f) {
            K18.b(g, K56.a(K56.a(e, winner), 8.0f, (float)aw * 0.6f), clawX, railY - 6.0f, 8.0f, -1, K18.c);
        }
    }

    private static void g(GuiGraphicsExtractor g, List<K55.C106> e, int n, int winner, float p, int ax, int ay, int aw, int ah) {
        float cx = (float)ax + (float)aw / 2.0f;
        float cy = (float)ay + (float)ah * 0.42f;
        float t = (float)K56.a() / 1000.0f;
        boolean burst = p >= 0.82f;
        K18.a(g, Math.round(cx), ay + 4, 1, Math.round(cy - (float)ay - 16.0f), -14536622);
        if (!burst) {
            float swing = (float)Math.sin(t * 3.0f) * (6.0f + 10.0f * p);
            Matrix3x2fStack ps = g.pose();
            ps.pushMatrix();
            ps.translate(cx + swing, cy);
            K18.a(g, -16, -12, 32, 24, 10, -45702);
            K18.a(g, -16, -12, 32, 8, 10, -34144);
            K18.a(g, -10.0f, 10.0f, 3.0f, -11548161);
            K18.a(g, 0.0f, 12.0f, 3.0f, -15797);
            K18.a(g, 10.0f, 10.0f, 3.0f, -10888821);
            ps.popMatrix();
            float ba = (float)Math.sin(t * 3.0f) * 0.5f;
            Matrix3x2fStack bp = g.pose();
            bp.pushMatrix();
            bp.translate((float)(ax + aw - 18), cy + 14.0f);
            bp.rotate(ba);
            K18.a(g, -2, -18, 4, 20, 2, -6657219);
            bp.popMatrix();
        } else {
            float bp = (p - 0.82f) / 0.18f;
            for (int i = 0; i < 16; ++i) {
                float a = (float)i / 16.0f * 6.283f;
                float d = bp * 40.0f;
                K18.a(g, cx + (float)Math.cos(a) * d, cy + (float)Math.sin(a) * d - bp * 6.0f, 2.5f, K56.a(i));
            }
            float dy = cy + K56.a(bp) * ((float)ah * 0.28f);
            K18.a(g, Math.round(cx - 18.0f), Math.round(dy - 8.0f), 36, 16, 6, -15724009);
            K18.b(g, K56.a(K56.a(e, winner), 8.5f, 54.0f), cx, dy, 8.5f, -11701, K18.c);
        }
    }

    private static float d(float x) {
        return x - (float)Math.floor(x);
    }
}

