package loqor.ait.data.schema.exterior;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.*;
import dev.pavatus.register.unlockable.Unlockable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.sounds.flight.FlightSound;
import loqor.ait.core.sounds.flight.FlightSoundRegistry;
import loqor.ait.core.sounds.travel.TravelSoundRegistry;
import loqor.ait.core.sounds.travel.map.TravelSoundMap;
import loqor.ait.core.tardis.animation.ExteriorAnimation;
import loqor.ait.core.tardis.vortex.reference.VortexReference;
import loqor.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import loqor.ait.data.Loyalty;
import loqor.ait.data.schema.BasicSchema;
import loqor.ait.data.schema.door.DoorSchema;
import loqor.ait.registry.impl.CategoryRegistry;
import loqor.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;

/**
 * A variant for a {@link ExteriorCategorySchema} which provides a model,
 * texture, emission, {@link ExteriorAnimation} and {@link DoorSchema} <br>
 * <br>
 * This should be registered in {@link ExteriorVariantRegistry} <br>
 * <br>
 * This should <b>ONLY</b> be created once in registry, you should grab the
 * class via {@link ExteriorVariantRegistry#get(Identifier)}, the identifier
 * being this variants id variable. <br>
 * <br>
 * It is recommended for implementations of this class to have a static
 * "REFERENCE" {@link Identifier} variable which other things can use to get
 * this from the {@link ExteriorVariantRegistry}
 *
 * @author duzo
 * @see ExteriorVariantRegistry
 */
public abstract class ExteriorVariantSchema extends BasicSchema implements Unlockable {
    private final Identifier category;
    private final Identifier id;
    private final Loyalty loyalty;

    // these three are for removal \/
    private final TravelSoundMap effects;
    private final FlightSound flight;
    private final VortexReference vortex;

    @Environment(EnvType.CLIENT)
    private ClientExteriorVariantSchema cachedSchema;

    protected ExteriorVariantSchema(Identifier category, Identifier id, Optional<Loyalty> loyalty, TravelSoundMap effects, FlightSound flight, VortexReference vortex) {
        super("exterior");
        this.category = category;

        this.id = id;
        this.loyalty = loyalty.orElse(null);
        this.effects = effects;
        this.flight = flight;
        this.vortex = vortex;
    }

    protected ExteriorVariantSchema(Identifier category, Identifier id, Loyalty loyalty, TravelSoundMap effects) {
        this(category, id, Optional.of(loyalty), effects, FlightSoundRegistry.DEFAULT, VortexReferenceRegistry.SPACE);
    }

    protected ExteriorVariantSchema(Identifier category, Identifier id, Loyalty loyalty) {
        this(category, id, Optional.of(loyalty), TravelSoundRegistry.DEFAULT, FlightSoundRegistry.DEFAULT, VortexReferenceRegistry.SPACE);
    }

    protected ExteriorVariantSchema(Identifier category, Identifier id) {
        this(category, id, Optional.empty(), TravelSoundRegistry.DEFAULT, FlightSoundRegistry.DEFAULT, VortexReferenceRegistry.SPACE);
    }

    public static Object serializer() {
        return new Serializer();
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.ofNullable(loyalty);
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.EXTERIOR;
    }

    public Identifier categoryId() {
        return this.category;
    }

    public ExteriorCategorySchema category() {
        return CategoryRegistry.getInstance().get(this.categoryId());
    }

    @Environment(EnvType.CLIENT)
    public ClientExteriorVariantSchema getClient() {
        if (this.cachedSchema == null)
            this.cachedSchema = ClientExteriorVariantRegistry.withParent(this);

        return cachedSchema;
    }

    /**
     * The bounding box for this exterior, will be used in
     * {@link #getNormalShape(BlockState, BlockPos)}
     */
    public VoxelShape bounding(Direction dir) {
        return null;
    }

    public abstract ExteriorAnimation animation(ExteriorBlockEntity exterior);

    public abstract DoorSchema door();

    public boolean hasPortals() {
        return this.category().hasPortals();
    }

    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return pos; // just cus some dont have portals
    }

    public double portalWidth() {
        return 1d;
    }

    public double portalHeight() {
        return 2d;
    }
    @Deprecated(forRemoval = true)
    public TravelSoundMap effects() {
        return this.effects;
    }
    @Deprecated(forRemoval = true)
    public FlightSound flight() {
        return this.flight;
    }
    @Deprecated(forRemoval = true)
    public VortexReference vortex() {
        return this.vortex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ExteriorVariantSchema other && id.equals(other.id);
    }

    private static class Serializer
            implements
            JsonSerializer<ExteriorVariantSchema>,
            JsonDeserializer<ExteriorVariantSchema> {

        @Override
        public ExteriorVariantSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = new Identifier(AITMod.MOD_ID, "capsule_default");
            }

            return ExteriorVariantRegistry.getInstance().get(id);
        }

        @Override
        public JsonElement serialize(ExteriorVariantSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
