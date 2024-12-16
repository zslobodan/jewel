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

package com.slobodanzivanovic.jewel.bootstrap

import com.slobodanzivanovic.jewel.coreui.EditorWindow
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

/**
 * @author Slobodan Zivanovic
 */
fun main() {

	SwingUtilities.invokeLater {
		JFrame().apply {
			createBufferStrategy(1)
			add(EditorWindow())
			defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
			isResizable = true
			title = "Jewel"
			minimumSize = Dimension(800, 600)
			pack()
			setLocationRelativeTo(null)
			isVisible = true
		}
	}
}
