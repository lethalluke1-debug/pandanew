# Lethal Client - Now Playing helper.
#
# Reads the Windows "system media" feed (the same one the volume overlay uses), so it sees
# whatever Spotify or Apple Music is playing. Uses only built-in Windows PowerShell + WinRT, so
# there is NOTHING to install and no bundled program to flag.
#
#   nowplaying.ps1 read   -> prints one compact JSON line ~8x a second with the current song
#
# The Java mod launches "read" once and keeps it running. To stay in sync with the music, the
# cheap play POSITION is read on every tick, while the heavy metadata (title / artist / cover
# art) is only fetched on a song change or every ~1.5s, so the position never gets held up.

param([string]$mode = "read", [string]$artDir = "")

$ErrorActionPreference = "Stop"
$OutputEncoding = [System.Text.Encoding]::UTF8

# --- WinRT async helper (await an IAsyncOperation<T> from PowerShell) ---
Add-Type -AssemblyName System.Runtime.WindowsRuntime
$asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
  $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1'
})[0]
function Await($op, $type) {
  $asTask = $asTaskGeneric.MakeGenericMethod($type)
  $t = $asTask.Invoke($null, @($op))
  $t.Wait(-1) | Out-Null
  $t.Result
}

[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType = WindowsRuntime] | Out-Null
# Touch the Storage.Streams assembly so the thumbnail types below resolve (art only; never fatal).
try { [Windows.Storage.Streams.DataReader, Windows.Storage.Streams, ContentType = WindowsRuntime] | Out-Null } catch { }
$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]
$propType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]
# Resolving the stream interface can fail on some systems; if it does, we simply skip cover art
# (song info still works) rather than letting the whole reader die.
$rasType = $null
try { $rasType = [Windows.Storage.Streams.IRandomAccessStreamWithContentType] } catch { }

function Get-Manager { Await ($mgrType::RequestAsync()) ($mgrType) }

# Write the current song's cover art to $out. Returns $true if it wrote a file.
function Save-Cover($props, $out) {
  if ($null -eq $rasType) { return $false }
  try {
    $thumb = $props.Thumbnail
    if ($null -eq $thumb) { return $false }
    $stream = Await ($thumb.OpenReadAsync()) ($rasType)
    if ($null -eq $stream -or $stream.Size -le 0) { return $false }
    $size = [uint32]$stream.Size
    $reader = [Windows.Storage.Streams.DataReader]::new($stream)
    [void](Await ($reader.LoadAsync($size)) ([uint32]))
    $bytes = New-Object byte[] $size
    $reader.ReadBytes($bytes)
    $reader.Dispose(); $stream.Dispose()
    $tmp = "$out.tmp"
    [System.IO.File]::WriteAllBytes($tmp, $bytes)
    [System.IO.File]::Copy($tmp, $out, $true)
    Remove-Item $tmp -ErrorAction SilentlyContinue
    return $true
  } catch {
    try { "$(Get-Date -Format o)  art: $($_.Exception.Message)" | Out-File -Append -Encoding utf8 (Join-Path (Split-Path $out) "music-debug.txt") } catch { }
    return $false
  }
}

# --- reader loop ---
$metaApp = ""; $metaTitle = ""; $metaArtist = ""; $metaArt = ""
$lastMeta = 0.0
$lastPos = 0
while ($true) {
  $line = '{"app":"","title":"","artist":"","status":"stopped","pos":0,"dur":0,"art":""}'
  try {
    $mgr = Get-Manager
    # Pick the session actually PLAYING Spotify or Apple Music (GetCurrentSession can stick to the
    # app you last touched, so skipping songs or switching apps would show the old one).
    $s = $null; $fallback = $null
    try {
      foreach ($cand in $mgr.GetSessions()) {
        $cid = "$($cand.SourceAppUserModelId)".ToLower()
        if (-not ($cid -match 'spotify|apple|itunes|music')) { continue }
        if ($null -eq $fallback) { $fallback = $cand }
        try { if ([int]$cand.GetPlaybackInfo().PlaybackStatus -eq 4) { $s = $cand; break } } catch { }
      }
    } catch { }
    if ($null -eq $s) { $s = $fallback }
    if ($null -eq $s) { $s = $mgr.GetCurrentSession() }
    if ($null -ne $s) {
      $app = "$($s.SourceAppUserModelId)"
      $info = $s.GetPlaybackInfo()
      $status = switch ([int]$info.PlaybackStatus) { 4 { "playing" } 5 { "paused" } default { "stopped" } }

      # Quick position read (cheap, no await) only to notice a song change (position jumps back to ~0).
      $posQuick = 0
      try { $posQuick = [long]$s.GetTimelineProperties().Position.TotalMilliseconds } catch { }

      # --- slow path: title / artist / cover (only on a song change or every ~1.5s) ---
      $nowMs = [System.DateTimeOffset]::Now.ToUnixTimeMilliseconds()
      $needMeta = ($app -ne $metaApp) -or ($metaTitle -eq "") -or ($posQuick -lt ($lastPos - 2500)) -or (($nowMs - $lastMeta) -gt 1500)
      if ($needMeta) {
        try {
          $props = Await ($s.TryGetMediaPropertiesAsync()) ($propType)
          $t = "$($props.Title)"; $ar = "$($props.Artist)"
          if ($artDir -ne "" -and ($t -ne $metaTitle -or $ar -ne $metaArtist -or $app -ne $metaApp)) {
            $cover = Join-Path $artDir "cover.png"
            Remove-Item $cover -ErrorAction SilentlyContinue
            Save-Cover $props $cover | Out-Null
            if (Test-Path $cover) { $metaArt = $cover } else { $metaArt = "" }
          }
          $metaApp = $app; $metaTitle = $t; $metaArtist = $ar
          $lastMeta = $nowMs
        } catch { }
      }

      # --- position read LAST, so it's FRESH at the moment we send it and never ends up stale behind
      #     the slow metadata fetch above. This is what keeps the lyrics in sync. ---
      $pos = 0; $dur = 0
      try {
        $tl = $s.GetTimelineProperties()
        $pos = [long]$tl.Position.TotalMilliseconds
        $dur = [long]$tl.EndTime.TotalMilliseconds
        # Windows stamps Position at LastUpdatedTime, which can be ~1s old. While playing, advance it
        # to "now" so the time isn't behind the music.
        if ($status -eq "playing") {
          try {
            $delta = ([System.DateTimeOffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds
            if ($delta -gt 0 -and $delta -lt 600000) { $pos += [long]$delta }
          } catch { }
        }
        if ($dur -gt 0 -and $pos -gt $dur) { $pos = $dur }
      } catch { }
      $lastPos = $pos

      # "t" is the wall-clock moment this position is valid for. The Java side anchors to it instead
      # of to when it receives the line, so the pipe/read delay is subtracted and lyrics stay in sync.
      $obj = [ordered]@{
        app = "$metaApp"; title = "$metaTitle"; artist = "$metaArtist";
        status = $status; pos = $pos; dur = $dur; art = "$metaArt";
        t = [System.DateTimeOffset]::Now.ToUnixTimeMilliseconds()
      }
      $line = ($obj | ConvertTo-Json -Compress)
    }
  } catch {
    $line = '{"app":"","title":"","artist":"","status":"stopped","pos":0,"dur":0,"art":""}'
  }
  [Console]::Out.WriteLine($line)
  [Console]::Out.Flush()
  Start-Sleep -Milliseconds 120
}
