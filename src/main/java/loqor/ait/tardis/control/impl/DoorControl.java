package loqor.ait.tardis.control.impl;

import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.control.Control;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import static loqor.ait.tardis.data.DoorData.useDoor;

public class DoorControl extends Control {

	private SoundEvent soundEvent = SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;

	public DoorControl() {
		super("door_control");
	}

	@Override
	public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
		if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
			this.addToControlSequence(tardis, player, console);
			return false;
		}

		this.soundEvent = !tardis.getDoor().isOpen() ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
		useDoor(tardis, world, player.getBlockPos(), player);
		return true;
	}

	@Override
	public SoundEvent getSound() {
		return this.soundEvent;
	}
}