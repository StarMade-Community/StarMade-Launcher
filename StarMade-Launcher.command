#!/bin/bash
# StarMade Launcher Script for macOS
# This script launches the StarMade game via the launcher JAR. Unfortunately, this script is needed for MacOS, as Apple
# requires Applications to be specially code signed and notarized to run without warning messages. This script
# circumvents that by running the JAR directly. Eventually, we do want to get the launcher properly signed and notarized,
# but for now, this is the best we can do. We still are including a proper .app bundle for MacOS in addition to this script,
# but users will need to manually approve that for it to work, and so this script is provided as a default for non-technical users.

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

# Check if launcher JAR exists
if [ ! -f "StarMade-Launcher.jar" ]; then
    write_log "StarMade-Launcher.jar not found."
    write_log "Please ensure the launcher is properly installed."
    wait_for_exit
    exit 1
fi

# Check if Java 8 exists
JAVA8_PATH="$SCRIPT_DIR/jre8/Contents/Home/bin/java"
if [ ! -f "$JAVA8_PATH" ]; then
    write_log "Java 8 runtime not found at $JAVA8_PATH"
    write_log "Please ensure the launcher is properly installed."
    wait_for_exit
    exit 1
fi

# Check if Java 23 exists
JAVA23_PATH="$SCRIPT_DIR/jre23/Contents/Home/bin/java"
if [ ! -f "$JAVA23_PATH" ]; then
    write_log "Java 23 runtime not found at $JAVA23_PATH"
    write_log "Please ensure the launcher is properly installed."
    wait_for_exit
    exit 1
fi

# Launch the StarMade Launcher
write_log "Launching StarMade Launcher..."
"$JAVA23_PATH" -jar "StarMade-Launcher.jar"
LAUNCHER_EXIT_CODE=$?
if [ $LAUNCHER_EXIT_CODE -ne 0 ]; then
    write_log "StarMade Launcher exited with code $LAUNCHER_EXIT_CODE"
    wait_for_exit
    exit $LAUNCHER_EXIT_CODE
fi
