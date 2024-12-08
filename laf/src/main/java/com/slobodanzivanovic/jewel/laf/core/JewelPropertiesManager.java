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
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Manages properties loading and processing for the Look and Feel.
 *
 * @author Slobodan Zivanovic
 */
class JewelPropertiesManager {

	private static final Logger LOGGER = JewelLafLoggerFactory.getLafLogger();
	private static final String GLOBAL_PREFIX = "*.";

	void loadProperties(UIDefaults defaults, Class<?> lafClass) {
		try {
			LOGGER.info("Starting properties loading for class: " + lafClass.getName());
			Properties properties = loadPropertiesFromHierarchy(lafClass);
			LOGGER.info("Loaded " + properties.size() + " properties from hierarchy");

			Map<String, Object> globals = processGlobalProperties(properties);
			LOGGER.info("Processed " + globals.size() + " global properties");

			applyProperties(defaults, properties, globals);
			LOGGER.info("Successfully applied all properties to UI defaults");
		} catch (Exception e) {
			LOGGER.error("Failed to load properties for " + lafClass.getName(), e);
			throw new RuntimeException("Failed to load UI properties", e);
		}
	}

	private Properties loadPropertiesFromHierarchy(Class<?> lafClass) {
		Properties properties = new Properties();
		Class<?> currentClass = lafClass;

		LOGGER.debug("Starting hierarchy properties loading from: " + lafClass.getName());
		while (JewelLaf.class.isAssignableFrom(currentClass)) {
			loadClassProperties(properties, currentClass);
			currentClass = currentClass.getSuperclass();
		}

		return properties;
	}

	private void loadClassProperties(Properties properties, Class<?> clazz) {
		String resourcePath = "/" + clazz.getName().replace('.', '/') + ".properties";
		LOGGER.debug("Loading properties from: " + resourcePath);

		try (InputStream stream = clazz.getResourceAsStream(resourcePath)) {
			if (stream != null) {
				int sizeBefore = properties.size();
				properties.load(stream);
				int loaded = properties.size() - sizeBefore;
				LOGGER.debug("Loaded " + loaded + " properties from " + resourcePath);
			} else {
				LOGGER.debug("No properties file found at: " + resourcePath);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load properties from: " + resourcePath, e);
		}
	}

	private Map<String, Object> processGlobalProperties(Properties properties) {
		Map<String, Object> globals = new HashMap<>();
		LOGGER.debug("Processing global properties");

		properties.stringPropertyNames().stream()
			.filter(key -> key.startsWith(GLOBAL_PREFIX))
			.forEach(key -> {
				String value = properties.getProperty(key);
				String globalKey = key.substring(GLOBAL_PREFIX.length());
				Object parsedValue = parseValue(value);
				globals.put(globalKey, parsedValue);
				LOGGER.debug("Processed global property: " + globalKey + " = " + parsedValue);
			});

		return globals;
	}

	private void applyProperties(UIDefaults defaults, Properties properties, Map<String, Object> globals) {
		LOGGER.debug("Applying globals to UI defaults");
		// Apply globals
		defaults.keySet().stream()
			.filter(key -> key instanceof String)
			.map(key -> (String) key)
			.filter(key -> key.contains("."))
			.forEach(key -> {
				String suffix = key.substring(key.lastIndexOf('.') + 1);
				Object globalValue = globals.get(suffix);
				if (globalValue != null) {
					defaults.put(key, globalValue);
					LOGGER.debug("Applied global value for: " + key);
				}
			});

		LOGGER.debug("Applying specific properties to UI defaults");
		// Apply specific properties
		properties.stringPropertyNames().stream()
			.filter(key -> !key.startsWith(GLOBAL_PREFIX))
			.forEach(key -> {
				Object value = parseValue(properties.getProperty(key));
				defaults.put(key, value);
				LOGGER.debug("Applied specific property: " + key + " = " + value);
			});
	}

	private Object parseValue(String value) {
		value = value.trim();

		switch (value) {
			case "null":
				LOGGER.debug("Parsed null value");
				return null;
			case "false":
				LOGGER.debug("Parsed boolean value: false");
				return false;
			case "true":
				LOGGER.debug("Parsed boolean value: true");
				return true;
		}

		ColorUIResource color = parseColor(value);
		if (color != null) {
			LOGGER.debug("Parsed color value: " + value);
			return color;
		}

		LOGGER.debug("Using raw string value: " + value);
		return value;
	}

	private ColorUIResource parseColor(String value) {
		try {
			if (value.length() == 6) {
				return new ColorUIResource(Integer.parseInt(value, 16));
			}
			if (value.length() == 8) {
				return new ColorUIResource(new Color(Integer.parseInt(value, 16), true));
			}
		} catch (NumberFormatException ignored) {
			LOGGER.debug("Failed to parse as color: " + value);
		}
		return null;
	}
}
