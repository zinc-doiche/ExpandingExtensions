package zinc.doiche.service.world.`object`

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_EXTENSION_WORLD")
class ExtensionWorld(
    @Embedded
    val displayedInfo: DisplayedInfo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @OneToMany(mappedBy = "extensionWorld", cascade = [CascadeType.ALL], orphanRemoval = true)
    val accessLevels: MutableList<AccessLevel> = mutableListOf()
}