plugins {
    application
    id("io.ktor.plugin") version "2.3.12"
}

repositories {
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-network:2.3.12")
}