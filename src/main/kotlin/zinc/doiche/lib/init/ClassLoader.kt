package zinc.doiche.lib.init

import zinc.doiche.Main.Companion.plugin
import java.io.File
import java.nio.file.Paths
import java.util.jar.JarFile

class ClassLoader {
    private val processors = ArrayList<Processor<*>>()

    fun <T> add(processorFactory: (ProcessorFactory<T>) -> ProcessorFactory<T>): ClassLoader {
        val factory = processorFactory.invoke(ProcessorFactory.factory())
        return add(factory.create())
    }

    fun add(processor: Processor<*>): ClassLoader {
        processors.add(processor)
        return this
    }

    fun process() {
        val objectList = mutableListOf<Any?>()

        processors.forEach {
            val preObject = it.preProcess()
            objectList.add(preObject)
        }
        getAllPath("zinc.doiche").forEach { path ->
            processors.forEachIndexed { index, processor ->
                val preObject = objectList[index]
                processor.process(path, preObject)
            }
        }
        processors.forEachIndexed { index, processor ->
            val preObject = objectList[index]
            processor.postProcess(preObject)
        }
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
