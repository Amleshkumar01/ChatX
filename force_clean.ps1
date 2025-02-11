# Request admin privileges
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator"))  
{  
    Write-Warning "Please run this script as Administrator!"
    Exit
}

Write-Host "Stopping Android Studio..."
Get-Process | Where-Object {$_.Name -like "*studio64*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "Stopping Java processes..."
Get-Process | Where-Object {$_.Name -like "*java*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "Stopping Gradle processes..."
Get-Process | Where-Object {$_.Name -like "*gradle*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "Waiting for processes to fully stop..."
Start-Sleep -Seconds 5

# Clean Gradle caches
$gradleCachePaths = @(
    "$env:USERPROFILE\.gradle\caches",
    "$env:USERPROFILE\.gradle\daemon",
    "$env:USERPROFILE\.gradle\wrapper",
    "$env:USERPROFILE\.android\build-cache"
)

foreach ($path in $gradleCachePaths) {
    if (Test-Path $path) {
        Write-Host "Removing $path..."
        Remove-Item -Path $path -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# Clean project build files
$projectBuildPaths = @(
    ".gradle",
    "app\build",
    "build",
    "app\build\intermediates",
    "app\build\outputs"
)

foreach ($path in $projectBuildPaths) {
    if (Test-Path $path) {
        Write-Host "Removing $path..."
        Remove-Item -Path $path -Recurse -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "Creating fresh Gradle directories..."
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.gradle"
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.gradle\caches"
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.gradle\daemon"
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.gradle\wrapper"

# Set permissions
$username = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
$acl = Get-Acl "$env:USERPROFILE\.gradle"
$permission = $username,"FullControl","ContainerInherit,ObjectInherit","None","Allow"
$accessRule = New-Object System.Security.AccessControl.FileSystemAccessRule $permission
$acl.SetAccessRule($accessRule)
Set-Acl "$env:USERPROFILE\.gradle" $acl

Write-Host "Clean complete. Please restart Android Studio."
