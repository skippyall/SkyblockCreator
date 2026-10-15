package io.github.null2264.skyblockcreator.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.null2264.skyblockcreator.worldgen.FlatLevelGeneratorSettingsExtension;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlatLevelGeneratorSettings.class)
public class FlatLevelGeneratorSettingsMixin implements FlatLevelGeneratorSettingsExtension {
    @Unique
    private BiomeSource biomeSource = null;

    @Override
    public BiomeSource skyblockcreator$getBiomeSource() {
        return biomeSource;
    }

    @Override
    public void skyblockcreator$setBiomeSource(BiomeSource biomeSource) {
        this.biomeSource = biomeSource;
    }

    @ModifyExpressionValue(method = "adjustGenerationSettings", at = @At(value = "INVOKE", target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z"))
    private boolean allowBiomeSource(boolean original, Holder<Biome> holder) {
        if(biomeSource != null) {
            return biomeSource.possibleBiomes().contains(holder);
        }
        return original;
    }
}
