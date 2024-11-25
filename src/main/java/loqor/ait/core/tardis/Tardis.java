package loqor.ait.core.tardis;

import java.util.Objects;
import java.util.UUID;

import dev.pavatus.register.unlockable.Unlockable;

import net.minecraft.world.World;

import loqor.ait.api.Initializable;
import loqor.ait.api.TardisComponent;
import loqor.ait.client.tardis.ClientTardis;
import loqor.ait.core.tardis.control.sequences.SequenceHandler;
import loqor.ait.core.tardis.handler.*;
import loqor.ait.core.tardis.handler.LandingPadHandler;
import loqor.ait.core.tardis.handler.LoyaltyHandler;
import loqor.ait.core.tardis.handler.permissions.PermissionHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandler;
import loqor.ait.registry.impl.DesktopRegistry;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;

public abstract class Tardis extends Initializable<TardisComponent.InitContext> {

    private UUID uuid;
    protected TardisDesktop desktop;
    protected TardisExterior exterior;
    protected TardisHandlersManager handlers;

    protected Tardis(UUID uuid, TardisDesktop desktop, TardisExterior exterior) {
        this.uuid = uuid;
        this.desktop = desktop;
        this.exterior = exterior;
        this.handlers = new TardisHandlersManager();
    }

    protected Tardis() {
    }

    @Override
    protected void onInit(TardisComponent.InitContext ctx) {
        TardisComponent.init(desktop, this, ctx);
        TardisComponent.init(exterior, this, ctx);
        TardisComponent.init(handlers, this, ctx);
    }

    public static void init(Tardis tardis, TardisComponent.InitContext ctx) {
        Initializable.init(tardis, ctx);
    }

    public UUID getUuid() {
        return uuid;
    }

    public ServerTardis asServer() {
        return (ServerTardis) this;
    }

    public ClientTardis asClient() {
        return (ClientTardis) this;
    }

    public TardisDesktop getDesktop() {
        return desktop;
    }

    public TardisExterior getExterior() {
        return exterior;
    }

    public DoorHandler door() {
        return this.getHandlers().get(TardisComponent.Id.DOOR);
    }

    public SonicHandler sonic() {
        return this.handler(TardisComponent.Id.SONIC);
    }

    public boolean getLockedTardis() {
        return this.door().locked();
    }

    public TravelHandler travel() {
        return this.handler(TardisComponent.Id.TRAVEL);
    }

    public RealFlightHandler flight() {
        return this.handler(TardisComponent.Id.FLIGHT);
    }

    public LoyaltyHandler loyalty() {
        return this.handler(TardisComponent.Id.LOYALTY);
    }

    public ServerAlarmHandler alarm() {
        return this.handler(TardisComponent.Id.ALARMS);
    }

    public StatsHandler stats() {
        return this.handler(TardisComponent.Id.STATS);
    }

    public InteriorChangingHandler interiorChangingHandler() {
        return this.handler(TardisComponent.Id.INTERIOR);
    }

    public EngineHandler engine() {
        return this.handler(TardisComponent.Id.ENGINE);
    }

    public ChameleonHandler chameleon() {
        return this.handler(TardisComponent.Id.CHAMELEON);
    }

    /**
     * Retrieves the TardisHandlersManager instance associated with the given UUID.
     *
     * @return TardisHandlersManager instance or null if it doesn't exist
     */
    public TardisHandlersManager getHandlers() {
        return handlers;
    }

    public <T extends TardisComponent> T handler(TardisComponent.IdLike type) {
        return this.handlers.get(type);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public double addFuel(double fuel) {
        return this.<FuelHandler>handler(TardisComponent.Id.FUEL).addFuel(fuel);
    }

    public void removeFuel(double fuel) {
        this.<FuelHandler>handler(TardisComponent.Id.FUEL).removeFuel(fuel);
    }

    public double getFuel() {
        return this.<FuelHandler>handler(TardisComponent.Id.FUEL).getCurrentFuel();
    }

    public void setFuelCount(double i) {
        this.<FuelHandler>handler(TardisComponent.Id.FUEL).setCurrentFuel(i);
    }

    public boolean isRefueling() {
        return this.<FuelHandler>handler(TardisComponent.Id.FUEL).refueling().get();
    }

    public void setRefueling(boolean b) {
        this.<FuelHandler>handler(TardisComponent.Id.FUEL).refueling().set(b);
    }

    public boolean isInDanger() {
        return this.<HadsHandler>handler(TardisComponent.Id.HADS).isInDanger();
    }

    public FuelHandler fuel() {
        return this.handler(TardisComponent.Id.FUEL);
    }

    public TardisCrashHandler crash() {
        return this.handler(TardisComponent.Id.CRASH_DATA);
    }

    public SequenceHandler sequence() {
        return this.handler(TardisComponent.Id.SEQUENCE);
    }

    public WaypointHandler waypoint() {
        return this.handler(TardisComponent.Id.WAYPOINTS);
    }

    public boolean isUnlocked(Unlockable unlockable) {
        return unlockable.freebie() || this.stats().isUnlocked(unlockable);
    }

    // for now this just checks that the exterior is the coral growth, which is bad.
    // but its fine
    // for
    // first beta
    // this should stop basic features of the tardis from happening
    public boolean isGrowth() {
        return hasGrowthExterior() || hasGrowthDesktop();
    }

    public boolean hasGrowthExterior() {
        return Objects.equals(getExterior().getVariant(), ExteriorVariantRegistry.CORAL_GROWTH);
    }

    public boolean hasGrowthDesktop() {
        return Objects.equals(getDesktop().getSchema(), DesktopRegistry.DEFAULT_CAVE);
    }

    public boolean areShieldsActive() {
        return this.<ShieldHandler>handler(TardisComponent.Id.SHIELDS).shielded().get();
    }

    public boolean areVisualShieldsActive() {
        return this.<ShieldHandler>handler(TardisComponent.Id.SHIELDS).visuallyShielded().get();
    }

    public SiegeHandler siege() {
        return this.handler(TardisComponent.Id.SIEGE);
    }

    public boolean isSiegeBeingHeld() {
        return this.<SiegeHandler>handler(TardisComponent.Id.SIEGE).isSiegeBeingHeld();
    }

    public void setSiegeBeingHeld(UUID b) {
        this.<SiegeHandler>handler(TardisComponent.Id.SIEGE).setSiegeBeingHeld(b);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public PermissionHandler permissions() {
        return this.handler(TardisComponent.Id.PERMISSIONS);
    }

    public OvergrownHandler overgrown() {
        return this.handler(TardisComponent.Id.OVERGROWN);
    }

    public CloakHandler cloak() {
        return this.handler(TardisComponent.Id.CLOAK);
    }

    public LandingPadHandler landingPad() {
        return this.handler(TardisComponent.Id.LANDING_PAD);
    }

    public SelfDestructHandler selfDestruct() {
        return this.handler(TardisComponent.Id.SELF_DESTRUCT);
    }

    public OpinionHandler opinions() {
        return this.handler(TardisComponent.Id.OPINION);
    }
    public SubSystemHandler subsystems() {
        return this.handler(TardisComponent.Id.SUBSYSTEM);
    }

    public abstract World getInteriorWorld();
}
