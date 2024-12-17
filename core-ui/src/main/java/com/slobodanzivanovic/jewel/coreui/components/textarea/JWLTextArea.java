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

import com.slobodanzivanovic.jewel.laf.UIPreferences;
import com.slobodanzivanovic.jewel.util.platform.PlatformInfo;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Stack;

/**
 * @author Slobodan Zivanovic
 */
public class JWLTextArea extends JTextArea {

	private static final String[] BRACKETS = {"()", "[]", "{}"};
	private final Stack<Integer> bracketStack = new Stack<>();
	private boolean autoIndentEnabled = true;

	private boolean highlightCurrentLine = true;
	private Color currentLineColor = new Color(255, 255, 170);
	private int currentCaretY;
	private int previousCaretY;

	public JWLTextArea() {
		initialize();
	}

	private void initialize() {
		setTabSize(4);
		disableEmptyBackspaceBeep();

		previousCaretY = currentCaretY = getInsets().top;

		// Custom caret (2px)
		setCaret(new DefaultCaret() {
			@Override
			public void paint(Graphics g) {
				if (isVisible()) {
					try {
						Rectangle2D r = modelToView2D(getDot());
						g.setColor(getCaretColor());
						g.fillRect((int) r.getX(), (int) r.getY(), 2, (int) r.getHeight());
					} catch (BadLocationException e) {
						// Ignore
					}
				}
			}
		});

		// Initial color based on theme
		updateHighlightColorForTheme();

		// Listener for updating color when theme changes
		UIManager.addPropertyChangeListener(e -> {
			if ("lookAndFeel".equals(e.getPropertyName())) {
				updateHighlightColorForTheme();
			}
		});

		addCaretListener(e -> updateCurrentLineHighlight());

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleKeyPress(e);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				handleKeyTyped(e);
			}
		});
	}

	// TODO: This is just for now some stuff dont work....
	private void handleKeyPress(KeyEvent e) {
		boolean isCommandKey = PlatformInfo.IS_MAC ? e.isMetaDown() : e.isControlDown();

		if (isCommandKey) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_D -> {
					e.consume();
					handleDuplicateLine(e);
				}
				case KeyEvent.VK_SLASH -> {
					e.consume();
					handleToggleComment(e);
				}
				case KeyEvent.VK_BACK_SPACE -> {
					e.consume();
					handleDeleteWord(e);
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
			handleEnterKey(e);
		} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
			e.consume();
			handleTab(e);
		}
	}

	private void handleKeyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		if (isOpenBracket(c)) {
			handleOpenBracket(e, c);
		} else if (isCloseBracket(c)) {
			handleCloseBracket(e, c);
		}
	}

	private String getIndentString(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}

	private int getIndentLevel(String line) {
		int count = 0;
		for (char c : line.toCharArray()) {
			if (c == '\t') count++;
			else break;
		}
		return count;
	}

	private void handleEnterKey(KeyEvent e) {
		if (!autoIndentEnabled) return;

		try {
			e.consume();
			int caretPos = getCaretPosition();
			int lineNum = getLineOfOffset(caretPos);
			int lineStart = getLineStartOffset(lineNum);
			String currentLine = getText(lineStart, getLineEndOffset(lineNum) - lineStart);

			// Calculate the base indent level from the current line
			int currentIndentLevel = getIndentLevel(currentLine);

			// Get the text before and after the caret on the current line
			String textBeforeCaret = currentLine.substring(0, caretPos - lineStart);
			String textAfterCaret = currentLine.substring(caretPos - lineStart);

			// Trim for checking braces but keep original indentation
			String trimmedBefore = textBeforeCaret.trim();
			String trimmedAfter = textAfterCaret.trim();

			StringBuilder newLine = new StringBuilder("\n");

			if (trimmedBefore.endsWith("{")) {
				if (trimmedAfter.startsWith("}")) {
					// Case: cursor is between braces
					// Add two lines: one indented, one with closing brace
					String indent = getIndentString(currentIndentLevel);
					String innerIndent = getIndentString(currentIndentLevel + 1);
					newLine.append(innerIndent).append("\n").append(indent);
					insert(newLine.toString(), caretPos);
					setCaretPosition(caretPos + innerIndent.length() + 1); // +1 for the newline
				} else {
					// Case: cursor is after opening brace
					String newIndent = getIndentString(currentIndentLevel + 1);
					newLine.append(newIndent);
					insert(newLine.toString(), caretPos);
					setCaretPosition(caretPos + newLine.length());
				}
			} else if (trimmedAfter.startsWith("}")) {
				// Case: cursor is before closing brace
				// Maintain the proper indent level for the closing brace
				String indent = getIndentString(Math.max(0, currentIndentLevel - 1));
				newLine.append(indent);
				insert(newLine.toString(), caretPos);
				setCaretPosition(caretPos + newLine.length());
			} else {
				// Normal case: maintain current indent level
				String indent = getIndentString(currentIndentLevel);
				newLine.append(indent);
				insert(newLine.toString(), caretPos);
				setCaretPosition(caretPos + newLine.length());
			}

		} catch (BadLocationException ex) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}

	private void handleOpenBracket(KeyEvent e, char c) {
		e.consume();
		int pos = getCaretPosition();
		String closeBracket = getMatchingBracket(c);
		try {
			getDocument().insertString(pos, String.valueOf(c), null);
			getDocument().insertString(pos + 1, closeBracket, null);
			setCaretPosition(pos + 1);
			bracketStack.push(pos);
		} catch (BadLocationException ex) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}

	private void handleCloseBracket(KeyEvent e, char c) {
		if (!bracketStack.isEmpty()) {
			int currentPos = getCaretPosition();

			try {
				char nextChar = getText(currentPos, 1).charAt(0);
				if (nextChar == c) {
					e.consume();
					setCaretPosition(currentPos + 1);
					bracketStack.pop();
				}
			} catch (BadLocationException ex) {
				UIManager.getLookAndFeel().provideErrorFeedback(this);
			}
		}
	}

	private void handleTab(KeyEvent e) {
		e.consume();
		insert("\t", getCaretPosition());
	}

	private void handleDuplicateLine(KeyEvent e) {
		try {
			e.consume();
			int caretPos = getCaretPosition();
			int lineNum = getLineOfOffset(caretPos);
			int lineStart = getLineStartOffset(lineNum);
			int lineEnd = getLineEndOffset(lineNum);
			String lineText = getText(lineStart, lineEnd - lineStart);
			insert(lineText, lineEnd);
		} catch (BadLocationException ex) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}

	// TODO: Just for now....
	private void handleToggleComment(KeyEvent e) {
		try {
			e.consume();
			int caretPos = getCaretPosition();
			int lineNum = getLineOfOffset(caretPos);
			int lineStart = getLineStartOffset(lineNum);
			int lineEnd = getLineEndOffset(lineNum);
			String lineText = getText(lineStart, lineEnd - lineStart);

			// Calculate the relative caret position within the line
			int relativeCaretPos = caretPos - lineStart;

			if (lineText.contains("//")) {
				// Find the position of // in the line
				int commentPos = lineText.indexOf("//");
				// Remove comment from wherever it is in the line
				getDocument().remove(lineStart + commentPos, 2);

				// When uncommenting, stay at same position but adjust if cursor was after comment
				setCaretPosition(caretPos > lineStart + commentPos ? caretPos - 2 : caretPos);
			} else {
				// Add comment at the start of the line
				getDocument().insertString(lineStart, "//", null);

				// Move to next line at same position if possible
				try {
					int nextLineStart = getLineStartOffset(lineNum + 1);
					int nextLineEnd = getLineEndOffset(lineNum + 1);
					if (nextLineEnd - nextLineStart >= relativeCaretPos) {
						setCaretPosition(nextLineStart + relativeCaretPos);
					}
				} catch (BadLocationException ex) {
					// We're on the last line, stay at current position
					setCaretPosition(caretPos + 2);
				}
			}
		} catch (BadLocationException ex) {
			// Silently handle any errors without feedback sound
		}
	}

	private void handleDeleteWord(KeyEvent e) {
		try {
			e.consume();
			int pos = getCaretPosition();
			int start = pos;

			String text = getText();
			while (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) {
				start--;
			}

			getDocument().remove(start, pos - start);
		} catch (BadLocationException ex) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}

	private boolean isOpenBracket(char c) {
		return c == '(' || c == '[' || c == '{';
	}

	private boolean isCloseBracket(char c) {
		return c == ')' || c == ']' || c == '}';
	}

	private String getMatchingBracket(char openBracket) {
		for (String pair : BRACKETS) {
			if (pair.charAt(0) == openBracket) {
				return String.valueOf(pair.charAt(1));
			}
		}
		return "";
	}

	public boolean getHighlightCurrentLine() {
		return highlightCurrentLine;
	}

	public void setHighlightCurrentLine(boolean highlight) {
		if (highlight != highlightCurrentLine) {
			highlightCurrentLine = highlight;
			repaint();
		}
	}

	public Color getCurrentLineColor() {
		return currentLineColor;
	}

	public void setCurrentLineColor(Color color) {
		if (color != null && !color.equals(currentLineColor)) {
			currentLineColor = color;
			repaint();
		}
	}

	private void updateHighlightColorForTheme() {
		String currentLaf = UIPreferences.getState().get(UIPreferences.KEY_LAF, "");

		if (currentLaf.contains("FlatDarkLaf")) {
			setCurrentLineColor(new Color(80, 80, 80));
		} else {
			setCurrentLineColor(new Color(230, 230, 230));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		// Create a copy of the graphics context to avoid modifying the original
		Graphics2D g2d = (Graphics2D) g.create();

		// FIXME: There is bug with selection and current line highlight

		try {
			Rectangle clip = g2d.getClipBounds();

			// Store the current caret state
			boolean caretVisible = getCaret().isVisible();
			// Temporarily hide caret for base painting
			getCaret().setVisible(false);

			// Paint the base component first WITHOUT the current line
			super.paintComponent(g2d);

			// Only paint the highlight if enabled
			if (highlightCurrentLine) {
				int caretPos = getCaretPosition();
				Rectangle2D rect = modelToView2D(caretPos);

				if (rect != null) {
					// Get the current line text before we paint the highlight
					int line = getLineOfOffset(caretPos);
					int start = getLineStartOffset(line);
					int end = getLineEndOffset(line);
					String text = getText(start, end - start);
					FontMetrics fm = g2d.getFontMetrics();

					// Paint the highlight
					g2d.setColor(currentLineColor);
					g2d.fillRect(clip.x, (int) rect.getY(), clip.width, (int) rect.getHeight());

					// Paint the text for this line again over the highlight
					g2d.setColor(getForeground());
					g2d.drawString(text, getInsets().left, (int) rect.getY() + fm.getAscent());
				}
			}

			// Restore caret and paint it
			getCaret().setVisible(caretVisible);
			paintCaret(g2d);

		} catch (BadLocationException e) {
			// Ignore
		} finally {
			g2d.dispose();
		}
	}

	private void paintCaret(Graphics g) {
		if (getCaret() != null && getCaret().isVisible()) {
			getCaret().paint(g);
		}
	}

	protected void updateCurrentLineHighlight() {
		try {
			int offset = getCaretPosition();
			Rectangle2D rect = modelToView2D(offset);
			if (rect != null) {
				currentCaretY = (int) rect.getY();
				if (currentCaretY != previousCaretY) {
					// Repaint the entire visible area to avoid artifacts
					Rectangle visible = getVisibleRect();
					repaint(visible);
					previousCaretY = currentCaretY;
				}
			}
		} catch (BadLocationException e) {
			// Ignore
		}
	}

	// Override this to ensure proper repainting during selection
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// Force immediate validation of the component
		validateContent();
	}

	private void validateContent() {
		revalidate();
		// Request focus if we dont have it
		if (!hasFocus() && isRequestFocusEnabled()) {
			requestFocusInWindow();
		}
	}

	private int getLineHeight() {
		FontMetrics fm = getFontMetrics(getFont());
		return fm.getHeight();
	}

	public void setAutoIndentEnabled(boolean enabled) {
		this.autoIndentEnabled = enabled;
	}

	public boolean isAutoIndentEnabled() {
		return autoIndentEnabled;
	}

	/**
	 * Helper method for disabling beep sound on backspace when there is no text
	 */
	private void disableEmptyBackspaceBeep() {
		Action originalAction = getActionMap().get(DefaultEditorKit.deletePrevCharAction);
		Action wrappedAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Only perform the action if there is text to delete
				if (getDocument().getLength() > 0 && getCaretPosition() > 0) {
					originalAction.actionPerformed(e);
				}
				// Do nothing when empty or at start, preventing the beep
			}
		};
		getActionMap().put(DefaultEditorKit.deletePrevCharAction, wrappedAction);
	}
}
