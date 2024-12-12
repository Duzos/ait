package loqor.ait.client.screens.widget;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import loqor.ait.api.Nameable;
import loqor.ait.api.TardisComponent;
import loqor.ait.client.sounds.ClientSoundManager;
import loqor.ait.client.tardis.ClientTardis;
import loqor.ait.core.sounds.flight.FlightSound;
import loqor.ait.core.sounds.flight.FlightSoundRegistry;
import loqor.ait.core.sounds.travel.TravelSound;
import loqor.ait.core.sounds.travel.TravelSoundRegistry;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.handler.ServerHumHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.core.tardis.vortex.reference.VortexReference;
import loqor.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import loqor.ait.data.hum.Hum;
import loqor.ait.registry.impl.HumRegistry;

public class SwitcherManager<T extends Nameable, U> implements Nameable {
    private final Function<T, T> next;
    private final Function<T, T> previous;
    private final BiConsumer<T, U> sync;
    private T current;
    protected final String id;

    public SwitcherManager(Function<T, T> next, Function<T, T> previous, BiConsumer<T, U> sync, T current, String id) {
        this.next = next;
        this.previous = previous;
        this.sync = sync;
        this.current = current;
        this.id = id.toLowerCase();
    }

    public void next() {
        this.current = this.next.apply(this.current);
    }
    public void previous() {
        this.current = this.previous.apply(this.current);
    }
    public void sync(U arg) {
        this.sync.accept(this.current, arg);
    }
    public T get() {
        return this.current;
    }

    @Override
    public String name() {
        return id;
    }

    public static class HumSwitcher extends SwitcherManager<Hum, ClientTardis> {
        public HumSwitcher(Hum current) {
            super(HumSwitcher::next, HumSwitcher::previous, HumSwitcher::sync, current, "hum");
        }
        public HumSwitcher(Tardis tardis) {
            this(tardis.<ServerHumHandler>handler(TardisComponent.Id.HUM).getHum());
        }

        private static Hum next(Hum current) {
            List<Hum> list = HumRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx < 0 || idx + 1 == list.size())
                return list.get(0);
            return list.get(idx + 1);
        }

        private static Hum previous(Hum current) {
            List<Hum> list = HumRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx <= 0)
                return list.get(list.size() - 1);
            return list.get(idx - 1);
        }

        private static void sync(Hum current, ClientTardis tardis) {
            ClientSoundManager.getHum().setServersHum(tardis, current);
        }
    }

    public static class VortexSwitcher extends SwitcherManager<VortexReference, ClientTardis> {
        public VortexSwitcher(VortexReference current) {
            super(VortexSwitcher::next, VortexSwitcher::previous, VortexSwitcher::sync, current, "vortex");
        }
        public VortexSwitcher(Tardis tardis) {
            this(tardis.stats().getVortexEffects());
        }

        private static VortexReference next(VortexReference current) {
            List<VortexReference> list = VortexReferenceRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx < 0 || idx + 1 == list.size())
                return list.get(0);
            return list.get(idx + 1);
        }

        private static VortexReference previous(VortexReference current) {
            List<VortexReference> list = VortexReferenceRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx <= 0)
                return list.get(list.size() - 1);
            return list.get(idx - 1);
        }

        private static void sync(VortexReference current, ClientTardis tardis) {
            tardis.stats().setVortexEffects(current);
        }
    }

    public static class TravelSoundSwitcher extends SwitcherManager<TravelSound, ClientTardis> {
        public final TravelHandlerBase.State target;

        protected TravelSoundSwitcher(TravelSound current, TravelHandlerBase.State target) {
            super((var) -> next(var, target), (var) -> previous(var, target), TravelSoundSwitcher::sync, current, target.name());

            this.target = target;
        }
        public TravelSoundSwitcher(Tardis tardis, TravelHandlerBase.State target) {
            this(tardis.stats().getTravelEffects().get(target), target);
        }

        private static TravelSound next(TravelSound current, TravelHandlerBase.State target) {
            TravelSound found = current;

            while (found == null || found.target() != target || found == current) {
                found = nextOfAnyState(found);
            }

            return found;
        }
        private static TravelSound nextOfAnyState(TravelSound current) {
            List<TravelSound> list = TravelSoundRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx < 0 || idx + 1 == list.size())
                return list.get(0);
            return list.get(idx + 1);
        }

        private static TravelSound previous(TravelSound current, TravelHandlerBase.State target) {
            TravelSound found = current;

            while (found == null || found.target() != target || found == current) {
                found = previousOfAnyState(found);
            }

            return found;
        }
        private static TravelSound previousOfAnyState(TravelSound current) {
            List<TravelSound> list = TravelSoundRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx <= 0)
                return list.get(list.size() - 1);
            return list.get(idx - 1);
        }

        private static void sync(TravelSound current, ClientTardis tardis) {
            tardis.stats().setTravelEffects(current);
        }
    }

    public static class FlightSoundSwitcher extends SwitcherManager<FlightSound, ClientTardis> {
        protected FlightSoundSwitcher(FlightSound current) {
            super(FlightSoundSwitcher::next, FlightSoundSwitcher::previous, FlightSoundSwitcher::sync, current, "flight");
        }
        public FlightSoundSwitcher(Tardis tardis) {
            this(tardis.stats().getFlightEffects());
        }

        private static FlightSound next(FlightSound current) {
            List<FlightSound> list = FlightSoundRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx < 0 || idx + 1 == list.size())
                return list.get(0);
            return list.get(idx + 1);
        }

        private static FlightSound previous(FlightSound current) {
            List<FlightSound> list = FlightSoundRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            if (idx <= 0)
                return list.get(list.size() - 1);
            return list.get(idx - 1);
        }

        private static void sync(FlightSound current, ClientTardis tardis) {
            tardis.stats().setFlightEffects(current);
        }
    }

    public static class ModeManager extends SwitcherManager<SwitcherManager, Object> {
        private final Tardis tardis;

        public ModeManager(Tardis tardis) {
            super((var) -> next(var, tardis), (var) -> previous(var, tardis), ModeManager::sync, new HumSwitcher(tardis), "mode");
            this.tardis = tardis;
        }

        private static SwitcherManager next(SwitcherManager current, Tardis tardis) {
            return switch (current.id) {
                case "hum" -> new VortexSwitcher(tardis);
                case "vortex" -> new FlightSoundSwitcher(tardis);
                case "flight" -> new TravelSoundSwitcher(tardis, TravelHandlerBase.State.DEMAT);
                case "demat" -> new TravelSoundSwitcher(tardis, TravelHandlerBase.State.MAT);
                default -> new HumSwitcher(tardis);
            };
        }
        private static SwitcherManager previous(SwitcherManager current, Tardis tardis) {
            return switch (current.id) {
                case "hum" -> new TravelSoundSwitcher(tardis, TravelHandlerBase.State.MAT);
                case "vortex" -> new HumSwitcher(tardis);
                case "flight" -> new VortexSwitcher(tardis);
                case "demat" -> new FlightSoundSwitcher(tardis);
                default -> new TravelSoundSwitcher(tardis, TravelHandlerBase.State.DEMAT);
            };
        }
        private static void sync(SwitcherManager current, Object object) {
            current.sync(object);
        }
    }
}
