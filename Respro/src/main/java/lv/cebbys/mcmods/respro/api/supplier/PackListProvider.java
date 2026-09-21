package lv.cebbys.mcmods.respro.api.supplier;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

public interface PackListProvider<T extends Pack> extends RepositorySource
{
    @NotNull ResourceLocation getId();

    @NotNull List<T> getPacks();

    @Override
    default void loadPacks(Consumer<Pack> consumer) {
        getPacks().forEach(consumer);
    }
}