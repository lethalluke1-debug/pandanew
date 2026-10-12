# Unlimited Veins (Fabric 26.2)

A small Fabric mod that, when you break an ore, searches in all 26 neighboring directions and breaks every connected block of the same ore. This is connected-vein mining, not line mining.

## Features
- Works on Minecraft 26.2 with Fabric Loader and Fabric API.
- Finds diagonally connected ore blocks as well as face-connected blocks.
- Keeps normal block drops and XP behavior by using the world's normal block-destruction method.
- High safety cap of 100,000 blocks per vein.
- Recognizes block registry IDs ending in `_ore`, plus ancient debris.

## Build requirements
- JDK 25 or newer
- Gradle 9.5.1 or newer
- Internet access for Gradle to download Minecraft/Fabric dependencies

## Build
Open a terminal in this project directory and run:

```bash
gradle build
```

On Windows, if Gradle is installed, run:

```powershell
gradle.bat build
```

The compiled mod should appear at:

```text
build/libs/unlimited-veins-1.0.0.jar
```

Copy that JAR into `.minecraft/mods` alongside Fabric Loader and a matching Fabric API for 26.2.

## Change the block cap
Edit `MAX_BLOCKS_PER_VEIN` in `UnlimitedVeins.java`, then rebuild. `100_000` is already a very high cap; extremely large veins can cause lag and may exceed the game/server's practical limits.

## Notes
- This project source has not been compiled or tested in this environment. The build requires downloading dependencies, and the current environment has no network access and only Java 21. Use JDK 25 and run the build locally; if Gradle reports an API or mapping error, share the error log to adjust the project.
- Use only in worlds/servers where you have permission. For multiplayer, the mod needs to be installed on the server for reliable server-side behavior.
