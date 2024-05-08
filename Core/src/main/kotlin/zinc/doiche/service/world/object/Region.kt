package zinc.doiche.service.world.`object`

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.bukkit.block.Biome
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
class Region(
    @Column(unique = true, nullable = false)
    val biome: Biome,

    @Embedded
    val displayedInfo: DisplayedInfo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}