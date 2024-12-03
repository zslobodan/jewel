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

import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.Optional;

/**
 * @author Slobodan Zivanovic
 */
public abstract class JewelLaf extends BasicLookAndFeel {

	private static final Logger LOGGER = JewelLafLoggerFactory.getLafLogger();

	private final BasicLookAndFeel basicLookAndFeel = new MetalLookAndFeel();

	@Override
	public String getID() {
		LOGGER.debug("Getting LaF ID: " + getName());
		return getName();
	}

	@Override
	public boolean isNativeLookAndFeel() {
		LOGGER.debug("Checking if native LaF");
		return true;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		LOGGER.debug("Checking if LaF is supported");
		return true;
	}

	@Override
	public void initialize() {
		LOGGER.info("Initializing " + getName() + " Look and Feel");
		try {
			basicLookAndFeel.initialize();
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
			Optional.of(basicLookAndFeel).ifPresent(BasicLookAndFeel::uninitialize);
			super.uninitialize();
			LOGGER.info("Successfully uninitialized " + getName());
		} catch (Exception e) {
			LOGGER.error("Failed to uninitialize " + getName() + ": " + e.getMessage());
			throw e;
		}
	}
}
