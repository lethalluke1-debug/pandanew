/*
 * Decompiled with CFR 0.152.
 */
package com.lethalauction.m;

import com.lethalauction.m.K45;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class K44 {
    public final long[][] a;
    public final double b;
    public final double c;
    public final float d;
    private static final Map<List<K45.C95>, K44> e = Collections.synchronizedMap(new IdentityHashMap());

    private K44(long[][] wordStart, double beatMs, double beatPhase, float confidence) {
        this.a = wordStart;
        this.b = beatMs;
        this.c = beatPhase;
        this.d = confidence;
    }

    public static K44 a(List<K45.C95> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }
        K44 t = e.get(lines);
        if (t == null) {
            t = K44.b(lines);
            if (e.size() > 20) {
                e.clear();
            }
            e.put(lines, t);
        }
        return t;
    }

    public static String[] a(String text) {
        String s = text == null ? "" : text.trim();
        return s.isEmpty() ? new String[]{} : s.split("\\s+");
    }

    static int b(String s) {
        int n = 0;
        boolean prevVowel = false;
        for (int i = 0; i < s.length(); ++i) {
            boolean vowel;
            boolean bl = vowel = "aeiouy\u00e4\u00f6\u00fcAEIOUY\u00c4\u00d6\u00dc".indexOf(s.charAt(i)) >= 0;
            if (vowel && !prevVowel) {
                ++n;
            }
            prevVowel = vowel;
        }
        return Math.max(1, n);
    }

    static float c(String s) {
        float f = K44.b(s);
        char c = s.charAt(s.length() - 1);
        if (c == ',' || c == '.' || c == '!' || c == '?' || c == ')') {
            f += 0.6f;
        }
        return f;
    }

    private static long a(List<K45.C95> lines, int i) {
        return i + 1 < lines.size() ? lines.get(i + 1).a() : lines.get(i).a() + 5000L;
    }

    private static K44 b(List<K45.C95> lines) {
        int n = lines.size();
        String[][] wordsOf = new String[n][];
        for (int i = 0; i < n; ++i) {
            wordsOf[i] = K44.a(lines.get(i).b());
        }
        ArrayList<Float> rates = new ArrayList<Float>();
        for (int i = 0; i < n; ++i) {
            long gap = K44.a(lines, i) - lines.get(i).a();
            if (wordsOf[i].length < 3 || gap < 900L || gap > 9000L) continue;
            float w = 0.0f;
            for (String word : wordsOf[i]) {
                w += K44.c(word);
            }
            rates.add(Float.valueOf(w / ((float)gap * 0.85f / 1000.0f)));
        }
        float sylPerSec = 4.0f;
        if (!rates.isEmpty()) {
            Collections.sort(rates);
            sylPerSec = ((Float)rates.get(rates.size() / 2)).floatValue();
        }
        sylPerSec = Math.max(2.2f, Math.min(8.0f, sylPerSec));
        ArrayList<Long> starts = new ArrayList<Long>();
        for (K45.C95 l : lines) {
            if (l.b().isBlank()) continue;
            starts.add(l.a());
        }
        double beatMs = 0.0;
        double beatPhase = 0.0;
        float conf = 0.0f;
        if (starts.size() >= 6) {
            for (double bpm = 60.0; bpm <= 180.0; bpm += 0.25) {
                float pref;
                double period = 60000.0 / bpm;
                double sumCos = 0.0;
                double sumSin = 0.0;
                Iterator iterator = starts.iterator();
                while (iterator.hasNext()) {
                    long s = (Long)iterator.next();
                    double ang = Math.PI * 2 * ((double)s % period) / period;
                    sumCos += Math.cos(ang);
                    sumSin += Math.sin(ang);
                }
                float strength = (float)(Math.sqrt(sumCos * sumCos + sumSin * sumSin) / (double)starts.size());
                float f = pref = bpm >= 80.0 && bpm <= 150.0 ? 1.0f : 0.93f;
                if (!(strength * pref > conf)) continue;
                conf = strength * pref;
                beatMs = period;
                double ph = Math.atan2(sumSin, sumCos);
                if (ph < 0.0) {
                    ph += Math.PI * 2;
                }
                beatPhase = ph / (Math.PI * 2) * period;
            }
        }
        boolean hasBeat = conf >= 0.45f && beatMs > 0.0;
        long[][] out = new long[n][];
        for (int i = 0; i < n; ++i) {
            String[] ws = wordsOf[i];
            out[i] = new long[ws.length];
            if (ws.length == 0) continue;
            long start = lines.get(i).a();
            long span = Math.min(K44.a(lines, i) - start, 7000L);
            float total = 0.0f;
            for (String word : ws) {
                total += K44.c(word);
            }
            double occupy = Math.min((double)span * 0.92, Math.max(300.0, (double)(total / sylPerSec) * 1000.0));
            double snap = 0.0;
            if (hasBeat) {
                double perWord = occupy / (double)ws.length;
                snap = perWord < beatMs / 2.0 * 0.75 ? beatMs / 4.0 : beatMs / 2.0;
            }
            long prev = start - 1L;
            double cum = 0.0;
            for (int j = 0; j < ws.length; ++j) {
                double snapped;
                double t = (double)start + cum / (double)total * occupy;
                if (j == 0) {
                    t = start;
                } else if (snap > 0.0 && Math.abs((snapped = beatPhase + (double)Math.round((t - beatPhase) / snap) * snap) - t) < snap * 0.6) {
                    t = snapped;
                }
                long v = Math.max(prev + 40L, Math.round(t));
                out[i][j] = v = Math.min(v, start + (long)((double)span * 0.97));
                prev = v;
                cum += (double)K44.c(ws[j]);
            }
        }
        return new K44(out, hasBeat ? beatMs : 0.0, beatPhase, hasBeat ? Math.min(1.0f, conf) : 0.0f);
    }

    public int a(int line, long pos) {
        int c;
        if (line < 0 || line >= this.a.length) {
            return 0;
        }
        long[] ts = this.a[line];
        for (c = 0; c < ts.length && ts[c] <= pos; ++c) {
        }
        return c;
    }
}

