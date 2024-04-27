package zinc.doiche.lib.init

class ProcessorFactoryImpl internal constructor(): ProcessorFactory {
    private var target: ProcessorFactory.Target = ProcessorFactory.Target.ANNOTATION
    private var preProcess: (MutableMap<String, Any>) -> Unit = {}
    private var processor: (Class<*>) -> Unit = {}
    private var postProcess: (Map<String, Any>) -> Unit = {}

    override fun target(target: ProcessorFactory.Target): ProcessorFactory {
        this.target = target
        return this
    }

    override fun preProcess(preProcess: (MutableMap<String, Any>) -> Unit): ProcessorFactory {
        this.preProcess = preProcess
        return this
    }

    override fun process(processor: (Class<*>) -> Unit): ProcessorFactory {
        this.processor = processor
        return this
    }

    override fun postProcess(postProcess: (Map<String, Any>) -> Unit): ProcessorFactory {
        this.postProcess = postProcess
        return this
    }

    override fun create(): Processor {
        return Processor(target, preProcess, processor, postProcess)
    }
}