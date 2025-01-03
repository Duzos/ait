package loqor.ait.core;

import dev.pavatus.multidim.MultiDim;
import dev.pavatus.multidim.api.VoidChunkGenerator;
import dev.pavatus.multidim.api.WorldBlueprint;
import loqor.ait.core.world.TardisServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

import loqor.ait.AITMod;

public class AITDimensions {
    public static final RegistryKey<World> TIME_VORTEX_WORLD = RegistryKey.of(RegistryKeys.WORLD,
            AITMod.id("time_vortex"));

    public static final RegistryKey<World> MARS = RegistryKey.of(RegistryKeys.WORLD,
            AITMod.id("mars"));
    public static final RegistryKey<World> MOON = RegistryKey.of(RegistryKeys.WORLD,
            AITMod.id("moon"));
    public static final RegistryKey<World> SPACE = RegistryKey.of(RegistryKeys.WORLD,
            AITMod.id("space"));

    public static WorldBlueprint TARDIS_WORLD_BLUEPRINT;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            TARDIS_WORLD_BLUEPRINT = new WorldBlueprint(AITMod.id("tardis"))
                    .setPersistent(true).shouldTickTime(false).withCreator(TardisServerWorld::new)
                    .withType(AITMod.id("tardis_dimension_type"))
                    .withGenerator(new VoidChunkGenerator(
                            server.getRegistryManager().get(RegistryKeys.BIOME),
                            RegistryKey.of(RegistryKeys.BIOME, AITMod.id("tardis"))
                    ));

            MultiDim.get(server).register(TARDIS_WORLD_BLUEPRINT);
        });
    }
}
