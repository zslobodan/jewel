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

package com.slobodanzivanovic.jewel.util.platform;

import java.util.Locale;
import java.util.Objects;

/**
 * Information about the current platform, OS, and Java environment.
 *
 * @author Slobodan Zivanovic
 */
public class PlatformInfo {

	private static final PlatformInfo INSTANCE = new PlatformInfo();

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
		String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		IS_WINDOWS = osName.contains("win");
		IS_MAC = osName.contains("mac");
		IS_LINUX = osName.contains("linux");
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

	private PlatformInfo() {
		this.osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		this.osVersion = System.getProperty("os.version");
		this.osArch = System.getProperty("os.arch");
		this.osType = determineOSType(osName);

		Runtime.Version version = Runtime.version();
		this.javaVersion = version.toString();
		this.javaMajorVersion = version.feature();
	}

	private OSType determineOSType(String osName) {
		if (osName.startsWith("mac")) return OSType.MACOS;
		if (osName.startsWith("win")) return OSType.WINDOWS;
		if (osName.contains("nix") || osName.contains("nux")) return OSType.LINUX;
		return OSType.UNKNOWN;
	}

	/**
	 * Get the singleton instance of PlatformInfo
	 *
	 * @return The PlatformInfo instance
	 */
	public static PlatformInfo getInstance() {
		return INSTANCE;
	}

	/**
	 * Get current platform OSType.
	 *
	 * @return The current platform OSType
	 */
	public static OSType getPlatform() {
		return INSTANCE.getOSType();
	}

	/**
	 * Get the detected OS type.
	 *
	 * @return The OSType enum value
	 */
	public OSType getOSType() {
		return osType;
	}

	/**
	 * Get the OS name.
	 *
	 * @return The OS name in lowercase
	 */
	public String getOsName() {
		return osName;
	}

	/**
	 * Get the OS version.
	 *
	 * @return The OS version string
	 */
	public String getOsVersion() {
		return osVersion;
	}

	/**
	 * Get the system architecture.
	 *
	 * @return The OS architecture string
	 */
	public String getOsArch() {
		return osArch;
	}

	/**
	 * Get the full Java version string.
	 *
	 * @return The Java version
	 */
	public String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * Get the major Java version number.
	 *
	 * @return The major version number
	 */
	public int getJavaMajorVersion() {
		return javaMajorVersion;
	}

	/**
	 * Check if the system is running on ARM64 architecture.
	 *
	 * @return true if running on ARM64/AArch64
	 */
	public boolean isAarch64() {
		return "aarch64".equals(osArch) || "arm64".equals(osArch);
	}

	/**
	 * Check if the system is Unix-based (macOS or Linux).
	 *
	 * @return true if the system is Unix-based
	 */
	public boolean isUnix() {
		return IS_MAC || IS_LINUX;
	}

	@Override
	public String toString() {
		return String.format("PlatformInfo[osType=%s, osVersion=%s, arch=%s, java=%s]",
			osType.getDisplayName(), osVersion, osArch, javaVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlatformInfo other)) return false;
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
