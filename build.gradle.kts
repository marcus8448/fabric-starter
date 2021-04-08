/*
 *
 */

import java.time.format.DateTimeFormatter

val minecraftVersion    = project.property("minecraft.version").toString()
val yarnBuild           = project.property("yarn.build").toString()
val loaderVersion       = project.property("loader.version").toString()

val modVersion          = project.property("mod.version").toString()
val modid               = project.property("mod.id").toString()
val modName             = project.property("mod.name").toString()
val modGroup            = project.property("mod.group").toString()
val modDescription      = project.property("mod.description").toString()

// Dependency Version
val fabricVersion       = project.property("fabric.version").toString()
val modMenuVersion      = project.property("modmenu.version").toString()
val lbaVersion          = project.property("lba.version").toString()
val reiVersion          = project.property("rei.version").toString()

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("0.6-SNAPSHOT")
    id("org.cadixdev.licenser") version("0.5.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = modGroup
version = modVersion

base {
    archivesBaseName = modName
}

loom {
    refmapName = "${modid}.refmap.json"
    if (project.file("src/main/resources/${modid}.accesswidener").exists()) {
        accessWidener = project.file("src/main/resources/${modid}.accesswidener")
    }

    runs {
        create("Client (Mixin)") {
            client()

            vmArgs(listOf(
                    "-ea",
                    "-javaagent:\"/{project.gradle.gradleUserHomeDir}/caches/modules-2/files-2.1/net.fabricmc/sponge-mixin/0.9.2+mixin.0.8.2/12c437eebf031967eaa7daad861e115932772cc7/sponge-mixin-0.9.2+mixin.0.8.2.jar\"",
                    "-server",
                    "-Xmx3G",
                    "-XX:+UseG1GC",
                    "-XX:+ParallelRefProcEnabled",
                    "-XX:MaxGCPauseMillis=200",
                    "-XX:+UnlockExperimentalVMOptions",
                    "-XX:+DisableExplicitGC",
                    "-XX:+AlwaysPreTouch",
                    "-XX:G1NewSizePercent=30",
                    "-XX:G1MaxNewSizePercent=40",
                    "-XX:G1HeapRegionSize=8M",
                    "-XX:G1ReservePercent=20",
                    "-XX:G1HeapWastePercent=5",
                    "-XX:G1MixedGCCountTarget=4",
                    "-XX:InitiatingHeapOccupancyPercent=15",
                    "-XX:G1MixedGCLiveThresholdPercent=90",
                    "-XX:G1RSetUpdatingPauseTimePercent=5",
                    "-XX:SurvivorRatio=32",
                    "-XX:+PerfDisableSharedMem",
                    "-XX:MaxTenuringThreshold=1"))
            property("fabric.log.level", "debug")
            property("mixin.debug.export", "true")

            if (JavaVersion.current() >= JavaVersion.VERSION_14) {
                vmArg("-XX:+ShowCodeDetailsInExceptionMessages")
            }
        }

        create("Server (Mixin)") {
            server()

            vmArgs(listOf(
                    "-ea",
                    "-javaagent:\"/{project.gradle.gradleUserHomeDir}/caches/modules-2/files-2.1/net.fabricmc/sponge-mixin/0.9.2+mixin.0.8.2/12c437eebf031967eaa7daad861e115932772cc7/sponge-mixin-0.9.2+mixin.0.8.2.jar\"",
                    "-server",
                    "-Xmx3G",
                    "-XX:+UseG1GC",
                    "-XX:+ParallelRefProcEnabled",
                    "-XX:MaxGCPauseMillis=200",
                    "-XX:+UnlockExperimentalVMOptions",
                    "-XX:+DisableExplicitGC",
                    "-XX:+AlwaysPreTouch",
                    "-XX:G1NewSizePercent=30",
                    "-XX:G1MaxNewSizePercent=40",
                    "-XX:G1HeapRegionSize=8M",
                    "-XX:G1ReservePercent=20",
                    "-XX:G1HeapWastePercent=5",
                    "-XX:G1MixedGCCountTarget=4",
                    "-XX:InitiatingHeapOccupancyPercent=15",
                    "-XX:G1MixedGCLiveThresholdPercent=90",
                    "-XX:G1RSetUpdatingPauseTimePercent=5",
                    "-XX:SurvivorRatio=32",
                    "-XX:+PerfDisableSharedMem",
                    "-XX:MaxTenuringThreshold=1"))
            property("fabric.log.level", "debug")
            property("mixin.debug.export", "true")

            if (JavaVersion.current() >= JavaVersion.VERSION_14) {
                vmArg("-XX:+ShowCodeDetailsInExceptionMessages")
            }
        }
    }
}

repositories {
    mavenLocal()
    maven("https://maven.shedaniel.me/") {
        content {
            includeGroupByRegex("me\\.shedaniel.*")
        }
    }
    maven("https://maven.terraformersmc.com/") {
        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven("https://alexiil.uk/maven") {
        content {
            includeGroup("alexiil.mc.lib")
        }
    }
}

/**
 * From:
 * @see net.fabricmc.loom.util.FabricApiExtension.getDependencyNotation
 */
fun getFabricApiModule(moduleName: String, fabricApiVersion: String): String {
    return String.format("net.fabricmc.fabric-api:%s:%s", moduleName,
            fabricApi.moduleVersion(moduleName, fabricApiVersion))
}

dependencies {
    // Minecraft, Mappings, Loader
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+build.$yarnBuild:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Fabric Api Modules
    listOf(
            "fabric-api-base"
    ).forEach {
        modImplementation(getFabricApiModule(it, fabricVersion)) { isTransitive = false }
    }

//    include(modApi("alexiil.mc.lib:libblockattributes-core:$lbaVersion") { isTransitive = false })
//    include(modApi("alexiil.mc.lib:libblockattributes-fluids:$lbaVersion") { isTransitive = false })
//    include(modApi("alexiil.mc.lib:libblockattributes-items:$lbaVersion") { isTransitive = false })

    // Optional Dependencies
    modImplementation("com.terraformersmc:modmenu:$modMenuVersion") { isTransitive = false }
    modImplementation("me.shedaniel:RoughlyEnoughItems:$reiVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "org.jetbrains")
    }

    // Other Dependencies
    modRuntime("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
}

tasks.processResources {
    inputs.property("version", modVersion)

    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to modVersion, "modid" to modid, "mod_name" to modName, "mod_description" to modDescription))
    }

    // Minify json resources
    // https://stackoverflow.com/questions/41028030/gradle-minimize-json-resources-in-processresources#41029113
    doLast {
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach {
            file: File -> file.writeText(groovy.json.JsonOutput.toJson(groovy.json.JsonSlurper().parse(file)))
        }
    }
}

java {
    withSourcesJar()
}

tasks.create<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

tasks.jar {
    from("LICENSE")
    manifest {
        attributes(mapOf(
                "Implementation-Title"     to modid,
                "Implementation-Version"   to modVersion,
                "Implementation-Vendor"    to "marcus8448",
                "Implementation-Timestamp" to DateTimeFormatter.ISO_DATE_TIME,
                "Maven-Artifact"           to "$modGroup:$modid:$modVersion"
        ))
    }
}

license {
    header = project.file("LICENSE_HEADER.txt")
    include("**/io/github/marcus8448/**/*.java")
    include("build.gradle.kts")
}
