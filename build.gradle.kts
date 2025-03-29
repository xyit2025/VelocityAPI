import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    java
    eclipse
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("xyz.jpenilla.run-velocity") version "2.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.bulefire"
version = "1.0-release"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    implementation("io.javalin:javalin:5.5.0")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")

}

val targetJavaVersion = 17
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks {
    runVelocity {
        // Configure the Velocity version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        velocityVersion("3.3.0-SNAPSHOT")
    }

    named<ShadowJar>("shadowJar"){
        archiveClassifier.set("shadow")
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates by tasks.registering(Copy::class) {
    val props = mapOf("version" to project.version)
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets.main.get().java.srcDir(generateTemplates.map { it.outputs })

idea {
    project {
        settings {
            taskTriggers {
                afterSync(generateTemplates)
            }
        }
    }
}

eclipse {
    synchronizationTasks(generateTemplates)
}

tasks.withType<Test> {
    useJUnitPlatform()
}