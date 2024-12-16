package buildsrc.convention

import org.panteleyev.jpackage.ImageType
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
//val currentIcon = when {
//    os.contains("windows") -> "$iconDir/jewel.ico"
//    os.contains("mac") -> "$iconDir/jewel.icns"
//    else -> "$iconDir/jewel.png"
//}

tasks.register<Copy>("prepareInstallerFiles") {
	from(tasks.jar)
	from(configurations.runtimeClasspath)
	into(libsDir)
	dependsOn(tasks.jar)
}

tasks.jpackage {
	dependsOn("prepareInstallerFiles")

	input = libsDir.absolutePath
	destination = osDir.absolutePath
	appName = "Jewel"
	mainJar = tasks.jar.get().archiveFileName.get()
	mainClass = "com.slobodanzivanovic.jewel.bootstrap.MainKt"
	appVersion = "1.0.0"
	copyright = "Copyright (C) 2024 Slobodan Zivanovic"
//  licenseFile = "${project.rootDir}/LICENSE"
//  icon = currentIcon

	type = when {
		os.contains("windows") -> ImageType.EXE
		os.contains("mac") -> ImageType.DMG
		else -> ImageType.DEB
	}

	windows {
		winMenu = true
		winShortcut = true
	}

	mac {
		macPackageIdentifier = "com.slobodanzivanovic.jewel"
		macPackageName = "Jewel"
		macPackageSigningPrefix = "com.slobodanzivanovic"
		javaOptions = listOf("-Xdock:name=Jewel")
	}

	linux {
		linuxShortcut = true
	}
}

tasks.register("packageApp") {
	dependsOn("clean", "jpackage")
	group = "application"
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
