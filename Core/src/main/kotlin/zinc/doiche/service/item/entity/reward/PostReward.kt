package zinc.doiche.service.item.entity.reward

import org.bukkit.entity.Player
import zinc.doiche.service.user.entity.post.Post

abstract class PostReward(
    val post: Post? = null,
    val amount: Int = 1
) {
    val id: Long? = null

    abstract fun giveReward(player: Player)
}