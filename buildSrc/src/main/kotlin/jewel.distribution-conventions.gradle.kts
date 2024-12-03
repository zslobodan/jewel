package buildsrc.convention

import java.util.*

plugins {
	id("org.panteleyev.jpackageplugin")
	id("application")
}

val rootBuildDir: File = project.rootProject.layout.buildDirectory.get().asFile
val cacheDir = file("$rootBuildDir/cache/jpackage")
val libsDir = file("$cacheDir/libs")

val os = System.getProperty("os.name").lowercase(Locale.getDefault())
val osDir = when {
	os.contains("windows") -> file("$rootBuildDir/windows")
	os.contains("mac") -> file("$rootBuildDir/macos")
	else -> file("$rootBuildDir/linux")
}

//val iconDir = file("${project.rootDir}/buildSrc/src/main/resources/icons")

tasks.register<Copy>("prepareInstallerFiles") {
	from(tasks.jar)
	from(configurations.runtimeClasspath)
	into(libsDir)
	dependsOn(tasks.jar)
}

//val currentIcon = when {
//    os.contains("windows") -> "$iconDir/jewel.ico"
//    os.contains("mac") -> "$iconDir/jewel.icns"
//    else -> "$iconDir/jewel.png"
//}

tasks.register<Exec>("packageApp") {
	dependsOn("prepareInstallerFiles")
	group = "application"

	workingDir = project.projectDir

	val jpackageCmd = "${System.getProperty("java.home")}/bin/jpackage"

	doFirst {
		osDir.mkdirs()
	}

	commandLine(
		jpackageCmd,
		"--input", libsDir.absolutePath,
		"--dest", osDir.absolutePath,
		"--name", "Jewel",
		"--main-jar", tasks.jar.get().archiveFileName.get(),
		"--main-class", "com.slobodanzivanovic.jewel.bootstrap.Main",
		"--app-version", "1.0.0",
		"--copyright", "Copyright (C) 2024 Slobodan Zivanovic",
//		"--license-file", "${project.rootDir}/LICENSE",
//		"--icon", currentIcon,
		"--type", when {
			os.contains("windows") -> "exe"
			os.contains("mac") -> "dmg"
			else -> "deb"
		}
	)

	doFirst {
		when {
			os.contains("windows") -> {
				commandLine.addAll(
					listOf(
						"--win-menu",
						"--win-shortcut"
					)
				)
			}

			os.contains("mac") -> {
				commandLine.addAll(
					listOf(
						"--mac-package-identifier", "com.slobodanzivanovic.jewel",
						"--mac-package-name", "Jewel",
						"--mac-package-signing-prefix", "com.slobodanzivanovic",
						"--java-options", "-Xdock:name=Jewel"
					)
				)
			}

			else -> {
				commandLine.addAll(
					listOf(
						"--linux-shortcut"
					)
				)
			}
		}
	}
}

tasks.register<Delete>("cleanDistribution") {
	delete(cacheDir)
	delete(osDir)
}

tasks.named("clean") {
	dependsOn("cleanDistribution")
}

tasks.named("distZip") { dependsOn("prepareInstallerFiles") }
tasks.named("distTar") { dependsOn("prepareInstallerFiles") }
tasks.named("startScripts") { dependsOn("prepareInstallerFiles") }
