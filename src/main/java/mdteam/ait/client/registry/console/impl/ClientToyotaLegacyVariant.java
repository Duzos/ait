package mdteam.ait.client.registry.console.impl;

import mdteam.ait.AITMod;
import mdteam.ait.client.models.consoles.ConsoleModel;
import mdteam.ait.client.models.consoles.ToyotaConsoleModel;
import mdteam.ait.client.registry.console.ClientConsoleVariantSchema;
import mdteam.ait.tardis.variant.console.ToyotaBlueVariant;
import mdteam.ait.tardis.variant.console.ToyotaLegacyVariant;
import net.minecraft.util.Identifier;

public class ClientToyotaLegacyVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID, ("textures/blockentities/consoles/toyota_legacy_default.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID, ("textures/blockentities/consoles/toyota_legacy_emission.png"));

    public ClientToyotaLegacyVariant() {
        super(ToyotaLegacyVariant.REFERENCE);
    }

    @Override
    public Identifier texture() {
        return TEXTURE;
    }

    @Override
    public Identifier emission() {
        return EMISSION;
    }
    @Override
    public ConsoleModel model() {
        return new ToyotaConsoleModel(ToyotaConsoleModel.getTexturedModelData().createModel());
    }
}
