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
import com.slobodanzivanovic.jewel.util.logging.Logger
import com.slobodanzivanovic.jewel.util.platform.PlatformInfo
import java.awt.Dimension
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

/**
 * @author Slobodan Zivanovic
 */
fun main() {

	when {
		PlatformInfo.IS_MAC -> {
			println(PlatformInfo.getInstance().osName)
		}

		PlatformInfo.IS_WINDOWS -> {
			println(PlatformInfo.getInstance().osName)
		}

		PlatformInfo.IS_LINUX -> {
			println(PlatformInfo.getInstance().osName)
		}
	}

	PlatformInfo.getInstance().logSystemInfo()

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

	try {
		val file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
		val logger = Logger(file)
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
