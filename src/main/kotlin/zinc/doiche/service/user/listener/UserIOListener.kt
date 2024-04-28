package zinc.doiche.service.user.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.annotation.ListenerRegistry
import zinc.doiche.service.user.UserService
import zinc.doiche.service.user.`object`.User
import zinc.doiche.service.user.user
import zinc.doiche.util.transaction
import kotlin.time.measureTime

@ListenerRegistry
class UserIOListener: Listener {
    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId

        if(event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return
        }
        UserService.repository.run {
            val user = findByUUID(uuid)?.apply {
                period.update()
            } ?: User(uuid).apply {
                transaction {
                    save(this)
                }
            }
            transaction {
                user.levelHolder.addLevel()
                saveID(uuid, user.id!!)
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {}

    @EventHandler
    fun onJump(event: PlayerJumpEvent) {
        val player = event.player
        val user = player.user ?: return
        val level = user.levelHolder.level

        player.sendMessage("Level: $level")
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val user = player.user ?: return
        transaction {
            UserService.repository.run {
                save(user)
                removeID(player.uniqueId)
            }
        }
    }
}