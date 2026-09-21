package lv.cebbys.mcmods.respro.constant;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackSource;

public class ResproConstants
{
    public static final String RESPRO = "respro";
    public static final String PACK_MCMETA_PATH = "pack.mcmeta";
    public static final String PACK_ICON_PATH = "pack.png";
    public static final String PACK_SOURCE_STRING_RESPRO = "pack.source.respro";
    public static final PackSource PACK_SOURCE_RESPRO = PackSource.create((name) -> {
        Component text = Component.translatable(PACK_SOURCE_STRING_RESPRO);
        return Component.translatable("pack.nameAndSource", name, text).withStyle(ChatFormatting.GRAY);
    }, true);
    public static final ResourceLocation RESPRO_PACK_MCMETA_LOCATION = new ResourceLocation(RESPRO, PACK_MCMETA_PATH);
    public static final ResourceLocation RESPRO_PACK_ICON_LOCATION = new ResourceLocation(RESPRO, PACK_ICON_PATH);
    // REF: https://minecraft.fandom.com/wiki/Data_pack#Pack_format
    public static final Integer PACK_FORMAT = 15;
}