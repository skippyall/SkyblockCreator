package io.github.null2264.skyblockcreator.command;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.data.ModConfig;
import io.github.null2264.skyblockcreator.data.StructureConfig;
import io.github.null2264.skyblockcreator.error.AlreadyHaveIsland;
import io.github.null2264.skyblockcreator.error.InvalidChunkGenerator;
import io.github.null2264.skyblockcreator.error.NoIslandFound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;

public class StructureWorldState extends SavedData
{
    private @Nullable ResourceLocation preset;
    private final HashMap<UUID, BlockPos> playerMap = new HashMap<>();
    private int x, y, dx, dy;
    private boolean spawnGenerated;

    public StructureWorldState() {
        this.x = Mod.CONFIG.getPlatformDistanceRadius();
        this.y = 0;
        this.dx = Mod.CONFIG.getPlatformDistanceRadius();
        this.dy = 0;
        this.spawnGenerated = false;
    }

    public static StructureWorldState createFromNbt(CompoundTag tag) {
        StructureWorldState state = new StructureWorldState();
        if(tag.contains("preset")) {
            state.preset = ResourceLocation.tryParse(tag.getString("preset"));
        }
        state.playerMap.clear();
        CompoundTag playerMapTag = tag.getCompound("playerMap");
        playerMapTag.getAllKeys().forEach(key -> {
            try {
                UUID uuid = UUID.fromString(key);
                BlockPos pos = BlockPos.of(playerMapTag.getLong(key));
                state.playerMap.put(uuid, pos);
            } catch (IllegalArgumentException ignored) {
            }
        });
        state.x = tag.getInt("x");
        state.y = tag.getInt("y");
        state.dx = tag.getInt("dx");
        state.dy = tag.getInt("dy");
        state.spawnGenerated = tag.getBoolean("spawnGenerated");
        return state;
    }

    public static void createInitial(LevelStorageSource.LevelStorageAccess storageAccess, ResourceKey<WorldPreset> preset) {
        Path structureWorldStatePath = storageAccess.getDimensionPath(ServerLevel.OVERWORLD).resolve("data/structureIslands.dat");

        try {
            Files.createDirectories(structureWorldStatePath.getParent());
        } catch (IOException e) {
            Mod.LOGGER.error("Could not create data directory", e);
        }
        StructureWorldState state = new StructureWorldState();
        state.preset = preset.location();
        state.setDirty();
        state.save(structureWorldStatePath.toFile());
    }

    public boolean isNotStructureWorld() {
        return !Mod.CONFIG.getStructureWorldConfigMap().containsKey(preset);
    }

    public void createIsland(ServerLevel world, ServerPlayer playerEntity) throws InvalidChunkGenerator, AlreadyHaveIsland {
        if (isNotStructureWorld()) {
            throw new InvalidChunkGenerator();
        }
        BlockPos islandPos = this.getIsland(playerEntity);
        if (islandPos != null) {
            throw new AlreadyHaveIsland();
        }
        islandPos = this.generateIsland(world, playerEntity);
        playerEntity.teleportToWithTicket(islandPos.getX(), islandPos.getY(), islandPos.getZ());
    }

    public void deleteIsland(ServerPlayer playerEntity) {
        playerMap.remove(playerEntity.getUUID());
    }

    public void deleteIsland(ServerLevel world, ServerPlayer playerEntity) throws InvalidChunkGenerator, NoIslandFound {
        if (isNotStructureWorld()) {
            throw new InvalidChunkGenerator();
        }
        BlockPos islandPos = this.getIsland(playerEntity);
        if (islandPos == null) {
            throw new NoIslandFound();
        }
        this.deleteIsland(playerEntity);
        this.teleportToIsland(world, playerEntity, Util.NIL_UUID);
    }

    public BlockPos getSpawnIsland() {
        return getIsland(Util.NIL_UUID);
    }

    public BlockPos getIsland(ServerPlayer playerEntity) {
        return getIsland(playerEntity.getUUID());
    }

    public BlockPos getIsland(UUID id) {
        return playerMap.get(id);
    }

