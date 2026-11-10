# Lethal Client — mod source

Reconstructed, buildable source for both client builds. Committed so the
source can never be lost again (the compiled jars are obfuscated and are not
a substitute for this).

## Layout
- `1.21.11/` — Minecraft 1.21.11 build (Java 21, Fabric Loom, intermediary mappings)
- `26.2/`    — Minecraft 26.2 build (Java 25, Fabric Loom, identity mappings)

Each is a self-contained Gradle project.

## Building
From inside a build's folder:

    ./gradlew build

The runnable mod jar lands in `build/libs/`.

- **1.21.11** builds on a Java 21+ JVM.
- **26.2** requires a Java 25 JVM to run Gradle (Minecraft 26.2 ships
  de-obfuscated, so it uses the bundled `identity-mappings.jar` instead of
  downloaded mappings).

## Notes
- Class/method names inside `com/lethalauction/m/` are machine names
  (`K1`, `K20`, `C65`, …) recovered from the distributed jars; behaviour is
  intact. Rename them to friendlier names as files are touched.
- The distributed jars in `../26.2/` and `../1.21.11/` remain the
  obfuscated release builds; this source produces un-obfuscated jars.
