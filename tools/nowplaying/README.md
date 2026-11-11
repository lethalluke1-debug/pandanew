# Now-playing (music) helper

The client's now-playing card shows what you're listening to (Spotify etc.).
Reading "now playing" from Windows needs a native media call, and when that
code lived **inside the mod jar** it tripped antivirus. So it was moved out:

- The **mod jar** now only *reads* a small file and launches nothing — it stays
  clean for antivirus, so downloads aren't flagged.
- This **separate helper** writes that file. You run it yourself; it is never
  bundled in or launched by the mod.

## Use it

1. Start Minecraft with Lethal Client and turn **on** the music / now-playing
   toggle in the client.
2. Run the helper (keep it running while you play):

   ```powershell
   powershell -ExecutionPolicy Bypass -File nowplaying-helper.ps1
   ```

   It writes to `%APPDATA%\.minecraft\config\lethalauction\nowplaying.json`
   every second. If your Minecraft uses a different folder (a launcher profile,
   MultiMC, etc.), pass it:

   ```powershell
   powershell -ExecutionPolicy Bypass -File nowplaying-helper.ps1 -OutFile "C:\path\to\.minecraft\config\lethalauction\nowplaying.json"
   ```

3. The card appears while the helper is running and something is playing. Stop
   the helper (Ctrl+C) or close it and the card disappears within a few seconds.

## File format

The mod reads one JSON object:

```json
{"app":"Spotify","title":"Song","artist":"Artist","status":"playing","pos":0,"dur":210000,"art":"","t":1731200000000}
```

Any tool can write this file — the PowerShell helper is just a convenient
default. `t` is epoch-ms of the last update; the card hides if it goes stale.

## Notes
- The helper is PowerShell using the Windows media API. Some antivirus is wary
  of any PowerShell; this is unsigned community script, so allow it if needed.
  Because it is **not** part of the mod, it cannot affect the mod's downloads.
- Album art isn't exported by the helper (`art` is empty); the client fetches
  art by title/artist on its own.
