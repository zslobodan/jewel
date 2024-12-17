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

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import com.formdev.flatlaf.util.FontUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Manages font configuration and creation for the application.
 * This class handles the setup of default fonts, including Inter for UI elements
 * and JetBrains Mono for editor text.
 *
 * @author Slobodan Zivanovic
 */
public class FontManager {

    /**
     * Cache the base editor font for comparison and reuse.
     * Helps optimize font creation by avoiding unnecessary recreations
     * of the same font instance
     */
    private static Font baseFont;

    public static void setupFonts() {
        FlatInterFont.installLazy();
        FlatJetBrainsMonoFont.installLazy();

        FlatLaf.setPreferredFontFamily(FlatInterFont.FAMILY);
        FlatLaf.setPreferredLightFontFamily(FlatInterFont.FAMILY_LIGHT);
        FlatLaf.setPreferredSemiboldFontFamily(FlatInterFont.FAMILY_SEMIBOLD);
        FlatLaf.setPreferredMonospacedFontFamily(FlatJetBrainsMonoFont.FAMILY);
    }

    public static Font createEditorFont(int sizeIncr) {
        int size = UIManager.getFont("defaultFont").getSize() + sizeIncr;
        Font font = FontUtils.getCompositeFont(FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, size);

        if (baseFont != font) {
            baseFont = font;
        }

        if (isFallbackFont(font)) {
            Font defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, size);
            font = defaultFont.deriveFont((float) size);
        }

        return font;
    }

    private static boolean isFallbackFont(Font font) {
        return Font.DIALOG.equalsIgnoreCase(font.getFamily());
    }
}
