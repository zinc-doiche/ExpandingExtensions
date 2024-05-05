
configurations.implementation {
    isCanBeResolved = true
}

dependencies {
    implementation(project(":Core"))
}

//configurations {
//    implementation {
//        isCanBeResolved = true
//    }
//}

tasks{
    withType<Jar> {
        dependsOn(":Core:build")
        val path = configurations.named("implementation") {
            map { if (it.isDirectory) it else zipTree(it) }
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(path)
    }
    reobfJar {
        outputJar.set(file("Y:\\home\\minecraft\\20.4\\plugins\\${project.name}-${project.version}.jar"))
    }
}