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

package com.slobodanzivanovic.jewel.laf;

import com.slobodanzivanovic.jewel.laf.util.JewelLafLoggerFactory;
import com.slobodanzivanovic.jewel.util.logging.Logger;
import com.slobodanzivanovic.jewel.util.system.SystemInfo;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * @author Slobodan Zivanovic
 */
public abstract class JewelLaf extends BasicLookAndFeel {

	private static final Logger LOGGER = JewelLafLoggerFactory.getLafLogger();

	private BasicLookAndFeel basicLookAndFeel;

	@Override
	public String getID() {
		LOGGER.debug("Getting LaF ID: " + getName());
		return getName();
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

	@Override
	public void initialize() {
		LOGGER.info("Initializing " + getName() + " Look and Feel");
		try {
			getBasicLookAndFeel().initialize();
			super.initialize();
			LOGGER.info("Successfully initialized " + getName());
		} catch (Exception e) {
			LOGGER.error("Failed to initialize " + getName() + ": " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void uninitialize() {
		LOGGER.info("Uninitialized " + getName() + " Look and Feel");
		try {
			if (basicLookAndFeel != null) {
				basicLookAndFeel.uninitialize();
			}
			super.uninitialize();
			LOGGER.info("Successfully uninitialized " + getName());
		} catch (Exception e) {
			LOGGER.error("Failed to uninitialize " + getName() + ": " + e.getMessage());
			throw e;
		}
	}

	private BasicLookAndFeel getBasicLookAndFeel() {
		if (basicLookAndFeel == null) {
			basicLookAndFeel = new MetalLookAndFeel();
		}
		return basicLookAndFeel;
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults defaults = getBasicLookAndFeel().getDefaults();

		initializeFonts(defaults);

		return defaults;
	}

	private void initializeFonts(UIDefaults defaults) {
		FontUIResource systemFont = null;

		if (SystemInfo.IS_WINDOWS) {
			Font windowsFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty("win.messagebox.font");
			if (windowsFont != null) {
				systemFont = new FontUIResource(windowsFont);
			}
		} else if (SystemInfo.IS_MAC) {
			Font macFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.application.font");
			if (macFont != null) {
				systemFont = new FontUIResource(macFont);
			}
		}

		if (systemFont == null) {
			systemFont = new FontUIResource("Dialog", Font.PLAIN, 12);
		}

		for (Object key : defaults.keySet()) {
			if (key instanceof String && ((String) key).endsWith(".font")) {
				Object value = defaults.get(key);
				if (value instanceof Font) {
					Font originalFont = (Font) value;
					FontUIResource newFont = new FontUIResource(
						systemFont.getFamily(),
						originalFont.getStyle(),
						originalFont.getSize()
					);
					defaults.put(key, newFont);
				}
			}
		}
	}
}
