import net.ltgt.gradle.errorprone.errorprone

plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradle.publish) apply false

    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
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
            googleJavaFormat(libs.versions.googleJavaFormat.get()).aosp()
        }

        kotlinGradle {
            ktlint()
        }
    }

    project.plugins.withType(JavaPlugin::class) {
        dependencies {
            "errorprone"(rootProject.libs.errorprone.core)
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
