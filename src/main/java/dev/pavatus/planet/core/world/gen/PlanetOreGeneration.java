package dev.pavatus.planet.core.world.gen;


import dev.pavatus.planet.core.planet.PlanetBiomeSelectors;
import dev.pavatus.planet.core.world.PlanetPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;

import net.minecraft.world.gen.GenerationStep;


public class PlanetOreGeneration {
    public static void generateOres() {
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_COAL_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_COPPER_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_IRON_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_REDSTONE_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_LAPIS_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_GOLD_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_DIAMOND_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMars(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.MARTIAN_EMERALD_ORE_PLACED_KEY);

        //Anorthosite

        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_COAL_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_COPPER_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_IRON_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_REDSTONE_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_LAPIS_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_GOLD_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_DIAMOND_ORE_PLACED_KEY);
        BiomeModifications.addFeature(PlanetBiomeSelectors.foundInMoon(),
                GenerationStep.Feature.UNDERGROUND_ORES, PlanetPlacedFeatures.ANORTHOSITE_EMERALD_ORE_PLACED_KEY);
    }

}
