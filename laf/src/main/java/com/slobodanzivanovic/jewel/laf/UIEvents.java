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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Slobodan Zivanovic
 */
public class UIEvents {

	public interface ThemeChangeListener {
		void onThemeChanged();
	}

	private static final List<ThemeChangeListener> themeListeners = new ArrayList<>();

	public static void addThemeChangeListener(ThemeChangeListener listener) {
		themeListeners.add(listener);
	}

	public static void removeThemeChangeListener(ThemeChangeListener listener) {
		themeListeners.remove(listener);
	}

	public static void fireThemeChanged() {
		for (ThemeChangeListener listener : themeListeners) {
			listener.onThemeChanged();
		}
	}
}
