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

import com.slobodanzivanovic.jewel.laf.core.JewelDarkLaf
import com.slobodanzivanovic.jewel.laf.core.JewelLafManager
import com.slobodanzivanovic.jewel.ui.EditorWindow
import com.slobodanzivanovic.jewel.util.logging.Logger
import com.slobodanzivanovic.jewel.util.system.SystemInfo
import java.awt.Dimension
import java.io.IOException
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Entry point
 *
 * @author Slobodan Zivanovic
 */
fun main() {
	// Initialize LaF and set up all system properties defined in LaF
	JewelLafManager.setup(JewelDarkLaf())

	SystemInfo.getInstance().logSystemInfo()

	SwingUtilities.invokeLater {
		JFrame("Jewel").apply {
			createBufferStrategy(1)
			add(EditorWindow())
			defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			isResizable = true
			title = "Jewel"
			minimumSize = Dimension(800, 600)
			pack()
			setLocationRelativeTo(null)
			isVisible = true
		}
	}

	try {
		val logger = Logger("main")
		logger.info("Application started")
		logger.info("Max memory: ${Runtime.getRuntime().maxMemory() / 1024 / 1024}MB")

		logger.sessionDirectory.also {
			println("Session directory is: $it")
		}

		logger.logFilePath.also {
			println("Log file is located at: $it")
		}

		Runtime.getRuntime().addShutdownHook(Thread {
			logger.info("Application shutting down")
		})
	} catch (e: IOException) {
		System.err.println("Failed to initialize logger: ${e.message}")
	}
}
