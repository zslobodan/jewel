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

import com.slobodanzivanovic.jewel.util.system.SystemInfo;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * Manages font configuration for the Look and Feel.
 *
 * @author Slobodan Zivanovic
 */
class JewelFontManager {

    void initializeFonts(UIDefaults defaults) {
        FontUIResource systemFont = getSystemFont();

        defaults.keySet().stream()
                .filter(key -> key instanceof String)
                .filter(key -> ((String) key).endsWith(".font"))
                .forEach(key -> updateFont(defaults, key, systemFont));
    }

    private FontUIResource getSystemFont() {
        if (SystemInfo.IS_WINDOWS) {
            return getWindowsSystemFont();
        } else if (SystemInfo.IS_MAC) {
            return getMacSystemFont();
        }
        return new FontUIResource("Dialog", Font.PLAIN, 12);
    }

    private FontUIResource getWindowsSystemFont() {
        Font windowsFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty("win.messagebox.font");
        return windowsFont != null ? new FontUIResource(windowsFont) : null;
    }

    private FontUIResource getMacSystemFont() {
        Font macFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.application.font");
        return macFont != null ? new FontUIResource(macFont) : null;
    }

    private void updateFont(UIDefaults defaults, Object key, FontUIResource systemFont) {
        if (systemFont == null) return;

        Object value = defaults.get(key);
        if (value instanceof Font originalFont) {
            defaults.put(key, new FontUIResource(
                    systemFont.getFamily(),
                    originalFont.getStyle(),
                    originalFont.getSize()
            ));
        }
    }
}
