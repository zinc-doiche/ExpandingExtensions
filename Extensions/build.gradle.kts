
tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
        dependsOn(project(":Core").tasks.named("jar"))
    }

    reobfJar {
        dependsOn(jar)
//        inputJar.set(jar.get().archiveFile)
        outputJar.set(file("Y:\\home\\minecraft\\20.6\\plugins\\${project.name}-$version.jar"))
    }
}