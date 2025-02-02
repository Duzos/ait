package loqor.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import loqor.ait.core.AITSounds;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.TardisDesktop;
import loqor.ait.core.tardis.control.Control;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;

public class RefuelerControl extends Control {

    public RefuelerControl() {
        super("refueler");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.isGrowth())
            return false;

        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED && tardis.travel().handbrake()) {
            tardis.setRefueling(!tardis.isRefueling());

            if (tardis.isRefueling())
                TardisDesktop.playSoundAtConsole(tardis.asServer().getInteriorWorld(), console, AITSounds.ENGINE_REFUEL, SoundCategory.BLOCKS, 10,
                        1);


            return true;
        }

        return false;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.ENGINE_REFUEL_CRANK;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }
}
