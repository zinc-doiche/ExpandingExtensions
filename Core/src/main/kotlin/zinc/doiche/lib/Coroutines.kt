package zinc.doiche.lib

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import zinc.doiche.ExpandingExtensions
import zinc.doiche.ExpandingExtensions.Companion.plugin

fun ExpandingExtensions.launchAsync(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    task: suspend CoroutineScope.() -> Unit
) {
    plugin.launch(plugin.asyncDispatcher, start, task)
}