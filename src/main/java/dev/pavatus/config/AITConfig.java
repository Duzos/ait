package dev.pavatus.config;

import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import loqor.ait.core.AITDimensions;

@Config(name = "aitconfig")
public class AITConfig implements ConfigData {

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public Server SERVER = new Server();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public Client CLIENT = new Client();

    public static AITConfig createAndLoad() {
        AutoConfig.register(AITConfig.class, JanksonConfigSerializer::new);
        return AutoConfig.getConfigHolder(AITConfig.class).getConfig();
    }

    public static class Server {
        public boolean MINIFY_JSON = false;

        public boolean GHOST_MONUMENT = true;
        public boolean LOCK_DIMENSIONS = true;

        @ConfigEntry.Gui.RequiresRestart
        public List<String> WORLDS_BLACKLIST = List.of(
                AITDimensions.TIME_VORTEX_WORLD.getValue().toString());

        public int TRAVEL_PER_TICK = 2;

        public boolean SEND_BULK = true;
        public int MAX_TARDISES = -1;

        @ConfigEntry.Gui.RequiresRestart
        public boolean STP_PRELOADING = false;
    }

    public static class Client {
        public float INTERIOR_HUM_VOLUME = 0.2f;

        public boolean CUSTOM_MENU = true;
        public boolean SHOW_EXPERIMENTAL_WARNING = false;
        public boolean ENVIRONMENT_PROJECTOR = true;
        public boolean DISABLE_LOYALTY_FOG = false;
        public boolean DISABLE_LOYALTY_SLEEPING_ACTIONBAR = false;
        public boolean ENABLE_TARDIS_BOTI = false;
        public boolean I_HATE_GL = true;
        public boolean ANIMATE_CONSOLE = true;

        public TemperatureType TEMPERATURE_TYPE = TemperatureType.CELCIUS;
    }

    public enum TemperatureType {
        CELCIUS,
        FAHRENHEIT,
        KELVIN
    }
}
