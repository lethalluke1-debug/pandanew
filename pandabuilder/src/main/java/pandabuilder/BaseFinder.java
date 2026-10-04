package pandabuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

/**
 * Scans loaded chunks for signs of players: storage, redstone, decoration blocks and entities that
 * world generation never (or rarely) places. Each chunk gets a score, neighbouring chunks are
 * grouped into clusters, and clusters above the sensitivity threshold are reported as bases.
 * Blocks that natural structures also generate (villages, dungeons, mineshafts, ruined portals...)
 * count for a quarter when that structure's marker blocks are in the same chunk.
 */
public final class BaseFinder {
   public static final String[] SENSITIVITY_NAMES = {"Low", "Normal", "High"};
   private static final double[] THRESHOLDS = {80.0, 40.0, 20.0};
   private static final double CHUNK_MIN_SCORE = 4.0;
   private static final int CLUSTER_REACH = 2;
   private static final int SAME_BASE_DISTANCE = 64;
   private static final int SWEEP_RADIUS = 34;
   private static final long SCAN_BUDGET_NANOS = 2_000_000L;
   private static final int MAX_RECORDS = 50_000;

   private static final Set<String> TARGET_LABELS = Set.of("chest", "trapped chest", "barrel", "shulker box", "dispenser", "sign", "hanging sign");
   private static final Rule NONE = new Rule("", 0, 0, false, null);
   private static final Map<Block, Rule> BLOCK_RULES = new IdentityHashMap<>();
   private static final Map<Object, Rule> ENTITY_RULES = new IdentityHashMap<>();

   private static final ArrayDeque<Long> queue = new ArrayDeque<>();
   private static final Set<Long> queued = new HashSet<>();
   private static final Set<Long> scanned = new HashSet<>();
   private static final Map<Long, ChunkHits> records = new HashMap<>();
   private static final List<Base> bases = new ArrayList<>();
   private static ClientLevel lastLevel;
   private static String worldId = "";
   private static boolean basesLoaded;
   private static int ticks;

   private BaseFinder() {
   }

   public static final class Base {
      public final String world;
      public String dimension;
      public int x;
      public int y;
      public int z;
      public int score;
      public String summary;
      public long found;

      Base(String world, String dimension, int x, int y, int z, int score, String summary, long found) {
         this.world = world;
         this.dimension = dimension;
         this.x = x;
         this.y = y;
         this.z = z;
         this.score = score;
         this.summary = summary;
         this.found = found;
      }
   }

   private record Rule(String label, double weight, double cap, boolean strong, String structure) {
      boolean counts() {
         return this.weight > 0.0;
      }
   }

   private static final class ChunkHits {
      final int cx;
      final int cz;
      final Map<Rule, Integer> blocks = new HashMap<>();
      final Map<Rule, Integer> entities = new HashMap<>();
      final Set<String> structures = new HashSet<>();
      final Set<String> entityStructures = new HashSet<>();
      final List<Target> targets = new ArrayList<>();
      double bx;
      double by;
      double bz;
      double bw;
      double ex;
      double ey;
      double ez;
      double ew;
      double score;

      ChunkHits(int cx, int cz) {
         this.cx = cx;
         this.cz = cz;
      }

      void score() {
         boolean natural = !this.structures.isEmpty() || !this.entityStructures.isEmpty();
         Map<Rule, Integer> all = new HashMap<>(this.blocks);
         this.entities.forEach((r, c) -> all.merge(r, c, Integer::sum));
         double total = 0.0;
         int strongKinds = 0;

         for (Map.Entry<Rule, Integer> e : all.entrySet()) {
            Rule r = e.getKey();
            double v = Math.min(r.cap, r.weight * e.getValue());
            if (!r.strong && natural) {
               v *= 0.25;
            }

            total += v;
            if (r.strong) {
               strongKinds++;
            }
         }

         // Real bases mix many kinds of player-only blocks; one odd block is usually noise.
         total += Math.max(0, strongKinds - 1) * 6.0;
         this.score = total;
      }
   }

