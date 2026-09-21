package lv.cebbys.mcmods.respro.utility.access;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;

public interface BiomeSourceAccess
{
    Optional<ResourceKey<Codec<? extends BiomeSource>>> getCodecKey();
}