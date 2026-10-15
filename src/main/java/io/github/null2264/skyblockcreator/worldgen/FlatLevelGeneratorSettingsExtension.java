package io.github.null2264.skyblockcreator.worldgen;

import net.minecraft.world.level.biome.BiomeSource;

public interface FlatLevelGeneratorSettingsExtension {
    BiomeSource skyblockcreator$getBiomeSource();
    void skyblockcreator$setBiomeSource(BiomeSource source);
}
