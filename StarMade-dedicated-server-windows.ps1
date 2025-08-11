# StarMade Dedicated Server Launch Script for Windows PowerShell
# This script launches the StarMade JAR in server mode

# Get the directory where this script is located
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

# Create log file if it doesn't exist
$LogFile = Join-Path $ScriptDir "server_log.txt"
if (!(Test-Path $LogFile)) {
    New-Item -Path $LogFile -ItemType File -Force | Out-Null
}

# Function to log messages
function Write-Log {
    param($Message)
    $Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $LogEntry = "[$Timestamp] $Message"
    Write-Host $LogEntry
    Add-Content -Path $LogFile -Value $LogEntry
}

# Check if StarMade directory exists
if (!(Test-Path "StarMade" -PathType Container)) {
    Write-Log "StarMade directory not found."
    Write-Log "Please run the StarMade Launcher first to download the game."
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Check if launcher JAR exists
if (!(Test-Path "StarMade-Launcher.jar")) {
    Write-Log "StarMade-Launcher.jar not found."
    Write-Log "Please ensure the launcher is properly installed."
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Check if Java 8 exists
$Java8Path = Join-Path $ScriptDir "jre8\bin\java.exe"
if (!(Test-Path $Java8Path)) {
    Write-Log "Java 8 runtime not found at $Java8Path"
    Write-Log "Please run the StarMade Launcher first to install the required Java runtime."
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Check if Java 23 exists
$Java23Path = Join-Path $ScriptDir "jre23\bin\java.exe"
if (!(Test-Path $Java23Path)) {
    Write-Log "Java 23 runtime not found at $Java23Path"
    Write-Log "Please run the StarMade Launcher first to install the required Java runtime."
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Determine port (default 4242)
$Port = 4242
if ($args.Length -gt 0) {
    $Port = $args[0]
}

$JavaPath = ""

Write-Log "Starting StarMade Server on port $Port..."

# Check version.txt for game version, if version starts with "0.1" or "0.2", use Java 8, otherwise use Java 23
$VersionFile = Join-Path $ScriptDir "StarMade\version.txt"
if (Test-Path $VersionFile) {
    $Version = Get-Content $VersionFile -First 1
    if ($Version -match "^0\.[12]") {
        $JavaPath = $Java8Path
        Write-Log "Using Java 8 for version $Version"
    } else {
        $JavaPath = $Java23Path
        Write-Log "Using Java 23 for version $Version"
    }
} else {
    Write-Log "version.txt not found, defaulting to Java 23."
    $JavaPath = $Java23Path
}

# Open up launch-settings.json to find the installation path
$LaunchSettings = Join-Path $ScriptDir "launch-settings.json"
if (!(Test-Path $LaunchSettings)) {
    Write-Log "launch-settings.json not found."
    Write-Log "Please run the StarMade Launcher first to create the necessary files."
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Extract the installation path from launch-settings.json
$LaunchSettingsContent = Get-Content $LaunchSettings -Raw
if ($LaunchSettingsContent -match '"installDir":\s*"([^"]*)"') {
    $InstallationPath = $Matches[1]
    # Replace forward slashes with backslashes for Windows paths
    $InstallationPath = $InstallationPath -replace '/', '\'
} else {
    Write-Log "Installation path not found in launch-settings.json."
    Write-Log "Please run the StarMade Launcher first to create the necessary files."
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Change to the StarMade installation directory
try {
    Set-Location $InstallationPath
} catch {
    Write-Log "Failed to change directory to StarMade installation path: $InstallationPath"
    Write-Host "Press enter to exit..."
    Read-Host
    exit 1
}

# Launch StarMade server
& "$JavaPath" "-Djava.library.path=lib;native/windows" -jar StarMade.jar -server "-port:$Port"

Write-Log "Server has stopped."
Write-Host "Press enter to exit..."
Read-Host
exit 0