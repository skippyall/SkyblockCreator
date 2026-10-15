package io.github.null2264.skyblockcreator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.null2264.skyblockcreator.worldgen.FlatLevelGeneratorSettingsExtension;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FlatLevelSource.class)
public class FlatLevelSourceMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;<init>(Lnet/minecraft/world/level/biome/BiomeSource;Ljava/util/function/Function;)V"))
    private static BiomeSource modifyBiomeSource(BiomeSource original, @Local(argsOnly = true) FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        BiomeSource biomeSource = ((FlatLevelGeneratorSettingsExtension) flatLevelGeneratorSettings).skyblockcreator$getBiomeSource();
        if(biomeSource != null) {
            return biomeSource;
        }
        return original;
    }
}
