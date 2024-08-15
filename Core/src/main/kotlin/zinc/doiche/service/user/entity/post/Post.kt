package zinc.doiche.service.user.entity.post

import org.bukkit.Material
import zinc.doiche.lib.embeddable.DisplayedInfo

class Post(
    val displayedInfo: DisplayedInfo,
    val material: Material,
    val content: String,
) {
    val id: Long? = null
}