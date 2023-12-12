package mdteam.ait.tardis;

import mdteam.ait.core.util.data.Exclude;

/**
 * Base class for all tardis components.
 * @implNote There should be NO logic run in the constructor. If you need to have such logic, implement it in {@link AbstractTardisComponent#init()} method!
 */
public abstract class AbstractTardisComponent {

    @Exclude
    private ITardis tardis;
    private final String id;

    public AbstractTardisComponent(ITardis tardis, String id) {
        this.tardis = tardis;
        this.id = id;
    }

    public void init() { }

    public Init getInitMode() {
        return Init.ALWAYS;
    }

    public ITardis getTardis() {
        return tardis;
    }

    public String getId() {
        return id;
    }

    public void setTardis(ITardis tardis) {
        this.tardis = tardis;
    }

    public enum Init {
        NO_INIT,
        ALWAYS, // always init
        FIRST, // when tardis placed
        DESERIALIZE // when tardis deserialized
    }
}
