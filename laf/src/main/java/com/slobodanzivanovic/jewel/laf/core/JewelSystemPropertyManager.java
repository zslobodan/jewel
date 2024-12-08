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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Slobodan Zivanovic
 */
class JewelSystemPropertyManager {
	private static final Logger LOGGER = JewelLafLoggerFactory.getLogger("JewelSystemPropertyManager");
	private final JewelPlatformProperties jewelPlatformProperties = new JewelPlatformProperties();

	void initializeSystemProperties(JewelLafOptions options) {
		Map<String, String> properties = new HashMap<>();

		// Add common properties
		properties.putAll(jewelPlatformProperties.getCommonProperties());

		// Add platform-specific properties
		properties.putAll(jewelPlatformProperties.getPlatformSpecificProperties());

		// Add custom properties
		properties.putAll(options.getCustomProperties());

		// Apply all properties
		applyProperties(properties, options);
	}

	private void applyProperties(Map<String, String> properties, JewelLafOptions options) {
		properties.forEach((key, value) -> {
			if (!options.isPropertyDisabled(key)) {
				System.setProperty(key, value);
				LOGGER.debug("Set system property: " + key + " = " + value);
			}
		});
	}
}
