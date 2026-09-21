package lv.cebbys.mcmods.respro.component.resource.string.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.BiomeSourceResourceInitializer;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.ChunkGeneratorResourceInitializer;
import lv.cebbys.mcmods.respro.component.mapper.JsonPart;
import lv.cebbys.mcmods.respro.component.resource.AbstractJsonObjectResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ChunkGeneratorResource extends AbstractJsonObjectResource implements ChunkGeneratorResourceInitializer
{
    @JsonPart("type")
    protected ResourceLocation type;
    @JsonPart("biome_source")
    protected BiomeSourceResource biomeSource;
    @JsonPart("settings")
    protected ResourceLocation settings;
    @JsonPart("dimension")
    protected String dimension;
    @JsonPart("playerSpawnOffset")
    protected List<Integer> playerSpawnOffset;
    @JsonPart("structureOffset")
    protected List<Integer> structureOffset;
    @JsonPart("structure")
    protected String structure;
    @JsonPart("fillmentBlock")
    protected ResourceLocation fillmentBlock;
    @JsonPart("enableTopBedrock")
    protected Boolean enableTopBedrock = null;
    @JsonPart("enableBottomBedrock")
    protected Boolean enableBottomBedrock = null;
    @JsonPart("isBedrockFlat")
    protected Boolean isBedrockFlat = null;

    public ChunkGeneratorResource() {}

    public ChunkGeneratorResource(String dimension) {
        this.dimension = dimension;
    }

    public @NotNull ChunkGeneratorResourceInitializer setBiomeSource(Consumer<BiomeSourceResourceInitializer> biomeSauceConsumer) {
        /*
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        Identifier preset = null;
        Class<MultiNoiseBiomeSource> multiNoiseBiomeSourceClass = MultiNoiseBiomeSource.class;
        if (biomeSource instanceof MultiNoiseBiomeSource)
            preset = ((MultiNoiseBiomeSourcePresetAccess) ((MultiNoiseBiomeSourceAccess) biomeSource).getPreset()).getPresetId();
        BiomeSourceResource biomeSourceResource = new BiomeSourceResource();
        biomeSourceResource.setValues(((BiomeSourceAccess) biomeSource).getCodecKey().orElseThrow().getRegistry(), preset);
        this.biomeSource = biomeSourceResource;
        this.settings = preset;
         */
        BiomeSourceResource biomeSauce = new BiomeSourceResource();
        biomeSauceConsumer.accept(biomeSauce);
        this.biomeSource = biomeSauce;
        return this;
    }

    public @NotNull ChunkGeneratorResourceInitializer setStructureChunkValues(BlockPos playerSpawnOffset, BlockPos structureOffset, String structure, ResourceLocation fillmentBlock, boolean enableTopBedrock, boolean enableBottomBedrock, boolean isBedrockFlat) {
        this.playerSpawnOffset = List.of(playerSpawnOffset.getX(), playerSpawnOffset.getY(), playerSpawnOffset.getZ());
        this.structureOffset = List.of(structureOffset.getX(), structureOffset.getY(), structureOffset.getZ());
        this.structure = structure;
        this.fillmentBlock = fillmentBlock;
        this.enableTopBedrock = enableTopBedrock;
        this.enableBottomBedrock = enableBottomBedrock;
        this.isBedrockFlat = isBedrockFlat;
        return this;
    }

    public @NotNull ChunkGeneratorResourceInitializer setFromCodec(Codec<? extends ChunkGenerator> chunkGeneratorCodec) {
        this.type = BuiltInRegistries.CHUNK_GENERATOR.getResourceKey(chunkGeneratorCodec).orElseThrow().location();
        return this;
    }

    public @NotNull ChunkGeneratorResourceInitializer setFromCodec(Codec<? extends ChunkGenerator> chunkGeneratorCodec, ResourceKey<NoiseGeneratorSettings> settings) {
        this.settings = settings.location();
        return setFromCodec(chunkGeneratorCodec);
    }

    @Override
    public boolean belongsTo(@NotNull("Provided ResourceType is null") PackType type) {
        return PackType.SERVER_DATA.equals(type);
    }

    @Override
    public void validate() throws ResourceValidationException {

    }
}