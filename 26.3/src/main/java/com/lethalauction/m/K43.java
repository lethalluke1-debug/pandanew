/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.lethalauction.m;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalTime;
import net.fabricmc.loader.api.FabricLoader;

final class K43 {
    private K43() {
    }

    static void a(String msg) {
        try {
            Path dir = FabricLoader.getInstance().getConfigDir().resolve("lethal-music");
            Files.createDirectories(dir, new FileAttribute[0]);
            String line = String.valueOf(LocalTime.now().withNano(0)) + "  " + msg + System.lineSeparator();
            Files.write(dir.resolve("music-debug.txt"), line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

