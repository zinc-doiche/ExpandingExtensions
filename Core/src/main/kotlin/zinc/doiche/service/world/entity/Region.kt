package zinc.doiche.service.world.entity

import org.bukkit.block.Biome
import zinc.doiche.lib.embeddable.DisplayedInfo

class Region(
    val biome: Biome,
    val displayedInfo: DisplayedInfo
) {
    val id: Long? = null

    val accessLevel: AccessLevel? = null
}