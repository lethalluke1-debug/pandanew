# Lethal Client — now-playing helper (Windows)
#
# Reads your current media (Spotify etc.) from the Windows "now playing" system
# and writes it to a small JSON file that the mod polls. The mod itself never
# runs this script and never launches any process — this helper is separate, so
# the mod jar stays clean for antivirus. Run this alongside Minecraft and turn
# on the music toggle in the client.
#
# Usage (PowerShell):
#   powershell -ExecutionPolicy Bypass -File nowplaying-helper.ps1
# Optional: point it at a non-default config folder
#   powershell -ExecutionPolicy Bypass -File nowplaying-helper.ps1 -OutFile "D:\path\.minecraft\config\lethalauction\nowplaying.json"

param(
  [string]$OutFile = "$env:APPDATA\.minecraft\config\lethalauction\nowplaying.json"
)

$dir = Split-Path $OutFile
if (!(Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }

Add-Type -AssemblyName System.Runtime.WindowsRuntime

$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
  $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1'
})[0]
function Await($op, $t) {
  $task = $asTask.MakeGenericMethod($t).Invoke($null, @($op))
  $task.Wait(-1) | Out-Null
  $task.Result
}

[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime] | Out-Null
$typeMgr   = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]
$typeProps = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]
$mgr = Await ($typeMgr::RequestAsync()) $typeMgr

Write-Host "Writing now-playing to $OutFile  (Ctrl+C to stop)"
while ($true) {
  try {
    $s = $mgr.GetCurrentSession()
    if ($s) {
      $p  = Await ($s.TryGetMediaPropertiesAsync()) $typeProps
      $pb = $s.GetPlaybackInfo()
      $tl = $s.GetTimelineProperties()
      $status = if ([int]$pb.PlaybackStatus -eq 4) { 'playing' } else { 'paused' }
      $o = [ordered]@{
        app    = [string]$s.SourceAppUserModelId
        title  = [string]$p.Title
        artist = [string]$p.Artist
        status = $status
        pos    = [long]$tl.Position.TotalMilliseconds
        dur    = [long]$tl.EndTime.TotalMilliseconds
        art    = ''
        t      = [long]([DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds())
      }
      ($o | ConvertTo-Json -Compress) | Set-Content -Path $OutFile -Encoding UTF8
    }
  } catch { }
  Start-Sleep -Milliseconds 1000
}
