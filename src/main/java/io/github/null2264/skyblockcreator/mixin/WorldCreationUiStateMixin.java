package io.github.null2264.skyblockcreator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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
public abstract class WorldCreationUiStateMixin
{

    @Shadow private WorldTypeEntry worldType;

    @WrapOperation(method = "updatePresetLists()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;getNonEmptyList(Lnet/minecraft/core/Registry;Lnet/minecraft/tags/TagKey;)Ljava/util/Optional;"))
    public Optional<List<WorldTypeEntry>> interceptPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag, Operation<Optional<List<WorldTypeEntry>>> original) {
        Optional<List<WorldTypeEntry>> presets = original.call(presetRegistry, tag);
        if (tag.equals(WorldPresetTags.EXTENDED) || presets.isEmpty()) return presets;

        ArrayList<WorldTypeEntry> mutablePresets = new ArrayList<>(presets.get());
        Mod.TO_BE_DISPLAYED.forEach(worldTypeKey -> {
            Optional<Holder.Reference<WorldPreset>> presetHolder = presetRegistry.getHolder(worldTypeKey);
            if(presetHolder.isPresent()) {
                WorldTypeEntry worldPreset = new WorldTypeEntry(presetHolder.get());
                if (worldTypeKey.location().equals(Mod.OVERRIDED_LEVEL_TYPE)) {
                    mutablePresets.add(0, worldPreset);
                    this.worldType = worldPreset;
                    // this.apply((dynamicRegistryManager, dimensionsRegistryHolder) -> worldPreset.preset().value().createDimensionsRegistryHolder());
                } else mutablePresets.add(worldPreset);
            }
        });

        return Optional.of(mutablePresets);
    }
}