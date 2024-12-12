package loqor.ait.core.tardis.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.pavatus.register.unlockable.Unlockable;

import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import loqor.ait.AITMod;
import loqor.ait.api.KeyedTardisComponent;
import loqor.ait.core.sounds.flight.FlightSound;
import loqor.ait.core.sounds.flight.FlightSoundRegistry;
import loqor.ait.core.sounds.travel.TravelSound;
import loqor.ait.core.sounds.travel.TravelSoundRegistry;
import loqor.ait.core.sounds.travel.map.TravelSoundMap;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.core.tardis.vortex.reference.VortexReference;
import loqor.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import loqor.ait.core.util.Cachable;
import loqor.ait.core.util.ServerLifecycleHooks;
import loqor.ait.data.Exclude;
import loqor.ait.data.properties.Property;
import loqor.ait.data.properties.Value;
import loqor.ait.data.properties.bool.BoolProperty;
import loqor.ait.data.properties.bool.BoolValue;
import loqor.ait.data.schema.desktop.TardisDesktopSchema;
import loqor.ait.registry.impl.DesktopRegistry;

public class StatsHandler extends KeyedTardisComponent {

    private static final Identifier NAME_PATH = new Identifier(AITMod.MOD_ID, "tardis_names.json");
    private static List<String> NAME_CACHE;

    private static final Property<String> NAME = new Property<>(Property.Type.STR, "name", "");
    private static final Property<String> PLAYER_CREATOR_NAME = new Property<>(Property.Type.STR, "player_creator_name",
            "");
    private static final Property<String> DATE = new Property<>(Property.Type.STR, "date", "");
    private static final Property<RegistryKey<World>> SKYBOX = new Property<>(Property.Type.WORLD_KEY, "skybox",
            World.END);
    private static final Property<HashSet<String>> UNLOCKS = new Property<>(Property.Type.STR_SET, "unlocks",
            new HashSet<>());
    private static final Property<Identifier> DEMAT_FX = new Property<>(Property.Type.IDENTIFIER, "demat_fx", new Identifier(""));
    private static final Property<Identifier> MAT_FX = new Property<>(Property.Type.IDENTIFIER, "mat_fx", new Identifier(""));
    private static final Property<Identifier> FLIGHT_FX = new Property<>(Property.Type.IDENTIFIER, "flight_fx", new Identifier(""));
    private static final Property<Identifier> VORTEX_FX = new Property<>(Property.Type.IDENTIFIER, "vortex_fx", new Identifier(""));
    private static final BoolProperty SECURITY = new BoolProperty("security", false);
    private static final BoolProperty HAIL_MARY = new BoolProperty("hail_mary", false);
    private static final BoolProperty RECEIVE_CALLS = new BoolProperty("receive_calls", true);

    private final Value<String> tardisName = NAME.create(this);
    private final Value<String> playerCreatorName = PLAYER_CREATOR_NAME.create(this);
    private final Value<String> creationDate = DATE.create(this);
    private final Value<RegistryKey<World>> skybox = SKYBOX.create(this);
    private final Value<HashSet<String>> unlocks = UNLOCKS.create(this);
    private final BoolValue security = SECURITY.create(this);
    private final BoolValue hailMary = HAIL_MARY.create(this);
    private final BoolValue receiveCalls = RECEIVE_CALLS.create(this);
    private final Value<Identifier> dematId = DEMAT_FX.create(this);
    private final Value<Identifier> matId = MAT_FX.create(this);
    private final Value<Identifier> flightId = FLIGHT_FX.create(this);
    private final Value<Identifier> vortexId = VORTEX_FX.create(this);

    @Exclude
    private Cachable<TravelSoundMap> travelFxCache;
    @Exclude
    private Cachable<FlightSound> flightFxCache;
    @Exclude
    private Cachable<VortexReference> vortexFxCache;

    public StatsHandler() {
        super(Id.STATS);
    }

    @Override
    public void onCreate() {
        this.markCreationDate();
        this.setName(StatsHandler.getRandomName());
    }

    @Override
    public void onLoaded() {
        skybox.of(this, SKYBOX);
        unlocks.of(this, UNLOCKS);
        tardisName.of(this, NAME);
        playerCreatorName.of(this, PLAYER_CREATOR_NAME);
        creationDate.of(this, DATE);
        security.of(this, SECURITY);
        hailMary.of(this, HAIL_MARY);
        receiveCalls.of(this, RECEIVE_CALLS);
        dematId.of(this, DEMAT_FX);
        matId.of(this, MAT_FX);
        flightId.of(this, FLIGHT_FX);
        vortexId.of(this, VORTEX_FX);

        vortexId.addListener((id) -> {
            if (this.vortexFxCache != null)
                this.vortexFxCache.invalidate();
            else this.getVortexEffects();
        });
        flightId.addListener((id) -> {
            if (this.flightFxCache != null)
                this.flightFxCache.invalidate();
            else this.getFlightEffects();
        });
        dematId.addListener((id) -> {
            if (this.travelFxCache != null)
                this.travelFxCache.invalidate();
            else this.getTravelEffects();
        });
        matId.addListener((id) -> {
            if (this.travelFxCache != null)
                this.travelFxCache.invalidate();
            else this.getTravelEffects();
        });

        for (Iterator<TardisDesktopSchema> it = DesktopRegistry.getInstance().iterator(); it.hasNext();) {
            this.unlock(it.next(), false);
        }
    }

    public boolean isUnlocked(Unlockable unlockable) {
        return this.unlocks.get().contains(unlockable.id().toString());
    }

