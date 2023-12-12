package mdteam.ait.client.renderers.doors;

import com.google.common.collect.ImmutableMap;
import mdteam.ait.AITMod;
import mdteam.ait.client.models.doors.FalloutDoor;
import mdteam.ait.client.renderers.exteriors.ExteriorEnum;
import mdteam.ait.core.blockentities.door.DoorBlockEntity;
import mdteam.ait.core.blocks.DoorBlock;
import mdteam.ait.core.util.TardisUtil;
import mdteam.ait.tardis.TardisDoor;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Map;

public class DoorRenderer<T extends DoorBlockEntity> implements BlockEntityRenderer<T> {
    public static final Identifier DOOR_TEXTURE = new Identifier(AITMod.MOD_ID, ("textures/blockentities/doors/shelter_door.png"));
    private final Map<ExteriorEnum, ModelPart> exteriormap;

    public Map<ExteriorEnum, ModelPart> getModels() {
        ImmutableMap.Builder<ExteriorEnum, ModelPart> builder = ImmutableMap.builder();
        builder.put(ExteriorEnum.SHELTER, FalloutDoor.getTexturedModelData().createModel());
        return builder.build();
    }

    public DoorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.exteriormap = this.getModels();
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.isLinked())
            return;

        BlockState blockState = entity.getCachedState();
        float f = blockState.get(DoorBlock.FACING).asRotation();

        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        FalloutDoor doorModel = (entity.getWorld() != TardisUtil.getTardisDimension()) ?
                new FalloutDoor(FalloutDoor.getTexturedModelData().createModel()) :
                new FalloutDoor(this.exteriormap.get(entity.getTardis().getExterior().getType()));

        float left = entity.getDoor().getState().getLeft();
        doorModel.door.yaw = entity.getDoor().getState() != TardisDoor.State.CLOSED ? left + 0.3f : left;

        doorModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(DOOR_TEXTURE)), light, overlay, 1, 1, 1, 1);
        matrices.pop();
    }
}
