package io.github.null2264.skyblockcreator.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class BiomeSourceFlatChunkGenerator {
    public static final Codec<FlatLevelSource> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)
            ).apply(instance, instance.stable((biomeSource, settings) -> {
                ((FlatLevelGeneratorSettingsExtension) settings).skyblockcreator$setBiomeSource(biomeSource);
                return new FlatLevelSource(settings);
            }))
    );
}
