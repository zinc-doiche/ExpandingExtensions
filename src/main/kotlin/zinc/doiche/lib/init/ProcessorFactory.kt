package zinc.doiche.lib.init

interface ProcessorFactory {
    fun target(target: Target): ProcessorFactory
    fun preProcess(preProcess: (MutableMap<String, Any>) -> Unit): ProcessorFactory
    fun process(processor: (Class<*>) -> Unit): ProcessorFactory
    fun postProcess(postProcess: (Map<String, Any>) -> Unit): ProcessorFactory
    fun create(): Processor

    companion object {
        fun factory(): ProcessorFactory {
            return ProcessorFactoryImpl()
        }
    }

    enum class Target {
        ANNOTATION,
        SUPER
    }
}