package lv.cebbys.mcmods.respro.api.initializer.worldgen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface WorldPresetsResourceInitializer
{
    @NotNull WorldPresetsResourceInitializer addWorldPreset(ResourceLocation worldPresetId);
}