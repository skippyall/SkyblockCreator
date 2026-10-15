package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.command.StructureWorldState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{

    @Inject(method = "fudgeSpawnLocation", at = @At("TAIL"))
    private void doSpawn(ServerLevel world, CallbackInfo ci) {
        StructureWorldState structureWorldState = world.getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        if(structureWorldState.isNotStructureWorld()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) (Object) this;
        try {
            structureWorldState.teleportToIsland(world, player, player.getUUID(), true);
        } catch (Exception ignored) {
        }
    }
}