package io.github.null2264.skyblockcreator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;

public class StructureDimensionConfig {
    public static final Codec<StructureDimensionConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION_TYPE).fieldOf("type").forGetter(StructureDimensionConfig::getDimensionType),
            ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(StructureDimensionConfig::getBiomeIdentifier),
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("fillmentBlock", Blocks.AIR).forGetter(StructureDimensionConfig::getFillmentBlock),
            Codec.BOOL.optionalFieldOf("topBedrockEnabled", false).forGetter(StructureDimensionConfig::isTopBedrockEnabled),
            Codec.BOOL.optionalFieldOf("bottomBedrockEnabled", false).forGetter(StructureDimensionConfig::isBottomBedrockEnabled),
            Codec.BOOL.optionalFieldOf("isBedrockFlat", false).forGetter(StructureDimensionConfig::isBedrockFlat)
    ).apply(instance, StructureDimensionConfig::new));

    private final ResourceKey<DimensionType> dimensionType;
    private final ResourceKey<Biome> biomeIdentifier;
    private final Block fillmentBlock;
    private final boolean topBedrockEnabled;
    private final boolean bottomBedrockEnabled;
    private final boolean isBedrockFlat;

    public StructureDimensionConfig(ResourceKey<DimensionType> dimensionType, ResourceKey<Biome> biomeIdentifier) {
        this.dimensionType = dimensionType;
        this.biomeIdentifier = biomeIdentifier;
        this.fillmentBlock = Blocks.AIR;
        this.topBedrockEnabled = false;
        this.bottomBedrockEnabled = false;
        this.isBedrockFlat = false;
    }

    public StructureDimensionConfig(ResourceKey<DimensionType> dimensionType, ResourceKey<Biome> biomeIdentifier, Block fillmentBlock, boolean topBedrockEnabled, boolean bottomBedrockEnabled, boolean isBedrockFlat) {
        this.dimensionType = dimensionType;
        this.biomeIdentifier = biomeIdentifier;
        this.fillmentBlock = fillmentBlock;
        this.topBedrockEnabled = topBedrockEnabled;
        this.bottomBedrockEnabled = bottomBedrockEnabled;
        this.isBedrockFlat = isBedrockFlat;
    }

    public ResourceKey<DimensionType> getDimensionType() {
        return dimensionType;
    }

    public ResourceKey<Biome> getBiomeIdentifier() {
        return biomeIdentifier;
    }

    public Block getFillmentBlock() {
        return fillmentBlock;
    }

    public boolean isTopBedrockEnabled() {
        return topBedrockEnabled;
    }

    public boolean isBottomBedrockEnabled() {
        return bottomBedrockEnabled;
    }

    public boolean isBedrockFlat() {
        return isBedrockFlat;
    }
}
