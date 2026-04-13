import net.ltgt.gradle.errorprone.errorprone

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1" apply false

    id("com.diffplug.spotless") version "6.25.0"
    id("net.ltgt.errorprone") version "4.1.0"
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.ltgt.errorprone")

    group = "org.zaproxy.gradle"

    repositories {
        mavenCentral()
    }

    spotless {
        java {
            licenseHeaderFile(rootProject.file("gradle/spotless/license.java"))
            googleJavaFormat("1.25.2").aosp()
        }

        kotlinGradle {
            ktlint()
        }
    }

    project.plugins.withType(JavaPlugin::class) {
        dependencies {
            "errorprone"("com.google.errorprone:error_prone_core:2.36.0")
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs = listOf("-Xlint:all", "-Xlint:-path", "-Xlint:-options", "-Werror")
        options.errorprone {
            disableAllChecks.set(true)
            error(
                "MissingOverride",
                "WildcardImport",
            )
        }
    }
}

subprojects {
    apply(plugin = "com.gradle.plugin-publish")

    java {
        val javaVersion = JavaVersion.VERSION_17
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    gradlePlugin {
        website.set("https://github.com/zaproxy/gradle-plugin-common")
        vcsUrl.set("https://github.com/zaproxy/gradle-plugin-common.git")
    }
}
