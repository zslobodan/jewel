rootProject.name = "jewel"

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

include("bootstrap")
include("core-ui")
include("util")
include("laf")
