package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public interface DimensionResourceInitializer
{
    @NotNull DimensionResourceInitializer setChunkGenerator(Consumer<ChunkGeneratorResourceInitializer> chunkGeneratorConsumer);

    @NotNull DimensionResourceInitializer setFromRegistry(ResourceKey<DimensionType> dimensionType);
}