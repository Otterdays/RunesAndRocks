Write-Host "Starting Runes and Rocks Server..." -ForegroundColor Cyan

# Optional: check if Docker is running (PostgreSQL + Redis)
$dockerOk = $false
try {
    docker ps 2>&1 | Out-Null
    $dockerOk = $?
} catch { }
if ($dockerOk) {
    Write-Host "Docker detected. DB/Redis will be available if containers are up." -ForegroundColor DarkGray
} else {
    Write-Host "Docker not detected or not running. Server will start in degraded mode (no login/persistence)." -ForegroundColor Yellow
    Write-Host "  Run 'docker-compose up -d' first for full functionality." -ForegroundColor DarkGray
}

.\gradlew.bat :server:run --console=plain

Write-Host "`nPress any key to exit..." -ForegroundColor Yellow
[void][System.Console]::ReadKey($true)
