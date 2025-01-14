package loqor.ait.data.schema.exterior.variant.box.client;

import org.joml.Vector3f;

import loqor.ait.core.tardis.handler.BiomeHandler;
import loqor.ait.data.datapack.exterior.BiomeOverrides;

public class ClientPoliceBoxRenaissanceVariant extends ClientPoliceBoxVariant {

    private final BiomeOverrides OVERRIDES = BiomeOverrides.builder(ClientPoliceBoxVariant.OVERRIDES)
            .with(type -> type.getTexture(this.texture()), BiomeHandler.BiomeType.SNOWY, BiomeHandler.BiomeType.CHORUS,
                    BiomeHandler.BiomeType.SCULK)
            .build();

    public ClientPoliceBoxRenaissanceVariant() {
        super("renaissance");
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.56f, 1.45f, 1.2f);
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
