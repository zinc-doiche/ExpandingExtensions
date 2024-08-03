package zinc.doiche.service.worldgen.`object`

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import org.bukkit.HeightMap
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.generator.*
import org.bukkit.util.Vector
import java.util.*
import kotlin.random.asKotlinRandom
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ExploreChunkGenerator: ChunkGenerator() {
    private val populators = mutableListOf<BlockPopulator>()

    init {
        populators.add(object : BlockPopulator() {
            private val maxY = 40
            private val minY = 20

            override fun populate(
                worldInfo: WorldInfo,
                random: Random,
                chunkX: Int,
                chunkZ: Int,
                limitedRegion: LimitedRegion
            ) {
                val blockData = Material.AIR.createBlockData()
                val posList = mutableListOf<Vector>()

                runBlocking {
                    withTimeout(10.seconds) {
                        while (posList.size < 4) {
                            val x = chunkX.shl(4) + random.nextInt(16)
                            val y = random.nextInt(minY, maxY + 1)
                            val z = chunkZ.shl(4) + random.nextInt(16)
                            val newPos = Vector(x, y, z)

                            if(!limitedRegion.getType(x, y, z).isEmpty &&
                                posList.all { pos -> pos.distance(newPos) > 4 }
                            ) {
                                val count = random.nextInt(8) + 1
                                val blockList = mutableListOf<Vector>()
                                val faceList = BlockFace.entries.toList().shuffled(random)
                                val lastPos = newPos.clone()

                                posList.add(newPos)

                                for (face in faceList) {
                                    val vector = Vector(
                                        lastPos.blockX + face.modX,
                                        lastPos.blockY + face.modY,
                                        lastPos.blockZ + face.modZ
                                    )
                                    val type = limitedRegion.getType(vector.blockX, vector.blockY, vector.blockZ)

                                    if(!blockList.contains(vector) && !type.isEmpty) {
                                        lastPos.copy(vector)
                                        blockList.add(vector)
                                        limitedRegion.setBlockData(vector, blockData)
                                    }
                                    if(blockList.size >= count) {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

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
        return populators
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