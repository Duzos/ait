package loqor.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import loqor.ait.core.AITSounds;
import loqor.ait.core.engine.SubSystem;
import loqor.ait.core.engine.impl.EngineSystem;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.control.Control;
import loqor.ait.core.tardis.handler.travel.TravelHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;

public class HandBrakeControl extends Control {

    public HandBrakeControl() {
        super("handbrake");
    }

    private SoundEvent soundEvent = AITSounds.HANDBRAKE_UP;

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        if (tardis.isInDanger())
            return false;

        EngineSystem.Phaser phaser = tardis.subsystems().engine().phaser();
        if (phaser.isPhasing()) {
            phaser.cancel();
            return true;
        }

        // todo make this fancier when moving stuff from flightdata to travelhandler
        boolean handbrake = tardis.travel().handbrake();
        handbrake = !handbrake;

        tardis.travel().handbrake(handbrake);

        if (tardis.isRefueling())
            tardis.setRefueling(false);

        this.soundEvent = handbrake ? AITSounds.HANDBRAKE_DOWN : AITSounds.HANDBRAKE_UP;
        TravelHandler travel = tardis.travel();

        if (handbrake && travel.getState() == TravelHandlerBase.State.FLIGHT) {
            if (travel.autopilot()) {
                travel.stopHere();
                travel.rematerialize();
            } else {
                travel.crash();
            }
        }

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return null;
    }
}
