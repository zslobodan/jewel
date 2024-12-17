plugins {
	id("buildsrc.convention.jewel.common-conventions")
	id("buildsrc.convention.jewel.distribution-conventions")
}

dependencies {
	implementation(project(":core-ui"))
	implementation(project(":laf"))
	implementation(project(":util"))
}

application {
	mainClass.set("com.slobodanzivanovic.jewel.bootstrap.MainKt")
	applicationName = rootProject.name
}
