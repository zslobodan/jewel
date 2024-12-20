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

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.slobodanzivanovic.jewel.util.platform.PlatformInfo;
import com.slobodanzivanovic.jewel.util.prefs.XMLPreferences;

import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * Manages UI preferences and system scale settings.
 * This class handles the persistence and application of user interface preferences,
 * including the look-and-feel theme (light/dark) and system scaling factors.
 * It uses XML-based preferences storage
 *
 * @author Slobodan Zivanovic
 */
public class UIPreferences {
	public static final String KEY_LAF = "laf";
	public static final String KEY_SYSTEM_SCALE_FACTOR = "systemScaleFactor";

	private static final String PREFS_ROOT_PATH = "/jewel-prefs";
	private static XMLPreferences state;

	/**
	 * Retrieves the current XML preferences state object.
	 *
	 * @return XMLPreferences object containing the current preferences state
	 */
	public static XMLPreferences getState() {
		return state;
	}

	/**
	 * Initializes the preferences system by setting up the XML preferences factory
	 * and creating the root preferences node.
	 */
	public static void init() {
		System.setProperty("java.util.prefs.PreferencesFactory",
			"com.slobodanzivanovic.jewel.util.prefs.XMLPreferencesFactory");

		state = (XMLPreferences) Preferences.userRoot().node(PREFS_ROOT_PATH);
	}

	/**
	 * Sets up the Look and Feel (LaF) based on stored preferences.
	 * Defaults to FlatLightLaf if no preference is found or if there an error.
	 * Also registers a property change listener to save LaF changes.
	 */
	public static void setupLaf() {
		try {
			String lafClassName = state.get(KEY_LAF, FlatLightLaf.class.getName());

			if (FlatDarkLaf.class.getName().equals(lafClassName)) {
				FlatDarkLaf.setup();
			} else {
				FlatLightLaf.setup();
			}
		} catch (Exception ex) {
			// TODO: Add logger
			ex.printStackTrace();
			FlatLightLaf.setup();
		}

		UIManager.addPropertyChangeListener(e -> {
			if ("lookAndFeel".equals(e.getPropertyName())) {
				state.put(KEY_LAF, UIManager.getLookAndFeel().getClass().getName());
			}
		});

		FlatInspector.install("ctrl shift alt X");
		FlatUIDefaultsInspector.install("ctrl shift alt Y");
	}

	/**
	 * Initializes system scaling properties.
	 */
	public static void initSystemScale() {
		if (PlatformInfo.IS_WINDOWS) {
			System.setProperty("sun.java2d.dpiaware", "true");
			System.setProperty("sun.java2d.uiScale.enabled", "true");
		}

		if (System.getProperty("sun.java2d.uiScale") == null) {
			String scaleFactor = getState().get(KEY_SYSTEM_SCALE_FACTOR, null);
			if (scaleFactor != null) {
				System.setProperty("sun.java2d.uiScale", scaleFactor);
				System.setProperty("flatlaf.uiScale", scaleFactor);
				System.setProperty("swing.defaultlaf.useSystemScale", "true");

				if (PlatformInfo.IS_WINDOWS) {
					System.setProperty("sun.java2d.win.uiScaleX", scaleFactor);
					System.setProperty("sun.java2d.win.uiScaleY", scaleFactor);
				}
			}
		}
	}

	/**
	 * Registers keyboard shortcuts for changing the system scale factor.
	 */
	public static void registerSystemScaleFactors(JFrame frame) {
		registerSystemScaleFactor(frame, "alt shift F1", null);
		registerSystemScaleFactor(frame, "alt shift F2", "1");

		if (PlatformInfo.IS_WINDOWS) {
			registerSystemScaleFactor(frame, "alt shift F3", "1.25");
			registerSystemScaleFactor(frame, "alt shift F4", "1.5");
			registerSystemScaleFactor(frame, "alt shift F5", "1.75");
			registerSystemScaleFactor(frame, "alt shift F6", "2");
			registerSystemScaleFactor(frame, "alt shift F7", "2.25");
			registerSystemScaleFactor(frame, "alt shift F8", "2.5");
			registerSystemScaleFactor(frame, "alt shift F9", "2.75");
			registerSystemScaleFactor(frame, "alt shift F10", "3");
			registerSystemScaleFactor(frame, "alt shift F11", "3.5");
			registerSystemScaleFactor(frame, "alt shift F12", "4");
		} else {
			registerSystemScaleFactor(frame, "alt shift F3", "2");
			registerSystemScaleFactor(frame, "alt shift F4", "3");
			registerSystemScaleFactor(frame, "alt shift F5", "4");

		}
	}

	private static void registerSystemScaleFactor(JFrame frame, String keyStrokeStr, String scaleFactor) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeStr);
		if (keyStroke == null)
			throw new IllegalArgumentException("Invalid key stroke '" + keyStrokeStr + "'");

		((JComponent) frame.getContentPane()).registerKeyboardAction(
			e -> applySystemScaleFactor(frame, scaleFactor),
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private static void applySystemScaleFactor(JFrame frame, String scaleFactor) {
		if (JOptionPane.showConfirmDialog(frame,
			"Change system scale factor to "
				+ (scaleFactor != null ? scaleFactor : "default")
				+ " and exit?",
			frame.getTitle(), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;

		if (scaleFactor != null)
			UIPreferences.getState().put(KEY_SYSTEM_SCALE_FACTOR, scaleFactor);
		else
			UIPreferences.getState().remove(KEY_SYSTEM_SCALE_FACTOR);

		System.exit(0);
	}

	/**
	 * TODO: MOVE THIS METHOD
	 * Toggles between light and dark themes using FlatLaf.
	 * Updates the UI immediately and persists the change in preferences.
	 */
	public static void toggleTheme() {
		String currentLaf = UIManager.getLookAndFeel().getClass().getName();
		try {
			if (FlatDarkLaf.class.getName().equals(currentLaf)) {
				FlatLightLaf.setup();
			} else {
				FlatDarkLaf.setup();
			}

			FlatLaf.updateUI();

			UIEvents.fireThemeChanged();

			state.put(KEY_LAF, UIManager.getLookAndFeel().getClass().getName());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
