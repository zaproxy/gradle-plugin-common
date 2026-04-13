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
    compileOnly("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("org.apache.commons:commons-configuration2:2.13.0")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    functionalTestImplementation("org.apiguardian:apiguardian-api:1.1.2")
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
