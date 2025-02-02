package loqor.ait.data.schema.exterior.variant.plinth;

import net.minecraft.util.math.Vec3d;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.tardis.animation.ExteriorAnimation;
import loqor.ait.core.tardis.animation.PulsatingAnimation;
import loqor.ait.data.Loyalty;
import loqor.ait.data.schema.door.DoorSchema;
import loqor.ait.data.schema.door.impl.PlinthDoorVariant;
import loqor.ait.data.schema.exterior.ExteriorVariantSchema;
import loqor.ait.data.schema.exterior.category.PlinthCategory;
import loqor.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class PlinthVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/plinth/plinth_";

    protected PlinthVariant(String name) {
        super(PlinthCategory.REFERENCE, AITMod.id("exterior/plinth/" + name),
                new Loyalty(Loyalty.Type.COMPANION));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(PlinthDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0.4875, -0.4); // NORTH
            case 1, 2, 3 -> pos.add(0.35f, 0.4875, -0.35f); // NORTH EAST p n
            case 4 -> pos.add(0.2, 0.4875, 0); // EAST
            case 5, 6, 7 -> pos.add(0.35f, 0.4875, 0.35f); // SOUTH EAST p p
            case 8 -> pos.add(0, 0.4875, 0.4); // SOUTH
            case 9, 10, 11 -> pos.add(-0.35f, 0.4875, 0.35f); // SOUTH WEST n p
            case 12 -> pos.add(-0.4, 0.4875, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.35f, 0.4875, -0.35f); // NORTH WEST n n
            default -> pos;
        };
    }

    @Override
    public double portalHeight() {
        return 2.7d;
    }

    @Override
    public double portalWidth() {
        return 0.75d;
    }
}
