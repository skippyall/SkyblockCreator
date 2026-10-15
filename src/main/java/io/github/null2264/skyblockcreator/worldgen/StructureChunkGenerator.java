package io.github.null2264.skyblockcreator.worldgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureChunkGenerator extends ChunkGenerator
{
    public static final Codec<StructureChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fillmentBlock", Blocks.AIR).stable().forGetter((generator) -> generator.fillmentBlock),
            Codec.BOOL.optionalFieldOf("enableTopBedrock", false).stable().forGetter((generator) -> generator.enableTopBedrock),
            Codec.BOOL.optionalFieldOf("enableBottomBedrock", false).stable().forGetter((generator) -> generator.enableBottomBedrock),
            Codec.BOOL.optionalFieldOf("isBedrockFlat", false).stable().forGetter((generator) -> generator.isBedrockFlat)
    ).apply(instance, instance.stable(StructureChunkGenerator::new)));

    private final Block fillmentBlock;
    private final boolean enableTopBedrock;
    private final boolean enableBottomBedrock;
    private final boolean isBedrockFlat;

    public StructureChunkGenerator(BiomeSource biomeSource, Block fillmentBlock, boolean enableTopBedrock, boolean enableBottomBedrock, boolean isBedrockFlat) {
        super(biomeSource);
        this.fillmentBlock = fillmentBlock;
        this.enableTopBedrock = enableTopBedrock;
        this.enableBottomBedrock = enableBottomBedrock;
        this.isBedrockFlat = isBedrockFlat;
    }

    public BlockState getFillmentBlock() {
        return fillmentBlock.defaultBlockState();
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk) {
        if (!getFillmentBlock().isAir()) {
            int startX = chunk.getPos().getMinBlockX();
            int startZ = chunk.getPos().getMinBlockZ();

            BlockPos.betweenClosed(startX, getMinY(), startZ, startX + 15, getGenDepth(), startZ + 15).forEach(blockPos -> chunk.setBlockState(blockPos, getFillmentBlock(), false));
        }
        if (enableTopBedrock || enableBottomBedrock) buildBedrock(chunk, new Random(RandomSupport.generateUniqueSeed()));
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getGenDepth() {
        return 320;
    }

    @Override
    public int getSeaLevel() {
        return 64;
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @SuppressWarnings("ConstantConditions")
    private void buildBedrock(ChunkAccess chunk, Random random) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();

        int bottomBedrockY = getMinY();
        int topBedrockY = this.getGenDepth() - 1;

        boolean shouldGenerateTopBedrock = topBedrockY + 4 >= 0 && topBedrockY < this.getGenDepth() && this.enableTopBedrock;
        boolean shouldGenerateBottomBedrock = bottomBedrockY + 4 >= 0 && bottomBedrockY < this.getGenDepth() && this.enableBottomBedrock;
        if (shouldGenerateTopBedrock || shouldGenerateBottomBedrock) {
            BlockPos.betweenClosed(startX, 0, startZ, startX + 15, 0, startZ + 15).forEach(blockPos -> {
                if (shouldGenerateTopBedrock) {
                    if (isBedrockFlat) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), topBedrockY, blockPos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                    } else {
                        for (int o = 0; o < 5; ++o) {
                            if (o <= random.nextInt(5)) {
                                chunk.setBlockState(mutable.set(blockPos.getX(), topBedrockY - o, blockPos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                            }
                        }
                    }
                }
                if (shouldGenerateBottomBedrock) {
                    if (isBedrockFlat) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), bottomBedrockY, blockPos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                    } else {
                        for (int o = 4; o >= 0; --o) {
                            if (o <= random.nextInt(5)) {
                                chunk.setBlockState(mutable.set(blockPos.getX(), bottomBedrockY + o, blockPos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState noiseConfig) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos) {
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager biomeAccess, StructureManager structureAccessor, ChunkAccess chunk, GenerationStep.Carving carverStep) {
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
    }

    @Nullable
    @Override
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel world, HolderSet<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return null;
    }
}