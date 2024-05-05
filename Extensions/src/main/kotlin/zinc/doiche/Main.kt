package zinc.doiche

import zinc.doiche.util.LoggerUtil

class Main: ExpandingExtensions() {
    override suspend fun onLoadAsync() {
        super.onLoadAsync()
    }

    override suspend fun onEnableAsync() {
        LoggerUtil.prefixedInfo("Version: Extension")
        super.onEnableAsync()
    }

    override suspend fun onDisableAsync() {
        super.onDisableAsync()
    }
}