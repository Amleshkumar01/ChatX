# Stop any running Gradle daemons
Write-Host "Stopping Gradle daemons..."
./gradlew --stop

# Clean directories
$dirsToClean = @(
    ".gradle",
    "app/build",
    "build",
    "app/build/intermediates",
    "app/build/outputs"
)

foreach ($dir in $dirsToClean) {
    if (Test-Path $dir) {
        Write-Host "Cleaning $dir..."
        Remove-Item -Path $dir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# Clean Gradle caches
$gradleCaches = "$env:USERPROFILE\.gradle\caches"
if (Test-Path $gradleCaches) {
    Write-Host "Cleaning Gradle caches..."
    Remove-Item -Path $gradleCaches -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host "Cleaning complete. Please rebuild your project."
