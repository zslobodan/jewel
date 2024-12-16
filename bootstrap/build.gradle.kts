plugins {
	id("buildsrc.convention.jewel.common-conventions")
	id("buildsrc.convention.jewel.distribution-conventions")
}

application {
	mainClass.set("com.slobodanzivanovic.jewel.bootstrap.Main")
}
