package loqor.ait.mixin.server;


import loqor.ait.core.AITDimensions;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.util.TardisUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {


	@Shadow
	private int joinInvulnerabilityTicks;

	@Inject(method = "tick", at = @At("TAIL"))
	private void AIT_tick(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

		// if player is in tardis and y is less than -100 save them
		if (player.getY() <= -100 && player.getServerWorld().getRegistryKey().equals(AITDimensions.TARDIS_DIM_WORLD)) {
			Tardis found = TardisUtil.findTardisByInterior(player.getBlockPos(), true);

			if (found == null) return;
			player.setVelocity(0, 0, 0);
			this.joinInvulnerabilityTicks = 60;
			TardisUtil.teleportInside(found, player);
			player.fallDistance = 0;
		}
	}
}