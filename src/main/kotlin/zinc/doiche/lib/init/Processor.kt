package zinc.doiche.lib.init

class Processor<T>(
    val preProcess: () -> T?,
    private val process: (Class<*>, T?) -> Unit,
    private val postProcess: (T?) -> Unit
) {
    fun process(path: String, preObject: Any?) {
        try {
            val clazz = Class.forName(path.substring(0, path.lastIndexOf('.')))
            process.invoke(clazz, preObject(preObject))
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e);
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun preObject(any: Any?): T? = any.runCatching {
        this as T?
    }.getOrNull()

    fun postProcess(preObject: Any?) {
        postProcess.invoke(preObject(preObject))
    }
}