package loqor.ait.core;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import loqor.ait.AITMod;

public class AITDimensions {
    public static final RegistryKey<World> TIME_VORTEX_WORLD = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(AITMod.MOD_ID, "time_vortex"));

    public static final RegistryKey<World> MARS = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(AITMod.MOD_ID, "mars"));
    public static final RegistryKey<World> MOON = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(AITMod.MOD_ID, "moon"));
    public static final RegistryKey<World> SPACE = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(AITMod.MOD_ID, "space"));
}
