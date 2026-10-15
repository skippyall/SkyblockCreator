package io.github.null2264.skyblockcreator;

import com.google.common.collect.Maps;
import io.github.null2264.skyblockcreator.command.StructureWorldCommand;
import io.github.null2264.skyblockcreator.data.ConfigLoader;
import io.github.null2264.skyblockcreator.data.ModConfig;
import io.github.null2264.skyblockcreator.worldgen.BiomeSourceFlatChunkGenerator;
import io.github.null2264.skyblockcreator.worldgen.StructureChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mod implements ModInitializer
{

    public static final String MOD_ID = "skyblockcreator";
    public static final Logger LOGGER = LogManager.getLogger("Structure World");
    public static final HashMap<String, CompoundTag> STRUCTURES = Maps.newHashMap();
    public static ModConfig CONFIG;
    public static List<ResourceKey<WorldPreset>> TO_BE_DISPLAYED = new ArrayList<>();
    public static ResourceLocation OVERRIDED_LEVEL_TYPE = null;

    public static ResourceLocation identifierOf(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, identifierOf("structure_chunk_generator"), StructureChunkGenerator.CODEC);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, identifierOf("biome_source_flat_generator"), BiomeSourceFlatChunkGenerator.CODEC);

        ConfigLoader.loadConfigs();
        for(ModConfig.StructureWorldConfig config : CONFIG.getStructureWorldConfigs()) {
            ResourceLocation worldPresetId = config.getPresetId();
            TO_BE_DISPLAYED.add(ResourceKey.create(Registries.WORLD_PRESET, worldPresetId));
            if (config.isOverridingDefault()) OVERRIDED_LEVEL_TYPE = worldPresetId;
        }

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, env) -> StructureWorldCommand.register(dispatcher)));
    }

}