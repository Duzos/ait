package dev.pavatus.planet.mixin.gravity;

import dev.pavatus.planet.core.planet.Planet;
import dev.pavatus.planet.core.planet.PlanetRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


@Mixin({AbstractMinecartEntity.class, ItemEntity.class, TntEntity.class})
public abstract class AssortedEntityMixin extends Entity {

    public AssortedEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void ait$tick(CallbackInfo ci) {
        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());

        if (planet == null)
            return;

        if (!planet.hasGravityModifier())
            return;

        Vec3d movement = this.getVelocity();
        this.setVelocity(movement.x, movement.y + (planet.gravity() == 0.075f ? 0.025f : planet.gravity() - 0.02f), movement.z); // todo - gravity broken on this
    }
}
