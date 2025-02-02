package loqor.ait.core.tardis.control.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import loqor.ait.api.link.LinkableItem;
import loqor.ait.core.AITSounds;
import loqor.ait.core.item.HandlesItem;
import loqor.ait.core.item.SonicItem2;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.TardisDesktop;
import loqor.ait.core.tardis.control.Control;
import loqor.ait.core.tardis.handler.ButlerHandler;
import loqor.ait.core.tardis.handler.SonicHandler;

public class SonicPortControl extends Control {

    public SonicPortControl() {
        super("sonic_port");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
            boolean leftClick) {
        SonicHandler handler = tardis.sonic();
        ButlerHandler butler = tardis.butler();

        //System.out.println(handler.getConsoleSonic());

        if ((leftClick || player.isSneaking()) && (handler.getConsoleSonic() != null || butler.getHandles() != null)) {
            if (handler.getConsoleSonic() != null) {
                SonicHandler.spawnItem(world, console, handler.takeConsoleSonic());
            } else {
                ButlerHandler.spawnItem(world, console, butler.takeHandles());
            }
            return true;
        }

        ItemStack stack = player.getMainHandStack();

        if (!((stack.getItem() instanceof SonicItem2) || (stack.getItem() instanceof HandlesItem))) return false;

        LinkableItem linker = (LinkableItem) stack.getItem();

        if (!linker.isLinked(stack) || player.isSneaking()) {
            linker.link(stack, tardis);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS,
                    1.0F, 1.0F);

            Vec3d vec3d = Vec3d.ofBottomCenter(console).add(0.0, 1.2f, 0.0);

            world.spawnParticles(ParticleTypes.GLOW, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 12, 0.4F, 1F, 0.4F,
                    5.0F);
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 12, 0.4F,
                    1F, 0.4F, 5.0F);
        }

        if (handler.getConsoleSonic() == null && stack.getItem() instanceof HandlesItem) {
            butler.insertHandles(stack, console);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        } else if (butler.getHandles() == null && stack.getItem() instanceof SonicItem2) {
            handler.insertConsoleSonic(stack, console);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        TardisDesktop.playSoundAtConsole(tardis.asServer().getInteriorWorld(), console, AITSounds.SONIC_PORT, SoundCategory.PLAYERS, 6f,
                1);
        return true;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }
}
