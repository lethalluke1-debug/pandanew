/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.lethalauction.m;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import net.fabricmc.loader.api.FabricLoader;

final class K36 {
    private static String a;
    private static final Path b;

    private K36() {
    }

    static synchronized String a() {
        if (a == null) {
            a = K36.b();
        }
        return a;
    }

    private static String b() {
        try {
            String saved;
            if (Files.exists(b, new LinkOption[0]) && !(saved = Files.readString(b).trim()).isEmpty()) {
                return saved;
            }
        }
        catch (IOException saved) {
            // empty catch block
        }
        String id = K36.a("lethalauction|" + String.valueOf(UUID.randomUUID())).substring(0, 32);
        try {
            Files.createDirectories(b.getParent(), new FileAttribute[0]);
            Files.writeString(b, (CharSequence)id, new OpenOption[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return id;
    }

    private static String a(String s) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static {
        b = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-install.txt");
    }
}

