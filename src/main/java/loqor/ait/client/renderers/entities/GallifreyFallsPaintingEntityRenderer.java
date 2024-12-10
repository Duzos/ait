/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package loqor.ait.client.renderers.entities;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import loqor.ait.AITMod;
import loqor.ait.client.boti.BOTI;
import loqor.ait.client.models.decoration.GallifreyFallsFrameModel;
import loqor.ait.client.models.decoration.GallifreyFallsModel;
import loqor.ait.core.entities.GallifreyFallsPaintingEntity;

@Environment(value=EnvType.CLIENT)
public class GallifreyFallsPaintingEntityRenderer
        extends EntityRenderer<GallifreyFallsPaintingEntity> {
    private Framebuffer framebuffer;
    public static final Identifier PAINTING_TEXTURE = new Identifier(AITMod.MOD_ID, "textures/painting/gallifrey_falls.png");
    public static final Identifier FRAME_TEXTURE = new Identifier(AITMod.MOD_ID, "textures/painting/frame.png");
    GallifreyFallsFrameModel frame;
    GallifreyFallsModel painting;
    public GallifreyFallsPaintingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        frame = new GallifreyFallsFrameModel(GallifreyFallsFrameModel.getTexturedModelData().createModel());
        painting = new GallifreyFallsModel(GallifreyFallsModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(GallifreyFallsPaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(paintingEntity.getBodyYaw()));
        matrixStack.translate(0, -0.5, 0.5);

        BOTI.renderGallifreyFallsPainting(matrixStack, frame, this.getLight(paintingEntity, i), vertexConsumerProvider);

        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(GallifreyFallsPaintingEntity entity) {
        return null;
    }

}
