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

# Clean build caches
$cachePaths = @(
    "$env:USERPROFILE\.gradle\buildOutputCleanup",
    "$env:USERPROFILE\.gradle\caches\build-cache-1",
    "$env:USERPROFILE\.gradle\caches\transforms-3",
    "$env:USERPROFILE\.gradle\caches\journal-1",
    "$env:USERPROFILE\.android\build-cache",
    ".gradle",
    ".idea",
    "app\build",
    "build"
)

foreach ($path in $cachePaths) {
    if (Test-Path $path) {
        Write-Host "Removing $path..."
        Remove-Item -Path $path -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# Create fresh directories with proper permissions
$newDirs = @(
    "$env:USERPROFILE\.gradle",
    "$env:USERPROFILE\.gradle\caches",
    "$env:USERPROFILE\.gradle\wrapper",
    "$env:USERPROFILE\.android"
)

foreach ($dir in $newDirs) {
    if (-not (Test-Path $dir)) {
        Write-Host "Creating $dir..."
        New-Item -ItemType Directory -Force -Path $dir
        
        # Set permissions
        $acl = Get-Acl $dir
        $username = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
        $accessRule = New-Object System.Security.AccessControl.FileSystemAccessRule($username, "FullControl", "ContainerInherit,ObjectInherit", "None", "Allow")
        $acl.SetAccessRule($accessRule)
        Set-Acl $dir $acl
    }
}

Write-Host "Creating fresh Gradle wrapper..."
$wrapperProperties = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@

New-Item -ItemType Directory -Force -Path "gradle\wrapper"
Set-Content -Path "gradle\wrapper\gradle-wrapper.properties" -Value $wrapperProperties -Force

Write-Host "Clean complete. Please restart Android Studio."
