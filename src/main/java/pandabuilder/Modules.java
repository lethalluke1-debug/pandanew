package pandabuilder;

import java.util.List;

public final class Modules {
    private Modules() {}

    public static final List<Module> ALL = List.of(
            new Module("Auto Totem", "Moves a totem into your offhand when it's empty.", Module.Tab.PVP,
                    () -> Settings.autoTotem, AutoTotem::toggle, PandaBuilderClient::autoTotemKey),
            new Module("Auto Crystal", "Places crystals on the obsidian you look at and breaks nearby ones.",
                    Module.Tab.PVP, AutoCrystal::isOn, AutoCrystal::toggle, PandaBuilderClient::autoCrystalKey),
            new Module("Auto XP", "Throws XP bottles from your hotbar every tick.", Module.Tab.PVP,
                    AutoXP::isOn, AutoXP::toggle, PandaBuilderClient::autoXpKey),
            new Module("Storage ESP", "Shows chests, shulkers, barrels and other base blocks through walls. "
                    + "Click the gear to pick blocks, search for any block to add, and turn boxes, tracers and "
                    + "distance markers on or off.",
                    Module.Tab.CHEATING, StorageEsp::isOn, StorageEsp::toggle, PandaBuilderClient::storageEspKey,
                    Module.STORAGE_ESP),
            new Module("Auto Mine", "Digs a straight tunnel the way you're facing and goes around lava, holes and bedrock. "
                    + "Click the gear for 3x3 Pickaxe mode.",
                    Module.Tab.CHEATING, AutoMine::isOn, AutoMine::toggle, PandaBuilderClient::autoMineKey,
                    Module.AUTO_MINE),
            new Module("Freecam", "Fly the camera away from your body. WASD, Space and Shift move, Sprint is faster.",
                    Module.Tab.CHEATING, Freecam::isOn, Freecam::toggle, PandaBuilderClient::freecamKey));
}
