package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	kotlin("jvm")
}

kotlin {
	jvmToolchain(21)
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	testLogging {
		events(
			TestLogEvent.FAILED,
			TestLogEvent.PASSED,
			TestLogEvent.SKIPPED
		)
	}
}