   // ---- Rules -------------------------------------------------------------------------------

   private static Rule strong(String label, double weight, double cap) {
      return new Rule(label, weight, cap, true, null);
   }

   private static Rule weak(String label, double weight, double cap) {
      return new Rule(label, weight, cap, false, null);
   }

   private static Rule marker(String structure) {
      return new Rule("", 0, 0, false, structure);
   }

   private static Rule blockRule(Block block) {
      Rule rule = BLOCK_RULES.get(block);
      if (rule == null) {
         Identifier id = BuiltInRegistries.BLOCK.getKey(block);
         rule = id == null || !"minecraft".equals(id.getNamespace()) ? NONE : classifyBlock(id.getPath());
         BLOCK_RULES.put(block, rule);
      }

      return rule;
   }

   private static boolean interesting(BlockState state) {
      return blockRule(state.getBlock()) != NONE;
   }

   private static Rule classifyBlock(String p) {
      // Structure markers first: they mean "nearby blocks may be natural".
      switch (p) {
         case "spawner":
            return marker("dungeon/mineshaft");
         case "trial_spawner":
         case "vault":
            return marker("trial chamber");
         case "bell":
            return marker("village");
         case "end_portal_frame":
         case "infested_stone_bricks":
         case "infested_mossy_stone_bricks":
         case "infested_cracked_stone_bricks":
            return marker("stronghold");
         case "reinforced_deepslate":
         case "sculk_shrieker":
            return marker("ancient city");
         case "crying_obsidian":
            return marker("ruined portal");
         case "prismarine_bricks":
         case "dark_prismarine":
            return marker("ocean monument");
         case "suspicious_sand":
         case "suspicious_gravel":
            return marker("ruins");
         case "purpur_block":
         case "purpur_pillar":
            return marker("end city");
         case "gilded_blackstone":
            return marker("bastion");
         case "cobweb":
            return marker("mineshaft");
      }

      if (p.endsWith("shulker_box")) return strong("shulker box", 30, 600);
      if (p.endsWith("_hanging_sign")) return strong("hanging sign", 8, 64);
      if (p.endsWith("_sign")) return strong("sign", 8, 64);
      if (p.endsWith("_concrete")) return strong("concrete", 1, 40);
      if (p.endsWith("_concrete_powder")) return strong("concrete powder", 0.5, 15);
      if (p.endsWith("_stained_glass") || p.endsWith("_stained_glass_pane") || p.equals("tinted_glass")) return strong("stained glass", 0.6, 25);
      if (p.endsWith("_glazed_terracotta")) return weak("glazed terracotta", 1, 20);
      if (p.endsWith("_banner")) return weak("banner", 5, 40);
      if (p.endsWith("_bed")) return weak("bed", 3, 24);
      if (p.endsWith("_wool")) return weak("wool", 0.2, 8);
      if (p.endsWith("_carpet") && !p.contains("moss")) return weak("carpet", 0.3, 8);
      if (p.startsWith("potted_") || p.equals("flower_pot")) return weak("flower pot", 0.5, 5);
      if (p.equals("player_head") || p.equals("player_wall_head")) return strong("player head", 8, 40);
      if ((p.endsWith("_head") || p.endsWith("_skull")) && !p.equals("piston_head")) return weak("mob head", 2, 10);

      return switch (p) {
         case "ender_chest" -> strong("ender chest", 15, 150);
         case "beacon" -> strong("beacon", 50, 200);
         case "conduit" -> strong("conduit", 35, 70);
         case "respawn_anchor" -> strong("respawn anchor", 20, 40);
         case "enchanting_table" -> strong("enchanting table", 12, 24);
         case "anvil", "chipped_anvil", "damaged_anvil" -> strong("anvil", 10, 30);
         case "netherite_block" -> strong("netherite block", 25, 250);
         case "diamond_block" -> strong("diamond block", 20, 200);
         case "emerald_block" -> strong("emerald block", 10, 100);
         case "iron_block" -> strong("iron block", 4, 60);
         case "lapis_block", "redstone_block" -> strong("ore block", 3, 30);
         case "gold_block" -> weak("gold block", 2, 20);
         case "hopper" -> strong("hopper", 6, 60);
         case "observer" -> strong("observer", 6, 60);
         case "comparator" -> strong("comparator", 6, 48);
         case "daylight_detector" -> strong("daylight sensor", 6, 24);
         case "redstone_lamp" -> strong("redstone lamp", 4, 40);
         case "target" -> strong("target block", 4, 16);
         case "note_block" -> strong("note block", 3, 15);
         case "jukebox" -> strong("jukebox", 6, 12);
         case "cake" -> strong("cake", 4, 8);
         case "scaffolding" -> strong("scaffolding", 1, 10);
         case "chiseled_bookshelf" -> strong("chiseled bookshelf", 3, 15);
         case "piston", "sticky_piston", "piston_head", "moving_piston" -> weak("piston", 4, 40);
         case "repeater" -> weak("repeater", 2, 20);
         case "redstone_wire" -> weak("redstone dust", 0.5, 10);
         case "crafter" -> weak("crafter", 6, 30);
         case "tnt" -> weak("TNT", 3, 30);
         case "chest" -> weak("chest", 2, 60);
         case "trapped_chest" -> weak("trapped chest", 3, 30);
         case "barrel" -> weak("barrel", 2, 40);
         case "furnace", "blast_furnace", "smoker" -> weak("furnace", 2, 24);
         case "dispenser", "dropper" -> weak("dispenser", 2, 20);
         case "brewing_stand" -> weak("brewing stand", 4, 16);
         case "crafting_table" -> weak("crafting table", 2, 6);
         case "lectern", "smithing_table", "cartography_table", "fletching_table", "loom", "grindstone", "stonecutter", "composter" ->
            weak("workstation", 1, 6);
         case "bookshelf" -> weak("bookshelf", 0.3, 6);
         case "glass", "glass_pane" -> weak("glass", 0.3, 12);
         case "sea_lantern" -> weak("sea lantern", 0.5, 10);
         case "lantern", "soul_lantern" -> weak("lantern", 0.5, 8);
         case "torch", "wall_torch", "soul_torch", "soul_wall_torch" -> weak("torch", 0.1, 3);
         case "obsidian" -> weak("obsidian", 0.3, 12);
         case "ladder" -> weak("ladder", 0.2, 4);
         case "farmland" -> weak("farmland", 0.05, 3);
         default -> NONE;
      };
   }

