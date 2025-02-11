# Request admin privileges
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator"))  
{  
    Write-Warning "Please run this script as Administrator!"
    Exit
}

$projectPath = $PSScriptRoot
$buildDirs = @(
    "$projectPath\.gradle",
    "$projectPath\app\build",
    "$projectPath\build",
    "$env:USERPROFILE\.gradle",
    "$projectPath\app\build\intermediates",
    "$projectPath\app\build\intermediates\incremental\debug"
)

Write-Host "Stopping Gradle and Java processes..."
Get-Process | Where-Object {$_.Name -like "*java*" -or $_.Name -like "*gradle*"} | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "Cleaning build directories..."
foreach ($dir in $buildDirs) {
    if (Test-Path $dir) {
        Write-Host "Removing $dir"
        Remove-Item -Path $dir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "Creating fresh directories with proper permissions..."
foreach ($dir in $buildDirs) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force
    }
    
    # Grant full control to the current user
    $acl = Get-Acl $dir
    $username = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
    $accessRule = New-Object System.Security.AccessControl.FileSystemAccessRule($username, "FullControl", "ContainerInherit,ObjectInherit", "None", "Allow")
    $acl.SetAccessRule($accessRule)
    Set-Acl $dir $acl
}

Write-Host "Permissions fixed. Please restart Android Studio."
