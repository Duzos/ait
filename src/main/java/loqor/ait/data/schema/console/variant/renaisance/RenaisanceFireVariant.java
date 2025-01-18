package loqor.ait.data.schema.console.variant.renaisance;

import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.data.Loyalty;
import loqor.ait.data.schema.console.ConsoleVariantSchema;
import loqor.ait.data.schema.console.type.RenaisanceType;

public class RenaisanceFireVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/renaissance_fire");

    public RenaisanceFireVariant() {
        super(RenaisanceType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.OWNER));
    }
}
