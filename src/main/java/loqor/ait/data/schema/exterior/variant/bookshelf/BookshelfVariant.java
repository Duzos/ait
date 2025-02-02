package loqor.ait.data.schema.exterior.variant.bookshelf;

import net.minecraft.util.math.Vec3d;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.tardis.animation.ExteriorAnimation;
import loqor.ait.core.tardis.animation.PulsatingAnimation;
import loqor.ait.data.Loyalty;
import loqor.ait.data.schema.door.DoorSchema;
import loqor.ait.data.schema.door.impl.BookshelfDoorVariant;
import loqor.ait.data.schema.exterior.ExteriorVariantSchema;
import loqor.ait.data.schema.exterior.category.BookshelfCategory;
import loqor.ait.registry.impl.door.DoorRegistry;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class BookshelfVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/bookshelf/bookshelf_";

    protected BookshelfVariant(String name) { // idk why i added the modid bit i dont use it later lol
        super(BookshelfCategory.REFERENCE, AITMod.id("exterior/bookshelf/" + name),
                new Loyalty(Loyalty.Type.COMPANION));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(BookshelfDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return switch (direction) {
            case 0 -> pos.add(0, 0.207, -0.628); // NORTH
            case 1, 2, 3 -> pos; // NORTH EAST
            case 4 -> pos.add(0.628, 0.207, 0); // EAST
            case 5, 6, 7 -> pos; // SOUTH EAST
            case 8 -> pos.add(0, 0.207, 0.628); // SOUTH
            case 9, 10, 11 -> pos; // SOUTH WEST
            case 12 -> pos.add(-0.628, 0.207, 0); // WEST
            case 13, 14, 15 -> pos; // NORTH WEST
            default -> pos;
        };
    }

    @Override
    public double portalHeight() {
        return 2.3d;
    }

    @Override
    public double portalWidth() {
        return 1.145d;
    }
}
