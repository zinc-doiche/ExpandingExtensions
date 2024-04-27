package zinc.doiche.lib.init

class Processor(
    val preProcess: (MutableMap<String, Any>) -> Unit,
    val process: (Class<*>, MutableMap<String, Any>) -> Unit,
    val postProcess: (Map<String, Any>) -> Unit
) {
    fun process(path: String, map: MutableMap<String, Any>) {
        try {
            val clazz = Class.forName(path.substring(0, path.lastIndexOf('.')))
            process.invoke(clazz, map)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e);
        }
    }
}