package io.github.null2264.skyblockcreator.worldgen;

import io.github.null2264.skyblockcreator.Mod;
import lv.cebbys.mcmods.respro.api.ResproRegistry;
import lv.cebbys.mcmods.respro.constant.ResproConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import java.util.ArrayList;

import static io.github.null2264.skyblockcreator.Mod.identifierOf;

public class StructureWorldPresets
{
    private static final ArrayList<ResourceKey<WorldPreset>> toBeDisplayed = new ArrayList<>();

    public static void register() {
        ResproRegistry.registerData(data -> {
            data.setDumpMode(FabricLoader.getInstance().isDevelopmentEnvironment());
            data.setPackId(Mod.identifierOf("datapack"));
            data.setPackProfile(profile -> {
                profile.setAlwaysEnabled(true);
                profile.setPackName(name -> name.setText("SkyblockCreator Internal Data"));
                profile.setPackIcon(icon -> icon.setFromResources(Mod.class, "assets/skyblockcreator/icon.png"));
                profile.setPackMeta(meta -> {
                    meta.setDescription("SkyblockCreator's Internal Datapack");
                    meta.setFormat(ResproConstants.PACK_FORMAT);
                });
            });
            Mod.CONFIG.getStructureWorldConfigs().forEach(config -> {
                String structure = config.getStructureIdentifier();
                ResourceLocation worldPresetId = Mod.identifierOf(structure);
                data.setWorldPreset(worldPresetId, worldPreset -> {
                    worldPreset.setDimensions(dim -> {
                        dim.setFromRegistry(BuiltinDimensionTypes.OVERWORLD);
                        dim.setChunkGenerator(chunkGenerator -> {
                            chunkGenerator.setFromCodec(StructureChunkGenerator.CODEC);
                            chunkGenerator.setStructureChunkValues(config.getPlayerSpawnOffset(), config.getStructureOffset(), structure, new ResourceLocation(config.getFillmentBlockIdentifier()), config.isTopBedrockEnabled(), config.isBottomBedrockEnabled(), config.isBedrockFlat());
                            chunkGenerator.setBiomeSource(biomeSource -> biomeSource.setFromCodec(FixedBiomeSource.CODEC, new ResourceLocation(config.getBiomeIdentifier())));
                        });
                    });

                    worldPreset.setDimensions(dim -> {
                        dim.setFromRegistry(BuiltinDimensionTypes.NETHER);
                        dim.setChunkGenerator(chunkGenerator -> {
                            if (config.getTheNetherConfig().isVoidMode()) {
                                chunkGenerator.setFromCodec(StructureChunkGenerator.CODEC);
                                chunkGenerator.setStructureChunkValues(config.getPlayerSpawnOffset(), config.getStructureOffset(), structure, ResourceLocation.tryBuild("minecraft", "air"), config.isTopBedrockEnabled(), config.isBottomBedrockEnabled(), config.isBedrockFlat());
                            } else {
                                chunkGenerator.setFromCodec(NoiseBasedChunkGenerator.CODEC, NoiseGeneratorSettings.NETHER);
                            }
                            chunkGenerator.setBiomeSource(biomeSource -> biomeSource.setFromCodec(MultiNoiseBiomeSource.CODEC, MultiNoiseBiomeSourceParameterLists.NETHER));
                        });
                    });

                    worldPreset.setDimensions(dim -> {
                        dim.setFromRegistry(BuiltinDimensionTypes.END);
                        dim.setChunkGenerator(chunkGenerator -> {
                            if (config.getTheEndConfig().isVoidMode()) {
                                chunkGenerator.setFromCodec(StructureChunkGenerator.CODEC);
                                chunkGenerator.setStructureChunkValues(config.getPlayerSpawnOffset(), config.getStructureOffset(), structure, ResourceLocation.tryBuild("minecraft", "air"), config.isTopBedrockEnabled(), config.isBottomBedrockEnabled(), config.isBedrockFlat());
                            } else {
                                chunkGenerator.setFromCodec(NoiseBasedChunkGenerator.CODEC, NoiseGeneratorSettings.END);
                            }
                            chunkGenerator.setBiomeSource(biomeSource -> biomeSource.setFromCodec(TheEndBiomeSource.CODEC));
                        });
                    });
                });
                toBeDisplayed.add(Mod.registryKeyOf(structure));
                if (config.isOverridingDefault()) Mod.OVERRIDED_LEVEL_TYPE = worldPresetId.getPath();
                Mod.LOGGER.info("Successfully registered " + structure + " world type.");
            });
            Mod.TO_BE_DISPLAYED = toBeDisplayed;
        });
    }
}