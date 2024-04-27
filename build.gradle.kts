plugins {
    kotlin("kapt") version "1.9.10"
    kotlin("plugin.allopen") version "1.9.10"
    kotlin("plugin.noarg") version "1.9.10"
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.10"
    id("xyz.jpenilla.run-paper") version "2.2.2" // Adds runServer task for testing
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

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.postgresql:postgresql:42.7.0")
    implementation("redis.clients:jedis:5.1.2")
    implementation(kotlin("reflect"))

//    implementation("jakarta.persistence:jakarta.persistence-api:2.2.3")
    implementation("org.hibernate:hibernate-core:5.6.3.Final") {
        exclude(group = "cglib", module = "cglib")
        exclude(group = "asm", module = "asm")
    }
    implementation("org.hibernate:hibernate-ehcache:5.6.3.Final")
    implementation("org.hibernate.validator:hibernate-validator:7.0.2.Final") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.hibernate:hibernate-c3p0:5.6.3.Final")
    compileOnly("com.querydsl:querydsl-apt:5.0.0")
    compileOnly("com.querydsl:querydsl-jpa:5.0.0")

    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
//    annotationProcessor("com.querydsl.apt.jpa.JPAAnnotationProcessor")
}

//의존성 탐색하도록 설정(duplicatesStrategy 설정시 필요)
configurations.implementation.configure {
    isCanBeResolved = true
}

tasks {
    reobfJar {
        outputJar = file("build/libs/${project.name}-${project.version}.jar")
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.implementation.get()
//            .filter{
////                println(it.name)
//                !it.name.contains("kommand") && !it.name.contains("kotlin-")
//            }
            .map { if (it.isDirectory) it else zipTree(it) })
    }
    java {
//        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    compileJava {
        options.encoding = "UTF-8" // We want UTF-8 for everything
//        options.release.set(17)
    }
    javadoc {
        options.encoding = "UTF-8" // We want UTF-8 for everything
    }
    assemble {
        dependsOn(reobfJar)
    }
}

kapt {
    arguments {
        arg("querydsl.jpa", true)
    }
}