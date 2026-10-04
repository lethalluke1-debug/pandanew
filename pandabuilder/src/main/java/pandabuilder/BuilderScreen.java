package pandabuilder;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Schematic picker with a material checklist and build controls. */
public class BuilderScreen extends Screen {
    private static final int FILE_ROW = 12;
    private static final int MAT_ROW = 18;
    private static final int LIST_TOP = 30;

    private List<Path> files = new ArrayList<>();
    private int fileScroll;
    private int matScroll;
    private String error;

    public BuilderScreen() {
        super(Component.literal("Panda Builder"));
    }

    @Override
    protected void init() {
        files = BuildManager.listSchematics();

        int y = height - 26;
        int w = 70;
        int x = 10;
        addRenderableWidget(Button.builder(Component.literal("Build Here"), b -> {
            BuildManager.startBuild();
            onClose();
        }).bounds(x, y, w, 20).build());
        x += w + 4;
        addRenderableWidget(Button.builder(Component.literal("Pause"), b -> BuildManager.pause()).bounds(x, y, w, 20).build());
        x += w + 4;
        addRenderableWidget(Button.builder(Component.literal("Resume"), b -> BuildManager.resume()).bounds(x, y, w, 20).build());
        x += w + 4;
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> BuildManager.cancel()).bounds(x, y, w, 20).build());
        x += w + 4;
        addRenderableWidget(Button.builder(Component.literal("Refresh"), b -> files = BuildManager.listSchematics()).bounds(x, y, w, 20).build());
        x += w + 4;
        addRenderableWidget(Button.builder(Component.literal("Open Folder"), b -> Util.getPlatform().openPath(BuildManager.schematicsDir()))
                .bounds(x, y, w + 10, 20).build());
    }

    private int fileListRight() {
        return Math.min(180, width / 3);
    }

    private int listBottom() {
        return height - 34;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        String title = "Panda Builder";
        graphics.text(font, title, (width - font.width(title)) / 2, 8, 0xFFFFFFFF, true);

        String baritone = BaritoneBridge.isInstalled() ? "Baritone: found" : "Baritone: NOT installed";
        graphics.text(font, baritone, width - font.width(baritone) - 8, 8,
                BaritoneBridge.isInstalled() ? 0xFF55FF55 : 0xFFFF5555, true);

        extractFileList(graphics, mouseX, mouseY);
        extractMaterials(graphics);
    }

    private void extractFileList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int left = 10;
        int right = fileListRight();
        graphics.fill(left, LIST_TOP, right, listBottom(), 0x88000000);
        graphics.text(font, "Schematics", left + 4, LIST_TOP - 11, 0xFFFFFF55, true);

        if (files.isEmpty()) {
            graphics.text(font, "No files in", left + 4, LIST_TOP + 4, 0xFFAAAAAA, true);
            graphics.text(font, ".minecraft/schematics", left + 4, LIST_TOP + 16, 0xFFAAAAAA, true);
            return;
        }

        Schematic selected = BuildManager.selected();
        int visible = (listBottom() - LIST_TOP - 4) / FILE_ROW;
        for (int i = 0; i < visible && i + fileScroll < files.size(); i++) {
            Path file = files.get(i + fileScroll);
            int y = LIST_TOP + 3 + i * FILE_ROW;
            boolean isSelected = selected != null && selected.path.equals(file);
            boolean hovered = mouseX >= left && mouseX < right && mouseY >= y - 1 && mouseY < y + FILE_ROW - 1;
            if (isSelected) {
                graphics.fill(left + 1, y - 2, right - 1, y + FILE_ROW - 2, 0xFF2266AA);
            } else if (hovered) {
                graphics.fill(left + 1, y - 2, right - 1, y + FILE_ROW - 2, 0x44FFFFFF);
            }
            String name = font.plainSubstrByWidth(file.getFileName().toString(), right - left - 8);
            graphics.text(font, name, left + 4, y, 0xFFFFFFFF, true);
        }
    }

    private void extractMaterials(GuiGraphicsExtractor graphics) {
        int left = fileListRight() + 10;
        int right = width - 10;
        graphics.fill(left, LIST_TOP, right, listBottom(), 0x88000000);

        Schematic selected = BuildManager.selected();
        if (error != null) {
            graphics.text(font, error, left + 4, LIST_TOP + 4, 0xFFFF5555, true);
            return;
        }
        if (selected == null) {
            graphics.text(font, "Pick a schematic on the left", left + 4, LIST_TOP + 4, 0xFFAAAAAA, true);
            return;
        }
        if (!selected.materialsKnown) {
            graphics.text(font, "Material list not available for old .schematic files.", left + 4, LIST_TOP + 4, 0xFFAAAAAA, true);
            graphics.text(font, "You can still press Build Here.", left + 4, LIST_TOP + 16, 0xFFAAAAAA, true);
            return;
        }

        Map<Item, Integer> have = BuildManager.inventoryCounts();
        int missingTypes = 0;
        int total = 0;
        for (Map.Entry<Item, Integer> e : selected.materials.entrySet()) {
            total += e.getValue();
            if (have.getOrDefault(e.getKey(), 0) < e.getValue()) missingTypes++;
        }
        String header = "Materials: " + total + " blocks, " + selected.materials.size() + " types, "
                + (missingTypes == 0 ? "you have everything" : missingTypes + " types short");
        graphics.text(font, header, left, LIST_TOP - 11, missingTypes == 0 ? 0xFF55FF55 : 0xFFFFAA00, true);

        List<Map.Entry<Item, Integer>> entries = new ArrayList<>(selected.materials.entrySet());
        int visible = (listBottom() - LIST_TOP - 4) / MAT_ROW;
        matScroll = Math.max(0, Math.min(matScroll, entries.size() - visible));
        for (int i = 0; i < visible && i + matScroll < entries.size(); i++) {
            Map.Entry<Item, Integer> e = entries.get(i + matScroll);
            int y = LIST_TOP + 3 + i * MAT_ROW;
            int need = e.getValue();
            int got = have.getOrDefault(e.getKey(), 0);
            graphics.item(new ItemStack(e.getKey()), left + 4, y);
            graphics.text(font, BuildManager.itemName(e.getKey()), left + 24, y + 4, 0xFFFFFFFF, true);
            String count = got + " / " + need;
            graphics.text(font, count, right - font.width(count) - 6, y + 4,
                    got >= need ? 0xFF55FF55 : 0xFFFF5555, true);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        double mouseX = event.x();
        double mouseY = event.y();
        if (mouseX >= 10 && mouseX < fileListRight() && mouseY >= LIST_TOP && mouseY < listBottom()) {
            int index = (int) ((mouseY - LIST_TOP - 1) / FILE_ROW) + fileScroll;
            if (index >= 0 && index < files.size()) {
                try {
                    BuildManager.select(files.get(index));
                    error = null;
                    matScroll = 0;
                } catch (Exception ex) {
                    error = "Couldn't read that file: " + ex.getMessage();
                    PandaBuilderClient.LOGGER.warn("Failed to load schematic", ex);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int step = verticalAmount > 0 ? -1 : 1;
        if (mouseX < fileListRight()) {
            int visible = (listBottom() - LIST_TOP - 4) / FILE_ROW;
            fileScroll = Math.max(0, Math.min(fileScroll + step, Math.max(0, files.size() - visible)));
        } else {
            matScroll = Math.max(0, matScroll + step);
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
