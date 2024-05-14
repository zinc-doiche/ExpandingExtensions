package zinc.doiche.service.worldgen.command

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.async
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.*
import zinc.doiche.service.worldgen.`object`.ExploreBiomeProvider
import zinc.doiche.service.worldgen.`object`.ExploreChunkGenerator
import zinc.doiche.util.LoggerUtil
import kotlin.time.measureTime

@CommandRegistry
class WorldGenCommand {
    @CommandFactory
    fun command() = Commands.literal("worldgen")
        .then(Commands.argument("name", StringArgumentType.string())
            .executesPlayer { context, player ->
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
                Command.SINGLE_SUCCESS
            })
        .build()

    @CommandFactory
    fun goto() = Commands.literal("goto")
        .requiresOp()
        .then(Commands.argument("name", StringArgumentType.string())
            .suggestArguments {
                Bukkit.getWorlds().map { it.name }
            }
            .executesPlayer { context, player ->
                val name = context.getArgument("name", String::class.java)
                val world = Bukkit.getWorld(name)
                if (world == null) {
                    player.sendMessage(LoggerUtil.prefixed("월드를 찾을 수 없습니다.", NamedTextColor.RED))
                    return@executesPlayer Command.SINGLE_SUCCESS
                }
                player.teleportAsync(world.spawnLocation).thenAccept { result ->
                    if(result) {
                        player.sendMessage(LoggerUtil.prefixed("월드로 이동 완료!"))
                    }
                }
                Command.SINGLE_SUCCESS
            })
        .build()
}