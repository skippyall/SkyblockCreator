package io.github.null2264.skyblockcreator.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.error.AlreadyHaveIsland;
import io.github.null2264.skyblockcreator.error.InvalidChunkGenerator;
import io.github.null2264.skyblockcreator.error.NoIslandFound;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StructureWorldCommand
{
    // TODO: Add team support

    private static final SimpleCommandExceptionType INVALID_CHUNK_GENERATOR = new SimpleCommandExceptionType(Component.translatable("commands.skyblockcreator.invalid_chunk_generator"));
    private static final SimpleCommandExceptionType ISLAND_FOR_UUID_ALREADY_EXISTS = new SimpleCommandExceptionType(Component.translatable("commands.skyblockcreator.island_for_uuid_already_exists"));
    private static final SimpleCommandExceptionType NO_ISLAND_FOR_UUID = new SimpleCommandExceptionType(Component.translatable("commands.skyblockcreator.no_island_for_uuid"));

    private static final LiteralArgumentBuilder<CommandSourceStack> create = Commands.literal("create").requires(serverCommandSource -> serverCommandSource.hasPermission(Mod.CONFIG.getCreatePlatformPermissionLevel())).executes(context -> {
        ServerPlayer playerEntity = context.getSource().getPlayerOrException();
        StructureWorldState structureWorldState = context.getSource().getLevel().getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        try {
            structureWorldState.createIsland(context.getSource().getLevel(), playerEntity);
        } catch (InvalidChunkGenerator e) {
            throw INVALID_CHUNK_GENERATOR.create();
        } catch (AlreadyHaveIsland e) {
            throw ISLAND_FOR_UUID_ALREADY_EXISTS.create();
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.skyblockcreator.created_island", playerEntity.getDisplayName()), false);
        return 1;
    }).then(Commands.argument("player", EntityArgument.player()).requires(serverCommandSource -> serverCommandSource.hasPermission(2)).executes(context -> {
        ServerPlayer playerEntity = EntityArgument.getPlayer(context, "player");
        StructureWorldState structureWorldState = context.getSource().getLevel().getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        try {
            structureWorldState.createIsland(context.getSource().getLevel(), playerEntity);
        } catch (InvalidChunkGenerator e) {
            throw INVALID_CHUNK_GENERATOR.create();
        } catch (AlreadyHaveIsland e) {
            throw ISLAND_FOR_UUID_ALREADY_EXISTS.create();
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.skyblockcreator.created_island", playerEntity.getDisplayName()), true);
        return 1;
    }));

    private static final LiteralArgumentBuilder<CommandSourceStack> delete = Commands.literal("delete").requires(serverCommandSource -> serverCommandSource.hasPermission(Mod.CONFIG.getCreatePlatformPermissionLevel())).executes(context -> {
        ServerPlayer playerEntity = context.getSource().getPlayerOrException();
        StructureWorldState structureWorldState = context.getSource().getLevel().getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        try {
            structureWorldState.deleteIsland(context.getSource().getLevel(), playerEntity);
        } catch (InvalidChunkGenerator e) {
            throw INVALID_CHUNK_GENERATOR.create();
        } catch (NoIslandFound e) {
            throw NO_ISLAND_FOR_UUID.create();
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.skyblockcreator.deleted_island", playerEntity.getDisplayName()), false);
        return 1;
    }).then(Commands.argument("player", EntityArgument.player()).requires(serverCommandSource -> serverCommandSource.hasPermission(2)).executes(context -> {
        ServerPlayer playerEntity = EntityArgument.getPlayer(context, "player");
        StructureWorldState structureWorldState = context.getSource().getLevel().getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        try {
            structureWorldState.deleteIsland(context.getSource().getLevel(), playerEntity);
        } catch (InvalidChunkGenerator e) {
            throw INVALID_CHUNK_GENERATOR.create();
        } catch (NoIslandFound e) {
            throw NO_ISLAND_FOR_UUID.create();
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.skyblockcreator.deleted_island", playerEntity.getDisplayName()), true);
        return 1;
    }));

    private static final LiteralArgumentBuilder<CommandSourceStack> teleport = Commands.literal("teleport").requires(serverCommandSource -> serverCommandSource.hasPermission(Mod.CONFIG.getTeleportToPlatformPermissionLevel())).executes(context -> {
        StructureWorldState structureWorldState = context.getSource().getLevel().getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        ServerPlayer player = context.getSource().getPlayerOrException();

        try {
            structureWorldState.teleportToIsland(context.getSource().getLevel(), player);
        } catch (InvalidChunkGenerator invalidChunk) {
            throw INVALID_CHUNK_GENERATOR.create();
        } catch (NoIslandFound noIsland) {
            throw NO_ISLAND_FOR_UUID.create();
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.skyblockcreator.teleported_to_island", player.getDisplayName()), false);
        return 1;
    }).then(Commands.argument("player", EntityArgument.player()).requires(serverCommandSource -> serverCommandSource.hasPermission(2)).executes(context -> {
        StructureWorldState structureWorldState = context.getSource().getLevel().getDataStorage().computeIfAbsent(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        try {
            structureWorldState.teleportToIsland(context.getSource().getLevel(), context.getSource().getPlayerOrException(), player);
        } catch (InvalidChunkGenerator invalidChunk) {
            throw INVALID_CHUNK_GENERATOR.create();
        } catch (NoIslandFound noIsland) {
            throw NO_ISLAND_FOR_UUID.create();
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.skyblockcreator.teleported_to_island", player.getDisplayName()), true);
        return 1;
    }));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainNode = dispatcher.register(Commands.literal(Mod.CONFIG.getCommandName()).then(create).then(delete).then(teleport));
        dispatcher.register(Commands.literal(Mod.CONFIG.getCommandAlias()).redirect(mainNode));
    }

}