gradlePlugin {
    plugins {
        create("zapCommonSettings") {
            id = "org.zaproxy.common.settings"
            implementationClass = "org.zaproxy.gradle.common.CommonSettingsPlugin"
            displayName = "Plugin for common ZAP build-related settings"
            description = "A Gradle plugin for common ZAP build-related settings."
            tags.set(listOf("zap", "zaproxy"))
        }
    }
}