    public void unlock(Unlockable unlockable) {
        this.unlock(unlockable, true);
    }

    private void unlock(Unlockable unlockable, boolean sync) {
        this.unlocks.flatMap(strings -> {
            strings.add(unlockable.id().toString());
            return strings;
        }, sync);
    }

    public Value<RegistryKey<World>> skybox() {
        return skybox;
    }

    public String getName() {
        String name = tardisName.get();

        if (name == null) {
            name = getRandomName();
            this.setName(name);
        }

        return name;
    }

    public BoolValue security() {
        return security;
    }

    public BoolValue hailMary() {
        return hailMary;
    }
    public BoolValue receiveCalls() {
        return this.receiveCalls;
    }

    public String getPlayerCreatorName() {
        String name = playerCreatorName.get();

        if (name == null) {
            name = getRandomName();
            this.setPlayerCreatorName(name);
        }

        return name;
    }

    public void setName(String name) {
        tardisName.set(name);
    }

    public void setPlayerCreatorName(String name) {
        playerCreatorName.set(name);
    }

    public static String getRandomName() {
        if (StatsHandler.shouldGenerateNames())
            StatsHandler.loadNames();

        if (NAME_CACHE == null)
            return "";

        return NAME_CACHE.get(AITMod.RANDOM.nextInt(NAME_CACHE.size()));
    }

    public static boolean shouldGenerateNames() {
        return (NAME_CACHE == null || NAME_CACHE.isEmpty());
    }

    private static void loadNames() {
        if (NAME_CACHE == null)
            NAME_CACHE = new ArrayList<>();

        NAME_CACHE.clear();

        try {
            Optional<Resource> resource = ServerLifecycleHooks.get().getResourceManager().getResource(NAME_PATH);

            if (resource.isEmpty()) {
                AITMod.LOGGER.error("ERROR in tardis_names.json:");
                AITMod.LOGGER.error("Missing Resource");
                return;
            }

            InputStream stream = resource.get().getInputStream();

            JsonArray list = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();

            for (JsonElement element : list) {
                NAME_CACHE.add(element.getAsString());
            }
        } catch (IOException e) {
            AITMod.LOGGER.error("ERROR in tardis_names.json", e);
        }
    }

    public Date getCreationDate() {
        Tardis tardis = this.tardis();

        if (creationDate.get() == null) {
            AITMod.LOGGER.error("{} was missing creation date! Resetting to now", tardis.getUuid().toString());
            markCreationDate();
        }

        String date = creationDate.get();

        try {
            return DateFormat.getDateTimeInstance(DateFormat.LONG, 3).parse(date);
        } catch (Exception e) {
            AITMod.LOGGER.error("Failed to parse date from {}", date);
            this.markCreationDate();

            return Date.from(Instant.now());
        }
    }

    public String getCreationString() {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, 3).format(this.getCreationDate());
    }

    public void markCreationDate() {
        creationDate.set(DateFormat.getDateTimeInstance(DateFormat.LONG, 3).format(Date.from(Instant.now())));
    }

    public void markPlayerCreatorName() {
        playerCreatorName.set(this.getPlayerCreatorName());
    }

    public TravelSoundMap getTravelEffects() {
        if (this.travelFxCache == null) {
            this.travelFxCache = new Cachable<>(this::createTravelEffectsCache);
        }

        return this.travelFxCache.get();
    }
    private TravelSoundMap createTravelEffectsCache() {
        TravelSoundMap map = new TravelSoundMap();

        map.put(TravelHandlerBase.State.DEMAT, TravelSoundRegistry.getInstance().getOrElse(this.dematId.get(), TravelSoundRegistry.DEFAULT_DEMAT));
        map.put(TravelHandlerBase.State.MAT, TravelSoundRegistry.getInstance().getOrElse(this.matId.get(), TravelSoundRegistry.DEFAULT_MAT));

        return map;
    }

    public FlightSound getFlightEffects() {
        if (this.flightFxCache == null) {
            this.flightFxCache = new Cachable<>(this::createFlightEffectsCache);
        }

        return this.flightFxCache.get();
    }
    private FlightSound createFlightEffectsCache() {
        return FlightSoundRegistry.getInstance().getOrFallback(this.flightId.get());
    }

    public VortexReference getVortexEffects() {
        if (this.vortexFxCache == null) {
            this.vortexFxCache = new Cachable<>(this::createVortexEffectsCache);
        }

        return this.vortexFxCache.get();
    }
    private VortexReference createVortexEffectsCache() {
        return VortexReferenceRegistry.getInstance().getOrFallback(this.vortexId.get());
    }

    public void setVortexEffects(VortexReference current) {
        this.vortexId.set(current.id());

        if (this.vortexFxCache != null)
            this.vortexFxCache.invalidate();
    }
    public void setFlightEffects(FlightSound current) {
        this.flightId.set(current.id());

        if (this.flightFxCache != null)
            this.flightFxCache.invalidate();
    }
    private void setDematEffects(TravelSound current) {
        this.dematId.set(current.id());

        if (this.travelFxCache != null)
            this.travelFxCache.invalidate();
    }
    private void setMatEffects(TravelSound current) {
        this.matId.set(current.id());

        if (this.travelFxCache != null)
            this.travelFxCache.invalidate();
    }
    public void setTravelEffects(TravelSound current) {
        switch (current.target()) {
            case DEMAT:
                this.setDematEffects(current);
                break;
            case MAT:
                this.setMatEffects(current);
                break;
        }
    }

}
