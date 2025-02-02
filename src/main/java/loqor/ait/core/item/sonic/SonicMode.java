package loqor.ait.core.item.sonic;

import java.util.function.Function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import loqor.ait.data.enummap.Ordered;
import loqor.ait.data.schema.sonic.SonicSchema;

public abstract class SonicMode implements Ordered {

    public static class Modes {
        public static final SonicMode[] VALUES = new SonicMode[5];
        private static int lastIndex = 0;

        public static final SonicMode INACTIVE = register(InactiveSonicMode::new);
        public static final SonicMode INTERACTION = register(InteractionSonicMode::new);
        public static final SonicMode OVERLOAD = register(OverloadSonicMode::new);
        public static final SonicMode SCANNING = register(ScanningSonicMode::new);
        public static final SonicMode TARDIS = register(TardisSonicMode::new);

        public static SonicMode register(Function<Integer, SonicMode> consumer) {
            SonicMode mode = consumer.apply(lastIndex);
            VALUES[lastIndex] = mode;

            lastIndex++;
            return mode;
        }

        public static SonicMode next(SonicMode mode) {
            int nextIndex = mode.index() + 1;

            if (nextIndex == VALUES.length)
                return VALUES[0];

            return VALUES[nextIndex];
        }

        public static SonicMode previous(SonicMode mode) {
            int previousIndex = mode.index() - 1;

            if (previousIndex < 0)
                return VALUES[VALUES.length - 1];

            return VALUES[previousIndex];
        }

        public static SonicMode get(int index) {
            return VALUES[index];
        }
    }

    private final int index;

    protected SonicMode(int index) {
        this.index = index;
    }

    public SonicMode next() {
        return Modes.next(this);
    }

    public SonicMode previous() {
        return Modes.previous(this);
    }

    public abstract Text text();

    public abstract int maxTime();

    public boolean startUsing(ItemStack stack, World world, PlayerEntity user, Hand hand) {
        return true;
    }

    public void tick(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) { }

    public void stopUsing(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) { }

    public void finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.stopUsing(stack, world, user, this.maxTime(), 0);
    }

    public abstract Identifier model(SonicSchema.Models models);

    public static HitResult getHitResult(LivingEntity user) {
        return ProjectileUtil.getCollision(user, entity -> !entity.isSpectator() && entity.canHit(), 16);
    }

    @Override
    public int index() {
        return index;
    }
}
