package loqor.ait.core.tardis.handler;

import dev.pavatus.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import loqor.ait.api.KeyedTardisComponent;
import loqor.ait.api.TardisEvents;
import loqor.ait.api.TardisTickable;
import loqor.ait.core.tardis.handler.travel.TravelHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.data.Loyalty;
import loqor.ait.data.properties.bool.BoolProperty;
import loqor.ait.data.properties.bool.BoolValue;

public class ShieldHandler extends KeyedTardisComponent implements TardisTickable {
    private static final BoolProperty IS_SHIELDED = new BoolProperty("is_shielded", false);
    private final BoolValue isShielded = IS_SHIELDED.create(this);
    public static BoolProperty IS_VISUALLY_SHIELDED = new BoolProperty("is_visually_shielded", false);
    private final BoolValue isVisuallyShielded = IS_VISUALLY_SHIELDED.create(this);

    public ShieldHandler() {
        super(Id.SHIELDS);
    }

    @Override
    public void onLoaded() {
        isShielded.of(this, IS_SHIELDED);
        isVisuallyShielded.of(this, IS_VISUALLY_SHIELDED);
    }

    public BoolValue shielded() {
        return isShielded;
    }

    public BoolValue visuallyShielded() {
        return isVisuallyShielded;
    }

    public void enable() {
        this.shielded().set(true);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, true, this.visuallyShielded().get());
    }

    public void disable() {
        this.shielded().set(false);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, false, this.visuallyShielded().get());
    }

    public void toggle() {
        if (this.shielded().get())
            this.disable();
        else
            this.enable();
    }

    public void enableVisuals() {
        this.visuallyShielded().set(true);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, this.shielded().get(), true);
    }

    public void disableVisuals() {
        this.visuallyShielded().set(false);
        TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, this.shielded().get(), false);
    }

    public void toggleVisuals() {
        if (this.visuallyShielded().get())
            this.disableVisuals();
        else
            this.enableVisuals();
    }

    public void disableAll() {
        this.disableVisuals();
        this.disable();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!this.shielded().get() || !this.tardis.subsystems().shields().isEnabled() || this.tardis().subsystems().shields().isBroken())
            return;

        TravelHandler travel = tardis.travel();

        if (!this.tardis.fuel().hasPower())
            this.disableAll();

        if (travel.getState() == TravelHandlerBase.State.FLIGHT)
            return;

        tardis.removeFuel(2 * travel.instability()); // idle drain of 2 fuel per tick
        CachedDirectedGlobalPos globalExteriorPos = travel.position();

        World world = globalExteriorPos.getWorld();
        BlockPos exteriorPos = globalExteriorPos.getPos();

        world.getOtherEntities(null, new Box(exteriorPos).expand(8f)).stream()
                .filter(entity -> entity.isPushable() || entity instanceof ProjectileEntity)
                .filter(entity -> !(entity instanceof ServerPlayerEntity player
                        && tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT))) // Exclude players with loyalty
                .forEach(entity -> {
                    if (entity instanceof ServerPlayerEntity player && entity.isSubmergedInWater())
                        player.addStatusEffect(
                                new StatusEffectInstance(StatusEffects.WATER_BREATHING, 15, 3, true, false, false));

                    if (this.visuallyShielded().get()) {
                        Vec3d centerExteriorPos = exteriorPos.toCenterPos();

                        if (entity.squaredDistanceTo(centerExteriorPos) <= 8f) {
                            Vec3d motion = entity.getBlockPos().toCenterPos().subtract(centerExteriorPos).normalize()
                                    .multiply(0.1f);

                            if (entity instanceof ProjectileEntity projectile) {
                                BlockPos pos = projectile.getBlockPos();

                                if (projectile instanceof TridentEntity) {
                                    projectile.getVelocity().add(motion.multiply(2f));

                                    world.playSound(null, pos, SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.BLOCKS, 1f,
                                            1f);
                                    return;
                                }

                                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.BLOCKS, 1f,
                                        1f);

                                projectile.discard();
                                return;
                            }

                            entity.setVelocity(entity.getVelocity().add(motion.multiply(2f)));

                            entity.velocityDirty = true;
                            entity.velocityModified = true;
                        }
                    }
                });
    }
}
