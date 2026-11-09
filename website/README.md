# Lethal Client — Website

This is the website / backend code, all in one Cloudflare Worker.

- **`worker.js`** — the whole site: the landing/buy page, login + dashboard,
  the admin panel, key verification, cosmetics/capes, Stripe billing, and the
  per-Minecraft-version download + auto-update endpoints.

## Deploying

`worker.js` runs as a Cloudflare Worker. Deploy it with Wrangler, or paste its
contents into the Worker in the Cloudflare dashboard. It needs the `LICENSES`
KV namespace (stores jars + capes) and the `DB` D1 database bound to it.

The newest website code is always this file — download `worker.js` from here.
