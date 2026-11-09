# Lethal Client

Builds and website for Lethal Client (a Minecraft Fabric mod for DonutSMP).

## Layout

- **`26.2/`** — the Minecraft 26.2 build (`LethalClient-26.2.jar`), Java 25.
- **`1.21.11/`** — the Minecraft 1.21.11 build (`LethalClient-1.21.11.jar`), Java 21.
- **`website/`** — the Cloudflare Worker that runs the whole site (`worker.js`): landing/buy page,
  login + dashboard, admin panel, key verification, cosmetics/capes, billing, and the per-version
  download + auto-update endpoints.

Both client builds are ProGuard-obfuscated and share the same feature set and auto-updater.

## Deploying

- **Website:** deploy `website/worker.js` to Cloudflare (Wrangler or paste into the Worker editor).
  It needs the `LICENSES` KV namespace and the `DB` D1 database bound to it. Committing here does not
  deploy the site.
- **Client:** upload each version's jar through the admin panel so the site serves it and auto-update
  picks it up. The repo copies are for reference/backup only.
