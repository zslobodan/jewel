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

package com.slobodanzivanovic.jewel.util.prefs;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * A factory for creating XML-based preferences that implements the PreferencesFactory interface.
 * The factory creates and manages a single root preferences node that serves as the base
 * for both system and user preferences hierarchies. This implementation treats system
 * and user preferences identically, returning the same root node for both.
 *
 * @author Slobodan Zivanovic
 */
public class XMLPreferencesFactory implements PreferencesFactory {

	// TODO: Change me, this is just for now!
	private static final String USER_HOME = System.getProperty("user.home");
	protected static final String PREFERENCES_DIR = USER_HOME + File.separator + ".jewel" + File.separator + "preferences";

	private static Preferences rootPreferences;

	@Override
	public Preferences systemRoot() {
		return userRoot();
	}

	@Override
	public Preferences userRoot() {
		if (rootPreferences == null) {
			rootPreferences = new XMLPreferences(null, "");
		}
		return rootPreferences;
	}
}
