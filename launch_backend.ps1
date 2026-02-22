Write-Host "Starting Runes and Rocks Server..." -ForegroundColor Cyan
.\gradlew.bat :server:run --console=plain

Write-Host "`nPress any key to exit..." -ForegroundColor Yellow
[void][System.Console]::ReadKey($true)
