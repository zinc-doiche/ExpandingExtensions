package zinc.doiche.service.world.entity

import jakarta.persistence.*
import org.bukkit.block.Biome
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_REGION")
class Region(
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    val biome: Biome,

    @Embedded
    val displayedInfo: DisplayedInfo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}