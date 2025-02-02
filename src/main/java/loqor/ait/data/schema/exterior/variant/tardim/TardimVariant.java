package loqor.ait.data.schema.exterior.variant.tardim;

import net.minecraft.util.math.Vec3d;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.tardis.animation.ExteriorAnimation;
import loqor.ait.core.tardis.animation.PulsatingAnimation;
import loqor.ait.data.Loyalty;
import loqor.ait.data.schema.door.DoorSchema;
import loqor.ait.data.schema.door.impl.TardimDoorVariant;
import loqor.ait.data.schema.exterior.ExteriorVariantSchema;
import loqor.ait.data.schema.exterior.category.TardimCategory;
import loqor.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class TardimVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/tardim/tardim_";

    protected TardimVariant(String name) {
        super(TardimCategory.REFERENCE, AITMod.id("exterior/tardim/" + name),
                new Loyalty(Loyalty.Type.OWNER));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(TardimDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0, -0.499f); // NORTH
            case 1, 2, 3 -> pos.add(0.349f, 0, -0.349f); // NORTH EAST p n
            case 4 -> pos.add(0.499f, 0, 0); // EAST
            case 5, 6, 7 -> pos.add(0.349f, 0, 0.349f); // SOUTH EAST p p
            case 8 -> pos.add(0, 0, 0.499f); // SOUTH
            case 9, 10, 11 -> pos.add(-0.349f, 0, 0.349f); // SOUTH WEST n p
            case 12 -> pos.add(-0.499f, 0, 0); // WEST
            case 13, 14, 15 -> pos.add(-0.349f, 0, -0.349f); // NORTH WEST n n
            default -> pos;
        };
    }
}
