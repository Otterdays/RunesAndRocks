Write-Host "Starting Runes and Rocks Client..." -ForegroundColor Green
.\gradlew.bat :client:run --console=plain

Write-Host "`nPress any key to exit..." -ForegroundColor Yellow
[void][System.Console]::ReadKey($true)