   private static Rule entityRule(Entity entity, boolean inEnd) {
      Object type = entity.getType();
      Rule rule = ENTITY_RULES.get(type);
      if (rule == null) {
         Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
         rule = id == null || !"minecraft".equals(id.getNamespace()) ? NONE : classifyEntity(id.getPath());
         ENTITY_RULES.put(type, rule);
      }

      // End crystals stand on the obsidian pillars in the End naturally.
      return inEnd && rule.label.equals("end crystal") ? NONE : rule;
   }

   private static Rule classifyEntity(String p) {
      if (p.endsWith("_boat") || p.endsWith("_raft")) return weak("boat", 2, 10);
      if (p.endsWith("minecart")) return weak("minecart", 2, 20);
      return switch (p) {
         case "armor_stand" -> strong("armor stand", 12, 60);
         case "item_frame", "glow_item_frame" -> strong("item frame", 6, 60);
         case "painting" -> strong("painting", 3, 30);
         case "end_crystal" -> strong("end crystal", 12, 48);
         case "villager", "iron_golem" -> marker("village");
         default -> NONE;
      };
   }

   // ---- Lifecycle -----------------------------------------------------------------------------

   public static void onChunkLoad(LevelChunk chunk) {
      if (Settings.baseFinder) {
         ChunkPos pos = chunk.getPos();
         enqueue(pos.x(), pos.z());
      }
   }

   private static long key(int cx, int cz) {
      return (long)cx & 4294967295L | ((long)cz & 4294967295L) << 32;
   }

