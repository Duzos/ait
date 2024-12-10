package dev.pavatus.planet.core.planet;

import java.util.function.Predicate;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;

import loqor.ait.AITMod;
import loqor.ait.core.AITDimensions;

public class PlanetBiomeSelectors {
    private static final RegistryKey<Registry<DimensionOptions>> DIMENSION_KEY =
            RegistryKey.ofRegistry(new Identifier(AITMod.MOD_ID, "dimension"));

    public static final RegistryKey<DimensionOptions> MARS_DIMENSION_OPTIONS =
            RegistryKey.of(DIMENSION_KEY, AITDimensions.MARS.getValue());

    public static final RegistryKey<DimensionOptions> MOON_DIMENSION_OPTIONS =
            RegistryKey.of(DIMENSION_KEY, AITDimensions.MOON.getValue());

    public static Predicate<BiomeSelectionContext> foundInMars() {
        return context -> context.canGenerateIn(MARS_DIMENSION_OPTIONS);
    }

    public static Predicate<BiomeSelectionContext> foundInMoon() {
        return context -> context.canGenerateIn(MOON_DIMENSION_OPTIONS);
    }
}
