package io.github.null2264.skyblockcreator;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.null2264.skyblockcreator.command.StructureWorldCommand;
import io.github.null2264.skyblockcreator.core.ModConfig;
import io.github.null2264.skyblockcreator.worldgen.StructureChunkGenerator;
import io.github.null2264.skyblockcreator.worldgen.StructureWorldPresets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Mod implements ModInitializer
{

    public static final String MOD_ID = "skyblockcreator";
    public static final Logger LOGGER = LogManager.getLogger("Structure World");
    public static final HashMap<String, NbtCompound> STRUCTURES = Maps.newHashMap();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig CONFIG;
    public static List<RegistryKey<WorldPreset>> TO_BE_DISPLAYED = List.of();
    public static String OVERRIDED_LEVEL_TYPE = null;
    public static Path configPath = Path.of(FabricLoader.getInstance().getConfigDir() + File.separator + MOD_ID);

    public static Identifier identifierOf(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static RegistryKey<WorldPreset> registryKeyOf(String id) {
        return RegistryKey.of(RegistryKeys.WORLD_PRESET, identifierOf(id));
    }

    @Override
    public void onInitialize() {
        Registry.register(Registries.CHUNK_GENERATOR, identifierOf("structure_chunk_generator"), StructureChunkGenerator.CODEC);
        File structuresFolder = new File(configPath + File.separator + "structures");
        File configFile = new File(Mod.configPath + File.separator + MOD_ID + ".json");

        LOGGER.info("Trying to read structures folder...");
        try {
            if (!structuresFolder.exists()) {
                LOGGER.info("No structures folder found, creating a new one...");
                if (structuresFolder.mkdirs()) {
                    Path builtinStructuresFolderPath = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new Exception("Couldn't find ModContainer")).findPath("structures").orElseThrow();
                    List<Path> builtinStructuresPath = Files.walk(builtinStructuresFolderPath).filter(Files::isRegularFile).toList();
                    for (Path builtinStructurePath : builtinStructuresPath) {
                        InputStream builtinStructureInputStream = Files.newInputStream(builtinStructurePath);
                        File outputFile = new File(structuresFolder, builtinStructurePath.getFileName().toString());
                        if (outputFile.createNewFile()) {
                            FileOutputStream structureOutputStream = new FileOutputStream(outputFile);
                            IOUtils.copy(builtinStructureInputStream, structureOutputStream);
                        }
                    }
                    LOGGER.info("Successfully created structures folder.");
                } else {
                    throw new Exception("Failed while creating structures folder.");
                }
            }
            if (structuresFolder.exists()) {
                LOGGER.info("Found structures folder, loading structures...");
                List<File> files = Arrays.stream(Objects.requireNonNull(structuresFolder.listFiles(pathname -> pathname.exists() && pathname.isFile() && pathname.getName().endsWith(".nbt")))).toList();
                for (File file : files) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    NbtCompound structure = NbtIo.readCompressed(dataInputStream);
                    STRUCTURES.put(file.getName().replace(".nbt", ""), structure);
                }
                LOGGER.info("Successfully loaded structures folder with " + STRUCTURES.size() + " structure.");
            }

        } catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the structures folder!", exception);
        }

        LOGGER.info("Trying to read config file...");
        try {
            if (configFile.createNewFile()) {
                LOGGER.info("No config file found, creating a new one...");
                String json = gson.toJson(JsonParser.parseString(gson.toJson(new ModConfig())));
                try (PrintWriter out = new PrintWriter(configFile)) {
                    out.println(json);
                }
                CONFIG = new ModConfig();
                LOGGER.info("Successfully created default config file with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
            } else {
                LOGGER.info("A config file was found, loading it..");
                CONFIG = gson.fromJson(new String(Files.readAllBytes(configFile.toPath())), ModConfig.class);
                if (CONFIG == null) {
                    throw new NullPointerException("The config file was empty.");
                } else {
                    LOGGER.info("Successfully loaded config file with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
                }
            }
        } catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the config file!", exception);
            CONFIG = new ModConfig();
            LOGGER.warn("Defaulting to original config with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
        }

        StructureWorldPresets.register();

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, env) -> StructureWorldCommand.register(dispatcher)));
    }

}