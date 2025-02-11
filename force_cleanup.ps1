# Request admin privileges
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator"))  
{  
    Write-Warning "Please run this script as Administrator!"
    Exit
}

Write-Host "Stopping all related processes..."
$processesToKill = @(
    "java",
    "gradle",
    "studio64",
    "adb",
    "kotlin",
    "studio"
)

foreach ($proc in $processesToKill) {
    Get-Process | Where-Object {$_.Name -like "*$proc*"} | Stop-Process -Force -ErrorAction SilentlyContinue
}

Write-Host "Waiting for processes to stop..."
Start-Sleep -Seconds 5

# Define paths to clean
$pathsToClean = @(
    "$env:USERPROFILE\.gradle",
    "$env:USERPROFILE\.android",
    "$env:LOCALAPPDATA\Android\Sdk\.tmp",
    ".gradle",
    ".idea",
    "app\build",
    "build"
)

foreach ($path in $pathsToClean) {
    if (Test-Path $path) {
        Write-Host "Taking ownership and setting permissions for $path..."
        # Take ownership
        takeown /F $path /R /D Y | Out-Null
        # Grant full permissions
        icacls $path /grant administrators:F /T | Out-Null
        
        Write-Host "Removing $path..."
        Remove-Item -Path $path -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# Create fresh directories with proper permissions
$newDirs = @(
    "$env:USERPROFILE\.gradle",
    "$env:USERPROFILE\.gradle\caches",
    "$env:USERPROFILE\.gradle\wrapper",
    "$env:USERPROFILE\.android",
    "app\build",
    ".gradle"
)

foreach ($dir in $newDirs) {
    Write-Host "Creating $dir with proper permissions..."
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
    
    # Set proper permissions
    $acl = Get-Acl $dir
    $username = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
    $rule = New-Object System.Security.AccessControl.FileSystemAccessRule($username, "FullControl", "ContainerInherit,ObjectInherit", "None", "Allow")
    $acl.SetAccessRule($rule)
    Set-Acl $dir $acl
}

Write-Host "Creating fresh Gradle wrapper..."
$wrapperProperties = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@

New-Item -ItemType Directory -Force -Path "gradle\wrapper" | Out-Null
Set-Content -Path "gradle\wrapper\gradle-wrapper.properties" -Value $wrapperProperties -Force

Write-Host "Cleanup complete. Please restart Android Studio."
