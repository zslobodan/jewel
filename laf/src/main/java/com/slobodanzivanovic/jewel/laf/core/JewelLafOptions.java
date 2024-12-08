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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Options class for configuring JewelLafManager behavior.
 *
 * @author Slobodan Zivanovic
 */
public class JewelLafOptions {
	private final Map<String, String> customProperties = new HashMap<>();
	private final Set<String> disabledProperties = new HashSet<>();

	public JewelLafOptions setProperty(String key, String value) {
		customProperties.put(key, value);
		return this;
	}

	public JewelLafOptions disableProperty(String key) {
		disabledProperties.add(key);
		return this;
	}

	public boolean isPropertyDisabled(String key) {
		return disabledProperties.contains(key);
	}

	public Map<String, String> getCustomProperties() {
		return new HashMap<>(customProperties);
	}
}
