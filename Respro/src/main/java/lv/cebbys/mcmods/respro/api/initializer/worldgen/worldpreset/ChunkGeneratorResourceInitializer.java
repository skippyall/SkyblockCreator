package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public interface ChunkGeneratorResourceInitializer
{
    @NotNull ChunkGeneratorResourceInitializer setBiomeSource(Consumer<BiomeSourceResourceInitializer> biomeSourceConsumer);

    @NotNull ChunkGeneratorResourceInitializer setStructureChunkValues(BlockPos playerSpawnOffset, BlockPos structureOffset, String structure, ResourceLocation fillmentBlock, boolean enableTopBedrock, boolean enableBottomBedrock, boolean isBedrockFlat);

    @NotNull ChunkGeneratorResourceInitializer setFromCodec(Codec<? extends ChunkGenerator> chunkGeneratorCodec);

    @NotNull ChunkGeneratorResourceInitializer setFromCodec(Codec<? extends ChunkGenerator> chunkGeneratorCodec, ResourceKey<NoiseGeneratorSettings> settings);
}