/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package com.lethalauction.m;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.auction.Auctions;
import com.lethalauction.m.K1;
import com.lethalauction.m.K2;
import com.lethalauction.m.K3;
import com.lethalauction.m.K9;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class K4 {
    public static final Path a = FabricLoader.getInstance().getConfigDir().resolve("lethalauction").resolve("capes");
    public static final int b = 5;
    private static final class_2960 c = class_2960.method_60655((String)"lethalauction", (String)"cape_upload");
    private static C63 d;
    private static final Map<String, C62> e;
    private static boolean f;
    private static String g;
    private static long h;
    private static volatile boolean i;

    private K4() {
    }

    public static List<C62> a() {
        if (!f) {
            K4.h();
        }
        return new ArrayList<C62>(e.values());
    }

    public static C63 b() {
        return d;
    }

    public static boolean c() {
        return K4.a().size() >= 5;
    }

    public static class_2960 d() {
        Auctions.C65 a;
        if (K9.k && (a = Auctions.c()) != null && !a.cancelled()) {
            K2.a(a);
            return K2.a;
        }
        if (K1.a(K9.i)) {
            return K1.b(K9.i);
        }
        C62 cape = K4.f();
        return cape == null ? null : cape.b();
    }

    public static String e() {
        Auctions.C65 a;
        if (K1.a(K9.i)) {
            return K9.i;
        }
        if (K9.i.isEmpty() && K9.k && K1.a(K9.j) && (a = Auctions.c()) != null && !a.cancelled()) {
            return K9.j;
        }
        return null;
    }

    public static C62 f() {
        String name = K9.i;
        if (name != null && !name.isEmpty()) {
            if (!f) {
                K4.h();
            }
            return e.get(name);
        }
        return null;
    }

    public static C62 g() {
        Auctions.C65 a;
        C62 cape = K4.f();
        if (cape == null && K9.k && !K9.j.isEmpty() && (a = Auctions.c()) != null && !a.cancelled()) {
            cape = e.get(K9.j);
        }
        return cape;
    }

    public static void a(String name) {
        String string = K9.i = name == null ? "" : name;
        if (!K9.i.isEmpty()) {
            K9.j = K9.i;
        }
        K9.b();
    }

    public static void h() {
        f = true;
        try {
            Files.createDirectories(a, new FileAttribute[0]);
        }
        catch (IOException var7) {
            LethalAuctionClient.LOGGER.warn("Could not create {}", (Object)a, (Object)var7);
            return;
        }
        ArrayList<Path> files = new ArrayList<Path>();
        try (Stream<Path> s = Files.list(a);){
            s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")).sorted().forEach(files::add);
        }
        catch (IOException var9) {
            LethalAuctionClient.LOGGER.warn("Could not list {}", (Object)a, (Object)var9);
        }
        ArrayList<String> present = new ArrayList<String>();
        for (Path file : files) {
            C62 cape;
            String name = file.getFileName().toString();
            present.add(name);
            if (e.containsKey(name) || (cape = K4.b(file)) == null) continue;
            e.put(name, cape);
        }
        for (String name : new ArrayList<String>(e.keySet())) {
            if (present.contains(name)) continue;
            class_310.method_1551().method_1531().method_4615(e.remove(name).b());
        }
    }

    private static C62 b(Path file) {
        try {
            C62 var7;
            try (InputStream in = Files.newInputStream(file, new OpenOption[0]);){
                class_1011 image = K4.a(class_1011.method_4309((InputStream)in));
                String key = file.getFileName().toString().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
                class_2960 id = class_2960.method_60655((String)"lethalauction", (String)("capes/" + key));
                int w = image.method_4307();
                int h = image.method_4323();
                class_310.method_1551().method_1531().method_4616(id, (class_1044)new class_1043(() -> "Lethal Client cape " + key, image));
                var7 = new C62(file.getFileName().toString(), id, w, h);
            }
            return var7;
        }
        catch (IOException | RuntimeException var10) {
            LethalAuctionClient.LOGGER.warn("Could not load cape {}", (Object)file, (Object)var10);
            return null;
        }
    }

    private static class_1011 a(class_1011 image) {
        if (image.method_4307() == image.method_4323() * 2) {
            return image;
        }
        int scale = Math.max(1, image.method_4323() / 17);
        class_1011 fixed = new class_1011(64 * scale, 32 * scale, true);
        for (int y = 0; y < Math.min(image.method_4323(), fixed.method_4323()); ++y) {
            for (int x = 0; x < Math.min(image.method_4307(), fixed.method_4307()); ++x) {
                fixed.method_61941(x, y, image.method_61940(x, y));
            }
        }
        image.close();
        return fixed;
    }

    public static void i() {
        if (!i && d == null) {
            if (K4.c()) {
                K4.d("All 5 slots are full. Delete a cape first.");
            } else {
                i = true;
                Thread t = new Thread(() -> {
                    try {
                        String chosen;
                        try (MemoryStack stack = MemoryStack.stackPush();){
                            PointerBuffer filters = stack.mallocPointer(3);
                            filters.put(stack.UTF8((CharSequence)"*.png"));
                            filters.put(stack.UTF8((CharSequence)"*.jpg"));
                            filters.put(stack.UTF8((CharSequence)"*.jpeg"));
                            filters.flip();
                            chosen = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Choose a picture for your cape", (CharSequence)"", (PointerBuffer)filters, (CharSequence)"Images", (boolean)false);
                        }
                        if (chosen != null) {
                            byte[] bytes = Files.readAllBytes(Path.of(chosen, new String[0]));
                            String base = Path.of(chosen, new String[0]).getFileName().toString().replaceAll("\\.[A-Za-z0-9]+$", "");
                            class_310.method_1551().execute(() -> K4.a(bytes, base));
                            return;
                        }
                    }
                    catch (IOException | RuntimeException var11) {
                        LethalAuctionClient.LOGGER.warn("Cape upload failed", (Throwable)var11);
                        class_310.method_1551().execute(() -> K4.d("Could not open that picture"));
                        return;
                    }
                    finally {
                        i = false;
                    }
                }, "Lethal Client cape picker");
                t.setDaemon(true);
                t.start();
            }
        }
    }

    public static void a(Path file) {
        try {
            K4.a(Files.readAllBytes(file), "Sunset");
        }
        catch (IOException var2) {
            LethalAuctionClient.LOGGER.warn("dev crop failed", (Throwable)var2);
        }
    }

    private static void a(byte[] bytes, String name) {
        try {
            class_1011 image = class_1011.method_49277((byte[])bytes);
            class_1011 preview = new class_1011(image.method_4307(), image.method_4323(), false);
            preview.method_4317(image);
            class_310.method_1551().method_1531().method_4616(c, (class_1044)new class_1043(() -> "Lethal Client cape upload", preview));
            d = new C63(image, name.isBlank() ? "Cape" : name);
        }
        catch (IOException | RuntimeException var4) {
            LethalAuctionClient.LOGGER.warn("Could not read picture", (Throwable)var4);
            K4.d("That picture could not be read (use PNG)");
        }
    }

    public static void j() {
        C63 p = d;
        if (p != null) {
            String name = K4.c(K4.b(p.b));
            try (class_1011 cape = K3.a(p.a, p.e, p.f, p.c, p.d);){
                Files.createDirectories(a, new FileAttribute[0]);
                cape.method_4314(a.resolve(name + ".png"));
            }
            catch (IOException | RuntimeException var7) {
                LethalAuctionClient.LOGGER.warn("Could not save cape", (Throwable)var7);
                K4.d("Could not save the cape");
            }
            K4.k();
            K4.h();
            K4.a(name + ".png");
            K4.d("Cape saved and equipped");
        }
    }

    public static void k() {
        if (d != null) {
            K4.d.a.close();
            d = null;
            class_310.method_1551().method_1531().method_4615(c);
        }
    }

    public static boolean a(C62 cape, String newName) {
        String clean = K4.b(newName);
        if (clean.isEmpty()) {
            return false;
        }
        String file = clean + ".png";
        if (file.equals(cape.a())) {
            return true;
        }
        if (Files.exists(a.resolve(file), new LinkOption[0])) {
            K4.d("A cape with that name already exists");
            return false;
        }
        try {
            Files.move(a.resolve(cape.a()), a.resolve(file), new CopyOption[0]);
        }
        catch (IOException var5) {
            LethalAuctionClient.LOGGER.warn("Could not rename cape", (Throwable)var5);
            return false;
        }
        boolean wasSelected = cape.a().equals(K9.i);
        K4.h();
        if (wasSelected) {
            K4.a(file);
        }
        return true;
    }

    private static String b(String name) {
        return name.trim().replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", " ");
    }

    private static String c(String base) {
        String name = base.isEmpty() ? "Cape" : base;
        int i = 2;
        while (Files.exists(a.resolve((String)name + ".png"), new LinkOption[0])) {
            name = base + " " + i++;
        }
        return name;
    }

    public static void a(C62 cape) {
        try {
            Files.deleteIfExists(a.resolve(cape.a()));
        }
        catch (IOException var2) {
            LethalAuctionClient.LOGGER.warn("Could not delete cape {}", (Object)cape.a(), (Object)var2);
        }
        if (cape.a().equals(K9.i)) {
            K4.a("");
        }
        K4.h();
    }

    private static void d(String text) {
        g = text;
        h = System.currentTimeMillis() + 3500L;
    }

    public static String l() {
        return System.currentTimeMillis() < h ? g : "";
    }

    static {
        e = new LinkedHashMap<String, C62>();
        g = "";
    }

    public static final class C63 {
        public final class_1011 a;
        public String b;
        private final int h;
        private final int i;
        public int c;
        public int d;
        public int e;
        public int f;
        public float g = 1.0f;

        C63(class_1011 image, String suggestedName) {
            this.a = image;
            this.b = suggestedName;
            int w = image.method_4307();
            int h = image.method_4323();
            if ((float)w / (float)h > 0.625f) {
                this.d = h;
                this.c = Math.max(1, Math.round((float)h * 0.625f));
            } else {
                this.c = w;
                this.d = Math.max(1, Math.round((float)w / 0.625f));
            }
            this.h = this.c;
            this.i = this.d;
            this.e = (w - this.c) / 2;
            this.f = (h - this.d) / 2;
        }

        public void a(float z) {
            this.g = Math.max(1.0f, Math.min(5.0f, z));
            int midX = this.e + this.c / 2;
            int midY = this.f + this.d / 2;
            this.c = Math.max(4, Math.round((float)this.h / this.g));
            this.d = Math.max(6, Math.round((float)this.i / this.g));
            this.a(midX - this.c / 2, midY - this.d / 2);
        }

        public class_2960 a() {
            return K4.c;
        }

        public void a(int x, int y) {
            this.e = Math.max(0, Math.min(this.a.method_4307() - this.c, x));
            this.f = Math.max(0, Math.min(this.a.method_4323() - this.d, y));
        }
    }

    public static final class C62 {
        private final String a;
        private final class_2960 b;
        private final int c;
        private final int d;
        C62(String a, class_2960 b, int c, int d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public String a() { return this.a; }
        public class_2960 b() { return this.b; }
        public int c() { return this.c; }
        public int d() { return this.d; }


    }
}

