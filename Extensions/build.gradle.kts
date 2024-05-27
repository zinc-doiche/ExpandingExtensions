
tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
        dependsOn(project(":Core").tasks.named("jar"))
    }

    reobfJar {
        val fileName = "${project.name}-$version"
        val path: String = env.OUTPUT_PATH.value
//        file("Y:\\home\\minecraft\\20.6\\plugins\\.jar")
        val file = file(path.replace("<name>", fileName))

        dependsOn(jar)
        outputJar.set(file)
    }
}