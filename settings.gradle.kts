rootProject.name = "resource-tweaker"

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.yakclient.net/snapshots")
        }
        gradlePluginPortal()
    }
}