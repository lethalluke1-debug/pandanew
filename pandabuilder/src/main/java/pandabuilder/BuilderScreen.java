package pandabuilder;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

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
        super(Text.literal("Panda Builder"));
    }

    @Override
    protected void init() {
        files = BuildManager.listSchematics();

        int y = height - 26;
        int w = 70;
        int x = 10;
        addDrawableChild(ButtonWidget.builder(Text.literal("Build Here"), b -> {
            BuildManager.startBuild();
            close();
        }).dimensions(x, y, w, 20).build());
        x += w + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("Pause"), b -> BuildManager.pause()).dimensions(x, y, w, 20).build());
        x += w + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("Resume"), b -> BuildManager.resume()).dimensions(x, y, w, 20).build());
        x += w + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> BuildManager.cancel()).dimensions(x, y, w, 20).build());
        x += w + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("Refresh"), b -> files = BuildManager.listSchematics()).dimensions(x, y, w, 20).build());
        x += w + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("Open Folder"), b -> Util.getOperatingSystem().open(BuildManager.schematicsDir().toFile()))
                .dimensions(x, y, w + 10, 20).build());
    }

    private int fileListRight() {
        return Math.min(180, width / 3);
    }

    private int listBottom() {
        return height - 34;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 8, 0xFFFFFFFF);

        String baritone = BaritoneBridge.isInstalled() ? "Baritone: found" : "Baritone: NOT installed";
        context.drawTextWithShadow(textRenderer, baritone, width - textRenderer.getWidth(baritone) - 8, 8,
                BaritoneBridge.isInstalled() ? 0xFF55FF55 : 0xFFFF5555);

        renderFileList(context, mouseX, mouseY);
        renderMaterials(context);
    }

    private void renderFileList(DrawContext context, int mouseX, int mouseY) {
        int left = 10;
        int right = fileListRight();
        context.fill(left, LIST_TOP, right, listBottom(), 0x88000000);
        context.drawTextWithShadow(textRenderer, "Schematics", left + 4, LIST_TOP - 11, 0xFFFFFF55);

        if (files.isEmpty()) {
            context.drawTextWithShadow(textRenderer, "No files in", left + 4, LIST_TOP + 4, 0xFFAAAAAA);
            context.drawTextWithShadow(textRenderer, ".minecraft/schematics", left + 4, LIST_TOP + 16, 0xFFAAAAAA);
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
                context.fill(left + 1, y - 2, right - 1, y + FILE_ROW - 2, 0xFF2266AA);
            } else if (hovered) {
                context.fill(left + 1, y - 2, right - 1, y + FILE_ROW - 2, 0x44FFFFFF);
            }
            String name = textRenderer.trimToWidth(file.getFileName().toString(), right - left - 8);
            context.drawTextWithShadow(textRenderer, name, left + 4, y, 0xFFFFFFFF);
        }
    }

    private void renderMaterials(DrawContext context) {
        int left = fileListRight() + 10;
        int right = width - 10;
        context.fill(left, LIST_TOP, right, listBottom(), 0x88000000);

        Schematic selected = BuildManager.selected();
        if (error != null) {
            context.drawTextWithShadow(textRenderer, error, left + 4, LIST_TOP + 4, 0xFFFF5555);
            return;
        }
        if (selected == null) {
            context.drawTextWithShadow(textRenderer, "Pick a schematic on the left", left + 4, LIST_TOP + 4, 0xFFAAAAAA);
            return;
        }
        if (!selected.materialsKnown) {
            context.drawTextWithShadow(textRenderer, "Material list not available for old .schematic files.", left + 4, LIST_TOP + 4, 0xFFAAAAAA);
            context.drawTextWithShadow(textRenderer, "You can still press Build Here.", left + 4, LIST_TOP + 16, 0xFFAAAAAA);
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
        context.drawTextWithShadow(textRenderer, header, left, LIST_TOP - 11, missingTypes == 0 ? 0xFF55FF55 : 0xFFFFAA00);

        List<Map.Entry<Item, Integer>> entries = new ArrayList<>(selected.materials.entrySet());
        int visible = (listBottom() - LIST_TOP - 4) / MAT_ROW;
        matScroll = Math.max(0, Math.min(matScroll, entries.size() - visible));
        for (int i = 0; i < visible && i + matScroll < entries.size(); i++) {
            Map.Entry<Item, Integer> e = entries.get(i + matScroll);
            int y = LIST_TOP + 3 + i * MAT_ROW;
            int need = e.getValue();
            int got = have.getOrDefault(e.getKey(), 0);
            context.drawItem(new ItemStack(e.getKey()), left + 4, y);
            context.drawTextWithShadow(textRenderer, e.getKey().getName().getString(), left + 24, y + 4, 0xFFFFFFFF);
            String count = got + " / " + need;
            context.drawTextWithShadow(textRenderer, count, right - textRenderer.getWidth(count) - 6, y + 4,
                    got >= need ? 0xFF55FF55 : 0xFFFF5555);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
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
    public boolean shouldPause() {
        return false;
    }
}
