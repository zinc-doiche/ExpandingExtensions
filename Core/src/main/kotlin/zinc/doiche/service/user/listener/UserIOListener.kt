package zinc.doiche.service.user.listener

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import zinc.doiche.lib.ListenerRegistry
import zinc.doiche.service.user.UserService
import zinc.doiche.service.user.entity.User
import zinc.doiche.service.user.user

@ListenerRegistry(async = true)
class UserIOListener: Listener {
    @EventHandler
    suspend fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId

        if(event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return
        }
        UserService.repository.runCatching {
            val user = findByUUID(uuid)?.apply {
                period.update()
            } ?: User(uuid).apply {
                transaction {
                    save(this@apply)
                }
            }
            saveId(uuid, user.id!!)
        }.onFailure {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Component.text("Data를 불러오는 데 실패했어요."))
        }
    }

    @EventHandler
    suspend fun onJoin(event: PlayerJoinEvent) {

    }

//    @EventHandler
//    fun onJump(event: PlayerJumpEvent) {
//        val player = event.player
//        val user = player.user ?: return
//        val level = user.levelHolder.level
//
//        player.sendMessage("Level: $level")
//    }

    @EventHandler
    suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val user = player.user ?: return
        UserService.repository.run {
            transaction {
                save(user)
                removeId(player.uniqueId)
            }
        }
    }
}