package zinc.doiche.service.user.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.annotation.ListenerRegistry
import zinc.doiche.service.user.UserService
import zinc.doiche.service.user.`object`.User
import zinc.doiche.service.user.user
import zinc.doiche.util.transaction

@ListenerRegistry
class UserIOListener: Listener {
    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId

        if(event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return
        }
        val user = UserService.repository.findByUUID(uuid)?.apply {
            period.update()
        } ?: User(uuid).apply {
            transaction {
                UserService.repository.save(this)
            }
        }

        transaction {
            user.levelHolder.addLevel()
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
}