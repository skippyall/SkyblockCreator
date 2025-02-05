package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.command.StructureWorldState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin
{

    @Inject(method = "moveToSpawn", at = @At("TAIL"))
    private void doSpawn(ServerWorld world, CallbackInfo ci) {
        if (StructureWorldState.isNotStructureWorld(world)) {
            return;
        }

        StructureWorldState structureWorldState = world.getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        try {
            structureWorldState.teleportToIsland(world, player, player.getUuid(), true);
        } catch (Exception ignored) {
        }
    }
}