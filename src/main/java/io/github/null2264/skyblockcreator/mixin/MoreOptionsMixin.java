package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState.WorldTypeEntry;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

@Mixin(WorldCreationUiState.class)
public abstract class MoreOptionsMixin
{

    @Shadow private WorldTypeEntry worldType;

    @Redirect(method = "updatePresetLists()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;getNonEmptyList(Lnet/minecraft/core/Registry;Lnet/minecraft/tags/TagKey;)Ljava/util/Optional;"))
    public Optional<List<WorldTypeEntry>> interceptPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
        Optional<List<WorldTypeEntry>> presets = WorldCreationUiState.getNonEmptyList(presetRegistry, tag);
        Mod.LOGGER.info(presets.toString());
        if (tag.equals(WorldPresetTags.EXTENDED) || presets.isEmpty()) return presets;

        ArrayList<WorldTypeEntry> mutablePresets = new ArrayList<>(presets.get());
        Mod.LOGGER.info(Mod.TO_BE_DISPLAYED.toString());
        Mod.TO_BE_DISPLAYED.forEach(worldTypeKey -> {
            WorldTypeEntry worldPreset = new WorldTypeEntry(presetRegistry.getHolder(worldTypeKey).orElseThrow());
            if (worldTypeKey.location().getPath().equals(Mod.OVERRIDED_LEVEL_TYPE)) {
                mutablePresets.add(0, worldPreset);
                this.worldType = worldPreset;
                // this.apply((dynamicRegistryManager, dimensionsRegistryHolder) -> worldPreset.preset().value().createDimensionsRegistryHolder());
            } else mutablePresets.add(worldPreset);
        });

        return Optional.of(mutablePresets);
    }
}