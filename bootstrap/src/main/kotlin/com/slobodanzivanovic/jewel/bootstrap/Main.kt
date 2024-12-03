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

import com.slobodanzivanovic.jewel.ui.EditorWindow
import com.slobodanzivanovic.jewel.util.logging.Logger
import com.slobodanzivanovic.jewel.util.system.SystemInfo
import java.awt.Dimension
import java.io.IOException
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * @author Slobodan Zivanovic
 */
fun main() {
	System.setProperty("sun.java2d.dpiaware", "true")
	System.setProperty("sun.awt.noerasebackground", "true")
	System.setProperty("swing.bufferPerWindow", "true")

	when {
		SystemInfo.IS_MAC -> {
			System.setProperty("apple.awt.application.name", "Jewel")
			System.setProperty("apple.laf.useScreenMenuBar", "true")
			System.setProperty("apple.awt.graphics.UseQuartz", "true")
			System.setProperty("apple.awt.fileDialogForDirectories", "true")
			System.setProperty("apple.awt.fullscreencapturealldisplays", "false")
			System.setProperty("com.apple.mrj.application.live-resize", "false")
			// NOTE: Don't enable Metal renderer as it might cause issues
			System.setProperty("sun.java2d.opengl", "true")
		}

		SystemInfo.IS_WINDOWS -> {
			// NOTE: D3D can sometimes cause issues, using OpenGL instead
			System.setProperty("sun.java2d.opengl", "true")
			System.setProperty("sun.java2d.ddforcevram", "true")
			System.setProperty("sun.java2d.ddscale", "true")
		}

		SystemInfo.IS_LINUX -> {
			System.setProperty("sun.java2d.opengl", "true")
			System.setProperty("sun.java2d.pmoffscreen", "false")
			System.setProperty("awt.useSystemAAFontSettings", "on")
		}
	}

	System.setProperty("swing.aatext", "true")
	System.setProperty("sun.java2d.uiScale.enabled", "true")
	System.setProperty("javax.swing.rebaseCssSizeMap", "true")

	val systemInfo = SystemInfo.getInstance()
	systemInfo.logSystemInfo()

	SwingUtilities.invokeLater {
		JFrame().apply {
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

		val sessionDirectory = logger.sessionDirectory
		println("Session directory is: $sessionDirectory")

		val logFilePath = logger.logFilePath
		println("Log file is located at: $logFilePath")

		Runtime.getRuntime().addShutdownHook(Thread {
			logger.info("Application shutting down")
		})
	} catch (e: IOException) {
		System.err.println("Failed to initialize logger: " + e.message)
	}
}
