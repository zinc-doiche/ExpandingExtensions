package zinc.doiche.service.world.entity

import zinc.doiche.lib.embeddable.DisplayedInfo

class AccessLevel(
    val displayedInfo: DisplayedInfo,
    val level: Int
) {
    val id: Long? = null
    val extensionWorld: ExtensionWorld? = null

//    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
//    val regions: MutableList<Region> = mutableListOf()
}