   private static void enqueue(int cx, int cz) {
      long k = key(cx, cz);
      if (queued.add(k)) {
         queue.add(k);
      }
   }

   public static void toggle() {
      Settings.baseFinder = !Settings.baseFinder;
      if (!Settings.baseFinder && AutoExplore.isRunning()) {
         AutoExplore.setEnabled(false);
      }

      Settings.save();
      if (Settings.baseFinder) {
         rescan();
      }

      message(
         Component.literal("Base Finder " + (Settings.baseFinder ? "ON" : "OFF"))
            .withStyle(Settings.baseFinder ? ChatFormatting.GREEN : ChatFormatting.RED)
      );
   }

   /** Forget per-chunk results and scan everything that's loaded again. */
   public static void rescan() {
      queue.clear();
      queued.clear();
      scanned.clear();
      records.clear();
      ticks = 0;
   }

   public static void tick(Minecraft client) {
      ClientLevel level = client.level;
      LocalPlayer player = client.player;
      if (level != lastLevel) {
         lastLevel = level;
         rescan();
         if (level != null) {
            worldId = computeWorldId(client);
            loadBases(client);
         }
      }

      if (!Settings.baseFinder || level == null || player == null) {
         return;
      }

      ticks++;
      if (ticks % 100 == 1) {
         sweep(level, player);
      }

      long start = System.nanoTime();

      while (!queue.isEmpty() && System.nanoTime() - start < SCAN_BUDGET_NANOS) {
         long k = queue.poll();
         queued.remove(k);
         int cx = (int)k;
         int cz = (int)(k >>> 32);
         LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
         if (chunk != null) {
            scanChunk(chunk, cx, cz);
            scanned.add(k);
         }
      }

      if (ticks % 40 == 0) {
         scanEntities(level);
         evaluate(level, player);
      }
   }

   private static void sweep(ClientLevel level, LocalPlayer player) {
      ChunkPos center = player.chunkPosition();

      for (int dx = -SWEEP_RADIUS; dx <= SWEEP_RADIUS; dx++) {
         for (int dz = -SWEEP_RADIUS; dz <= SWEEP_RADIUS; dz++) {
            int cx = center.x() + dx;
            int cz = center.z() + dz;
            if (!scanned.contains(key(cx, cz)) && level.getChunkSource().getChunkNow(cx, cz) != null) {
               enqueue(cx, cz);
            }
         }
      }
   }

   // ---- Scanning ------------------------------------------------------------------------------

