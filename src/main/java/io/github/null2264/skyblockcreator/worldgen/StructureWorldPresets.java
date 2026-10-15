package io.github.null2264.skyblockcreator.worldgen;

import com.mojang.serialization.Lifecycle;
import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.data.ModConfig;
import io.github.null2264.skyblockcreator.mixin.WorldPresetAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StructureWorldPresets {
    public static <T> void registerDynamic(ResourceKey<? extends Registry<T>> resourceKey, WritableRegistry<T> registry, RegistryOps.RegistryInfoLookup lookup) {
        if(resourceKey.location().equals(Registries.WORLD_PRESET.location())) {
            //noinspection unchecked
            WritableRegistry<WorldPreset> presets = ((WritableRegistry<WorldPreset>) registry);
            WorldPreset normalVanilla = presets.get(WorldPresets.NORMAL);

            for(ModConfig.StructureWorldConfig config : Mod.CONFIG.getStructureWorldConfigs()) {
                ResourceKey<WorldPreset> presetKey = ResourceKey.create(Registries.WORLD_PRESET, config.getPresetId());
                try {
                    WorldPreset preset = createPreset(config, lookup, normalVanilla);
                    if(preset != null) {
                        presets.register(presetKey, preset, Lifecycle.stable());
                    }
                } catch (Exception e) {
                    Mod.LOGGER.error("Could not inject preset {}", presetKey.location(), e);
                }
            }
        }
    }

    public static @Nullable WorldPreset createPreset(ModConfig.StructureWorldConfig config, RegistryOps.RegistryInfoLookup lookup, @Nullable WorldPreset normalVanilla) {
        if(config.getDimensionConfigs().isEmpty()) {
            return null;
        }

        HolderGetter<DimensionType> dimensionTypes = lookup.lookup(Registries.DIMENSION_TYPE).orElseThrow().getter();
        HolderGetter<Biome> biomes = lookup.lookup(Registries.BIOME).orElseThrow().getter();
        Map<ResourceKey<LevelStem>, LevelStem> dimensions = new HashMap<>();

        config.getDimensionConfigs().forEach((dimension, dimensionConfig) -> {
            Holder<DimensionType> dimensionType = dimensionTypes.getOrThrow(dimensionConfig.getDimensionType());
            StructureChunkGenerator chunkGenerator = new StructureChunkGenerator(
                    new FixedBiomeSource(biomes.getOrThrow(dimensionConfig.getBiomeIdentifier())),
                    dimensionConfig.getFillmentBlock(),
                    dimensionConfig.isTopBedrockEnabled(),
                    dimensionConfig.isBottomBedrockEnabled(),
                    dimensionConfig.isBedrockFlat()
            );

            dimensions.put(dimension, new LevelStem(dimensionType, chunkGenerator));
        });


        if(normalVanilla != null) {
            //Add default vanilla dimensions not mentioned in the config
            //Should we somehow copy the LevelStem?
            ((WorldPresetAccessor) normalVanilla).getDimensions().forEach(dimensions::putIfAbsent);
        }

        return new WorldPreset(dimensions);
    }
}
