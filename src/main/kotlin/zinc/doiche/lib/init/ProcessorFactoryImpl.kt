package zinc.doiche.lib.init

class ProcessorFactoryImpl<T> internal constructor(): ProcessorFactory<T> {
    private var preProcess: () -> T? = { null }
    private var processor: (Class<*>, T?) -> Unit = { _, _ -> }
    private var postProcess: (T?) -> Unit = {}

    override fun preProcess(preProcess: () -> T?): ProcessorFactory<T> {
        this.preProcess = preProcess
        return this
    }

    override fun process(processor: (Class<*>, T?) -> Unit): ProcessorFactory<T> {
        this.processor = { clazz, preObject -> processor.invoke(clazz, preObject) }
        return this
    }

    override fun postProcess(postProcess: (T?) -> Unit): ProcessorFactory<T> {
        this.postProcess = postProcess
        return this
    }

    override fun create(): Processor<T> {
        return Processor(preProcess, processor, postProcess)
    }
}