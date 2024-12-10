package loqor.ait.data.datapack;

import static loqor.ait.data.datapack.DatapackConsole.EMPTY;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.sounds.flight.FlightSoundRegistry;
import loqor.ait.core.sounds.travel.TravelSoundRegistry;
import loqor.ait.core.sounds.travel.map.TravelSoundMap;
import loqor.ait.core.tardis.animation.ExteriorAnimation;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import loqor.ait.data.Loyalty;
import loqor.ait.data.datapack.exterior.BiomeOverrides;
import loqor.ait.data.schema.door.DoorSchema;
import loqor.ait.data.schema.exterior.ExteriorVariantSchema;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DatapackExterior extends ExteriorVariantSchema {

    public static final Identifier DEFAULT_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/desktop/missing_preview.png");

    protected final Identifier parent;
    protected final Identifier texture;
    protected final Identifier emission;
    protected final BiomeOverrides overrides;
    protected final boolean initiallyDatapack;

    public static final Codec<DatapackExterior> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(ExteriorVariantSchema::id),
                    Identifier.CODEC.fieldOf("category").forGetter(ExteriorVariantSchema::categoryId),
                    Identifier.CODEC.fieldOf("parent").forGetter(DatapackExterior::getParentId),
                    Identifier.CODEC.fieldOf("texture").forGetter(DatapackExterior::texture),
                    Identifier.CODEC.optionalFieldOf("emission", EMPTY).forGetter(DatapackExterior::emission),
                    Loyalty.CODEC.optionalFieldOf("loyalty").forGetter(DatapackExterior::requirement),
                    BiomeOverrides.CODEC.fieldOf("overrides").orElse(BiomeOverrides.EMPTY)
                            .forGetter(DatapackExterior::overrides),
                    TravelSoundMap.CODEC.optionalFieldOf("effects", new TravelSoundMap().of(TravelHandlerBase.State.DEMAT, TravelSoundRegistry.DEFAULT_DEMAT).of(TravelHandlerBase.State.MAT, TravelSoundRegistry.DEFAULT_MAT)).forGetter(ExteriorVariantSchema::effects),
                    Identifier.CODEC.optionalFieldOf("flight", FlightSoundRegistry.DEFAULT.id()).forGetter(DatapackExterior::flightId),
                    Identifier.CODEC.optionalFieldOf("vortex", VortexReferenceRegistry.SPACE.id()).forGetter(DatapackExterior::vortexId),
                    Codec.BOOL.optionalFieldOf("isDatapack", true).forGetter(DatapackExterior::wasDatapack))
            .apply(instance, DatapackExterior::new));

    public DatapackExterior(Identifier id, Identifier category, Identifier parent, Identifier texture,
            Identifier emission, Optional<Loyalty> loyalty, BiomeOverrides overrides, TravelSoundMap effects, Identifier flightId, Identifier vortexId, boolean isDatapack) {
        super(category, id, loyalty, effects, FlightSoundRegistry.getInstance().get(flightId), VortexReferenceRegistry.getInstance().get(vortexId));
        this.parent = parent;
        this.texture = texture;
        this.emission = emission;
        this.initiallyDatapack = isDatapack;
        this.overrides = overrides;
    }

    public static DatapackExterior fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static DatapackExterior fromJson(JsonObject json) {
        AtomicReference<DatapackExterior> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack exterior variant: {}", err);
        });

        return created.get();
    }

    public ExteriorVariantSchema getParent() {
        return ExteriorVariantRegistry.getInstance().get(this.getParentId());
    }

    public Identifier getParentId() {
        return this.parent;
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return this.getParent().animation(exterior);
    }

    @Override
    public DoorSchema door() {
        return this.getParent().door();
    }

    public BiomeOverrides overrides() {
        return overrides;
    }

    @Override
    public VoxelShape bounding(Direction dir) {
        return this.getParent().bounding(dir);
    }

    @Override
    public boolean hasPortals() {
        return this.getParent().hasPortals();
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return this.getParent().adjustPortalPos(pos, direction);
    }

    @Override
    public double portalWidth() {
        return this.getParent().portalWidth();
    }

    @Override
    public double portalHeight() {
        return this.getParent().portalHeight();
    }

    public boolean wasDatapack() {
        return this.initiallyDatapack;
    }

    public Identifier texture() {
        return this.texture;
    }

    public Identifier emission() {
        return this.emission;
    }
    public Identifier flightId() {
        return this.flight().id();
    }
    public Identifier vortexId() {
        return this.vortex().id();
    }
}
