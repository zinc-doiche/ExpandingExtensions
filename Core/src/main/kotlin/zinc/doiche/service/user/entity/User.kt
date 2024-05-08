package zinc.doiche.service.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Transient
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import zinc.doiche.lib.embeddable.Period
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
