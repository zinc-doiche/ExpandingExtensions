package zinc.doiche.service.item.entity.reward

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import zinc.doiche.lib.Translatable
import zinc.doiche.lib.TranslationRegistry
import zinc.doiche.service.item.entity.ItemData
import zinc.doiche.service.item.entity.reward.ItemMessages.MESSAGE_INVENTORY_FULL
import zinc.doiche.service.user.entity.post.Post
import zinc.doiche.util.LoggerUtil.prefixed

class ItemReward(
    val itemData: ItemData? = null,
    post: Post? = null,
    amount: Int = 1
): PostReward(post, amount) {

    override fun giveReward(player: Player) {
        val inventory = player.inventory

        itemData?.let {
            val item = it.getItem(this.amount)
            inventory.addItem(item)
        }?.takeIf {
            it.isNotEmpty()
        }?.run {
            val remain = get(0) ?: return
            remain.amount = this@ItemReward.amount - remain.amount
            inventory.removeItem(remain)

            player.sendMessage(prefixed(MESSAGE_INVENTORY_FULL))
        }
    }
}

@TranslationRegistry
object ItemMessages {
    @Translatable(key = "message.inventory.full", defaultValue = "<red>인벤토리에 빈 공간이 부족해요.")
    lateinit var MESSAGE_INVENTORY_FULL: Component
}