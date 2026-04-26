operator fun Project.get(property: String): String {
    return property(property) as String
}

buildscript {
    dependencies {

    }
}

plugins {
    id("maven-publish")
    id("fabric-loom") version project.property("loom_version") as String
}


group = project["maven_group"]

allprojects {
    version = "${project["mod_version"]}+${project["supported_version"]}"

    apply {
        plugin("fabric-loom")
        plugin("maven-publish")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Shared deps between SkyblockCreator and Respro
    dependencies {
        minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
        mappings("net.fabricmc:yarn:${project["yarn_mappings"]}:v2")

        modImplementation("net.fabricmc:fabric-loader:${project["loader_version"]}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${project["fabric_version"]}")
    }

    tasks.processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        inputs.property("version", project.version)

        from(sourceSets.main.get().resources.srcDirs) {
            include("fabric.mod.json")
            expand(mutableMapOf("version" to project.version))
        }

        from(sourceSets.main.get().resources.srcDirs) {
            exclude("fabric.mod.json")
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/skyblockcreator.accesswidener"))
}

repositories {
    // for server translation
    maven {
        url = uri("https://maven.nucleoid.xyz")
    }
    // for Fabric API
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    mavenLocal()
}

/*allprojects {
    loom {
        mods {
            named("respro") {
                sourceSet(project(":Respro").sourceSets["main"])
            }
            named("skyblockcreator") {
                sourceSet(project(":").sourceSets["main"])
            }
        }
    }
}*/

// Dependencies for SkyblockCreator
dependencies {

    "xyz.nucleoid:server-translations-api:${project["server_translation"]}".apply {
        include(this)
        modImplementation(this)
    }

    ":Respro".apply {
        implementation(project(this, "namedElements"))
        include(project(this))
    }
}

tasks.jar {
    from("COPYING.md")
}