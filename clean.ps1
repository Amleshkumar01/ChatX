Write-Host "Stopping Gradle daemon..."
./gradlew --stop

Write-Host "Cleaning project..."
Remove-Item -Path ".gradle","app/build","build" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Cleaning Gradle caches..."
$gradleCache = "$env:USERPROFILE\.gradle\caches"
$gradleWrapper = "$env:USERPROFILE\.gradle\wrapper"

Get-Process | Where-Object {$_.Name -like "*java*" -or $_.Name -like "*gradle*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Start-Sleep -Seconds 2

Remove-Item -Path $gradleCache -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path $gradleWrapper -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Clean completed. Please restart Android Studio."
