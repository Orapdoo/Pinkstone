plugins {
    id("multiloader-platform")

    id("net.fabricmc.fabric-loom") version ("1.15.4")
}

base {
    archivesName = "pinkstone-fabric"
}

val configurationApiModJava: Configuration = configurations.create("apiJava") {
    isCanBeResolved = true
}

val configurationCommonModJava: Configuration = configurations.create("commonJava") {
    isCanBeResolved = true
}

val configurationFrapiModJava: Configuration = configurations.create("frapiJava") {
    isCanBeResolved = true
}

val configurationApiModSources: Configuration = configurations.create("apiSources") {
    isCanBeResolved = true
}

val configurationCommonModResources: Configuration = configurations.create("commonResources") {
    isCanBeResolved = true
}

val configurationFrapiModResources: Configuration = configurations.create("frapiResources") {
    isCanBeResolved = true
}

dependencies {
    configurationCommonModJava(project(path = ":common", configuration = "commonMainJava"))
    configurationApiModJava(project(path = ":common", configuration = "commonApiJava"))
    configurationCommonModJava(project(path = ":common", configuration = "commonBootJava"))
    if (BuildConfig.SUPPORT_FRAPI) configurationFrapiModJava(project(path = ":frapi", configuration = "frapiMainJava"))

    configurationApiModSources(project(path = ":common", configuration = "commonApiSources"))

    configurationCommonModResources(project(path = ":common", configuration = "commonMainResources"))
    configurationCommonModResources(project(path = ":common", configuration = "commonApiResources"))
    configurationCommonModResources(project(path = ":common", configuration = "commonBootResources"))
    if (BuildConfig.SUPPORT_FRAPI) configurationFrapiModResources(project(path = ":frapi", configuration = "frapiMainResources"))
}

sourceSets.apply {
    main {
        compileClasspath += configurationCommonModJava
        compileClasspath += configurationApiModJava
        runtimeClasspath += configurationCommonModJava
        runtimeClasspath += configurationApiModJava
        if (BuildConfig.SUPPORT_FRAPI) {
            runtimeClasspath += configurationFrapiModJava
        }
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

    if (BuildConfig.SUPPORT_FRAPI) {
        addEmbeddedDependency("net.fabricmc.fabric-api:fabric-renderer-api-v1:8.0.1+9c919dacc9")
    }

    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-lifecycle-events-v1:2.6.15+81748cc8f1")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-rendering-fluids-v1:3.1.43+4ebb5c083e")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-resource-loader-v0:3.3.4+4fc5413f3e")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-resource-loader-v1:1.0.10+81748cc8f1")
    addEmbeddedDependency("net.fabricmc.fabric-api:fabric-transitive-access-wideners-v1:7.0.7+81748cc8f1")
}

loom {
    accessWidenerPath.set(file("src/main/resources/sodium-fabric.accesswidener"))

    mixin {
        useLegacyMixinAp = false
    }

    runs {
        named("client") {
            client()
            configName = "Fabric/Client"
            appendProjectPathToConfigName = false
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks {
    jar {
        from(configurationCommonModJava)
        from(configurationApiModJava)
        if (BuildConfig.SUPPORT_FRAPI) {
            from(configurationFrapiModJava)
        }
    }

    val apiJar = register<org.gradle.jvm.tasks.Jar>("apiJar") {
        archiveClassifier.set("api")
        from(configurationApiModJava)
        from(sourceSets.main.get().resources)
        destinationDirectory.set(file(rootProject.layout.buildDirectory).resolve("api"))
    }

    val apiSourcesJar = register<org.gradle.jvm.tasks.Jar>("apiSourcesJar") {
        archiveClassifier.set("api-sources")
        from(configurationApiModSources)
        from(sourceSets.main.get().resources)
        destinationDirectory.set(file(rootProject.layout.buildDirectory).resolve("api-sources"))
    }

    jar {
        destinationDirectory.set(file(rootProject.layout.buildDirectory).resolve("mods"))
    }

    processResources {
        from(configurationCommonModResources)
        if (BuildConfig.SUPPORT_FRAPI) {
            from(configurationFrapiModResources)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = rootProject.name + "-" + project.name
            version = version

            from(components["java"])
        }

        create<MavenPublication>("mavenApi") {
            groupId = project.group as String
            artifactId = rootProject.name + "-" + project.name + "-api"
            version = version

            artifact(tasks.named("apiJar")) {
                classifier = null
            }

            artifact(tasks.named("apiSourcesJar")) {
                classifier = "sources"
            }

            pom.packaging = "jar"
        }
    }
}