package zinc.doiche

class Main: ExpandingExtensions() {
    override suspend fun onLoadAsync() {
        preLoad()
    }

    override suspend fun onEnableAsync() {
        preEnable()
    }

    override suspend fun onDisableAsync() {
        postDisable()
    }
}