package lv.cebbys.mcmods.respro.api.pack;

import lv.cebbys.mcmods.respro.component.resource.pack.profile.PackProfileResource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Data extends Pack
{
    ResourceLocation location;

    public Data(@NotNull ResourceLocation packLocation, @NotNull PackProfileResource packProfile, @NotNull Pack.ResourcesSupplier supplier) {
        super(packLocation.toString(), packProfile.isAlwaysEnabled(), supplier, Component.literal(packProfile.getName().getAsString()), Objects.requireNonNull(readPackInfo(packLocation.toString(), supplier)), PackCompatibility.forFormat(packProfile.getMeta().getFormat(), PackType.SERVER_DATA), packProfile.getPosition(), packProfile.isPinned(), PackSource.create((name) -> {
            Component text = Component.literal(packProfile.getSource().getAsString());
            return Component.translatable("pack.nameAndSource", name, text).withStyle(ChatFormatting.GRAY);
        }, true));
        location = packLocation;
    }
}