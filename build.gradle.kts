plugins {
    kotlin("jvm") version "1.7.10"

    id("net.yakclient") version "1.0.1"
    kotlin("kapt") version "1.8.10"
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
    add( "kapt", "net.yakclient:yakclient-preprocessor:1.0-SNAPSHOT")
    implementation("net.yakclient:client-api:1.0-SNAPSHOT")
    implementation("com.durganmcbroom:artifact-resolver:1.0-SNAPSHOT")
    implementation("com.durganmcbroom:artifact-resolver-simple-maven:1.0-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
}


tasks.named<JavaExec>("launch") {
    jvmArgs("-XstartOnFirstThread", "-Xmx2G", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC", "-XX:G1NewSizePercent=20", "-XX:G1ReservePercent=20", "-XX:MaxGCPauseMillis=50", "-XX:G1HeapRegionSize=32M")
}

yakclient {
    model {
        groupId = "net.yakclient.extensions"
        name = "resource-tweaker"
        version = "1.0-SNAPSHOT"

        packagingType = "jar"
        extensionClass = "net.yakclient.extensions.resource.tweaker.ResourceTweaker"
    }

    tweakerPartition {
        entrypoint.set("net.yakclient.extensions.resource.tweaker.ResourceEnvironmentTweaker")
        this.dependencies {
            implementation("net.yakclient.components:ext-loader:1.0-SNAPSHOT")
            implementation("net.yakclient:boot:1.0-SNAPSHOT")
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
                implementation(tweakerPartition.sourceSet.output)
                implementation("net.yakclient:archives:1.1-SNAPSHOT")
                minecraft("1.20.1")
                add("kaptAll", "net.yakclient:yakclient-preprocessor:1.0-SNAPSHOT")
                implementation("net.yakclient:boot:1.0-SNAPSHOT")
                implementation("net.yakclient:common-util:1.0-SNAPSHOT")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
                implementation("net.yakclient:client-api:1.0-SNAPSHOT")
            }

            supportedVersions.addAll(listOf("1.20.1", "1.19.2"))
        }

    }
}



java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}