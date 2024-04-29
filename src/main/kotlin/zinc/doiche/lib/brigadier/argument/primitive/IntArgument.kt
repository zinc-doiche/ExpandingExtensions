package zinc.doiche.lib.brigadier.argument.primitive

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import zinc.doiche.lib.brigadier.argument.RequiredArgument


class IntArgument(name: String) : RequiredArgument<Int>(name) {
    private val bound = intArrayOf(Int.MIN_VALUE, Int.MAX_VALUE)

    constructor(name: String, min: Int, max: Int) : this(name) {
        bound[0] = min
        bound[1] = max
    }

    override fun argument(): RequiredArgumentBuilder<CommandSourceStack, Int> {
        return RequiredArgumentBuilder.argument(
            this.name, IntegerArgumentType.integer(
                bound[0], bound[1]
            )
        )
    }
}