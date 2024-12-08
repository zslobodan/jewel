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

import com.slobodanzivanovic.jewel.laf.core.util.JewelLafLoggerFactory;
import com.slobodanzivanovic.jewel.util.logging.Logger;

import javax.swing.*;

/**
 * Manager class for Jewel Look and Feel setup and configuration.
 *
 * @author Slobodan Zivanovic
 */
public final class JewelLafManager {

	private static final Logger LOGGER = JewelLafLoggerFactory.getLogger("JewelLafManager");
	private static final JewelSystemPropertyManager propertyManager = new JewelSystemPropertyManager();

	private JewelLafManager() {
		throw new AssertionError();
	}

	/**
	 * Sets up the Look and Feel.
	 *
	 * @param lookAndFeel the Look and Feel to set
	 * @return true if setup was successful
	 */
	public static boolean setup(LookAndFeel lookAndFeel) {
		return setup(lookAndFeel, new JewelLafOptions());
	}

	/**
	 * Sets up the Look and Feel with custom options.
	 *
	 * @param lookAndFeel the Look and Feel to set
	 * @param options     custom setup options
	 * @return true if setup was successful
	 */
	public static boolean setup(LookAndFeel lookAndFeel, JewelLafOptions options) {
		try {
			propertyManager.initializeSystemProperties(options);
			UIManager.setLookAndFeel(lookAndFeel);
			LOGGER.info("Successfully set up LookAndFeel: " + lookAndFeel);
			return true;
		} catch (UnsupportedLookAndFeelException e) {
			LOGGER.error("Failed to setup look and feel: " + lookAndFeel.getClass().getName(), e);
			return false;
		}
	}
}
