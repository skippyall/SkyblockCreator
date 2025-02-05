package io.github.null2264.skyblockcreator.worldgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StructureChunkGenerator extends ChunkGenerator
{
    public static final Codec<StructureChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.STRING.stable().fieldOf("dimension").forGetter((generator) -> generator.dimension), BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource), Codec.STRING.stable().fieldOf("structure").forGetter((generator) -> generator.structure), BlockPos.CODEC.fieldOf("structureOffset").forGetter((generator) -> generator.structureOffset), BlockPos.CODEC.fieldOf("playerSpawnOffset").forGetter((generator) -> generator.playerSpawnOffset), Identifier.CODEC.optionalFieldOf("fillmentBlock", Identifier.of("minecraft", "air")).stable().forGetter((generator) -> generator.fillmentBlock), Codec.BOOL.optionalFieldOf("enableTopBedrock", false).stable().forGetter((generator) -> generator.enableTopBedrock), Codec.BOOL.optionalFieldOf("enableBottomBedrock", false).stable().forGetter((generator) -> generator.enableBottomBedrock), Codec.BOOL.optionalFieldOf("isBedrockFlat", false).stable().forGetter((generator) -> generator.isBedrockFlat)).apply(instance, instance.stable(StructureChunkGenerator::new)));
    private final String structure;
    private final BlockPos structureOffset;
    private final BlockPos playerSpawnOffset;
    private final Identifier fillmentBlock;
    private final boolean enableTopBedrock;
    private final boolean enableBottomBedrock;
    private final boolean isBedrockFlat;
    private final String dimension;

    public StructureChunkGenerator(String dimension, BiomeSource biomeSource, String structure, BlockPos structureOffset, BlockPos playerSpawnOffset, Identifier fillmentBlock, boolean enableTopBedrock, boolean enableBottomBedrock, boolean isBedrockFlat) {
        super(biomeSource);
        this.structure = structure;
        this.structureOffset = structureOffset;
        this.playerSpawnOffset = playerSpawnOffset;
        this.fillmentBlock = fillmentBlock;
        this.enableTopBedrock = enableTopBedrock;
        this.enableBottomBedrock = enableBottomBedrock;
        this.isBedrockFlat = isBedrockFlat;
        this.dimension = dimension;
    }

    public BlockState getFillmentBlock() {
        return Registries.BLOCK.get(fillmentBlock).getDefaultState();
    }

    public String getStructure() {
        return structure;
    }

    public BlockPos getStructureOffset() {
        return structureOffset;
    }

    public BlockPos getPlayerSpawnOffset() {
        return playerSpawnOffset;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if (!getFillmentBlock().isAir()) {
            int startX = chunk.getPos().getStartX();
            int startZ = chunk.getPos().getStartZ();

            BlockPos.iterate(startX, getMinimumY(), startZ, startX + 15, getWorldHeight(), startZ + 15).forEach(blockPos -> chunk.setBlockState(blockPos, getFillmentBlock(), false));
        }
        if (enableTopBedrock || enableBottomBedrock) buildBedrock(chunk, new Random(RandomSeed.getSeed()));
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getWorldHeight() {
        return 320;
    }

    @Override
    public int getSeaLevel() {
        return 64;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @SuppressWarnings("ConstantConditions")
    private void buildBedrock(Chunk chunk, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();

        int bottomBedrockY = getMinimumY();
        int topBedrockY = this.getWorldHeight() - 1;

        boolean shouldGenerateTopBedrock = topBedrockY + 4 >= 0 && topBedrockY < this.getWorldHeight() && this.enableTopBedrock;
        boolean shouldGenerateBottomBedrock = bottomBedrockY + 4 >= 0 && bottomBedrockY < this.getWorldHeight() && this.enableBottomBedrock;
        if (shouldGenerateTopBedrock || shouldGenerateBottomBedrock) {
            BlockPos.iterate(startX, 0, startZ, startX + 15, 0, startZ + 15).forEach(blockPos -> {
                if (shouldGenerateTopBedrock) {
                    if (isBedrockFlat) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), topBedrockY, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                    } else {
                        for (int o = 0; o < 5; ++o) {
                            if (o <= random.nextInt(5)) {
                                chunk.setBlockState(mutable.set(blockPos.getX(), topBedrockY - o, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                            }
                        }
                    }
                }
                if (shouldGenerateBottomBedrock) {
                    if (isBedrockFlat) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), bottomBedrockY, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                    } else {
                        for (int o = 4; o >= 0; --o) {
                            if (o <= random.nextInt(5)) {
                                chunk.setBlockState(mutable.set(blockPos.getX(), bottomBedrockY + o, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
    }

    @Nullable
    @Override
    public Pair<BlockPos, RegistryEntry<Structure>> locateStructure(ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return null;
    }
}