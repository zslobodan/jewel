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

package com.slobodanzivanovic.jewel.laf.core.util;

import com.slobodanzivanovic.jewel.util.logging.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory class for managing Jewel Logger instances across the LaF.
 *
 * @author Slobodan Zivanovic
 */
public final class JewelLafLoggerFactory {

	private static final ConcurrentHashMap<String, Logger> LOGGERS = new ConcurrentHashMap<>();

	// Logger categories
	public static final String LAF_CORE = "jewel-laf";
	public static final String LAF_UI = "jewel-laf-ui";

	private JewelLafLoggerFactory() {
	}

	/**
	 * Gets or creates a Logger instance for the specified category.
	 */
	public static Logger getLogger(String category) {
		return LOGGERS.computeIfAbsent(category, cat -> {
			try {
				return new Logger(cat);
			} catch (IOException e) {
				throw new RuntimeException("Failed to create logger for category: " + cat, e);
			}
		});
	}

	/**
	 * Gets a Logger instance for LaF core functionality.
	 */
	public static Logger getLafLogger() {
		return getLogger(LAF_CORE);
	}

	/**
	 * Gets a Logger instance for LaF UI components.
	 */
	public static Logger getUiLogger() {
		return getLogger(LAF_UI);
	}

	/**
	 * Clears all logger instances.
	 */
	public static void closeAll() {
		LOGGERS.clear();
	}
}
