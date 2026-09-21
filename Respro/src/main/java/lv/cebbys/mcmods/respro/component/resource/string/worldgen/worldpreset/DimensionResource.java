package lv.cebbys.mcmods.respro.component.resource.string.worldgen.worldpreset;

import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.ChunkGeneratorResourceInitializer;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.DimensionResourceInitializer;
import lv.cebbys.mcmods.respro.component.mapper.JsonPart;
import lv.cebbys.mcmods.respro.component.resource.AbstractJsonObjectResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DimensionResource extends AbstractJsonObjectResource implements DimensionResourceInitializer
{
    @JsonPart("type")
    private ResourceLocation type;
    @JsonPart("generator")
    private ChunkGeneratorResource chunkGenerator;

    public @NotNull DimensionResourceInitializer setChunkGenerator(Consumer<ChunkGeneratorResourceInitializer> chunkGeneratorConsumer) {
        ChunkGeneratorResource chunkGenerator = new ChunkGeneratorResource(type.getPath());
        chunkGeneratorConsumer.accept(chunkGenerator);
        this.chunkGenerator = chunkGenerator;
        return this;
    }

    @Override
    public @NotNull DimensionResourceInitializer setFromRegistry(ResourceKey<DimensionType> dimensionType) {
        this.type = dimensionType.location();
        return this;
    }

    public ResourceLocation getDimId() {
        return type;
    }

    @Override
    public boolean belongsTo(@NotNull("Provided ResourceType is null") PackType type) {
        return PackType.SERVER_DATA.equals(type);
    }

    @Override
    public void validate() throws ResourceValidationException {

    }
}