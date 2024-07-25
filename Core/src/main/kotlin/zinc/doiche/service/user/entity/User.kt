package zinc.doiche.service.user.entity

import jakarta.persistence.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import zinc.doiche.lib.embeddable.Period
import zinc.doiche.service.world.entity.UserAccessLevelAuthentication
import java.util.*

@Entity
@Table(name = "TBL_USER")
class User(
    @Column(unique = true)
    val uuid: UUID,

    @Embedded
    val levelHolder: LevelHolder = LevelHolder(),

    @Embedded
    val period: Period = Period()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCESS_LEVEL_AUTHENTICATION_ID")
    val userAccessLevelAuthentication: UserAccessLevelAuthentication? = null

    @get:Transient
    val player: Player?
        get() = Bukkit.getPlayer(uuid)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "User(uuid=$uuid, levelHolder=$levelHolder, period=$period, id=$id)"
    }
}
