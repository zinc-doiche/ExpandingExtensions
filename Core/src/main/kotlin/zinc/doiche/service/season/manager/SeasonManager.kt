package zinc.doiche.service.season.manager

import zinc.doiche.lib.Configuration
import zinc.doiche.lib.Read
import zinc.doiche.service.season.SeasonService
import zinc.doiche.service.world.ExtensionWorldService
import zinc.doiche.service.world.entity.ExtensionWorld
import zinc.doiche.util.toObject
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration.Companion.days

@Configuration("season")
class SeasonManager internal constructor() {
    val rotations: MutableList<Rotation> = mutableListOf()
    lateinit var configuration: Config
        private set

    fun addRotation(rotation: Rotation) {
        rotations.add(rotation)
    }

    fun removeRotation(rotation: Rotation) {
        rotations.remove(rotation)
    }

    @Read("config.json")
    private fun read(file: File) {
        val config = file.toObject(Config::class.java)
        val current = LocalDateTime.now()
        val currentDate = current.toLocalDate();
        val firstInitDateTime = LocalDateTime.of(LocalDate.now(), config.initTime)

        configuration = config

        if(current.isBefore(firstInitDateTime)) {
            current.until(firstInitDateTime, ChronoUnit.MILLIS)
        } else {
            current.until(firstInitDateTime.plusDays(1), ChronoUnit.MILLIS)
        }.let { delay ->
            runTimer(delay, config)
        }

        config.rotations.forEach { rotation ->
            ExtensionWorldService.repository.findByName(rotation.worldName)?.let {
                rotation.worldId = it.id
            }
            if(rotation.startDate.isBefore(currentDate) && rotation.endDate.isAfter(currentDate)) {
                addRotation(rotation)
            }
        }
    }

    private fun runTimer(delay: Long, config: Config) = fixedRateTimer(
        name = "Season",
        daemon = false,
        initialDelay = delay,
        period = 1.days.inWholeMilliseconds
    ) {
        val currentDate = LocalDate.now()

        for (rotation in config.rotations) {
            val endDateNext = rotation.endDate.plusDays(1)

            if(endDateNext.isEqual(currentDate)) {
                removeRotation(rotation)
                continue
            }
            if(rotation.startDate.isEqual(currentDate)) {
                addRotation(rotation)
            }
        }
    }
}

data class Rotation(
    val worldName: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    @Transient
    var worldId: Long? = null
        set(value) {
            if(field != null) {
                throw IllegalStateException("WorldId is already set.")
            }
            field = value
        }

    val world: ExtensionWorld?
        get() = worldId?.let {
            ExtensionWorldService.repository.findById(it)
        }

    fun getRemainTime(): Pair<ChronoUnit, Long> {
        val currentDateTime = LocalDateTime.now()
        val initTime = SeasonService.seasonManager.configuration.initTime
        val endDateTime = LocalDateTime.of(endDate.plusDays(1), initTime)

        val deltaDays = ChronoUnit.DAYS.between(currentDateTime, endDateTime)
        if(deltaDays > 0) {
            return Pair(ChronoUnit.DAYS, deltaDays)
        }

        val deltaHours = ChronoUnit.HOURS.between(currentDateTime, endDateTime)
        if(deltaHours > 0) {
            return Pair(ChronoUnit.HOURS, deltaHours)
        }

        val deltaMinutes = ChronoUnit.MINUTES.between(currentDateTime, endDateTime)
        if (deltaMinutes > 0) {
            return Pair(ChronoUnit.MINUTES, deltaMinutes)
        }

        val deltaSeconds = ChronoUnit.SECONDS.between(currentDateTime, endDateTime)
        return Pair(ChronoUnit.SECONDS, deltaSeconds)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rotation

        if (worldName != other.worldName) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = worldName.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        return result
    }
}

data class Config(
    val initTime: LocalTime,
    val rotations: List<Rotation>
)



