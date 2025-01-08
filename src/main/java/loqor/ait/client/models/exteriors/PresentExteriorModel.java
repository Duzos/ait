package loqor.ait.client.models.exteriors;

import loqor.ait.api.link.v2.Linkable;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;

import loqor.ait.AITMod;
import loqor.ait.client.animation.exterior.door.DoorAnimations;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.tardis.handler.DoorHandler;
import net.minecraft.entity.Entity;

public class PresentExteriorModel extends ExteriorModel {
    private final ModelPart present;
    public PresentExteriorModel(ModelPart root) {
        this.present = root.getChild("present");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData present = modelPartData.addChild("present", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -32.0F, -10.0F, 20.0F, 32.0F, 20.0F, new Dilation(0.0F))
        .uv(0, 52).cuboid(-10.0F, -38.6F, -10.0F, 20.0F, 6.0F, 20.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData right_door = present.addChild("right_door", ModelPartBuilder.create().uv(64, 78).cuboid(-10.0F, -16.0F, 0.0F, 10.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(10.0F, -16.0F, -10.001F));

        ModelPartData left_door = present.addChild("left_door", ModelPartBuilder.create().uv(44, 78).cuboid(0.0F, -16.0F, 0.0F, 10.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(-10.0F, -16.0F, -10.001F));

        ModelPartData bow = present.addChild("bow", ModelPartBuilder.create().uv(0, 78).cuboid(-11.0F, -12.0F, 0.0F, 22.0F, 13.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, -39.6F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        present.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        if (exterior.tardis().isEmpty())
            return;

        matrices.push();
        matrices.translate(0, -1.5f, 0);

        if (!AITMod.CONFIG.CLIENT.ANIMATE_DOORS) {
            DoorHandler door = exterior.tardis().get().door();

            this.present.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? 8F : 0.0F;
            this.present.getChild("right_door").yaw = (door.isRightOpen() || door.isBothOpen())
                    ? -8F
                    : 0.0F;
        }

        super.renderWithAnimations(exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (falling.tardis().isEmpty())
            return;

        matrices.push();
        matrices.translate(0, -1.5f, 0);

        if (!AITMod.CONFIG.CLIENT.ANIMATE_DOORS) {
            DoorHandler door = falling.tardis().get().door();

            this.present.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? 9F : 0.0F;
            this.present.getChild("right_door").yaw = (door.isRightOpen() || door.isBothOpen())
                    ? -9F
                    : 0.0F;
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return present;
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return switch (state) {
            case CLOSED -> DoorAnimations.EXTERIOR_BOTH_CLOSE_ANIMATION;
            case FIRST -> DoorAnimations.EXTERIOR_FIRST_OPEN_ANIMATION;
            case SECOND -> DoorAnimations.EXTERIOR_SECOND_OPEN_ANIMATION;
            case BOTH -> DoorAnimations.EXTERIOR_BOTH_OPEN_ANIMATION;
        };
    }
}