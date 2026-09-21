package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.jetbrains.annotations.NotNull;

public interface BiomeSourceResourceInitializer
{
    @NotNull BiomeSourceResourceInitializer setValues(ResourceLocation type, ResourceLocation preset);

    @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec);

    @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, ResourceLocation biomeId);

    @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, ResourceKey<MultiNoiseBiomeSourceParameterList> preset);
}