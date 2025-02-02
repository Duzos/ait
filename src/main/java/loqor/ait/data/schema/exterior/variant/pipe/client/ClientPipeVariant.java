package loqor.ait.data.schema.exterior.variant.pipe.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.client.models.exteriors.ExteriorModel;
import loqor.ait.client.models.exteriors.PipeExteriorModel;
import loqor.ait.core.tardis.handler.BiomeHandler;
import loqor.ait.data.datapack.exterior.BiomeOverrides;
import loqor.ait.data.schema.exterior.ClientExteriorVariantSchema;

public abstract class ClientPipeVariant extends ClientExteriorVariantSchema {
    private final String name;
    protected static final String CATEGORY_PATH = "textures/blockentities/exteriors/pipe";
    protected static final Identifier CATEGORY_IDENTIFIER = new Identifier(AITMod.MOD_ID,
            CATEGORY_PATH + "/pipe.png");
    protected static final Identifier BIOME_IDENTIFIER = new Identifier(AITMod.MOD_ID, CATEGORY_PATH + "/biome" + "/pipe.png");
    protected static final String TEXTURE_PATH = CATEGORY_PATH + "/pipe_";

    protected static final BiomeOverrides OVERRIDES = BiomeOverrides.builder()
            .with(type -> type.getTexture(BIOME_IDENTIFIER), BiomeHandler.BiomeType.SNOWY,
                    BiomeHandler.BiomeType.SCULK, BiomeHandler.BiomeType.CHORUS, BiomeHandler.BiomeType.CHERRY,
                    BiomeHandler.BiomeType.SANDY, BiomeHandler.BiomeType.RED_SANDY, BiomeHandler.BiomeType.MUDDY)
            .build();

    protected ClientPipeVariant(String name) {
        super(AITMod.id("exterior/pipe/" + name));

        this.name = name;
    }

    @Override
    public ExteriorModel model() {
        return new PipeExteriorModel(PipeExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier texture() {
        return AITMod.id(TEXTURE_PATH + name + ".png");
    }

    @Override
    public Identifier emission() {
        return null;
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.50f, 2.04f, 1.9f);
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
