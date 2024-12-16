/*
 * Copyright (C) 2024 Slobodan Zivanovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slobodanzivanovic.jewel.util.logging;

import com.slobodanzivanovic.jewel.util.platform.PlatformInfo;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A thread-safe logging utility that provides file-based logging capabilities with automatic log rotation
 * and cleanup features. The logger creates log files in platform-specific directories and manages log
 * file size and retention periods. Key features include thread-safe logging operations, automatic log
 * rotation when size limit is reached, automatic cleanup of old log files, platform-specific log
 * directory locations, and session-based log organization.
 *
 * @author Slobodan Zivanovic
 */
public class Logger {

	private final Path logFilePath;
	private final ReentrantLock lock = new ReentrantLock();
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final long MIN_REQUIRED_SPACE = 10 * 1024 * 1024;
	private static final int MAX_LOG_SIZE = 10 * 1024 * 1024;
	private static final long MAX_LOG_AGE_DAYS = 15;

	private static final String SESSION_FOLDER;
	private static final DateTimeFormatter SESSION_FOLDER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

	static {
		SESSION_FOLDER = LocalDateTime.now().format(SESSION_FOLDER_FORMATTER);
	}

	/**
	 * Defines the available logging levels.
	 */
	public enum LogLevel {
		INFO, WARNING, ERROR, DEBUG
	}

	/**
	 * Constructs a new Logger instance with the specified filename.
	 *
	 * @param filename The base name for the log file (without extension)
	 * @throws IOException If the log directory cannot be created or accessed,
	 *                     or if there's insufficient disk space
	 */
	public Logger(String filename) throws IOException {
		Path logDir = getSystemLogDirectory().resolve(SESSION_FOLDER);
		Files.createDirectories(logDir);

		this.logFilePath = logDir.resolve(filename + ".log");

		validateSystem();
		if (!Files.exists(logFilePath)) {
			Files.createFile(logFilePath);
		}

		cleanupOldLogs();
	}

	/**
	 * Logs a info message.
	 *
	 * @param message The info message to be logged
	 */
	public void info(String message) {
		log(LogLevel.INFO, message);
	}

	/**
	 * Logs a warning message.
	 *
	 * @param message The warning message to be logged
	 */
	public void warning(String message) {
		log(LogLevel.WARNING, message);
	}

	/**
	 * Logs a error message.
	 *
	 * @param message The error message to be logged
	 */
	public void error(String message) {
		log(LogLevel.ERROR, message);
	}

	/**
	 * Logs a error message with an exception.
	 *
	 * @param message   The error message to be logged
	 * @param throwable The exception to be logged
	 */
	public void error(String message, Throwable throwable) {
		log(LogLevel.ERROR, message + "\nException: " +
			throwable.getClass().getName() + ": " + throwable.getMessage() + "\n" +
			Arrays.stream(throwable.getStackTrace())
				.map(StackTraceElement::toString)
				.collect(Collectors.joining("\n")));
	}

	/**
	 * Logs a debug message.
	 *
	 * @param message The debug message to be logged
	 */
	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	/**
	 * Internal method to handle the actual logging process.
	 * Thread-safe implementation using ReentrantLock.
	 *
	 * @param level   The logging level of the message
	 * @param message The message to be logged
	 */
	private void log(LogLevel level, String message) {
		lock.lock();
		try {
			validateSystem();

			if (Files.size(logFilePath) > MAX_LOG_SIZE) {
				rotateLog();
			}

			String timestamp = LocalDateTime.now().format(dateFormatter);
			String formattedMessage = String.format("[%s] [%s] %s%n", timestamp, level, message);

			Files.write(logFilePath, formattedMessage.getBytes(), StandardOpenOption.APPEND);

		} catch (IOException e) {
			System.err.println("Failed to write to log file: " + e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Rotates the current log file by renaming it with a timestamp
	 * and creating a new empty log file.
	 *
	 * @throws IOException If the file rotation operation fails
	 */
	private void rotateLog() throws IOException {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		Path rotatedFile = logFilePath
			.resolveSibling(logFilePath.getFileName().toString().replace(".log", "-" + timestamp + ".log"));

		Files.move(logFilePath, rotatedFile, StandardCopyOption.REPLACE_EXISTING);
		Files.createFile(logFilePath);
	}

	/**
	 * Removes log folders older than MAX_LOG_AGE_DAYS.
	 */
	private void cleanupOldLogs() {
		try {
			Path logsDir = getSystemLogDirectory();
			LocalDateTime cutoffDate = LocalDateTime.now().minusDays(MAX_LOG_AGE_DAYS);

			try (Stream<Path> folders = Files.list(logsDir)) {
				folders.forEach(folder -> {
					if (Files.isDirectory(folder)) {
						try {
							LocalDateTime folderDate = LocalDateTime.parse(folder.getFileName().toString(),
								SESSION_FOLDER_FORMATTER);
							if (folderDate.isBefore(cutoffDate)) {
								deleteDirectory(folder);
							}
						} catch (Exception e) {
							System.err.println("Skipping invalid log folder: " + folder.getFileName());
						}
					}
				});
			}
		} catch (IOException e) {
			System.err.println("Failed to cleanup old logs: " + e.getMessage());
		}
	}

	/**
	 * Recursively deletes a directory and all its contents.
	 *
	 * @param path The path to the directory to be deleted
	 * @throws IOException If the deletion operation fails
	 */
	private void deleteDirectory(Path path) throws IOException {
		if (!Files.exists(path)) {
			return;
		}

		try (Stream<Path> contents = Files.walk(path)) {
			contents.sorted(Comparator.reverseOrder()).forEach(subPath -> {
				try {
					Files.delete(subPath);
				} catch (IOException e) {
					System.err.println("Failed to delete " + subPath + ": " + e.getMessage());
				}
			});
		}
	}

	/**
	 * Returns the platform-specific system log directory.
	 * Windows: %APPDATA%\Jewel\logs
	 * macOS: ~/Library/Logs/Jewel
	 * Linux/Unix: ~/.jewel/logs
	 *
	 * @return Path to the system's log directory
	 */
	private Path getSystemLogDirectory() {
		String userHome = System.getProperty("user.home");

		if (PlatformInfo.IS_WINDOWS) {
			return Paths.get(System.getenv("APPDATA"), "Jewel", "logs");
		} else if (PlatformInfo.IS_MAC) {
			// NOTE: We probably should use the home for both macOS and Linux
			return Paths.get(userHome, "Library", "Logs", "Jewel");
		} else {
			return Paths.get(userHome, ".jewel", "logs");
		}
	}

	/**
	 * Returns the path to the current log file.
	 *
	 * @return Path object representing the current log file location
	 */
	public Path getLogFilePath() {
		return logFilePath;
	}

	/**
	 * Returns the path to the current session directory.
	 *
	 * @return Path object representing the current session directory
	 */
	public Path getSessionDirectory() {
		return logFilePath.getParent();
	}

	/**
	 * Validates the system conditions required for logging.
	 * Checks for sufficient disk space and write permissions.
	 *
	 * @throws IOException If system requirements are not met
	 */
	private void validateSystem() throws IOException {
		FileStore store = Files.getFileStore(logFilePath.getParent());
		long usableSpace = store.getUsableSpace();

		if (usableSpace < MIN_REQUIRED_SPACE) {
			throw new IOException("Insufficient disk space for logging. Required: " + MIN_REQUIRED_SPACE
				+ " bytes, Available: " + usableSpace + " bytes");
		}

		if (!Files.isWritable(logFilePath.getParent())) {
			throw new IOException("No write permission in log directory: " + logFilePath.getParent());
		}
	}
}
