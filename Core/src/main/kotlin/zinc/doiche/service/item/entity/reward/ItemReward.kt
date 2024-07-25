package zinc.doiche.service.item.entity.reward

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import zinc.doiche.lib.Translatable
import zinc.doiche.lib.TranslationRegistry
import zinc.doiche.service.item.entity.ItemData
import zinc.doiche.service.user.entity.post.Post
import zinc.doiche.util.LoggerUtil.prefixed

@Entity
@Table(name = "TBL_ITEM_REWARD")
class ItemReward(
    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    val itemData: ItemData? = null,

    post: Post? = null,

    amount: Int = 1

): PostReward(post, amount) {

    @TranslationRegistry
    companion object {
        @Translatable(key = "message.inventory.full", defaultValue = ["<red>인벤토리에 빈 공간이 부족해요."])
        val MESSAGE_INVENTORY_FULL: Component = Component.empty()
    }

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