package zinc.doiche.service.world.`object`

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
class AccessLevel(
    @Embedded
    val displayedInfo: DisplayedInfo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extensionWorldId")
    val extensionWorld: ExtensionWorld? = null

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    val regions: MutableList<Region> = mutableListOf()
}