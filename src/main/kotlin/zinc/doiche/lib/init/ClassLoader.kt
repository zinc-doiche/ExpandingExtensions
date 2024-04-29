package zinc.doiche.lib.init

import zinc.doiche.Main.Companion.plugin
import java.io.File
import java.nio.file.Paths
import java.util.jar.JarFile

class ClassLoader {
    private val processors = ArrayList<Processor>()

    fun add(processorFactory: (ProcessorFactory) -> ProcessorFactory): ClassLoader {
        val factory = processorFactory.invoke(ProcessorFactory.factory())
        return add(factory.create())
    }

    fun add(processor: Processor): ClassLoader {
        processors.add(processor)
        return this
    }

    fun process() {
        val map = mutableMapOf<String, Any>()
        val pathes = getAllPath("zinc.doiche")
        pathes.forEach { _ -> processors.forEach { it.preProcess.invoke(map) } }
        pathes.forEach { path -> processors.forEach { it.process(path, map) } }
        pathes.forEach { _ -> processors.forEach { it.postProcess.invoke(map) } }
    }

    private fun getAllPath(packageName: String): List<String> {
        val list = mutableListOf<String>()
        searchInProject { jarFile ->
            jarFile.versionedStream().forEach { jarEntry ->
                val path = jarEntry.toString().replace('/', '.')
                if(!path.startsWith(packageName) && !path.endsWith(".class")) {
                    return@forEach
                }
                list.add(path)
            }
        }
        return list
    }

    companion object {
        internal fun searchInProject(block: (JarFile) -> Unit) {
            val folder = File(Paths.get("./plugins/").toUri());
            val files = folder.listFiles() ?: return;
            for (file in files) {
                if(!file.name.contains(plugin.name) || !file.name.endsWith(".jar")) {
                    continue
                }
                JarFile(file).use(block)
            }
        }
    }
}
