package zinc.doiche.lib.schedule

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import zinc.doiche.ExpandingExtensions.Companion.plugin
import kotlin.coroutines.CoroutineContext

class Scheduler {
    fun runTimer() {
        plugin.launch {

        }
    }

    fun runAsyncTimer(period: Long, delay: Long, task: CoroutineContext.() -> Unit) {
        plugin.launch {
            async {
                delay(delay)
                while (true) {
                    task(this.coroutineContext)
                    delay(period)
                }
            }
        }
    }
}