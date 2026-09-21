package lv.cebbys.mcmods.respro.mixin;

import com.mojang.serialization.Codec;
import lv.cebbys.mcmods.respro.utility.access.BiomeSourceAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin implements BiomeSourceAccess
{
    @Shadow
    protected abstract Codec<? extends BiomeSource> codec();

    // Literally copied and pasted from ChunkGenerator
    public Optional<ResourceKey<Codec<? extends BiomeSource>>> getCodecKey() {
        return BuiltInRegistries.BIOME_SOURCE.getResourceKey(codec());
    }
}