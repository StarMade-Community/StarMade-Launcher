#!/bin/bash
# StarMade Dedicated Server Launch Script for macOS
# This script launches the StarMade JAR in server mode

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Create log file if it doesn't exist
LOG_FILE="$SCRIPT_DIR/server_log.txt"
if [ ! -f "$LOG_FILE" ]; then
    touch "$LOG_FILE"
fi

# Function to log messages
write_log() {
    local message="$1"
    local timestamp=$(date "+%Y-%m-%d %H:%M:%S")
    local log_entry="[$timestamp] $message"
    echo "$log_entry"
    echo "$log_entry" >> "$LOG_FILE"
}

# Function to wait for user input before exit
wait_for_exit() {
    echo "Press enter to exit..."
    read
}

# Check if StarMade directory exists
if [ ! -d "StarMade" ]; then
    write_log "StarMade directory not found."
    write_log "Please run the StarMade Launcher first to download the game."
    wait_for_exit
    exit 1
fi

# Check if launcher JAR exists
if [ ! -f "StarMade-Launcher.jar" ]; then
    write_log "StarMade-Launcher.jar not found."
    write_log "Please ensure the launcher is properly installed."
    wait_for_exit
    exit 1
fi

# Check if Java 8 exists
JAVA8_PATH="$SCRIPT_DIR/jre8/bin/java"
if [ ! -f "$JAVA8_PATH" ]; then
    write_log "Java 8 runtime not found at $JAVA8_PATH"
    write_log "Please run the StarMade Launcher first to install the required Java runtime."
    wait_for_exit
    exit 1
fi

# Check if Java 23 exists
JAVA23_PATH="$SCRIPT_DIR/jre23/bin/java"
if [ ! -f "$JAVA23_PATH" ]; then
    write_log "Java 23 runtime not found at $JAVA23_PATH"
    write_log "Please run the StarMade Launcher first to install the required Java runtime."
    wait_for_exit
    exit 1
fi

# Determine port (default 4242)
PORT=4242
if [ $# -gt 0 ]; then
    PORT="$1"
fi

JAVA_PATH=""

write_log "Starting StarMade Server on port $PORT..."

# Check version.txt for game version, if version starts with "0.1" or "0.2", use Java 8, otherwise use Java 23
VERSION_FILE="$SCRIPT_DIR/StarMade/version.txt"
if [ -f "$VERSION_FILE" ]; then
    VERSION=$(head -n 1 "$VERSION_FILE")
    if [[ "$VERSION" =~ ^0\.[12] ]]; then
        JAVA_PATH="$JAVA8_PATH"
        write_log "Using Java 8 for version $VERSION"
    else
        JAVA_PATH="$JAVA23_PATH"
        write_log "Using Java 23 for version $VERSION"
    fi
else
    write_log "version.txt not found, defaulting to Java 23."
    JAVA_PATH="$JAVA23_PATH"
fi

# Open up launch-settings.json to find the installation path
LAUNCH_SETTINGS="$SCRIPT_DIR/launch-settings.json"
if [ ! -f "$LAUNCH_SETTINGS" ]; then
    write_log "launch-settings.json not found."
    write_log "Please run the StarMade Launcher first to create the necessary files."
    wait_for_exit
    exit 1
fi

# Extract the installation path from launch-settings.json using grep and sed
INSTALLATION_PATH=$(grep -o '"installDir":[[:space:]]*"[^"]*"' "$LAUNCH_SETTINGS" | sed 's/"installDir":[[:space:]]*"//; s/"//')

if [ -z "$INSTALLATION_PATH" ]; then
    write_log "Installation path not found in launch-settings.json."
    write_log "Please run the StarMade Launcher first to create the necessary files."
    wait_for_exit
    exit 1
fi

# Change to the StarMade installation directory
if ! cd "$INSTALLATION_PATH" 2>/dev/null; then
    write_log "Failed to change directory to StarMade installation path: $INSTALLATION_PATH"
    wait_for_exit
    exit 1
fi

# Launch StarMade server (using macOS native library path)
"$JAVA_PATH" "-Djava.library.path=lib:native/macos" -jar StarMade.jar -server "-port:$PORT"

write_log "Server has stopped."
wait_for_exit
exit 0