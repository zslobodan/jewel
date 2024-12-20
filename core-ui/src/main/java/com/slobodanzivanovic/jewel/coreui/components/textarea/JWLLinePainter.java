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

package com.slobodanzivanovic.jewel.coreui.components.textarea;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 * @author Slobodan Zivanovic
 *
 * <a href="https://github.com/tips4java/tips4java/blob/main/source/LinePainter.java">LinePainter</a>
 */
public class JWLLinePainter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
	private final JTextComponent component;
	private Color color;
	private Rectangle lastView = new Rectangle(0, 0, 5, 5);

	public JWLLinePainter(JTextComponent component, Color color) {
		this.component = component;
		setColor(color);

		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
	}

	public void setColor(Color color) {
		if (this.color != color) {
			this.color = color;
			component.repaint();
		}
	}

	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			Rectangle2D r = c.modelToView2D(c.getCaretPosition());
			g.setColor(color);
			g.fillRect(0, (int) r.getY(), c.getWidth(), (int) r.getHeight());
			if (lastView == null) {
				lastView = new Rectangle(0, (int) r.getY(), 5, (int) r.getHeight());
			}
		} catch (BadLocationException ignored) {
		}
	}

	private void resetHighlight() {
		// Use invokeLater to make sure updates to the Document are completed
		SwingUtilities.invokeLater(() -> {
			try {
				int offset = component.getCaretPosition();
				Rectangle2D currentView = component.modelToView2D(offset);
				// Remove highlighting from previously highlighted line
				if (lastView.y != (int) currentView.getY()) {
					component.repaint(0, lastView.y, component.getWidth(), lastView.height);
					lastView = new Rectangle(0, (int) currentView.getY(), 5, (int) currentView.getHeight());
				}
			} catch (BadLocationException ignored) {
			}
		});
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		resetHighlight();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		resetHighlight();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		resetHighlight();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
