package mdteam.ait.tardis.variant.console;

import mdteam.ait.AITMod;
import mdteam.ait.tardis.console.CoralConsole;
import net.minecraft.util.Identifier;

public class BlueCoralVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = new Identifier(AITMod.MOD_ID, "console/coral_blue");

    public BlueCoralVariant() {
        super(CoralConsole.REFERENCE, REFERENCE);
    }
}
