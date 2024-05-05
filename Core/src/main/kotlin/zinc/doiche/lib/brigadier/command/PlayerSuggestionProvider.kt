package zinc.doiche.lib.brigadier.command

import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

fun interface PlayerSuggestionProvider {
    @Throws(CommandSyntaxException::class)
    fun getSuggestions(player: Player, builder: SuggestionsBuilder): CompletableFuture<Suggestions>
}
