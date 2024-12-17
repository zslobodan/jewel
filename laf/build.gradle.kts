plugins {
	id("buildsrc.convention.jewel.common-conventions")
}

dependencies {
	implementation(project(":util"))
	implementation("com.formdev:flatlaf:3.5.2")
	implementation("com.formdev:flatlaf-extras:3.5.2")
	implementation("com.formdev:flatlaf-fonts-inter:4.0")
	implementation("com.formdev:flatlaf-fonts-jetbrains-mono:2.304")
}
