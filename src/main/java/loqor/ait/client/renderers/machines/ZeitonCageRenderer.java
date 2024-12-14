package loqor.ait.client.renderers.machines;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import loqor.ait.AITMod;
import loqor.ait.client.models.machines.ZeitonCageModel;
import loqor.ait.core.AITBlocks;
import loqor.ait.core.blockentities.ZeitonCageBlockEntity;
import loqor.ait.core.blocks.ZeitonCageBlock;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ZeitonCageRenderer<T extends ZeitonCageBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier ZEITON_CAGE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/zeiton_cage.png"));
    private final ZeitonCageModel zeitonCageModel;

    private ItemStack stack = new ItemStack(AITBlocks.ZEITON_BLOCK);

    public ZeitonCageRenderer(BlockEntityRendererFactory.Context ctx) {
        this.zeitonCageModel = new ZeitonCageModel(ZeitonCageModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(ZeitonCageBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        boolean bl = entity.getCachedState().get(ZeitonCageBlock.FUEL) == ZeitonCageBlock.REQUIRED_FUEL;

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.zeitonCageModel.getPart().getChild("cube").visible = !bl;
        this.zeitonCageModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(ZEITON_CAGE_TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (bl) {
            matrices.push();
            matrices.scale(3.25f, 3.25f, 3.25f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().player.age / 100.0f * 360f));
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.push();
            matrices.translate(0, -0.02f, 0);
            matrices.scale(1.1f, 1.1f, 1.1f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.GLASS), ModelTransformationMode.GROUND, light,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
            matrices.pop();
        }

        matrices.pop();
    }
}
