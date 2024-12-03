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

package com.slobodanzivanovic.jewel.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Slobodan Zivanovic
 */
public class EditorWindow extends JPanel {

	private final JTextPane textPane;

	public EditorWindow() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1280, 720));

		setDoubleBuffered(true);
		setOpaque(true);

		textPane = new JTextPane() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width
					<= getParent().getSize().width;
			}
		};

		textPane.setOpaque(true);
		textPane.setDoubleBuffered(true);
		textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		textPane.setBorder(null);
		textPane.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		JScrollPane scrollPane = getJScrollPane();
		scrollPane.getViewport().setScrollMode(
			JViewport.BACKINGSTORE_SCROLL_MODE
		);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);

		Color bgColor = textPane.getBackground();
		scrollPane.getViewport().setBackground(bgColor);
		setBackground(bgColor);

		add(scrollPane, BorderLayout.CENTER);
	}

	private JScrollPane getJScrollPane() {
		JScrollPane scrollPane = new JScrollPane(
			textPane,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		) {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(
					RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY
				);
				super.paint(g2d);
			}
		};

		scrollPane.setDoubleBuffered(true);
		return scrollPane;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
			RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY
		);
		g2d.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON
		);
		super.paintComponent(g2d);
	}
}
