package lv.cebbys.mcmods.respro.component.resource.core;

import lv.cebbys.mcmods.respro.component.resource.AbstractResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BinaryResource extends AbstractResource
{
    private byte[] content;
    private boolean nullable;

    public @NotNull InputStream getAsStream() {
        if (content == null) return InputStream.nullInputStream();
        return new ByteArrayInputStream(content);
    }

    public boolean belongsTo(@NotNull PackType type) {
        return true;
    }

    @Override
    public void validate() throws ResourceValidationException {
        if (!nullable) {
            if (content == null) throw new ResourceValidationException("BinaryResource content is null");
        }
    }
}