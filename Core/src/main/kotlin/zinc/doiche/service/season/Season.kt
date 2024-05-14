package zinc.doiche.service.season

import zinc.doiche.lib.Configuration
import zinc.doiche.lib.Read
import zinc.doiche.util.toObject
import java.io.File
import java.time.LocalDate

@Configuration("season")
class Season {
    @Read("config.json")
    fun read(file: File) {
        val config = file.toObject(Config::class.java)
        for (rotation in config.rotations) {

        }
    }
}

data class Rotation(
    val world: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
}

data class Config(
    val rotations: List<Rotation>
)