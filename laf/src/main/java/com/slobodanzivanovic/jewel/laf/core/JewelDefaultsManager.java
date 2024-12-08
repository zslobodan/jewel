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
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Manages UI defaults for Jewel Look and Feel.
 *
 * @author Slobodan Zivanovic
 */
class JewelDefaultsManager {

    private static final Logger LOGGER = JewelLafLoggerFactory.getLafLogger();
    private final JewelFontManager jewelFontManager;
    private final JewelPropertiesManager jewelPropertiesManager;

    JewelDefaultsManager() {
        LOGGER.debug("Initializing JewelDefaultsManager");
        this.jewelFontManager = new JewelFontManager();
        this.jewelPropertiesManager = new JewelPropertiesManager();
    }

    UIDefaults initializeDefaults(Class<?> lafClass) {
        try {
            LOGGER.info("Starting defaults initialization for: " + lafClass.getName());

            LOGGER.debug("Creating base UI defaults from MetalLookAndFeel");
            UIDefaults defaults = new MetalLookAndFeel().getDefaults();

            LOGGER.debug("Initializing fonts");
            jewelFontManager.initializeFonts(defaults);

            LOGGER.debug("Loading and applying properties");
            jewelPropertiesManager.loadProperties(defaults, lafClass);

            LOGGER.info("Successfully initialized UI defaults");
            return defaults;
        } catch (Exception e) {
            LOGGER.error("Failed to initialize defaults for " + lafClass.getName(), e);
            throw new RuntimeException("Failed to initialize UI defaults", e);
        }
    }
}
