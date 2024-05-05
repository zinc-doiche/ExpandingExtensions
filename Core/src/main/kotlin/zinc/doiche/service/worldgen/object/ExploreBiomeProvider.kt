package zinc.doiche.service.worldgen.`object`

import org.bukkit.block.Biome
import org.bukkit.generator.BiomeParameterPoint
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class ExploreBiomeProvider: BiomeProvider() {
    private val biomes = mutableListOf(Biome.PLAINS, Biome.DESERT)

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        return Biome.DESERT
    }

    override fun getBiome(
        worldInfo: WorldInfo,
        x: Int,
        y: Int,
        z: Int,
        biomeParameterPoint: BiomeParameterPoint
    ): Biome {
        return Biome.PLAINS
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return biomes
    }
}