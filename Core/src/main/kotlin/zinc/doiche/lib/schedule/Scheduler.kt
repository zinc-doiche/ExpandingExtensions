package zinc.doiche.lib.schedule

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.*
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.launchAsync
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext

class Scheduler {
    fun runTimer() {
        plugin.launch {

        }
    }

    fun runAsyncTimer(repeatTime: Int, delay: Long, task: CoroutineContext.(count: Int) -> Unit) {
        plugin.launchAsync {
            repeat(repeatTime) { i ->
                while (true) {
                    task(this.coroutineContext, i)
                    delay(delay)
                }
            }
        }
    }

    fun runAsyncEveryDayAt(runAt: LocalTime, task: CoroutineContext.() -> Unit) {
        plugin.launchAsync {
            val current = LocalDateTime.now()
            val runTime = runAt.atDate(current.toLocalDate())

            if (current.isBefore(runTime)) {
                current.until(runTime, ChronoUnit.MILLIS)
            } else {
                current.plusDays(1).until(runTime, ChronoUnit.MILLIS)
            }.let { delay ->
                delay(delay)
            }
            task.invoke(this.coroutineContext)
        }
    }
}