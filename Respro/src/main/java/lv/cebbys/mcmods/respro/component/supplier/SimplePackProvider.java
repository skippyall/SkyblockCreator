package lv.cebbys.mcmods.respro.component.supplier;

import lv.cebbys.mcmods.respro.api.pack.Data;
import lv.cebbys.mcmods.respro.component.resource.pack.ResproResourcePack;
import lv.cebbys.mcmods.respro.utility.IdentifierUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimplePackProvider<T extends Pack>
{
    private final Supplier<ResproResourcePack<?, ?>> packResources;
    private final Function<ResproResourcePack<?, ?>, Data> packCreator;

    public SimplePackProvider(Supplier<ResproResourcePack<?, ?>> packResources, Function<ResproResourcePack<?, ?>, Data> packCreator) {
        this.packResources = packResources;
        this.packCreator = packCreator;
    }

    public @NotNull ResourceLocation getId() {
        return IdentifierUtils.suffix(packResources.get().getId(), "_supplier");
    }

    public @NotNull Data getPack() {
        return packCreator.apply(packResources.get());
    }
}