    public BlockPos generateIsland(ServerLevel world) {
        BlockPos islandPos = generateIsland(world, Util.NIL_UUID);
        this.spawnGenerated = true;
        return islandPos;
    }

    public BlockPos generateIsland(ServerLevel world, ServerPlayer playerEntity) {
        return generateIsland(world, playerEntity.getUUID());
    }

    public BlockPos generateIsland(ServerLevel world, UUID uuid) {
        int x = this.x;
        int y = this.y;
        int dx = this.dx;
        int dy = this.dy;
        if (!playerMap.containsKey(uuid)) {
            ModConfig.StructureWorldConfig optionalConfig = Mod.CONFIG.getStructureWorldConfigMap().get(preset);
            if (optionalConfig != null) {
                StructureConfig config = optionalConfig.getStructureConfig();
                HolderGetter<Block> blockLookup = world.registryAccess().registryOrThrow(Registries.BLOCK).asLookup();
                CompoundTag structureTemplateNbt = Mod.STRUCTURES.get(config.getStructure());
                Vec3i playerSpawnOffset = config.getPlayerSpawnOffset();
                Vec3i structureOffset = config.getStructureOffset();

                BlockPos origin = new BlockPos(8, 64, 8);
                BlockPos island;

                if (!uuid.equals(Util.NIL_UUID)) {
                    island = origin.offset(this.x, 0, this.y);
                    if ((x == y) || (x < 0 && x == -y) || (x > 0 && x == Mod.CONFIG.getPlatformDistanceRadius() - y)) {
                        this.dx = -dy;
                        this.dy = dx;
                    }
                    this.x = x + this.dx;
                    this.y = y + this.dy;
                } else {
                    island = origin;
                }

                if (structureTemplateNbt != null && !(uuid.equals(Util.NIL_UUID) && spawnGenerated)) {
                    StructureTemplate structureTemplate = new StructureTemplate();
                    structureTemplate.load(blockLookup, structureTemplateNbt);
                    structureTemplate.placeInWorld(world, island.offset(structureOffset), island.offset(structureOffset), new StructurePlaceSettings(), world.random, Block.UPDATE_ALL);
                }
                playerMap.put(uuid, island.offset(playerSpawnOffset));
            }
            setDirty();
        }
        return playerMap.get(uuid);
    }

    public void teleportToIsland(ServerLevel world, ServerPlayer player) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, player);
    }

    public void teleportToIsland(ServerLevel world, ServerPlayer player, ServerPlayer owner) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, owner.getUUID());
    }

    public void teleportToIsland(ServerLevel world, ServerPlayer player, UUID uuid) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, uuid, false);
    }

    public void teleportToIsland(ServerLevel world, ServerPlayer player, UUID uuid, Boolean forceTeleport) throws InvalidChunkGenerator, NoIslandFound {
        if (isNotStructureWorld() && !uuid.equals(Util.NIL_UUID)) {
            throw new InvalidChunkGenerator();
        }
        BlockPos islandPos = this.getIsland(uuid);
        if (islandPos == null) {
            if (!uuid.equals(Util.NIL_UUID) && !forceTeleport) throw new NoIslandFound();
            else {
                BlockPos spawnIsland = this.getSpawnIsland();
                islandPos = spawnIsland != null ? spawnIsland : this.generateIsland(world);
            }
        }

        if (forceTeleport) player.setPos(islandPos.getX(), islandPos.getY(), islandPos.getZ());
        else player.teleportToWithTicket(islandPos.getX(), islandPos.getY(), islandPos.getZ());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag playerMapTag = new CompoundTag();
        if(preset != null) {
            tag.putString("preset", preset.toString());
        }
        playerMap.forEach((uuid, blockPos) -> playerMapTag.putLong(uuid.toString(), blockPos.asLong()));
        tag.put("playerMap", playerMapTag);
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("dx", dx);
        tag.putInt("dy", dy);
        tag.putBoolean("spawnGenerated", spawnGenerated);
        return tag;
    }

}