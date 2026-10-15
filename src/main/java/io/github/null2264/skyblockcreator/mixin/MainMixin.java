package io.github.null2264.skyblockcreator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.null2264.skyblockcreator.command.StructureWorldState;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Main;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Main.class)
public class MainMixin {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/ServerPacksSource;createPackRepository(Ljava/nio/file/Path;)Lnet/minecraft/server/packs/repository/PackRepository;"))
    private static void onCreateWorld(CallbackInfo ci, @Local LevelStorageSource.LevelStorageAccess storageAccess, @Local DedicatedServerSettings settings) {
        String levelType = settings.getProperties().worldDimensionData.levelType();
        ResourceKey<WorldPreset> presetKey = Optional.ofNullable(ResourceLocation.tryParse(levelType))
                .map(resourceLocation -> ResourceKey.create(Registries.WORLD_PRESET, resourceLocation))
                .or(() -> Optional.ofNullable(DedicatedServerProperties.WorldDimensionData.LEGACY_PRESET_NAMES.get(levelType)))
                .orElse(WorldPresets.NORMAL);
        StructureWorldState.createInitial(storageAccess, presetKey);
    }
}
