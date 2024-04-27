package zinc.doiche.lib.init

class ProcessorFactoryImpl internal constructor(): ProcessorFactory {
    private var preProcess: (MutableMap<String, Any>) -> Unit = {}
    private var processor: (Class<*>, MutableMap<String, Any>) -> Unit = { _, _ -> }
    private var postProcess: (Map<String, Any>) -> Unit = {}

    override fun preProcess(preProcess: (MutableMap<String, Any>) -> Unit): ProcessorFactory {
        this.preProcess = preProcess
        return this
    }

    override fun process(processor: (Class<*>) -> Unit): ProcessorFactory {
        this.processor = { clazz, _ -> processor.invoke(clazz) }
        return this
    }

    override fun process(processor: (Class<*>, MutableMap<String, Any>) -> Unit): ProcessorFactory {
        this.processor = processor
        return this
    }

    override fun postProcess(postProcess: (Map<String, Any>) -> Unit): ProcessorFactory {
        this.postProcess = postProcess
        return this
    }

    override fun create(): Processor {
        return Processor(preProcess, processor, postProcess)
    }
}