package mdteam.ait.tardis.control.impl.pos;

import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.control.Control;
import mdteam.ait.tardis.util.AbsoluteBlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public abstract class PosControl extends Control {
	private final PosType type;

	public PosControl(PosType type, String id) {
		super(id);
		this.type = type;
	}

	public PosControl(PosType type) {
		this(type, type.asString());
	}

	@Override
	public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, boolean leftClick) {

		if (tardis.getHandlers().getSequenceHandler().hasActiveSequence()) {
			if (tardis.getHandlers().getSequenceHandler().controlPartOfSequence(this)) {
				this.addToControlSequence(tardis);
				return false;
			}
		}

		TardisTravel travel = tardis.getTravel();
		AbsoluteBlockPos.Directed destination = travel.getDestination();

		BlockPos pos = this.type.add(destination, (leftClick) ? -IncrementManager.increment(tardis) : IncrementManager.increment(tardis), destination.getWorld());
		travel.setDestination(new AbsoluteBlockPos.Directed(pos, destination.getWorld(), destination.getSpecific()), false);

		messagePlayerDestination(player, travel);

		return true;
	}

	private void messagePlayerDestination(ServerPlayerEntity player, TardisTravel travel) {
		AbsoluteBlockPos.Directed dest = travel.getDestination();
		Text text = Text.translatable("tardis.message.control.randomiser.poscontrol").append(Text.literal(" " + dest.getX() + " | " + dest.getY() + " | " + dest.getZ()));
		player.sendMessage(text, true);
	}

	@Override
	public boolean shouldHaveDelay() {
		return false;
	}
}
