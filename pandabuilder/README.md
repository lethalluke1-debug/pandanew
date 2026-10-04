# Panda Builder

A Fabric 1.21.1 client mod for building schematics in survival with the blocks in your inventory.

- **Right Shift** opens the menu (rebindable in Controls).
- Lists schematics in `.minecraft/schematics` (`.schem`, `.litematic`, `.schematic`).
- Shows every block a schematic needs and how many you have (green = enough, red = short).
- **Build Here** starts building at your feet. Baritone walks to each spot and places blocks from your inventory.
- Warns in chat when you run out of a block. Get more, then press **Resume** (or type `#resume`).
- HUD in the top-left shows build status and what you're still missing.

This mod does not hide from anti-cheat. Check the server's rules before using Baritone there.

## Get the jar

Every push builds the mod on GitHub: open the repo's **Actions** tab, then **Build Panda Builder**, then the latest run, and download **pandabuilder-jar**.

To build it yourself instead, install Java 21 and Gradle 8.10+ and run `gradle build` in this folder. The jar is in `build/libs/`.

## Install

Put these in `.minecraft/mods`, all for Minecraft 1.21.1:

1. `pandabuilder-1.0.0.jar`
2. Fabric API
3. Baritone, using the **api-fabric** jar from github.com/cabaletta/baritone/releases (this mod needs the API version)

Put your schematic files in `.minecraft/schematics/`. Names can't contain spaces.
