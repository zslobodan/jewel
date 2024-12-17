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

import com.slobodanzivanovic.jewel.coreui.components.textarea.JWLTextArea;
import com.slobodanzivanovic.jewel.laf.FontManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Slobodan Zivanovic
 */
public class EditorWindow extends JPanel {

	private final JWLTextArea textArea;
	private final JScrollPane scrollPane;

	public EditorWindow() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1280, 720));
		setDoubleBuffered(true);
		setOpaque(true);

		textArea = createTextArea();
		scrollPane = createScrollPane(textArea);

		add(scrollPane, BorderLayout.CENTER);
	}

	private JWLTextArea createTextArea() {
		JWLTextArea area = new JWLTextArea();

		area.setFont(FontManager.createEditorFont(0));
		area.setTabSize(4);
		area.setHighlightCurrentLine(true);

		area.setLineWrap(false);
		area.setWrapStyleWord(false);

		area.setAutoIndentEnabled(true);

		area.setBackground(UIManager.getColor("TextArea.background"));
		area.setForeground(UIManager.getColor("TextArea.foreground"));
		area.setCaretColor(UIManager.getColor("TextArea.caretColor"));
		area.setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));
		area.setSelectedTextColor(UIManager.getColor("TextArea.selectionForeground"));

		area.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		return area;
	}

	private JScrollPane createScrollPane(JWLTextArea editor) {
		JScrollPane pane = new JScrollPane(editor);

		pane.setBorder(BorderFactory.createEmptyBorder());

		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		return pane;
	}

	/**
	 * Gets the text area component.
	 *
	 * @return The text area component
	 */
	public JWLTextArea getTextArea() {
		return textArea;
	}

	/**
	 * Gets the scroll pane containing the text area.
	 *
	 * @return The scroll pane
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
