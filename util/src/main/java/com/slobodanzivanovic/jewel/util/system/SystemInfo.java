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

package com.slobodanzivanovic.jewel.util.system;

import com.slobodanzivanovic.jewel.util.logging.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Provides information about the current platform, operating system, and Java environment.
 *
 * @author Slobodan Zivanovic
 */
public final class SystemInfo {
	private static final SystemInfo INSTANCE = new SystemInfo();
	private static final Logger logger;

	public static final boolean IS_WINDOWS;
	public static final boolean IS_MAC;
	public static final boolean IS_LINUX;

	private final String osName;
	private final String osVersion;
	private final String osArch;
	private final OSType osType;
	private final String javaVersion;
	private final int javaMajorVersion;

	static {
		Logger tempLogger;
		try {
			tempLogger = new Logger("platform-info");
		} catch (IOException e) {
			System.err.println("Failed to initialize logger: " + e.getMessage());
			tempLogger = null;
		}
		logger = tempLogger;

		String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		IS_WINDOWS = osName.startsWith("windows");
		IS_MAC = osName.startsWith("mac");
		IS_LINUX = osName.startsWith("linux");

		if (logger != null) {
			logger.debug("Platform detection: Windows=" + IS_WINDOWS +
				", macOS=" + IS_MAC +
				", Linux=" + IS_LINUX);
		}
	}

	public enum OSType {
		WINDOWS("Windows"),
		LINUX("Linux"),
		MACOS("macOS"),
		UNKNOWN("Unknown");

		private final String displayName;

		OSType(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	private SystemInfo() {
		this.osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		this.osVersion = System.getProperty("os.version");
		this.osArch = System.getProperty("os.arch");
		this.osType = determineOSType(osName);

		Runtime.Version version = Runtime.version();
		this.javaVersion = version.toString();
		this.javaMajorVersion = version.feature();

		if (logger != null) {
			logger.debug("Platform initialized: " + toString());
		}
	}

	private OSType determineOSType(String osName) {
		if (osName.startsWith("mac")) return OSType.MACOS;
		if (osName.startsWith("win")) return OSType.WINDOWS;
		if (osName.contains("nix") || osName.contains("nux")) return OSType.LINUX;
		return OSType.UNKNOWN;
	}

	/**
	 * Gets the current platform's OSType.
	 *
	 * @return The current platform's OSType
	 */
	public static OSType getPlatform() {
		return INSTANCE.getOSType();
	}

	/**
	 * Gets the operating system name.
	 *
	 * @return The OS name in lowercase
	 */
	public String getOSName() {
		return osName;
	}

	/**
	 * Gets the operating system version.
	 *
	 * @return The OS version string
	 */
	public String getOSVersion() {
		return osVersion;
	}

	/**
	 * Gets the system architecture.
	 *
	 * @return The OS architecture string
	 */
	public String getOSArch() {
		return osArch;
	}

	/**
	 * Gets the detected operating system type.
	 *
	 * @return The OSType enum value
	 */
	public OSType getOSType() {
		return osType;
	}

	/**
	 * Gets the full Java version string.
	 *
	 * @return The Java version
	 */
	public String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * Gets the major Java version number.
	 *
	 * @return The major version number
	 */
	public int getJavaMajorVersion() {
		return javaMajorVersion;
	}

	/**
	 * Checks if the system is running on ARM64 architecture.
	 *
	 * @return true if running on ARM64/AArch64
	 */
	public boolean isAarch64() {
		return "aarch64".equals(osArch) || "arm64".equals(osArch);
	}

	/**
	 * Checks if the system is Unix-based (macOS or Linux).
	 *
	 * @return true if the system is Unix-based
	 */
	public boolean isUnix() {
		return IS_MAC || IS_LINUX;
	}

	/**
	 * Gets the singleton instance of PlatformInfo.
	 *
	 * @return The PlatformInfo instance
	 */
	public static SystemInfo getInstance() {
		return INSTANCE;
	}

	public void logSystemInfo() {
		try {
			Logger sysLogger = new Logger("system-info");
			sysLogger.info("System Information:");
			sysLogger.info("OS Type: " + osType.getDisplayName());
			sysLogger.info("OS Name: " + osName);
			sysLogger.info("OS Version: " + osVersion);
			sysLogger.info("OS Architecture: " + osArch);
			sysLogger.info("Java Version: " + javaVersion);
			sysLogger.info("Java Major Version: " + javaMajorVersion);
			sysLogger.info("ARM64 Architecture: " + isAarch64());
		} catch (IOException e) {
			logger.error("Failed to log system information: " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return String.format("PlatformInfo[osType=%s, osVersion=%s, arch=%s, java=%s]",
			osType.getDisplayName(), osVersion, osArch, javaVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SystemInfo other)) return false;
		return Objects.equals(osName, other.osName) &&
			Objects.equals(osVersion, other.osVersion) &&
			Objects.equals(osArch, other.osArch) &&
			osType == other.osType &&
			Objects.equals(javaVersion, other.javaVersion) &&
			javaMajorVersion == other.javaMajorVersion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(osName, osVersion, osArch, osType, javaVersion, javaMajorVersion);
	}
}
