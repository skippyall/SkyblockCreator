package io.github.null2264.skyblockcreator.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin
{
    @ModifyReceiver(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;toString()Ljava/lang/String;"))
    public ResourceLocation handleLevelType(ResourceLocation instance) {
        if (Mod.OVERRIDED_LEVEL_TYPE != null) {
            Mod.LOGGER.debug("Redirecting level_type to '" + Mod.OVERRIDED_LEVEL_TYPE + "'");
            return Mod.OVERRIDED_LEVEL_TYPE;
        }
        return instance;
    }
}