   private static void scanChunk(LevelChunk chunk, int cx, int cz) {
      long k = key(cx, cz);
      ChunkHits hits = new ChunkHits(cx, cz);
      ChunkHits old = records.get(k);
      if (old != null) {
         hits.entities.putAll(old.entities);
         hits.entityStructures.addAll(old.entityStructures);
         hits.ex = old.ex;
         hits.ey = old.ey;
         hits.ez = old.ez;
         hits.ew = old.ew;
      }

      LevelChunkSection[] sections = chunk.getSections();
      int baseX = cx << 4;
      int baseZ = cz << 4;

      for (int i = 0; i < sections.length; i++) {
         LevelChunkSection section = sections[i];
         // The palette check skips whole 16x16x16 sections that hold none of the blocks we care about.
         if (section == null || section.hasOnlyAir() || !section.maybeHas(BaseFinder::interesting)) {
            continue;
         }

         int baseY = chunk.getSectionYFromSectionIndex(i) << 4;

         for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
               for (int x = 0; x < 16; x++) {
                  Rule rule = blockRule(section.getBlockState(x, y, z).getBlock());
                  if (rule == NONE) {
                     continue;
                  }

                  if (rule.structure != null) {
                     hits.structures.add(rule.structure);
                  } else {
                     hits.blocks.merge(rule, 1, Integer::sum);
                     if (TARGET_LABELS.contains(rule.label)) {
                        hits.targets.add(new Target(baseX + x, baseY + y, baseZ + z, rule.label));
                     }

                     double w = rule.strong ? rule.weight * 2.0 : rule.weight;
                     hits.bx += (baseX + x) * w;
                     hits.by += (baseY + y) * w;
                     hits.bz += (baseZ + z) * w;
                     hits.bw += w;
                  }
               }
            }
         }
      }

      store(k, hits);
   }

   private static void store(long k, ChunkHits hits) {
      hits.score();
      if (hits.blocks.isEmpty() && hits.entities.isEmpty()) {
         records.remove(k);
      } else if (records.size() < MAX_RECORDS || records.containsKey(k)) {
         records.put(k, hits);
      }
   }

   private static void scanEntities(ClientLevel level) {
      boolean inEnd = dimensionName(level).equals("the_end");
      Map<Long, ChunkHits> found = new HashMap<>();

      for (Entity entity : level.entitiesForRendering()) {
         Rule rule = entityRule(entity, inEnd);
         if (rule == NONE) {
            continue;
         }

         ChunkPos cp = entity.chunkPosition();
         ChunkHits h = found.computeIfAbsent(key(cp.x(), cp.z()), kk -> new ChunkHits(cp.x(), cp.z()));
         if (rule.structure != null) {
            h.entityStructures.add(rule.structure);
         } else {
            h.entities.merge(rule, 1, Integer::sum);
            double w = rule.strong ? rule.weight * 2.0 : rule.weight;
            h.ex += entity.getX() * w;
            h.ey += entity.getY() * w;
            h.ez += entity.getZ() * w;
            h.ew += w;
         }
      }

      // Replace entity results for every loaded chunk (entities move, die and get picked up).
      for (Map.Entry<Long, ChunkHits> e : new ArrayList<>(records.entrySet())) {
         ChunkHits h = e.getValue();
         if (!found.containsKey(e.getKey()) && level.getChunkSource().getChunkNow(h.cx, h.cz) != null && (!h.entities.isEmpty() || !h.entityStructures.isEmpty())) {
            h.entities.clear();
            h.entityStructures.clear();
            h.ex = h.ey = h.ez = h.ew = 0.0;
            store(e.getKey(), h);
         }
      }

      for (Map.Entry<Long, ChunkHits> e : found.entrySet()) {
         ChunkHits fresh = e.getValue();
         ChunkHits h = records.get(e.getKey());
         if (h == null) {
            h = new ChunkHits(fresh.cx, fresh.cz);
         }

         h.entities.clear();
         h.entities.putAll(fresh.entities);
         h.entityStructures.clear();
         h.entityStructures.addAll(fresh.entityStructures);
         h.ex = fresh.ex;
         h.ey = fresh.ey;
         h.ez = fresh.ez;
         h.ew = fresh.ew;
         store(e.getKey(), h);
      }
   }

   // ---- Clustering and reporting --------------------------------------------------------------

   private static void evaluate(ClientLevel level, LocalPlayer player) {
      double threshold = THRESHOLDS[Math.max(0, Math.min(2, Settings.baseSensitivity))];
      Set<Long> seen = new HashSet<>();
      String dim = dimensionName(level);

      for (Map.Entry<Long, ChunkHits> start : records.entrySet()) {
         if (start.getValue().score < CHUNK_MIN_SCORE || !seen.add(start.getKey())) {
            continue;
         }

         // Flood-fill neighbouring chunks (allowing small gaps) into one cluster.
         List<ChunkHits> cluster = new ArrayList<>();
         ArrayDeque<ChunkHits> todo = new ArrayDeque<>();
         todo.add(start.getValue());

         while (!todo.isEmpty()) {
            ChunkHits h = todo.poll();
            cluster.add(h);

            for (int dx = -CLUSTER_REACH; dx <= CLUSTER_REACH; dx++) {
               for (int dz = -CLUSTER_REACH; dz <= CLUSTER_REACH; dz++) {
                  long nk = key(h.cx + dx, h.cz + dz);
                  ChunkHits n = records.get(nk);
                  if (n != null && n.score >= CHUNK_MIN_SCORE && seen.add(nk)) {
                     todo.add(n);
                  }
               }
            }
         }

         double score = 0.0;
         double sx = 0.0;
         double sy = 0.0;
         double sz = 0.0;
         double sw = 0.0;
         Map<String, Integer> labels = new HashMap<>();
         Set<String> structures = new TreeSet<>();

         for (ChunkHits h : cluster) {
            score += h.score;
            sx += h.bx + h.ex;
            sy += h.by + h.ey;
            sz += h.bz + h.ez;
            sw += h.bw + h.ew;
            h.blocks.forEach((r, c) -> labels.merge(r.label, c, Integer::sum));
            h.entities.forEach((r, c) -> labels.merge(r.label, c, Integer::sum));
            structures.addAll(h.structures);
            structures.addAll(h.entityStructures);
         }

         if (score < threshold || sw <= 0.0) {
            continue;
         }

         int x = (int)Math.floor(sx / sw);
         int y = (int)Math.floor(sy / sw);
         int z = (int)Math.floor(sz / sw);
         String summary = summarize(labels, structures);
         report(player, dim, x, y, z, (int)Math.round(score), summary);
      }
   }

   private static String summarize(Map<String, Integer> labels, Set<String> structures) {
      List<Map.Entry<String, Integer>> list = new ArrayList<>(labels.entrySet());
      list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
      Map<String, Integer> priority = new LinkedHashMap<>();

      // Always show the high-value finds, then fill up with the most common blocks.
      for (String label : new String[]{"beacon", "shulker box", "ender chest", "netherite block", "diamond block", "conduit", "armor stand"}) {
         if (labels.containsKey(label)) {
            priority.put(label, labels.get(label));
         }
      }

      for (Map.Entry<String, Integer> e : list) {
         if (priority.size() >= 5) {
            break;
         }

         priority.putIfAbsent(e.getKey(), e.getValue());
      }

      StringBuilder sb = new StringBuilder();
      priority.forEach((label, count) -> {
         if (sb.length() > 0) {
            sb.append(", ");
         }

         sb.append(count).append(' ').append(label);
      });
      if (!structures.isEmpty()) {
         sb.append(" (near ").append(String.join(", ", structures)).append(')');
      }

      return sb.toString();
   }

   private static void report(LocalPlayer player, String dim, int x, int y, int z, int score, String summary) {
      for (Base b : bases) {
         if (b.world.equals(worldId) && b.dimension.equals(dim) && Math.abs(b.x - x) <= SAME_BASE_DISTANCE && Math.abs(b.z - z) <= SAME_BASE_DISTANCE) {
            if (score > b.score) {
               b.x = x;
               b.y = y;
               b.z = z;
               b.score = score;
               b.summary = summary;
               saveBases();
            }

            return;
         }
      }

      Base base = new Base(worldId, dim, x, y, z, score, summary, System.currentTimeMillis());
      bases.add(base);
      saveBases();
      int dist = (int)Math.sqrt(distanceSq(player, x, z));
      message(
         Component.literal("Base found at " + x + ", " + y + ", " + z + " (" + dist + "m away, score " + score + "): ")
            .withStyle(ChatFormatting.GREEN)
            .append(Component.literal(summary).withStyle(ChatFormatting.YELLOW))
      );
      AutoExplore.onBaseFound();
   }

   // ---- Results for the UI --------------------------------------------------------------------

   public static List<Base> basesInThisWorld() {
      List<Base> list = new ArrayList<>();

      for (Base b : bases) {
         if (b.world.equals(worldId)) {
            list.add(b);
         }
      }

      return list;
   }

   /** A block the Storage Run visits: containers (chest, barrel, shulker box, dispenser/dropper) and signs. */
   public record Target(int x, int y, int z, String kind) {
      public boolean isSign() {
         return this.kind.endsWith("sign");
      }

      public boolean isChest() {
         return this.kind.endsWith("chest");
      }
   }

   /** Visit targets the scanner has seen within {@code radius} blocks (horizontally) of x/z. */
   public static List<Target> targetsNear(int x, int z, int radius) {
      List<Target> list = new ArrayList<>();

      for (ChunkHits h : records.values()) {
         int mx = (h.cx << 4) + 8;
         int mz = (h.cz << 4) + 8;
         if (Math.abs(mx - x) > radius + 16 || Math.abs(mz - z) > radius + 16) {
            continue;
         }

         for (Target p : h.targets) {
            if (Math.abs(p.x() - x) <= radius && Math.abs(p.z() - z) <= radius) {
               list.add(p);
            }
         }
      }

      return list;
   }

   public static void clearBases() {
      bases.removeIf(b -> b.world.equals(worldId));
      saveBases();
      rescan();
   }

   public static int queuedChunks() {
      return queue.size();
   }

   public static int scannedChunks() {
      return scanned.size();
   }

   public static String currentDimension() {
      ClientLevel level = Minecraft.getInstance().level;
      return level == null ? "" : dimensionName(level);
   }

   public static double distanceSq(LocalPlayer player, int x, int z) {
      double dx = player.getX() - x;
      double dz = player.getZ() - z;
      return dx * dx + dz * dz;
   }

   public static Base nearest(LocalPlayer player) {
      String dim = currentDimension();
      Base best = null;

      for (Base b : bases) {
         if (b.world.equals(worldId) && b.dimension.equals(dim) && (best == null || distanceSq(player, b.x, b.z) < distanceSq(player, best.x, best.z))) {
            best = b;
         }
      }

      return best;
   }

   static String dimensionName(ClientLevel level) {
      Identifier id = level.dimension().identifier();
      return "minecraft".equals(id.getNamespace()) ? id.getPath() : id.toString();
   }

   // ---- Saving --------------------------------------------------------------------------------

   private static String computeWorldId(Minecraft client) {
      ServerData server = client.getCurrentServer();
      if (server != null && server.ip != null) {
         return "server:" + server.ip;
      }

      MinecraftServer local = client.getSingleplayerServer();
      return local != null ? "world:" + local.getWorldData().getLevelName() : "unknown";
   }

   private static Path basesFile() {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("pandabuilder").resolve("bases.tsv");
   }

   private static String clean(String s) {
      return s.replace('\t', ' ').replace('\n', ' ');
   }

   private static void loadBases(Minecraft client) {
      if (basesLoaded) {
         return;
      }

      basesLoaded = true;
      Path file = basesFile();
      if (!Files.exists(file)) {
         return;
      }

      try {
         for (String line : Files.readAllLines(file)) {
            String[] f = line.split("\t", -1);
            if (f.length < 8 || line.startsWith("#")) {
               continue;
            }

            try {
               bases.add(new Base(f[0], f[1], Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]), Integer.parseInt(f[5]), f[7], Long.parseLong(f[6])));
            } catch (NumberFormatException ignored) {
            }
         }
      } catch (IOException e) {
         PandaBuilderClient.LOGGER.warn("Could not read found bases", e);
      }
   }

   private static void saveBases() {
      Path file = basesFile();

      try {
         Files.createDirectories(file.getParent());

         try (BufferedWriter w = Files.newBufferedWriter(file)) {
            w.write("# world\tdimension\tx\ty\tz\tscore\tfoundAtMillis\tsummary\n");

            for (Base b : bases) {
               w.write(clean(b.world) + "\t" + b.dimension + "\t" + b.x + "\t" + b.y + "\t" + b.z + "\t" + b.score + "\t" + b.found + "\t" + clean(b.summary) + "\n");
            }
         }
      } catch (IOException e) {
         PandaBuilderClient.LOGGER.warn("Could not save found bases", e);
      }
   }

   static void message(Component text) {
      Minecraft client = Minecraft.getInstance();
      if (client.player != null) {
         client.player.sendSystemMessage(Component.literal("[Panda Builder] ").withStyle(ChatFormatting.AQUA).append(text));
      }
   }
}
