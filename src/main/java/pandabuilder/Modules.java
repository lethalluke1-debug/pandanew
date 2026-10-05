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
            new Module("ESP", "Boxes, names, distance and health for other players, through walls.", Module.Tab.CHEATING,
                    Esp::isOn, Esp::toggle, PandaBuilderClient::espKey),
            new Module("Auto Mine", "Digs a straight 1x2 tunnel the way you're facing. Stops at lava, holes and bedrock.",
                    Module.Tab.CHEATING, AutoMine::isOn, AutoMine::toggle, PandaBuilderClient::autoMineKey),
            new Module("Freecam", "Fly the camera away from your body. WASD, Space and Shift move, Sprint is faster.",
                    Module.Tab.CHEATING, Freecam::isOn, Freecam::toggle, PandaBuilderClient::freecamKey));
}
