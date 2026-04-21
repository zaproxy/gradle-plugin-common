val functionalTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val functionalTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val functionalTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
    extendsFrom(configurations.compileOnly.get())
}

dependencies {
    compileOnly(libs.spotless.plugin)
    implementation(libs.commons.configuration2)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platformLauncher)
    functionalTestImplementation(libs.apiguardian)
}

val functionalTestTask =
    tasks.register<Test>("functionalTest") {
        description = "Runs the functional tests."
        group = "verification"
        testClassesDirs = functionalTest.output.classesDirs
        classpath = functionalTest.runtimeClasspath
        mustRunAfter(tasks.test)
        dependsOn(createFunctionalTestClasspathManifest)
    }

val createFunctionalTestClasspathManifest =
    tasks.register<Task>("createFunctionalTestClasspathManifest") {
        description = "Creates a manifest file with the plugin classpath."
        group = "build"
        val outputDir = layout.buildDirectory.dir("resources/functionalTest")
        inputs.files(functionalTest.runtimeClasspath)
        outputs.dir(outputDir)
        doLast {
            val dir = outputDir.get().asFile
            dir.mkdirs()
            file("$dir/pluginClasspath.txt").writeText(
                functionalTest.runtimeClasspath.joinToString("\n"),
            )
        }
    }

tasks.check {
    dependsOn(functionalTestTask)
}

gradlePlugin {
    plugins {
        create("zapCommon") {
            id = "org.zaproxy.common"
            implementationClass = "org.zaproxy.gradle.common.CommonPlugin"
            displayName = "Plugin for common ZAP build-related configs and tasks"
            description = "A Gradle plugin for common ZAP build-related configs and tasks."
            tags.set(listOf("zap", "zaproxy"))
        }
    }
    testSourceSets(functionalTest)
}
