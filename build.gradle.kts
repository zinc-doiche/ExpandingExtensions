import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import io.papermc.paperweight.userdev.PaperweightUserExtension
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.hidetake.groovy.ssh.connection.AllowAnyHosts
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    val kotlinVersion = "1.9.24"

    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion apply false
    kotlin("plugin.noarg") version kotlinVersion apply false
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
    id("org.hidetake.ssh") version "2.11.2" apply true
    id("com.github.johnrengelman.shadow") version "8.1.1" apply true
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.kapt")
        plugin("org.jetbrains.kotlin.plugin.jpa")
        plugin("org.jetbrains.kotlin.plugin.allopen")
        plugin("org.jetbrains.kotlin.plugin.noarg")
        plugin("io.papermc.paperweight.userdev")
        plugin("org.hidetake.ssh")
        plugin("com.github.johnrengelman.shadow")
    }

    group = "zinc.doiche"
    version = "1.0"
    description = "Vanilla+ RPG Plugin"

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://repo.maven.apache.org/maven2/")
        }
    }

    with(extensions) {
        configure(AllOpenExtension::class) {
            annotation("jakarta.persistence.Entity")
            annotation("jakarta.persistence.Embeddable")
            annotation("jakarta.persistence.MappedSuperclass")
        }
        configure(PaperweightUserExtension::class) {
            reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
        }
        configure(JavaPluginExtension::class) {
            toolchain.languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        val implementation by configurations
        val compileOnly by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations
        val kapt by configurations
        val paperweight = extensions.getByType(PaperweightUserDependenciesExtension::class)

        paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

//        Coroutines ==========================================================================
        implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.18.0")
        implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.18.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

//        Reflections ==========================================================================
        compileOnly(kotlin("reflect"))
        implementation("org.reflections:reflections:0.9.12")

//        Database ==========================================================================
        compileOnly("com.google.code.gson:gson:2.10.1")
        implementation("redis.clients:jedis:5.1.2")
        implementation("org.postgresql:postgresql:42.7.3")
        implementation("org.hibernate:hibernate-core:6.5.0.Final") {
            exclude(group = "cglib", module = "cglib")
            exclude(group = "asm", module = "asm")
        }
        implementation("org.hibernate:hibernate-jcache:6.5.0.Final")
        implementation("org.ehcache:ehcache:3.10.0")
        implementation("com.zaxxer:HikariCP:5.1.0")
        implementation("com.querydsl:querydsl-core:5.0.0")
        implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
        implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
        implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
        implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
        kapt("jakarta.persistence:jakarta.persistence-api:3.1.0")
        kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

        implementation("com.fasterxml.jackson.core:jackson-core:2.16.2")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.16.2")

//        Test ==========================================================================
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

        testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
        testImplementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-test:2.15.0")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
        withType<Javadoc> {
            options.encoding = "UTF-8"
        }
        withType<ProcessResources> {
            filteringCharset = "UTF-8" // We want UTF-8 for everything
            val properties = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.20"
            )
            inputs.properties(properties)
        }
        withType<Test> {
            useJUnitPlatform()
        }
    }
}

applyTo(
    "Extensions",
    "Port",
    "Proxy"
) {
    val server = Remote(
        mapOf<String, Any>(
            "host" to project.property("host") as String,
            "port" to (project.property("port") as String).toInt(),
            "user" to project.property("user") as String,
            "password" to project.property("password") as String,
            "knownHosts" to AllowAnyHosts.instance
        )
    )

    tasks.create(name = "deploy") {

        dependsOn("shadowJar")

        doLast {
            ssh.run(delegateClosureOf<RunHandler> {
                session(server, delegateClosureOf<SessionHandler> {
                    val file = "$projectDir/build/libs/${project.name}-${project.version}-all.jar"
                    val directory = "/home/minecraft/${rootProject.name}/${project.name}/plugins"

                    put(
                        hashMapOf(
                            "from" to file,
                            "into" to directory
                        )
                    )
                })
            })
        }
    }
}

fun Project.applyTo(vararg projects: String, action: Project.() -> Unit) {
    projects.map {
        project(":$it")
    }.forEach { project ->
        action.invoke(project)
    }
}