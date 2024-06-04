package zinc.doiche.service.user.entity.reward

import jakarta.persistence.*
import org.bukkit.entity.Player
import zinc.doiche.service.user.entity.Post

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class PostReward(
    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    val post: Post? = null,

    val amount: Int = 1
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    abstract fun giveReward(player: Player)
}