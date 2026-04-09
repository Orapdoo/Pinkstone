plugins {
    id("multiloader-base")
    id("java-library")

    id("net.fabricmc.fabric-loom") version ("1.15.4")
}

base {
    archivesName = "pinkstone-frapi"
}

val configurationCommonModJava: Configuration = configurations.create("commonJava") {
    isCanBeResolved = true
}

val configurationCommonModResources: Configuration = configurations.create("commonResources") {
    isCanBeResolved = true
}

dependencies {
    configurationCommonModJava(project(path = ":common", configuration = "commonMainJava"))
    configurationCommonModJava(project(path = ":common", configuration = "commonApiJava"))
    configurationCommonModJava(project(path = ":common", configuration = "commonBootJava"))

    configurationCommonModResources(project(path = ":common", configuration = "commonMainResources"))
    configurationCommonModResources(project(path = ":common", configuration = "commonApiResources"))
    configurationCommonModResources(project(path = ":common", configuration = "commonBootResources"))
}

sourceSets.apply {
    main {
        compileClasspath += configurationCommonModJava
        runtimeClasspath += configurationCommonModJava
    }
}

loom {
    accessWidenerPath = file("src/main/resources/sodium-frapi.accesswidener")

    mixin {
        useLegacyMixinAp = false
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION}")

    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")

    fun addEmbeddedDependency(dependency: String) {
        implementation(dependency)
        include(dependency)
    }

    // Fabric API modules
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-api-base:1.0.5+4ebb5c083e")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-block-view-api-v2:1.0.39+4ebb5c083e")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-rendering-v1:16.2.7+f4ffd2e53e")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-renderer-api-v1:8.0.1+9c919dacc9")
}

fun exportSourceSetJava(name: String, sourceSet: SourceSet) {
    val configuration = configurations.create("${name}Java") {
        isCanBeResolved = true
        isCanBeConsumed = true
    }

    val compileTask = tasks.getByName<JavaCompile>(sourceSet.compileJavaTaskName)
    artifacts.add(configuration.name, compileTask.destinationDirectory) {
        builtBy(compileTask)
    }
}

fun exportSourceSetResources(name: String, sourceSet: SourceSet) {
    val configuration = configurations.create("${name}Resources") {
        isCanBeResolved = true
        isCanBeConsumed = true
    }

    val compileTask = tasks.getByName<ProcessResources>(sourceSet.processResourcesTaskName)
    compileTask.apply {
        exclude("**/README.txt")
        exclude("/*.accesswidener")
    }

    artifacts.add(configuration.name, compileTask.destinationDir) {
        builtBy(compileTask)
    }
}

// Exports the compiled output of the source set to the named configuration.
fun exportSourceSet(name: String, sourceSet: SourceSet) {
    exportSourceSetJava(name, sourceSet)
    exportSourceSetResources(name, sourceSet)
}

exportSourceSet("frapiMain", sourceSets["main"])
tasks.jar { enabled = false }
