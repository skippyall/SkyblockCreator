package io.github.null2264.skyblockcreator.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.null2264.skyblockcreator.command.StructureWorldState;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @Shadow
    @Final
    WorldCreationUiState uiState;

    @ModifyExpressionValue(method = "createNewWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createNewWorldDirectory()Ljava/util/Optional;"))
    private Optional<LevelStorageSource.LevelStorageAccess> onCreateWorld(Optional<LevelStorageSource.LevelStorageAccess> original) {
        if(original.isPresent()) {
            LevelStorageSource.LevelStorageAccess access = original.get();
            Holder<WorldPreset> presetHolder = uiState.getWorldType().preset();
            if(presetHolder != null) {
                Optional<ResourceKey<WorldPreset>> preset = presetHolder.unwrapKey();
                if(preset.isPresent()) {
                    StructureWorldState.createInitial(access, preset.get());
                }
            }
        }
        return original;
    }
}
