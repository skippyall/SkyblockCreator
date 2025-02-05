import org.ajoberstar.grgit.Grgit
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHReleaseBuilder
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.modrinth.minotaur.TaskModrinthUpload
import com.modrinth.minotaur.request.VersionType

operator fun Project.get(property: String): String {
    return property(property) as String
}

buildscript {
    dependencies {
        classpath("org.kohsuke:github-api:${project.property("github_api_version") as String}")
    }
}

// https://youtrack.jetbrains.com/issue/KTIJ-19369.
@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
plugins {
    id("maven-publish")
    id("fabric-loom") version project.property("loom_version") as String
    id("org.ajoberstar.grgit")
    id("com.matthewprenger.cursegradle")
    id("com.modrinth.minotaur")
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
        options.release.set(16)
    }

    java {
        withSourcesJar()
    }
}

val environment: Map<String, String> = System.getenv()
val versionSplit = (version as String).split("+")

val releaseName = "${versionSplit[0]} (mc${versionSplit[1]})"
val releaseType = versionSplit[0].split("-").let { if(it.isNotEmpty()) if(it[1] == "BETA" || it[1] == "ALPHA") it[1] else "ALPHA" else "RELEASE" }
val releaseFile = "${buildDir}/libs/${base.archivesName.get()}-${version}.jar"
val cfGameVersion = (version as String).split("+")[1].let{ if(it == project["minecraft_version"]) it else "$it-Snapshot"}

fun getChangeLog(): String {
    return "A changelog can be found at https://github.com/null2264/$name/commits/"
}

fun getBranch(): String {
    environment["GITHUB_REF"]?.let { branch ->
        return branch.substring(branch.lastIndexOf("/") + 1)
    }
    val grgit = try {
        extensions.getByName("grgit") as Grgit
    }catch (ignored: Exception) {
        return "unknown"
    }
    val branch = grgit.branch.current().name
    return branch.substring(branch.lastIndexOf("/") + 1)
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

/*
allprojects {
    loom {
        mods {
            named("respro") {
                sourceSet(project(":Respro").sourceSets["main"])
                sourceSet(project(":Respro").sourceSets["test"])
            }
            named("skyblockcreator") {
                sourceSet(project(":").sourceSets["main"])
            }
        }
    }
}
 */

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

// GitHub publishing
task("github") {
    dependsOn(tasks.remapJar)
    group = "upload"

    onlyIf { environment.containsKey("GITHUB_TOKEN") }

    doLast {
        val github = GitHub.connectUsingOAuth(environment["GITHUB_TOKEN"])
        val repository = github.getRepository(environment["GITHUB_REPOSITORY"])

        val releaseBuilder = GHReleaseBuilder(repository, version as String)
        releaseBuilder.name(releaseName)
        releaseBuilder.body(getChangeLog())
        releaseBuilder.commitish(getBranch())

        val ghRelease = releaseBuilder.create()
        ghRelease.uploadAsset(file(releaseFile), "application/java-archive")
    }
}

// CurseForge publishing
curseforge {
    environment["CURSEFORGE_API_KEY"]?.let { apiKey = it }

    project(closureOf<CurseProject> {
        id = project["curseforge_id"]
        changelog = getChangeLog()
        releaseType = this@Build_gradle.releaseType.toLowerCase()
        addGameVersion(cfGameVersion)
        addGameVersion("Fabric")

        mainArtifact(file(releaseFile), closureOf<CurseArtifact> {
            displayName = releaseName
            relations(closureOf<CurseRelation> {
                requiredDependency("fabric-api")
            })
        })

        afterEvaluate {
            uploadTask.dependsOn("remapJar")
        }

    })

    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}

// Modrinth publishing
task<TaskModrinthUpload>("modrinth") {
    dependsOn(tasks.remapJar)
    group = "upload"

    onlyIf {
        environment.containsKey("MODRINTH_TOKEN")
    }
    token = environment["MODRINTH_TOKEN"]

    projectId = project["modrinth_id"]
    changelog = getChangeLog()

    versionNumber = version as String
    versionName = releaseName
    versionType = VersionType.valueOf(releaseType)

    uploadFile = file(releaseFile)

    addGameVersion(project["minecraft_version"])
    addLoader("fabric")
}