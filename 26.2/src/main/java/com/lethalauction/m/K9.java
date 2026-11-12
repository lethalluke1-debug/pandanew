/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.lethalauction.m;

import com.lethalauction.LethalAuctionClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class K9 {
    private static final Path bg = FabricLoader.getInstance().getConfigDir().resolve("lethalauction.properties");
    public static final int a = 2;
    public static int b = 2;
    public static final int[][] c = new int[][]{{4890367, 3046368, 1727424, 666202}, {11561983, 9128928, 6959040, 2099786}, {3072192, 2078880, 1411192, 404518}, {16739248, 14699408, 12595056, 3934756}};
    public static boolean d = false;
    public static boolean e = true;
    public static boolean f = true;
    public static boolean g = true;
    public static float h = 0.4f;
    public static String i = "";
    public static String j = "";
    public static boolean k = false;
    public static boolean l = true;
    public static boolean m = false;
    public static final String n = "Player";
    public static boolean o = false;
    public static int p = 0;
    public static boolean q = true;
    public static boolean r = true;
    public static String s = "";
    public static boolean t = false;
    public static boolean u = true;
    public static float v = 0.01f;
    public static float w = 0.32f;
    public static float x = 1.0f;
    public static boolean y = false;
    public static boolean z = true;
    public static boolean A = true;
    public static boolean B = false;
    public static String C = "1m";
    public static float D = 0.01f;
    public static float E = 0.02f;
    public static float F = 1.0f;
    public static boolean G = false;
    public static int H = 1;
    public static boolean I = true;
    public static int J = 0;
    public static boolean K = true;
    public static boolean L = false;
    public static boolean M = true;
    public static boolean N = true;
    public static boolean O = false;
    public static float P = 4.0f;
    public static boolean Q = true;
    public static boolean R = true;
    public static boolean S = true;
    public static boolean T = false;
    public static boolean U = false;
    public static String font = "";
    public static boolean V = true;
    public static float W = 0.5f;
    public static float X = 0.02f;
    public static float Y = 1.0f;
    public static boolean Z = true;
    public static int aa = 0;
    public static boolean ab = false;
    public static boolean ac = true;
    public static String ad = "wss://lethal-client-chat.onrender.com/chat";
    public static String ae = "";
    public static String af = "";
    public static String ag = "";
    public static float ah = 0.02f;
    public static float ai = 0.25f;
    public static float aj = 1.0f;
    public static int ak = 8;
    public static int al = 0;
    public static int am = 2;
    public static boolean an = true;
    public static float ao = 0.5f;
    public static float ap = 0.2f;
    public static float aq = 1.0f;
    public static int ar = 0;
    public static String as = "1";
    public static String at = "1";
    public static String au = "";
    public static int av = 0;
    public static String aw = "";
    public static int ax = 0;
    public static boolean ay = false;
    public static int az = 1;
    public static int aA = 1;
    public static int aB = 0;
    public static int aC = 1;
    public static float aD = 1.0f;
    public static boolean aE = true;
    public static long aF = 0L;
    public static String aG = "";
    public static String aH = "";
    public static String aI = "";
    public static boolean aJ = true;
    public static boolean aK = true;
    public static boolean aL = false;
    public static int aM = 1;
    public static String aN = "";
    public static boolean aO = false;
    public static int aP = 0;
    public static int aQ = 0;
    public static int aR = 1;
    public static float aS = 1.0f;
    public static boolean aT = false;
    public static boolean aU = false;
    public static boolean aV = false;
    public static int aW = 0;
    public static int aX = 0;
    public static float aY = 0.8f;
    public static float aZ = 0.45f;
    public static String ba = "AUCTION";
    public static String bb = "";
    public static String bc = "1";
    public static String bd = "60";
    public static String be = "";
    public static String bf = "Pay over = I keep";

    private K9() {
    }

    public static void a() {
        if (Files.exists(bg, new LinkOption[0])) {
            Properties p = new Properties();
            try (BufferedReader r = Files.newBufferedReader(bg);){
                p.load(r);
            }
            catch (IOException var8) {
                LethalAuctionClient.LOGGER.warn("Could not read {}", (Object)bg, (Object)var8);
                return;
            }
            try {
                b = Integer.parseInt(p.getProperty("preset", Integer.toString(2)));
                if (b < 0) {
                    b = 2;
                }
            }
            catch (NumberFormatException var6) {
                b = 2;
            }
            for (int ci = 0; ci < c.length; ++ci) {
                String raw = p.getProperty("customTheme" + ci, "");
                if (raw == null || raw.isEmpty()) continue;
                String[] parts = raw.split(",");
                for (int k = 0; k < 4 && k < parts.length; ++k) {
                    try {
                        K9.c[ci][k] = Integer.parseInt(parts[k].trim(), 16) & 0xFFFFFF;
                        continue;
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
            }
            d = Boolean.parseBoolean(p.getProperty("seeThrough", "false"));
            e = Boolean.parseBoolean(p.getProperty("frostedBlur", "true"));
            f = Boolean.parseBoolean(p.getProperty("ambientBackground", "true"));
            g = Boolean.parseBoolean(p.getProperty("moduleSound", "true"));
            ba = p.getProperty("lastPage", ba);
            bb = p.getProperty("formMinBid", bb);
            bc = p.getProperty("formQuantity", bc);
            bd = p.getProperty("formTimer", bd);
            be = p.getProperty("formWorth", be);
            bf = p.getProperty("formNote", bf);
            i = p.getProperty("cape", i);
            j = p.getProperty("lastCape", i);
            k = Boolean.parseBoolean(p.getProperty("auctionCape", "false"));
            l = Boolean.parseBoolean(p.getProperty("nametagIcon", "true"));
            m = Boolean.parseBoolean(p.getProperty("fakeName", "false"));
            o = Boolean.parseBoolean(p.getProperty("skyEnabled", "false"));
            try {
                K9.p = Integer.parseInt(p.getProperty("skyPreset", "0"));
            }
            catch (NumberFormatException e) {
                K9.p = 0;
            }
            q = Boolean.parseBoolean(p.getProperty("skyShootingStars", "true"));
            r = Boolean.parseBoolean(p.getProperty("skyHideClouds", "true"));
            s = p.getProperty("txMinimum", "");
            t = Boolean.parseBoolean(p.getProperty("profitHud", "false"));
            y = Boolean.parseBoolean(p.getProperty("statsHud", "false"));
            z = Boolean.parseBoolean(p.getProperty("statsFps", "true"));
            B = Boolean.parseBoolean(p.getProperty("payGuard", "false"));
            C = p.getProperty("payGuardAmount", "1m");
            A = Boolean.parseBoolean(p.getProperty("statsPing", "true"));
            G = Boolean.parseBoolean(p.getProperty("titleScene", "false"));
            I = Boolean.parseBoolean(p.getProperty("titlePetals", "true"));
            K = Boolean.parseBoolean(p.getProperty("freelookEnabled", "true"));
            L = Boolean.parseBoolean(p.getProperty("freelookToggle", "false"));
            M = Boolean.parseBoolean(p.getProperty("freelookThirdPerson", "true"));
            N = Boolean.parseBoolean(p.getProperty("zoomEnabled", "true"));
            O = Boolean.parseBoolean(p.getProperty("zoomToggle", "false"));
            Q = Boolean.parseBoolean(p.getProperty("zoomScroll", "true"));
            R = Boolean.parseBoolean(p.getProperty("zoomSmooth", "true"));
            S = Boolean.parseBoolean(p.getProperty("zoomSound", "true"));
            T = Boolean.parseBoolean(p.getProperty("fullbright", "false"));
            U = Boolean.parseBoolean(p.getProperty("musicEnabled", "false"));
            font = p.getProperty("uiFont", "");
            V = Boolean.parseBoolean(p.getProperty("musicHud", "true"));
            Z = Boolean.parseBoolean(p.getProperty("headphones", "true"));
            ab = Boolean.parseBoolean(p.getProperty("streamEnabled", "false"));
            ac = Boolean.parseBoolean(p.getProperty("streamHud", "true"));
            ad = p.getProperty("streamServer", ad);
            ae = p.getProperty("streamTwitch", "");
            af = p.getProperty("streamYoutube", "");
            ag = p.getProperty("streamTiktok", "");
            ay = Boolean.parseBoolean(p.getProperty("skyLyrics", "false"));
            aG = p.getProperty("cosmetics", "");
            aH = p.getProperty("sticker", "");
            aI = p.getProperty("figure", "");
            aJ = Boolean.parseBoolean(p.getProperty("figureShare", "true"));
            aK = Boolean.parseBoolean(p.getProperty("figureSeeOthers", "true"));
            aL = Boolean.parseBoolean(p.getProperty("figureShoulder", "false"));
            aN = p.getProperty("skin", "");
            aO = Boolean.parseBoolean(p.getProperty("originalSkinSlim", "false"));
            try {
                aP = Integer.parseInt(p.getProperty("crosshairStyle", "0"));
                aQ = Integer.parseInt(p.getProperty("crosshairColor", "0"));
                aR = Integer.parseInt(p.getProperty("crosshairSize", "1"));
            }
            catch (NumberFormatException e) {
                // empty catch block
            }
            u = Boolean.parseBoolean(p.getProperty("profitSkin", "true"));
            try {
                v = Float.parseFloat(p.getProperty("profitX", "0.01"));
                w = Float.parseFloat(p.getProperty("profitY", "0.32"));
                x = Float.parseFloat(p.getProperty("profitScale", "1.0"));
                D = Float.parseFloat(p.getProperty("statsX", "0.01"));
                E = Float.parseFloat(p.getProperty("statsY", "0.02"));
                F = Float.parseFloat(p.getProperty("statsScale", "1.0"));
                H = Integer.parseInt(p.getProperty("titleTime", "1"));
                J = Integer.parseInt(p.getProperty("titlePlace", "0"));
                P = Float.parseFloat(p.getProperty("zoomAmount", "4.0"));
                W = Float.parseFloat(p.getProperty("musicX", "0.5"));
                X = Float.parseFloat(p.getProperty("musicY", "0.02"));
                Y = Float.parseFloat(p.getProperty("musicScale", "1.0"));
                ah = Float.parseFloat(p.getProperty("streamX", "0.02"));
                ai = Float.parseFloat(p.getProperty("streamY", "0.25"));
                aj = Float.parseFloat(p.getProperty("streamScale", "1.0"));
                ak = Integer.parseInt(p.getProperty("streamLines", "8"));
                al = Integer.parseInt(p.getProperty("streamTheme", "0"));
                am = Integer.parseInt(p.getProperty("streamAnim", "2"));
                an = Boolean.parseBoolean(p.getProperty("wheelHud", "true"));
                ao = Float.parseFloat(p.getProperty("wheelX", "0.5"));
                ap = Float.parseFloat(p.getProperty("wheelY", "0.2"));
                aq = Float.parseFloat(p.getProperty("wheelScale", "1.0"));
                ar = Integer.parseInt(p.getProperty("wheelMode", "0"));
                as = p.getProperty("wheelKeyword", "1");
                at = p.getProperty("wheelAmount", "1");
                au = p.getProperty("wheelLabel", "");
                av = Integer.parseInt(p.getProperty("wheelTimer", "0"));
                aw = p.getProperty("wheelImage", "");
                ax = Integer.parseInt(p.getProperty("chatGameType", "0"));
                aa = Integer.parseInt(p.getProperty("headphonesColor", "0"));
                az = Integer.parseInt(p.getProperty("lyricsStyle", "1"));
                aA = Integer.parseInt(p.getProperty("lyricsPlacement", "1"));
                aB = Integer.parseInt(p.getProperty("lyricsOffset", "0"));
                aC = Integer.parseInt(p.getProperty("lyricsColor", "1"));
                aD = Float.parseFloat(p.getProperty("lyricsSize", "1.0"));
                aE = Boolean.parseBoolean(p.getProperty("lyricsBeat", "true"));
                aF = Long.parseLong(p.getProperty("sessionStart", "0"));
            }
            catch (NumberFormatException e) {
                // empty catch block
            }
            aS = K9.a(K9.a(p.getProperty("sbScale"), 1.0f), 0.5f, 1.5f);
            aT = Boolean.parseBoolean(p.getProperty("sbRounded", "false"));
            aU = Boolean.parseBoolean(p.getProperty("sbHead", "false"));
            aV = Boolean.parseBoolean(p.getProperty("sbDonut", "false"));
            aW = Math.floorMod((int)K9.a(p.getProperty("sbRegion"), 0.0f), 7);
            aX = Math.floorMod((int)K9.a(p.getProperty("sbBackground"), 0.0f), 4);
            aY = K9.a(K9.a(p.getProperty("sbHue"), 0.8f), 0.0f, 1.0f);
            aZ = K9.a(K9.a(p.getProperty("sbOpacity"), 0.45f), 0.0f, 1.0f);
            try {
                h = Math.max(0.0f, Math.min(1.0f, Float.parseFloat(p.getProperty("volume", "0.4"))));
            }
            catch (NumberFormatException var5) {
                h = 0.4f;
            }
        }
    }

    public static void b() {
        Properties p = new Properties();
        p.setProperty("preset", Integer.toString(b));
        for (int ci = 0; ci < c.length; ++ci) {
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < 4; ++k) {
                if (k > 0) {
                    sb.append(',');
                }
                sb.append(String.format("%06x", c[ci][k] & 0xFFFFFF));
            }
            p.setProperty("customTheme" + ci, sb.toString());
        }
        p.setProperty("seeThrough", Boolean.toString(d));
        p.setProperty("frostedBlur", Boolean.toString(e));
        p.setProperty("ambientBackground", Boolean.toString(f));
        p.setProperty("moduleSound", Boolean.toString(g));
        p.setProperty("lastPage", ba);
        p.setProperty("formMinBid", bb);
        p.setProperty("formQuantity", bc);
        p.setProperty("formTimer", bd);
        p.setProperty("formWorth", be);
        p.setProperty("formNote", bf);
        p.setProperty("cape", i);
        p.setProperty("lastCape", j);
        p.setProperty("auctionCape", Boolean.toString(k));
        p.setProperty("nametagIcon", Boolean.toString(l));
        p.setProperty("fakeName", Boolean.toString(m));
        p.setProperty("skyEnabled", Boolean.toString(o));
        p.setProperty("skyPreset", Integer.toString(K9.p));
        p.setProperty("skyShootingStars", Boolean.toString(q));
        p.setProperty("skyHideClouds", Boolean.toString(r));
        p.setProperty("txMinimum", s);
        p.setProperty("profitHud", Boolean.toString(t));
        p.setProperty("cosmetics", aG);
        p.setProperty("sticker", aH);
        p.setProperty("figure", aI);
        p.setProperty("figureShare", Boolean.toString(aJ));
        p.setProperty("figureSeeOthers", Boolean.toString(aK));
        p.setProperty("figureShoulder", Boolean.toString(aL));
        p.setProperty("skin", aN);
        p.setProperty("originalSkinSlim", Boolean.toString(aO));
        p.setProperty("crosshairStyle", Integer.toString(aP));
        p.setProperty("crosshairColor", Integer.toString(aQ));
        p.setProperty("crosshairSize", Integer.toString(aR));
        p.setProperty("profitSkin", Boolean.toString(u));
        p.setProperty("profitX", Float.toString(v));
        p.setProperty("profitY", Float.toString(w));
        p.setProperty("profitScale", Float.toString(x));
        p.setProperty("statsHud", Boolean.toString(y));
        p.setProperty("statsFps", Boolean.toString(z));
        p.setProperty("payGuard", Boolean.toString(B));
        p.setProperty("payGuardAmount", C);
        p.setProperty("statsPing", Boolean.toString(A));
        p.setProperty("statsX", Float.toString(D));
        p.setProperty("statsY", Float.toString(E));
        p.setProperty("statsScale", Float.toString(F));
        p.setProperty("titleScene", Boolean.toString(G));
        p.setProperty("titleTime", Integer.toString(H));
        p.setProperty("titlePetals", Boolean.toString(I));
        p.setProperty("titlePlace", Integer.toString(J));
        p.setProperty("freelookEnabled", Boolean.toString(K));
        p.setProperty("freelookToggle", Boolean.toString(L));
        p.setProperty("freelookThirdPerson", Boolean.toString(M));
        p.setProperty("zoomEnabled", Boolean.toString(N));
        p.setProperty("zoomToggle", Boolean.toString(O));
        p.setProperty("zoomAmount", Float.toString(P));
        p.setProperty("zoomScroll", Boolean.toString(Q));
        p.setProperty("zoomSmooth", Boolean.toString(R));
        p.setProperty("zoomSound", Boolean.toString(S));
        p.setProperty("fullbright", Boolean.toString(T));
        p.setProperty("musicEnabled", Boolean.toString(U));
        p.setProperty("uiFont", font);
        p.setProperty("musicHud", Boolean.toString(V));
        p.setProperty("musicX", Float.toString(W));
        p.setProperty("musicY", Float.toString(X));
        p.setProperty("musicScale", Float.toString(Y));
        p.setProperty("streamEnabled", Boolean.toString(ab));
        p.setProperty("streamHud", Boolean.toString(ac));
        p.setProperty("streamServer", ad);
        p.setProperty("streamTwitch", ae);
        p.setProperty("streamYoutube", af);
        p.setProperty("streamTiktok", ag);
        p.setProperty("streamX", Float.toString(ah));
        p.setProperty("streamY", Float.toString(ai));
        p.setProperty("streamScale", Float.toString(aj));
        p.setProperty("streamLines", Integer.toString(ak));
        p.setProperty("streamTheme", Integer.toString(al));
        p.setProperty("streamAnim", Integer.toString(am));
        p.setProperty("wheelHud", Boolean.toString(an));
        p.setProperty("wheelX", Float.toString(ao));
        p.setProperty("wheelY", Float.toString(ap));
        p.setProperty("wheelScale", Float.toString(aq));
        p.setProperty("wheelMode", Integer.toString(ar));
        p.setProperty("wheelKeyword", as);
        p.setProperty("wheelAmount", at);
        p.setProperty("wheelLabel", au);
        p.setProperty("wheelTimer", Integer.toString(av));
        p.setProperty("wheelImage", aw);
        p.setProperty("chatGameType", Integer.toString(ax));
        p.setProperty("headphones", Boolean.toString(Z));
        p.setProperty("headphonesColor", Integer.toString(aa));
        p.setProperty("skyLyrics", Boolean.toString(ay));
        p.setProperty("lyricsStyle", Integer.toString(az));
        p.setProperty("lyricsPlacement", Integer.toString(aA));
        p.setProperty("lyricsOffset", Integer.toString(aB));
        p.setProperty("lyricsColor", Integer.toString(aC));
        p.setProperty("lyricsSize", Float.toString(aD));
        p.setProperty("lyricsBeat", Boolean.toString(aE));
        p.setProperty("sessionStart", Long.toString(aF));
        p.setProperty("sbScale", Float.toString(aS));
        p.setProperty("sbRounded", Boolean.toString(aT));
        p.setProperty("sbHead", Boolean.toString(aU));
        p.setProperty("sbDonut", Boolean.toString(aV));
        p.setProperty("sbRegion", Integer.toString(aW));
        p.setProperty("sbBackground", Integer.toString(aX));
        p.setProperty("sbHue", Float.toString(aY));
        p.setProperty("sbOpacity", Float.toString(aZ));
        p.setProperty("volume", Float.toString(h));
        try {
            Files.createDirectories(bg.getParent(), new FileAttribute[0]);
            try (BufferedWriter w = Files.newBufferedWriter(bg, new OpenOption[0]);){
                p.store(w, "Lethal Client settings");
            }
        }
        catch (IOException var6) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", (Object)bg, (Object)var6);
        }
    }

    private static float a(String s, float def) {
        try {
            return s == null ? def : Float.parseFloat(s);
        }
        catch (NumberFormatException var3) {
            return def;
        }
    }

    private static float a(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static void c() {
        b = 2;
        d = false;
        e = true;
        f = true;
    }
}

