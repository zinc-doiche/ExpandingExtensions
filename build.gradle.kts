plugins {
    kotlin("kapt") version "1.9.10"
    kotlin("plugin.jpa") version "1.9.10"
    kotlin("plugin.allopen") version "1.9.10"
    kotlin("plugin.noarg") version "1.9.10"
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.12"
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
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
//    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final") {
//        exclude(group = "org.slf4j", module = "slf4j-api")
//    }
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.3")
    implementation("org.hibernate:hibernate-jcache:6.5.0.Final")
    implementation("org.ehcache:ehcache:3.10.0")
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("com.querydsl:querydsl-core:5.0.0")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
//    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")
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
}

//의존성 탐색하도록 설정(duplicatesStrategy 설정시 필요)
configurations.implementation.configure {
    isCanBeResolved = true
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.implementation.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    javadoc {
        options.encoding = "UTF-8" // We want UTF-8 for everything
    }
    assemble {
        dependsOn(reobfJar)
    }
    reobfJar {
        outputJar.set(file("Y:\\home\\minecraft\\20.4\\plugins\\${project.name}-${project.version}.jar"))
    }
    test {
        useJUnitPlatform()
    }
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources/META-INF")
        }
    }
}

kapt {
    arguments {
        arg("querydsl.jpa", true)
    }
}
