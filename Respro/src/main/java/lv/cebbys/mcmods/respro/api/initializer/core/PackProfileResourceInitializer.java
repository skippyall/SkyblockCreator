package lv.cebbys.mcmods.respro.api.initializer.core;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import net.minecraft.server.packs.repository.Pack;

public interface PackProfileResourceInitializer
{
    @NotNull PackProfileResourceInitializer setPackIcon(@NotNull Consumer<ImageResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackName(@NotNull Consumer<StringResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackSource(@NotNull Consumer<StringResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackMeta(@NotNull Consumer<MetaResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackInsertionPosition(@NotNull Pack.Position insertionPosition);

    @NotNull PackProfileResourceInitializer setAlwaysEnabled(boolean packAlwaysEnabled);

    @NotNull PackProfileResourceInitializer setPinned(boolean packPinned);
}