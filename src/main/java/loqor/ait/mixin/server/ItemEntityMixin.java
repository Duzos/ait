package loqor.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import loqor.ait.core.blocks.ZeitonCageBlock;
import loqor.ait.core.item.SiegeTardisItem;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.dim.TardisDimension;
import loqor.ait.core.tardis.util.TardisUtil;
import loqor.ait.data.DirectedGlobalPos;

// mmm mixin i love mixin
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void ait$tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        ItemStack stack = entity.getStack();

        if (entity.getWorld().isClient())
            return;

        if (stack.getItem() instanceof SiegeTardisItem) {
            Tardis found = SiegeTardisItem.getTardis(entity.getWorld(), stack);

            if (found == null)
                return;

            // kill ourselves and place down the exterior
            SiegeTardisItem.placeTardis(found, SiegeTardisItem.fromEntity(entity));
            entity.kill();
        }

        // if entity is in tardis and y is less than -100 save them
        if (entity.getY() <= -100 && TardisDimension.isTardisDimension(entity.getWorld())) {
            Tardis found = TardisDimension.get(entity.getWorld()).orElse(null);

            if (found == null)
                return;

            TardisUtil.teleportInside(found, entity);
        }

        // if entity is zeiton cage and y is less than -100 give it back :(
        if (entity.getY() <= -60 && ZeitonCageBlock.isCageItem(stack)) {
            ServerPlayerEntity nearest = TardisUtil.findNearestPlayer(DirectedGlobalPos.Cached.create((ServerWorld) entity.getWorld(), entity.getBlockPos(), (byte)0)).orElse(null);
            ZeitonCageBlock.onVoid(stack, nearest);
            entity.kill();
        }
    }
}
