package zinc.doiche.service.item.command

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.*
import zinc.doiche.util.LoggerUtil
import zinc.doiche.service.item.ItemDataService
import zinc.doiche.util.append
import zinc.doiche.util.tag
import zinc.doiche.util.toData

@CommandRegistry
class ItemDataCommand {
    @CommandFactory
    fun data() = Commands.literal("itemdata")
        .requiresOp()
        .then(Commands.literal("debug")
            .executesPlayer { _, player ->
                player.inventory.itemInMainHand.let {
                    if(it.isEmpty) {
                        player.sendMessage(LoggerUtil.prefixed("손에 아이템을 들고 있어야 합니다", NamedTextColor.RED))
                    } else {
                        player.sendMessage(it.toString())
                        player.sendMessage(it.tag.toString())
                    }
                }
                Command.SINGLE_SUCCESS
            })
        .then(Commands.literal("get")
            .then(Commands.argument("name", StringArgumentType.string())
                .suggestArguments {
                    ItemDataService.repository.findAllNames()
                }
                .executesPlayer { context, player ->
                    val name = context.getArgument("name", String::class.java)
                    plugin.launch {
                        get(player, name)
                    }
                    Command.SINGLE_SUCCESS
                }
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                    .executesPlayer { context, player ->
                        val name = context.getArgument("name", String::class.java)
                        val amount = context.getArgument("amount", Int::class.java)
                        plugin.launch {
                            get(player, name, amount)
                        }
                        Command.SINGLE_SUCCESS
                    }
                )))
        .then(Commands.literal("add")
            .then(Commands.argument("name", StringArgumentType.string())
                .executesPlayer { context, player ->
                    val name = context.getArgument("name", String::class.java)
                    plugin.launch {
                        ItemDataService.repository.findByName(name)?.let {
                            player.sendMessage(LoggerUtil.prefixed("이미 존재하는 이름입니다: $name", NamedTextColor.RED))
                        } ?: run {
                            val itemInMainHand = player.inventory.itemInMainHand
                            if(itemInMainHand.isEmpty) {
                                player.sendMessage(LoggerUtil.prefixed("손에 아이템을 들고 있어야 합니다", NamedTextColor.RED))
                                return@run
                            }
                            val itemData = itemInMainHand.toData(name)
                            ItemDataService.repository.transaction {
                                save(itemData)
                            }
                            player.sendMessage(LoggerUtil.prefixed("${name}(을)를 추가하였습니다"))
                        }
                    }
                    Command.SINGLE_SUCCESS
                }))
        .then(Commands.literal("list")
            .then(Commands.argument("page", IntegerArgumentType.integer(1))
                .executesPlayer { context, player ->
                    val page = context.getArgument("page", Int::class.java)
                    plugin.launch {
                        list(player, page)
                    }
                    Command.SINGLE_SUCCESS
                })
            .executesPlayer { _, player ->
                plugin.launch {
                    list(player)
                }
                Command.SINGLE_SUCCESS
            })
        .build()

    private fun get(player: Player, name: String, amount: Int = 1) {
        ItemDataService.repository.findByName(name)?.let { itemData ->
            val item = itemData.getItem(amount)
            player.inventory.addItem(item)
            player.sendMessage(LoggerUtil.prefixed("'")
                .append(itemData.displayName())
                .append("' ${item.amount}개를 지급하였습니다."))
        } ?: {
            player.sendMessage(LoggerUtil.prefixed("아이템 데이터를 찾을 수 없습니다: $name", NamedTextColor.RED))
        }
    }

    private fun list(player: Player, page: Int = 1) {
        val pageable = ItemDataService.repository.findByPage(page, 10)
        val list = pageable.content.map { text(" - $it") }
        player.sendMessage(
            LoggerUtil.prefixed("아이템 데이터 목록: ${pageable.page}/${pageable.maxPage}")
            .append(" | ", NamedTextColor.AQUA)
            .append("전체 ${pageable.total}개", NamedTextColor.GRAY)
            .apply {
                list.forEach {
                    appendNewline()
                    append(it)
                }
            })
    }
}