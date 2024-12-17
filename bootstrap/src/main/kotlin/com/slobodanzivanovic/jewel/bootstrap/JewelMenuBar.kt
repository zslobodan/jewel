package com.slobodanzivanovic.jewel.bootstrap

import com.slobodanzivanovic.jewel.laf.UIPreferences
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.KeyStroke

// NOTE: JUST FOR NOW
class JewelMenuBar : JMenuBar() {
	init {
		add(createViewMenu())
	}

	private fun createViewMenu(): JMenu {
		val viewMenu = JMenu("View").apply {
			mnemonic = KeyEvent.VK_V
		}

		val toggleThemeItem = JMenuItem("Toggle Theme").apply {
			accelerator = KeyStroke.getKeyStroke(
				KeyEvent.VK_T,
				InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK
			)
			addActionListener {
				UIPreferences.toggleTheme()
			}
		}

		viewMenu.add(toggleThemeItem)
		return viewMenu
	}
}
