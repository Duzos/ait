package mdteam.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FalloutDoor extends Model {
	public ModelPart door;
	public ModelPart frame;
	public FalloutDoor(ModelPart root) {
		super(RenderLayer::getEntityCutoutNoCull);
		this.door = root.getChild("door");
		this.frame = root.getChild("frame");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData door = modelPartData.addChild("door", ModelPartBuilder.create().uv(24, 24).cuboid(0.0F, -16.0F, -0.5F, 11.0F, 32.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.5F, 8.0F, 6.975F));

		ModelPartData frame = modelPartData.addChild("frame", ModelPartBuilder.create().uv(24, 11).cuboid(-5.5F, -36.0F, -11.55F, 11.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(24, 0).cuboid(-7.5F, -41.0F, -11.55F, 15.0F, 5.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 35).cuboid(5.5F, -36.0F, -11.55F, 2.0F, 34.0F, 2.0F, new Dilation(0.0F))
		.uv(8, 35).cuboid(-7.5F, -36.0F, -11.55F, 2.0F, 34.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-5.5F, -36.0F, -10.55F, 11.0F, 34.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 26.0F, 17.55F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		door.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		frame.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}