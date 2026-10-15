package io.github.null2264.skyblockcreator.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.github.null2264.skyblockcreator.Mod.*;

public class ConfigLoader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Path configPath = Path.of(FabricLoader.getInstance().getConfigDir() + File.separator + MOD_ID);

    public static void loadConfigs() {
        File structuresFolder = new File(configPath + File.separator + "structures");
        File configFile = new File(configPath + File.separator + MOD_ID + ".json");

        LOGGER.info("Trying to read structures folder...");
        try {
            if (!structuresFolder.exists()) {
                LOGGER.info("No structures folder found, creating a new one...");
                addBuiltinStructures(structuresFolder);
            }
            if (structuresFolder.exists()) {
                LOGGER.info("Found structures folder, loading structures...");
                loadStructures(structuresFolder);
            }
        } catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the structures folder!", exception);
        }

        LOGGER.info("Trying to read config file...");
        try {
            if (configFile.createNewFile()) {
                LOGGER.info("No config file found, creating a new one...");
                CONFIG = new ModConfig();
                JsonElement element = ModConfig.CODEC.encodeStart(JsonOps.INSTANCE, CONFIG).getOrThrow(false, m -> {});
                String json = gson.toJson(element);
                try (PrintWriter out = new PrintWriter(configFile)) {
                    out.println(json);
                }
                LOGGER.info("Successfully created default config file with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
            } else {
                LOGGER.info("A config file was found, loading it..");
                JsonElement element = JsonParser.parseReader(Files.newBufferedReader(configFile.toPath()));
                CONFIG = ModConfig.CODEC.decode(JsonOps.INSTANCE, element).getOrThrow(false, m -> {}).getFirst();
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
    }

    private static void loadStructures(File structuresFolder) throws IOException {
        List<File> files = Arrays.stream(Objects.requireNonNull(structuresFolder.listFiles(pathname -> pathname.exists() && pathname.isFile() && pathname.getName().endsWith(".nbt")))).toList();
        for (File file : files) {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            CompoundTag structure = NbtIo.readCompressed(dataInputStream);
            STRUCTURES.put(file.getName().replace(".nbt", ""), structure);
        }
        LOGGER.info("Successfully loaded structures folder with " + STRUCTURES.size() + " structure.");
    }

    public static void addBuiltinStructures(File structuresFolder) throws Exception {
        if (structuresFolder.mkdirs()) {
            Path builtinStructuresFolderPath = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new Exception("Couldn't find ModContainer")).findPath("structures").orElseThrow();
            List<Path> builtinStructuresPath;
            try(Stream<Path> stream = Files.walk(builtinStructuresFolderPath)) {
                builtinStructuresPath = stream.filter(Files::isRegularFile).toList();
            }
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
}
