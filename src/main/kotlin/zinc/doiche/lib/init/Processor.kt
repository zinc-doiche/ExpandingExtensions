package zinc.doiche.lib.init

class Processor(
    val target: ProcessorFactory.Target,
    val preProcess: (MutableMap<String, Any>) -> Unit,
    val process: (Class<*>) -> Unit,
    val postProcess: (Map<String, Any>) -> Unit
) {
    fun process(path: String) {
        try {
            val clazz = when(target) {
                ProcessorFactory.Target.ANNOTATION -> Class.forName(path.substring(0, path.lastIndexOf('.')))
                ProcessorFactory.Target.SUPER -> Class.forName(path.substring(0, path.lastIndexOf('.')))
            }
            process.invoke(clazz)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e);
        }
    }
}