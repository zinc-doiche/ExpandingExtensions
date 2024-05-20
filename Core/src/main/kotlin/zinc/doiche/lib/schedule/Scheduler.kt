package zinc.doiche.lib.schedule

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.*
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
import kotlin.coroutines.CoroutineContext

fun runTimer(repeatTime: Int, delay: Long, task: CoroutineContext.(count: Int) -> Unit) {
    plugin.launch(Dispatchers.Main) {
        repeat(repeatTime) { i ->
            task(this.coroutineContext, i)
            delay(delay)
        }
    }
}

fun runAsyncTimer(repeatTime: Int, delay: Long, task: CoroutineContext.(count: Int) -> Unit) {
    plugin.launchAsync {
        repeat(repeatTime) { i ->
            task(this.coroutineContext, i)
            delay(delay)
        }
    }
}

suspend fun runTimerInScope(repeatTime: Int, delay: Long, task: (count: Int) -> Unit) {
    repeat(repeatTime) { i ->
        task(i)
        delay(delay)
    }
}
