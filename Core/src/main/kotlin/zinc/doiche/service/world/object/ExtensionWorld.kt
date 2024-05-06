package zinc.doiche.service.world.`object`

import com.google.common.collect.Table
import com.google.common.collect.Tables
import org.bukkit.block.Biome

class ExtensionWorld(
    val name: String,
    val displayName: String,
    val description: Array<String>,
    val accessLevel: Table<Int, Biome, AccessLevel> = Tables.newCustomTable()
) {

}