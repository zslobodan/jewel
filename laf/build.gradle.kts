plugins {
	id("buildsrc.convention.jewel.common-conventions")
}

dependencies {
	implementation(project(":util"))
	implementation("com.formdev:flatlaf:3.5.2")
}
