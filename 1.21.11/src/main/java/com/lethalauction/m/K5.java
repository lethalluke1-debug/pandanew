/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 */
package com.lethalauction.m;

import com.lethalauction.LethalAuctionClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;

public final class K5 {
    private static final Path a = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-keys.properties");
    private static final String b = "key_key.lethalauction.open:";
    private static boolean c;
    private static int d;
    private static Map<String, String> e;
    private static boolean f;

    public static void a() {
        try {
            Path file = FabricLoader.getInstance().getGameDir().resolve("options.txt");
            f = Files.exists(file, new LinkOption[0]) && !Files.readString(file, StandardCharsets.UTF_8).contains(b);
        }
        catch (IOException e) {
            f = false;
        }
    }

    private K5() {
    }

    public static void a(class_310 mc) {
        Map<String, String> now;
        if (!c) {
            c = true;
            K5.b(mc);
            e = K5.c(mc);
            K5.a(e);
            return;
        }
        if (++d % 40 == 0 && !(now = K5.c(mc)).equals(e)) {
            e = now;
            K5.a(now);
        }
    }

    private static void b(class_310 mc) {
        Map<String, String> backup = K5.b();
        if (backup.isEmpty()) {
            return;
        }
        if (!f) {
            return;
        }
        int restored = 0;
        for (class_304 key : mc.field_1690.field_1839) {
            String value = backup.get(key.method_1431());
            if (value == null || value.equals(key.method_1428())) continue;
            try {
                key.method_1422(class_3675.method_15981((String)value));
                ++restored;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        class_304.method_1426();
        mc.field_1690.method_1640();
        LethalAuctionClient.LOGGER.info("Put back {} keybinds that were reset", (Object)restored);
    }

    private static Map<String, String> c(class_310 mc) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (class_304 key : mc.field_1690.field_1839) {
            map.put(key.method_1431(), key.method_1428());
        }
        return map;
    }

    private static Map<String, String> b() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (!Files.exists(a, new LinkOption[0])) {
            return map;
        }
        Properties p = new Properties();
        try (BufferedReader r = Files.newBufferedReader(a);){
            p.load(r);
        }
        catch (IOException e) {
            return map;
        }
        for (String name : p.stringPropertyNames()) {
            map.put(name, p.getProperty(name));
        }
        return map;
    }

    private static void a(Map<String, String> map) {
        Properties p = new Properties();
        p.putAll(map);
        try {
            Files.createDirectories(a.getParent(), new FileAttribute[0]);
            try (BufferedWriter w = Files.newBufferedWriter(a, new OpenOption[0]);){
                p.store(w, "Lethal Client keybind backup");
            }
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not save the keybind backup", (Throwable)e);
        }
    }

    static {
        e = new HashMap<String, String>();
    }
}

