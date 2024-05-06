package zinc.doiche.service.worldgen.command

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.async
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.CommandFactory
import zinc.doiche.lib.CommandRegistry
import zinc.doiche.lib.brigadier.CommandBuilder
import zinc.doiche.service.worldgen.`object`.ExploreBiomeProvider
import zinc.doiche.service.worldgen.`object`.ExploreChunkGenerator
import zinc.doiche.util.LoggerUtil
import kotlin.time.measureTime

@CommandRegistry
class WorldGenCommand {
    @CommandFactory
    fun command() = CommandBuilder.literal("worldgen")
        .then(CommandBuilder.string("name")
            .executesAsPlayer { context, player ->
                val name = context.getArgument("name", String::class.java)
                val provider = ExploreBiomeProvider()
                val generator = ExploreChunkGenerator()
                plugin.launch {
                    async {
                        player.sendMessage(LoggerUtil.prefixed("월드 생성 중..."))
                        val duration = measureTime {
                            WorldCreator.name(name)
//                                .biomeProvider(provider)
                                .generator(generator)
                                .generateStructures(true)
                                .createWorld()
                        }.toComponents { seconds, nanoseconds ->
                            val nano = nanoseconds.toString().substring(0, 1)
                            "$seconds.${nano}s"
                        }
                        player.sendMessage(LoggerUtil.prefixed("월드 생성 완료! (${duration} 소요됨)"))
                    }
                }
                CommandBuilder.SINGLE_SUCCESS
            })
        .build()

    @CommandFactory
    fun goto() = CommandBuilder.literal("goto")
        .requiresOp()
        .then(CommandBuilder.string("name")
            .suggestArguments {
                Bukkit.getWorlds().map { it.name }
            }
            .executesAsPlayer { context, player ->
                val name = context.getArgument("name", String::class.java)
                val world = Bukkit.getWorld(name)
                if (world == null) {
                    player.sendMessage(LoggerUtil.prefixed("월드를 찾을 수 없습니다.", NamedTextColor.RED))
                    return@executesAsPlayer CommandBuilder.SINGLE_SUCCESS
                }
                player.teleportAsync(world.spawnLocation).thenAccept { result ->
                    if(result) {
                        player.sendMessage(LoggerUtil.prefixed("월드로 이동 완료!"))
                    }
                }
                CommandBuilder.SINGLE_SUCCESS
            })
        .build()
}