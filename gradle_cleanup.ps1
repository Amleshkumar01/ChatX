# Request admin privileges
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator"))  
{  
    Write-Warning "Please run this script as Administrator!"
    Exit
}

Write-Host "Stopping processes..."
Get-Process | Where-Object {$_.Name -like "*java*" -or $_.Name -like "*gradle*" -or $_.Name -like "*studio64*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "Waiting for processes to stop..."
Start-Sleep -Seconds 5

$paths = @(
    "$env:USERPROFILE\.gradle\8.2",
    "$env:USERPROFILE\.gradle\buildOutputCleanup",
    "$env:USERPROFILE\.gradle\caches",
    "$env:USERPROFILE\.gradle\daemon",
    ".gradle",
    "app\.gradle",
    "app\build",
    "build"
)

foreach ($path in $paths) {
    if (Test-Path $path) {
        Write-Host "Removing $path..."
        Remove-Item -Path $path -Recurse -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "Creating fresh Gradle wrapper..."
New-Item -ItemType Directory -Force -Path "gradle\wrapper"

$wrapperProperties = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@

Set-Content -Path "gradle\wrapper\gradle-wrapper.properties" -Value $wrapperProperties

Write-Host "Clean complete. Please restart Android Studio."
