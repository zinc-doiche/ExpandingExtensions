package zinc.doiche.service.user

import org.bukkit.entity.Player
import zinc.doiche.service.user.`object`.User

internal val Player.user: User?
    get() = UserService.repository.findByUUID(uniqueId)