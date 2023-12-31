import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    kotlin("jvm") version "1.9.21"

    id("maven-publish")
    id("net.yakclient") version "1.0.3"
}

tasks.wrapper {
    gradleVersion = "8.6-rc-1"
}

group = "net.yakclient.extensions"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        isAllowInsecureProtocol = true
        url = uri("http://maven.yakclient.net/snapshots")
    }
}

dependencies {
    implementation("net.yakclient:client-api:1.0-SNAPSHOT")
    implementation("com.durganmcbroom:artifact-resolver:1.0-SNAPSHOT")
    implementation("com.durganmcbroom:artifact-resolver-simple-maven:1.0-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
}

tasks.named<JavaExec>("launch") {
    if (Os.isFamily(Os.FAMILY_MAC)) {
        jvmArgs(
            "-XstartOnFirstThread",
            "-Xmx2G",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+UseG1GC",
            "-XX:G1NewSizePercent=20",
            "-XX:G1ReservePercent=20",
            "-XX:MaxGCPauseMillis=50",
            "-XX:G1HeapRegionSize=32M"
        )
    }
}

yakclient {
    model {
        groupId.set("net.yakclient.extensions")
        name.set("resource-tweaker")
        version.set("1.0-SNAPSHOT")

        packagingType.set( "jar")
        extensionClass.set("net.yakclient.extensions.resource.tweaker.ResourceTweaker")
    }

    tweakerPartition {
        entrypoint.set("net.yakclient.extensions.resource.tweaker.ResourceEnvironmentTweaker")
        this.dependencies {
            implementation("net.yakclient.components:ext-loader:1.0-SNAPSHOT")
            implementation("net.yakclient:boot:1.1-SNAPSHOT")
            implementation("net.yakclient:archives:1.1-SNAPSHOT")
            implementation("com.durganmcbroom:jobs:1.0-SNAPSHOT")
            implementation("com.durganmcbroom:artifact-resolver-simple-maven:1.0-SNAPSHOT")
            implementation("com.durganmcbroom:artifact-resolver:1.0-SNAPSHOT")
        }
    }

    partitions {
        create("all") {
            this.dependencies {
                implementation(main)
                implementation(tweakerPartition.get().sourceSet.output)
                implementation("net.yakclient:archives:1.1-SNAPSHOT")
                minecraft("1.20.1")
                implementation("net.yakclient:boot:1.0-SNAPSHOT")
                implementation("net.yakclient:common-util:1.0-SNAPSHOT")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
                implementation("net.yakclient:client-api:1.0-SNAPSHOT")
            }

            mappingsType.set("mojang")

            supportedVersions.addAll(listOf("1.20.1", "1.19.2"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("prod") {
            artifact(tasks.jar)
            artifact(tasks.generateErm) {
                classifier = "erm"
            }

            groupId = "net.yakclient.extensions"
            artifactId = "resource-tweaker"
        }
    }

    repositories {
        if (!project.hasProperty("maven-user") || !project.hasProperty("maven-pass")) return@repositories

        maven {
            val repo = if (project.findProperty("isSnapshot") == "true") "snapshots" else "releases"

            isAllowInsecureProtocol = true

            url = uri("http://maven.yakclient.net/$repo")

            credentials {
                username = project.findProperty("maven-user") as String
                password = project.findProperty("maven-pass") as String
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}