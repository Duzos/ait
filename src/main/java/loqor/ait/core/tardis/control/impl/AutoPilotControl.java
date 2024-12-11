package loqor.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import loqor.ait.core.engine.SubSystem;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.control.Control;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;

public class AutoPilotControl extends Control {

    public AutoPilotControl() {
        // ☸ ?
        super("protocol_116");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        // @TODO make a real world flight control.. later
        if (leftClick && tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            if (tardis.door().isOpen()) {
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_CHAIN_FALL, SoundCategory.BLOCKS, 1.0F,
                        1.0F);
                return true;
            } else {
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_CLUSTER_PLACE,
                        SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            // tardis.flight().enterFlight(player);
            return true;
        }

        boolean autopilot = tardis.travel().autopilot();
        tardis.travel().autopilot(!autopilot);
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.BLOCK_LEVER_CLICK;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.STABILISERS;
    }
}
