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

package com.slobodanzivanovic.jewel.coreui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Slobodan Zivanovic
 */
public class EditorWindow extends JPanel {

	public EditorWindow() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1280, 720));

		setDoubleBuffered(true);
		setOpaque(true);

		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
	}
}