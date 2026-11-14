@echo off
REM Double-click this to feed "now playing" to Lethal Client.
REM Keep this window open while you play. Close it (or press Ctrl+C) to stop.
title Lethal Client - Now Playing helper
echo Starting now-playing helper... keep this window open while you play.
echo (Close this window to stop the music card.)
echo.
powershell -ExecutionPolicy Bypass -NoProfile -File "%~dp0nowplaying-helper.ps1"
echo.
echo Helper stopped.
pause
