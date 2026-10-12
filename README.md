# PreView

A YouTube Shorts / TikTok view predictor. Upload a vertical video (or paste a link), and PreView plays it, scans it in the browser and forecasts a 7‑day view range.

## Run it

It's a single static file with no build step. Open `index.html` in a browser, or serve the folder:

```
npx serve .
```

Deploy anywhere that hosts static files (GitHub Pages, Netlify, Vercel).

## How the prediction works

1. **Scan**: samples a frame every ~0.5s (denser in the first 3s) to measure motion, scene cuts, brightness, contrast, colorfulness, resolution and aspect ratio. Decodes the audio track for loudness and dead air. Nothing is uploaded to a server.
2. **Score**: eight signals (hook, pacing, visuals, audio, length, format, caption, timing) are weighted into a 0–100 Viral Score.
3. **Forecast**: subscribers/followers and recent average views set a baseline, niche and posting time adjust it, and the score shifts it up or down. The result is shown as a P10–P90 range with a probability curve and a 7‑day growth curve.

Imported links play via the YouTube/TikTok embed but can't be frame-scanned (browser cross-origin rules), so those predictions use fewer signals and a wider range.

The model is a hand-tuned heuristic, not trained on real analytics. To make it genuinely accurate you'd collect (video features → actual views) pairs and fit the weights in `score()` and `forecast()`.

History is saved in the browser's localStorage.
