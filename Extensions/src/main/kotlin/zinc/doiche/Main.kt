package zinc.doiche

import zinc.doiche.util.LoggerUtil

class Main: ExpandingExtensions() {
    override suspend fun onEnableAsync() {
        super.onEnableAsync()
        LoggerUtil.prefixedInfo("Extensions Type")
    }
}