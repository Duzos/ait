package dev.pavatus.planet.mixin;

import dev.pavatus.planet.core.planet.Planet;
import dev.pavatus.planet.core.planet.PlanetWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.World;

@Mixin(World.class)
public class WorldMixin implements PlanetWorld {

    @Unique private Planet planet;

    @Unique private boolean isAPlanet;

    @Override
    public boolean ait_planet$isAPlanet() {
        return isAPlanet;
    }

    @Override
    public @Nullable Planet ait_planet$getPlanet() {
        return planet;
    }

    @Override
    public void ait_planet$setPlanet(Planet planet) {
        this.planet = planet;
    }

    @Override
    public void ait_planet$setIsAPlanet(boolean isAPlanet) {
        this.isAPlanet = isAPlanet;
    }
}
