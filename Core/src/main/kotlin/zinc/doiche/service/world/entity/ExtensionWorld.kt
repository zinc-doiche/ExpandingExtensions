package zinc.doiche.service.world.entity

import zinc.doiche.lib.embeddable.DisplayedInfo

class ExtensionWorld(
    val displayedInfo: DisplayedInfo,
    val customModelData: Int = 0
) {
    val id: Long? = null

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "extensionWorld", cascade = [CascadeType.ALL], orphanRemoval = true)
//    val accessLevels: MutableList<AccessLevel> = mutableListOf()

}