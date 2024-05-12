import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import io.papermc.paperweight.userdev.PaperweightUserExtension
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    val kotlinVersion = "1.9.24"

    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion apply false
    kotlin("plugin.noarg") version kotlinVersion apply false
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
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

    extensions.getByType(AllOpenExtension::class).apply {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.Embeddable")
        annotation("jakarta.persistence.MappedSuperclass")
    }

    configurations {
        named("implementation") {
            isCanBeResolved = true
        }
    }

    extensions.getByType(PaperweightUserExtension::class).apply {
        reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
    }

    extensions.getByType(JavaPluginExtension::class).apply {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    dependencies {
        val implementation by configurations
        val compileOnly by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations
        val kapt by configurations
        val paperweight = extensions.getByType(PaperweightUserDependenciesExtension::class)

        if(project.name != "Core") {
            implementation(project(":Core", "runtimeElements"))
        }

        paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
        implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.15.0")
        implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.15.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
        implementation(kotlin("reflect"))
        implementation("org.reflections:reflections:0.9.12")
        compileOnly("com.google.code.gson:gson:2.10.1")
        implementation("org.postgresql:postgresql:42.7.3")
        implementation("redis.clients:jedis:5.1.2")

        implementation("org.hibernate:hibernate-core:6.5.0.Final") {
            exclude(group = "cglib", module = "cglib")
            exclude(group = "asm", module = "asm")
        }
//        implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final") {
//            exclude(group = "org.slf4j", module = "slf4j-api")
//        }
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
        // JUnit Jupiter API and Engine dependencies
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        // This dependency is used to enable more expressive tests
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
            filesMatching("plugin.yml") {
                expand(properties)
            }
        }
        withType<Test> {
            useJUnitPlatform()
        }
    }
}