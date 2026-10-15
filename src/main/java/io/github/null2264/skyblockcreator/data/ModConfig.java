package io.github.null2264.skyblockcreator.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;

public class ModConfig
{
    public static final Codec<ModConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StructureWorldConfig.CODEC.listOf().fieldOf("structureWorldConfigs").forGetter(ModConfig::getStructureWorldConfigs),
            Codec.INT.fieldOf("createPlatformPermissionLevel").forGetter(ModConfig::getCreatePlatformPermissionLevel),
            Codec.INT.fieldOf("teleportToPlatformPermissionLevel").forGetter(ModConfig::getTeleportToPlatformPermissionLevel),
            Codec.INT.fieldOf("platformDistanceRadius").forGetter(ModConfig::getPlatformDistanceRadius),
            Codec.STRING.fieldOf("commandName").forGetter(ModConfig::getCommandName),
            Codec.STRING.fieldOf("commandName").forGetter(ModConfig::getCommandAlias)
    ).apply(instance, ModConfig::new));

    private final List<StructureWorldConfig> structureWorldConfigs;
    private final Map<ResourceLocation, StructureWorldConfig> cacheMap;

    private final int createPlatformPermissionLevel;
    private final int teleportToPlatformPermissionLevel;
    private final int platformDistanceRadius;
    private final String commandName;
    private final String commandAlias;

    public ModConfig(List<StructureWorldConfig> structureWorldConfigs, int createPlatformPermissionLevel, int teleportToPlatformPermissionLevel, int platformDistanceRadius, String commandName, String commandAlias) {
        this.structureWorldConfigs = structureWorldConfigs;
        this.cacheMap = createMap();
        this.createPlatformPermissionLevel = createPlatformPermissionLevel;
        this.teleportToPlatformPermissionLevel = teleportToPlatformPermissionLevel;
        this.platformDistanceRadius = platformDistanceRadius;
        this.commandName = commandName;
        this.commandAlias = commandAlias;
    }

    public ModConfig() {
        this.structureWorldConfigs = Arrays.asList(
                new StructureWorldConfig("simple_tree", Biomes.FOREST, new BlockPos(-2, 0, -2), new BlockPos(0, 8, 0)),
                new StructureWorldConfig("classic_skyblock", Biomes.PLAINS, new BlockPos(-3, 0, -1), new BlockPos(0, 3, 0)),
                new StructureWorldConfig(
                        Mod.identifierOf("stoneblock"),
                        new StructureConfig("stoneblock", new BlockPos(-5, -2, -5), new BlockPos(0, 0, 0)),
                        Map.of(LevelStem.OVERWORLD, new StructureDimensionConfig(BuiltinDimensionTypes.OVERWORLD, Biomes.PLAINS, Blocks.STONE, true, true, true))
                )
        );
        this.cacheMap = createMap();
        this.createPlatformPermissionLevel = 0;
        this.teleportToPlatformPermissionLevel = 0;
        this.platformDistanceRadius = 1000;
        this.commandName = "skyblock";
        this.commandAlias = "sb";
    }

    public int getCreatePlatformPermissionLevel() {
        return createPlatformPermissionLevel;
    }

    public int getTeleportToPlatformPermissionLevel() {
        return teleportToPlatformPermissionLevel;
    }

    public int getPlatformDistanceRadius() {
        return platformDistanceRadius;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandAlias() {
        return commandAlias;
    }

    public List<StructureWorldConfig> getStructureWorldConfigs() {
        return structureWorldConfigs;
    }

    public Map<ResourceLocation, StructureWorldConfig> getStructureWorldConfigMap() {
        return cacheMap;
    }

    private Map<ResourceLocation, StructureWorldConfig> createMap() {
        Map<ResourceLocation, StructureWorldConfig> map = new HashMap<>();
        for (StructureWorldConfig structureWorldConfig : structureWorldConfigs) {
            map.put(structureWorldConfig.presetId, structureWorldConfig);
        }
        return Collections.unmodifiableMap(map);
    }

    public static class StructureWorldConfig
    {
        public static final Codec<StructureWorldConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("presetId").forGetter(StructureWorldConfig::getPresetId),
                StructureConfig.CODEC.fieldOf("structureConfig").forGetter(StructureWorldConfig::getStructureConfig),
                Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), StructureDimensionConfig.CODEC).optionalFieldOf("dimensionConfigs",Map.of()).forGetter(StructureWorldConfig::getDimensionConfigs),
                Codec.BOOL.fieldOf("isOverridingDefault").forGetter(StructureWorldConfig::isOverridingDefault)
        ).apply(instance, StructureWorldConfig::new));

        private final ResourceLocation presetId;
        private final StructureConfig structureConfig;
        private final Map<ResourceKey<LevelStem>, StructureDimensionConfig> dimensionConfigs;
        private final boolean overridingDefault;

        public StructureWorldConfig(ResourceLocation presetId, StructureConfig structureConfig, Map<ResourceKey<LevelStem>, StructureDimensionConfig> dimensionConfigs, boolean overridingDefault) {
            this.presetId = presetId;
            this.structureConfig = structureConfig;
            this.dimensionConfigs = dimensionConfigs;
            this.overridingDefault = overridingDefault;
        }

        public StructureWorldConfig(ResourceLocation presetId, StructureConfig structureConfig, Map<ResourceKey<LevelStem>, StructureDimensionConfig> dimensionConfigs) {
            this(presetId, structureConfig, dimensionConfigs, false);
        }

        public StructureWorldConfig(String presetId, ResourceKey<Biome> biome, BlockPos structureOffset, BlockPos playerSpawnOffset) {
            this(
                    Mod.identifierOf(presetId),
                    new StructureConfig(presetId, structureOffset, playerSpawnOffset),
                    Map.of(LevelStem.OVERWORLD, new StructureDimensionConfig(BuiltinDimensionTypes.OVERWORLD, biome))
            );
        }

        public ResourceLocation getPresetId() {
            return presetId;
        }

        public StructureConfig getStructureConfig() {
            return structureConfig;
        }

        public Map<ResourceKey<LevelStem>, StructureDimensionConfig> getDimensionConfigs() {
            return dimensionConfigs;
        }

        public boolean isOverridingDefault() {
            return overridingDefault;
        }
    }

}