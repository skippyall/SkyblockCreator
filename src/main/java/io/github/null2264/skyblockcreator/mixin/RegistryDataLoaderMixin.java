package io.github.null2264.skyblockcreator.mixin;

import com.mojang.serialization.Decoder;
import io.github.null2264.skyblockcreator.worldgen.StructureWorldPresets;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Inject(method = "loadRegistryContents", at = @At("TAIL"))
    private static <E> void onLoadRegistry(RegistryOps.RegistryInfoLookup registryInfoLookup, ResourceManager resourceManager, ResourceKey<? extends Registry<E>> resourceKey, WritableRegistry<E> writableRegistry, Decoder<E> decoder, Map<ResourceKey<?>, Exception> map, CallbackInfo ci) {
        StructureWorldPresets.registerDynamic(resourceKey, writableRegistry, registryInfoLookup);
    }
}
