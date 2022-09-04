import com.soywiz.korge.gradle.*

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.sample.demo"

// To enable all targets at once

	//targetAll()

// To enable targets based on properties/environment variables
	//targetDefault()

// To selectively enable targets
	
	targetJvm()
	//targetJs()
	//targetDesktop()
	//targetIos()
	//targetAndroidIndirect() // targetAndroidDirect()

	serializationJson()
	//targetAndroidDirect()

    dependencyMulti("com.hiperbou.vm", "vm", "1.0-SNAPSHOT")
    dependencyMulti("com.hiperbou.vm", "conversation", "1.0-SNAPSHOT")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xcontext-receivers", "-Xskip-prerelease-check")
    }
}
