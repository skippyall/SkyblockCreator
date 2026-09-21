package lv.cebbys.mcmods.respro.component.resource.string.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.BiomeSourceResourceInitializer;
import lv.cebbys.mcmods.respro.component.mapper.JsonPart;
import lv.cebbys.mcmods.respro.component.resource.AbstractJsonObjectResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import lv.cebbys.mcmods.respro.utility.access.MultiNoiseBiomeSourcePresetAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.jetbrains.annotations.NotNull;

public class BiomeSourceResource extends AbstractJsonObjectResource implements BiomeSourceResourceInitializer
{
    @JsonPart("type")
    protected ResourceLocation type;
    @JsonPart("preset")
    protected ResourceLocation preset;
    @JsonPart("biome")
    protected ResourceLocation biome;

    @Override
    public @NotNull BiomeSourceResourceInitializer setValues(ResourceLocation type, ResourceLocation preset) {
        this.type = type;
        this.preset = preset;
        return this;
    }

    public @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec) {
        this.type = BuiltInRegistries.BIOME_SOURCE.getResourceKey(biomeSourceCodec).orElseThrow().location();
        return this;
    }

    public @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, ResourceLocation biomeId) {
        this.biome = biomeId;
        return setFromCodec(biomeSourceCodec);
    }

    public @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, ResourceKey<MultiNoiseBiomeSourceParameterList> preset) {
        this.preset = preset.location();
        return setFromCodec(biomeSourceCodec);
    }

    @Override
    public boolean belongsTo(@NotNull("Provided ResourceType is null") PackType type) {
        return PackType.SERVER_DATA.equals(type);
    }

    @Override
    public void validate() throws ResourceValidationException {

    }
}