package smlauncher.util.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Comprehensive logging utility for the StarMade Launcher
 */
public class LauncherLogger {
	// Singleton instance
	private static LauncherLogger instance;

	// Logging configuration
	private static final String LOG_DIRECTORY = "./StarMade/logs";
	private static final int MAX_LOG_FILES = 5;
	private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private static final DateTimeFormatter LOG_ENTRY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	// Logging levels
	public enum LogLevel {
		DEBUG(java.util.logging.Level.FINE),
		INFO(java.util.logging.Level.INFO),
		WARN(java.util.logging.Level.WARNING),
		ERROR(java.util.logging.Level.SEVERE),
		FATAL(java.util.logging.Level.SEVERE);

		private final java.util.logging.Level level;

		LogLevel(java.util.logging.Level level) {
			this.level = level;
		}

		public java.util.logging.Level getJavaLevel() {
			return level;
		}
	}

	// Logging state
	private boolean debugMode = false;
	private Path currentLogFile;

	// Private constructor for singleton
	private LauncherLogger() {
		initializeLogging();
	}

	/**
	 * Get the singleton instance of LauncherLogger
	 * @return LauncherLogger instance
	 */
	public static synchronized LauncherLogger getInstance() {
		if(instance == null) {
			instance = new LauncherLogger();
		}
		return instance;
	}

	/**
	 * Initialize logging system
	 */
	private void initializeLogging() {
		try {
			// Ensure log directory exists
			Files.createDirectories(Paths.get(LOG_DIRECTORY));

			// Rotate log files
			rotateLogFiles();

			// Create new log file
			String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
			currentLogFile = Paths.get(LOG_DIRECTORY, "launcher_" + timestamp + ".log");
			Files.createFile(currentLogFile);

			// Log application start
			log(LogLevel.INFO, "StarMade Launcher initialized", null);
		} catch(IOException e) {
			// Fallback to console logging if file setup fails
			System.err.println("Failed to initialize logging: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Rotate log files, keeping only the most recent logs
	 */
	private void rotateLogFiles() throws IOException {
		Path logDirPath = Paths.get(LOG_DIRECTORY);

		// List log files, sorted by last modified time
		Files.list(logDirPath).filter(path -> path.toString().endsWith(".log")).sorted((a, b) -> {
			try {
				return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
			} catch(IOException e) {
				return 0;
			}
		}).skip(MAX_LOG_FILES - 1L).forEach(path -> {
			try {
				Files.delete(path);
			} catch(IOException e) {
				System.err.println("Could not delete log file: " + path);
			}
		});
	}

	/**
	 * Set debug mode
	 * @param enabled Whether debug logging is enabled
	 */
	public void setDebugMode(boolean enabled) {
		this.debugMode = enabled;
	}

	/**
	 * Log a debug message
	 * @param message Message to log
	 */
	public void debug(String message) {
		if(debugMode) {
			log(LogLevel.DEBUG, message, null);
		}
	}

	/**
	 * Log an info message
	 * @param message Message to log
	 */
	public void info(String message) {
		log(LogLevel.INFO, message, null);
	}

	/**
	 * Log a warning message
	 * @param message Message to log
	 */
	public void warning(String message) {
		log(LogLevel.WARN, message, null);
	}

	/**
	 * Log a warning message with exception
	 * @param message Message to log
	 * @param throwable Exception to log
	 */
	public void warning(String message, Throwable throwable) {
		log(LogLevel.WARN, message, throwable);
	}

	/**
	 * Log an error message
	 * @param message Message to log
	 */
	public void error(String message) {
		log(LogLevel.ERROR, message, null);
	}

	/**
	 * Log an error message with exception
	 * @param message Message to log
	 * @param throwable Exception to log
	 */
	public void error(String message, Throwable throwable) {
		log(LogLevel.ERROR, message, throwable);
	}

	/**
	 * Log a fatal error message
	 * @param message Message to log
	 * @param throwable Fatal exception
	 */
	public void fatal(String message, Throwable throwable) {
		log(LogLevel.FATAL, message, throwable);
	}

	/**
	 * Core logging method
	 * @param level Log level
	 * @param message Message to log
	 * @param throwable Optional exception
	 */
	private synchronized void log(LogLevel level, String message, Throwable throwable) {
		// Prepare log entry
		String timestamp = LocalDateTime.now().format(LOG_ENTRY_FORMATTER);
		StringBuilder logEntry = new StringBuilder();
		logEntry.append(String.format("[%s] [%s] %s", timestamp, level.name(), message));

		// Add stack trace if exception is present
		if(throwable != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			logEntry.append("\n").append(sw);
		}
		logEntry.append("\n");

		// Console output based on log level
		String logMessage = logEntry.toString();
		switch(level) {
			case DEBUG:
			case INFO:
				System.out.println(logMessage);
				break;
			case WARN:
			case ERROR:
			case FATAL:
				System.err.println(logMessage);
				break;
		}

		// Write to log file
		try {
			if(currentLogFile != null) {
				Files.write(currentLogFile, logMessage.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			}
		} catch(IOException e) {
			System.err.println("Failed to write to log file: " + e.getMessage());
		}
	}

	/**
	 * Shutdown the logger, performing any necessary cleanup
	 */
	public void shutdown() {
		log(LogLevel.INFO, "StarMade Launcher shutting down", null);
	}

	// Ensure logger is shut down on JVM exit
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LauncherLogger logger = getInstance();
			logger.shutdown();
		}));
	}
}