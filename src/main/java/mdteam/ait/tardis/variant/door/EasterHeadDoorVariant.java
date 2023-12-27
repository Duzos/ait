package mdteam.ait.tardis.variant.door;

import mdteam.ait.AITMod;
import mdteam.ait.client.models.doors.ClassicDoorModel;
import mdteam.ait.client.models.doors.DoorModel;
import mdteam.ait.client.models.doors.EasterHeadDoorModel;
import net.minecraft.util.Identifier;

public class EasterHeadDoorVariant extends DoorSchema {

    public static final Identifier REFERENCE = new Identifier(AITMod.MOD_ID, "door/easter_head");

    public EasterHeadDoorVariant() {
        super(REFERENCE);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public DoorModel model() {
        return new EasterHeadDoorModel(EasterHeadDoorModel.getTexturedModelData().createModel());
    }

}
