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

package com.slobodanzivanovic.jewel.laf.core;

import com.slobodanzivanovic.jewel.util.system.SystemInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Default properties for LaF, Editor.
 *
 * @author Slobodan Zivanovic
 */
class JewelPlatformProperties {

	Map<String, String> getCommonProperties() {
		Map<String, String> properties = new HashMap<>();
		properties.put("sun.java2d.dpiaware", "true");
		properties.put("sun.awt.noerasebackground", "true");
		properties.put("swing.bufferPerWindow", "true");
		properties.put("swing.aatext", "true");
		properties.put("sun.java2d.uiScale.enabled", "true");
		properties.put("javax.swing.rebaseCssSizeMap", "true");
		return properties;
	}

	Map<String, String> getPlatformSpecificProperties() {
		if (SystemInfo.IS_MAC) {
			return getMacProperties();
		} else if (SystemInfo.IS_WINDOWS) {
			return getWindowsProperties();
		} else if (SystemInfo.IS_LINUX) {
			return getLinuxProperties();
		}
		return new HashMap<>();
	}

	private Map<String, String> getMacProperties() {
		Map<String, String> properties = new HashMap<>();
		properties.put("apple.awt.application.name", "Jewel");
		properties.put("apple.laf.useScreenMenuBar", "true");
		properties.put("apple.awt.graphics.UseQuartz", "true");
		properties.put("apple.awt.fileDialogForDirectories", "true");
		properties.put("apple.awt.fullscreencapturealldisplays", "false");
		properties.put("com.apple.mrj.application.live-resize", "false");
		properties.put("sun.java2d.opengl", "true");
		return properties;
	}

	private Map<String, String> getLinuxProperties() {
		Map<String, String> properties = new HashMap<>();
		properties.put("sun.java2d.opengl", "true");
		properties.put("sun.java2d.pmoffscreen", "false");
		properties.put("awt.useSystemAAFontSettings", "on");
		return properties;
	}

	private Map<String, String> getWindowsProperties() {
		Map<String, String> properties = new HashMap<>();
		properties.put("sun.java2d.opengl", "true");
		properties.put("sun.java2d.ddforcevram", "true");
		properties.put("sun.java2d.ddscale", "true");
		return properties;
	}
}
