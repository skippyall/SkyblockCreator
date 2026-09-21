package lv.cebbys.mcmods.respro.component.resource.string.worldgen;

import lv.cebbys.mcmods.respro.api.initializer.worldgen.WorldPresetsResourceInitializer;
import lv.cebbys.mcmods.respro.component.mapper.JsonPart;
import lv.cebbys.mcmods.respro.component.resource.AbstractJsonObjectResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WorldPresetsResource extends AbstractJsonObjectResource implements WorldPresetsResourceInitializer
{
    @JsonPart("replace")
    protected Boolean dimensions = false;
    @JsonPart("values")
    protected ArrayList<ResourceLocation> worldPreset = new ArrayList<>();

    public @NotNull WorldPresetsResourceInitializer addWorldPreset(ResourceLocation worldPresetId) {
        worldPreset.add(worldPresetId);
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