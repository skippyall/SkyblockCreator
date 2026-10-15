package io.github.null2264.skyblockcreator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;

public class StructureConfig {
    public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("structure").forGetter(s -> s.structure),
            Vec3i.CODEC.fieldOf("structureOffset").forGetter(s -> s.structureOffset),
            Vec3i.CODEC.fieldOf("playerSpawnOffset").forGetter(s -> s.playerSpawnOffset)
    ).apply(instance, StructureConfig::new));

    private final String structure;
    private final Vec3i structureOffset;
    private final Vec3i playerSpawnOffset;

    public StructureConfig(String structure, Vec3i structureOffset, Vec3i playerSpawnOffset) {
        this.structure = structure;
        this.structureOffset = structureOffset;
        this.playerSpawnOffset = playerSpawnOffset;
    }

    public String getStructure() {
        return structure;
    }

    public Vec3i getStructureOffset() {
        return structureOffset;
    }

    public Vec3i getPlayerSpawnOffset() {
        return playerSpawnOffset;
    }
}
