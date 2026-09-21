package lv.cebbys.mcmods.respro.component.resource.string.worldgen.worldpreset;

import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.DimensionResourceInitializer;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.WorldPresetResourceInitializer;
import lv.cebbys.mcmods.respro.component.mapper.JsonPart;
import lv.cebbys.mcmods.respro.component.resource.AbstractJsonObjectResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WorldPresetResource extends AbstractJsonObjectResource implements WorldPresetResourceInitializer
{
    @JsonPart("dimensions")
    protected Map<ResourceLocation, DimensionResource> dimensions = new HashMap<>();

    @Override
    public @NotNull WorldPresetResourceInitializer setDimensions(Consumer<DimensionResourceInitializer> dimensionConsumer) {
        DimensionResource dim = new DimensionResource();
        dimensionConsumer.accept(dim);
        dimensions.put(dim.getDimId(), dim);
        return this;
    }

    @Override
    public boolean belongsTo(@NotNull("Provided ResourceType is null") PackType type) {
        return PackType.SERVER_DATA.equals(type);
    }

    @Override
    public void validate() throws ResourceValidationException {

    }
}