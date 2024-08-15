package zinc.doiche.service.user.entity

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import zinc.doiche.lib.embeddable.Period
import zinc.doiche.service.world.entity.UserAccessLevelAuthentication
import java.util.*

class User(
    val uuid: UUID,
    val levelHolder: LevelHolder = LevelHolder(),
    val period: Period = Period()
) {
    val id: Long? = null
    val userAccessLevelAuthentication: UserAccessLevelAuthentication? = null

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
