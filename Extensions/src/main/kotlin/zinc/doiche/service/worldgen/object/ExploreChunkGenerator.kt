package zinc.doiche.service.worldgen.`object`

import org.bukkit.HeightMap
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class ExploreChunkGenerator: ChunkGenerator() {
    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        chunkData.apply {
            forXYZ { x, y, z ->
                val biome = chunkData.getBiome(x, y, z)
                if(y <= biome.ordinal) {
                    setBlock(x, y, z, Material.GREEN_CONCRETE)
                }
            }
        }
    }

    override fun generateSurface(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        super.generateSurface(worldInfo, random, chunkX, chunkZ, chunkData)
    }

    override fun generateBedrock(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        super.generateBedrock(worldInfo, random, chunkX, chunkZ, chunkData)
    }

    override fun generateCaves(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        super.generateCaves(worldInfo, random, chunkX, chunkZ, chunkData)
    }

    override fun getDefaultBiomeProvider(worldInfo: WorldInfo): BiomeProvider? {
        return super.getDefaultBiomeProvider(worldInfo)
    }

    override fun getBaseHeight(worldInfo: WorldInfo, random: Random, x: Int, z: Int, heightMap: HeightMap): Int {
        return super.getBaseHeight(worldInfo, random, x, z, heightMap)
    }

    override fun canSpawn(world: World, x: Int, z: Int): Boolean {
        return super.canSpawn(world, x, z)
    }

    override fun getDefaultPopulators(world: World): MutableList<BlockPopulator> {
        return super.getDefaultPopulators(world)
    }

    override fun getFixedSpawnLocation(world: World, random: Random): Location? {
        return super.getFixedSpawnLocation(world, random)
    }

    override fun shouldGenerateNoise(): Boolean {
        return super.shouldGenerateNoise()
    }

    override fun shouldGenerateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return super.shouldGenerateNoise(worldInfo, random, chunkX, chunkZ)
    }

    override fun shouldGenerateSurface(): Boolean {
        return super.shouldGenerateSurface()
    }

    override fun shouldGenerateSurface(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return super.shouldGenerateSurface(worldInfo, random, chunkX, chunkZ)
    }

    override fun shouldGenerateCaves(): Boolean {
        return super.shouldGenerateCaves()
    }

    override fun shouldGenerateCaves(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return super.shouldGenerateCaves(worldInfo, random, chunkX, chunkZ)
    }

    override fun shouldGenerateDecorations(): Boolean {
        return super.shouldGenerateDecorations()
    }

    override fun shouldGenerateDecorations(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return super.shouldGenerateDecorations(worldInfo, random, chunkX, chunkZ)
    }

    override fun shouldGenerateMobs(): Boolean {
        return super.shouldGenerateMobs()
    }

    override fun shouldGenerateMobs(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return super.shouldGenerateMobs(worldInfo, random, chunkX, chunkZ)
    }

    override fun shouldGenerateStructures(): Boolean {
        return super.shouldGenerateStructures()
    }

    override fun shouldGenerateStructures(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int): Boolean {
        return super.shouldGenerateStructures(worldInfo, random, chunkX, chunkZ)
    }

    override fun createVanillaChunkData(world: World, x: Int, z: Int): ChunkData {
        return super.createVanillaChunkData(world, x, z)
    }

    private fun ChunkData.forXYZ(block: (Int, Int, Int) -> Unit) {
        for (y in minHeight until maxHeight) {
            for (x in 0..15) {
                for (z in 0..15) {
                    block(x, y, z)
                }
            }
        }
    }
}