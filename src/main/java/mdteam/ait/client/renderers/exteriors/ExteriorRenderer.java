package mdteam.ait.client.renderers.exteriors;

import mdteam.ait.AITMod;
import mdteam.ait.client.models.exteriors.ExteriorModel;
import mdteam.ait.client.models.exteriors.SiegeModeModel;
import mdteam.ait.client.registry.ClientExteriorVariantRegistry;
import mdteam.ait.client.registry.exterior.ClientExteriorVariantSchema;
import mdteam.ait.client.renderers.AITRenderLayers;
import mdteam.ait.core.blockentities.ExteriorBlockEntity;
import mdteam.ait.core.blocks.ExteriorBlock;
import mdteam.ait.tardis.TardisExterior;
import mdteam.ait.tardis.data.SonicHandler;
import mdteam.ait.tardis.data.properties.PropertiesHandler;
import mdteam.ait.tardis.wrapper.client.ClientTardis;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ExteriorRenderer<T extends ExteriorBlockEntity> implements BlockEntityRenderer<T> {
	private ExteriorModel model;
	private SiegeModeModel siege;
	private final EntityRenderDispatcher dispatcher;


	public ExteriorRenderer(BlockEntityRendererFactory.Context ctx) {
		this.dispatcher = ctx.getEntityRenderDispatcher();
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.findTardis().isEmpty()) {
			return;
		}

		ClientTardis tardis = (ClientTardis) entity.findTardis().get();

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		TardisExterior tardisExterior = tardis.getExterior();
		ClientExteriorVariantSchema exteriorVariant = ClientExteriorVariantRegistry.withParent(tardisExterior.getVariant());

		Class<? extends ExteriorModel> modelClass = exteriorVariant.model().getClass();

		if (model != null && !(model.getClass().isInstance(modelClass))) // fixme this is bad it seems to constantly create a new one anyway but i didnt realise.
			model = null;

		if (model == null)
			this.model = exteriorVariant.model();

		// BlockState blockState = entity.getCachedState();
		// float f = blockState.get(ExteriorBlock.FACING).asRotation();

		float f = tardis.getTravel().getPosition().getSpecific().toRotation();

		int maxLight = 0xF000F0;
		matrices.push();
		matrices.translate(0.5, 0, 0.5);

		if (MinecraftClient.getInstance().player == null) return;

		Identifier texture = exteriorVariant.texture();
		Identifier emission = exteriorVariant.emission();

		float wrappedDegrees = MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getHeadYaw() +
				(entity.findTardis().get().getHandlers().getExteriorPos().getDirection() == Direction.NORTH ||
						entity.findTardis().get().getHandlers().getExteriorPos().getDirection() == Direction.SOUTH ? f + 180f : f));

		if (exteriorVariant.equals(ClientExteriorVariantRegistry.DOOM)) {
			texture = DoomConstants.getTextureForRotation(wrappedDegrees, entity.findTardis().get());
			emission = DoomConstants.getEmissionForRotation(DoomConstants.getTextureForRotation(wrappedDegrees, entity.findTardis().get()), entity.findTardis().get());
		}

		//matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(!exteriorVariant.equals(ClientExteriorVariantRegistry.DOOM) ? f :
		//		MinecraftClient.getInstance().player.getHeadYaw() + ((wrappedDegrees > -135 && wrappedDegrees < 135) ? 180f : 0f)));

		matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));
		matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

		// -------------------------------------------------------------------------------------------------------------------

		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
		try {
			if (entity.findTardis().get().isSiegeMode()) {
				if (siege == null) siege = new SiegeModeModel(SiegeModeModel.getTexturedModelData().createModel());
				siege.renderWithAnimations(entity, this.siege.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(SiegeModeModel.TEXTURE)), maxLight, overlay, 1, 1, 1, 1);
				matrices.pop();
				return;
			}
		} catch (Exception e) {
			AITMod.LOGGER.error("Failed to render siege mode", e);
		}

		String name = entity.findTardis().get().getHandlers().getStats().getName();
		if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
		}

		if (model != null) {
			model.renderWithAnimations(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1, 1, 1);
			if (entity.findTardis().get().getHandlers().getOvergrown().isOvergrown()) {
				model.renderWithAnimations(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(entity.findTardis().get().getHandlers().getOvergrown().getOvergrownTexture())), light, overlay, 1, 1, 1, 1);
			}
			if (emission != null && entity.findTardis().get().hasPower()) {
				boolean alarms = PropertiesHandler.getBool(entity.findTardis().get().getHandlers().getProperties(), PropertiesHandler.ALARM_ENABLED);

				model.renderWithAnimations(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.tardisRenderEmissionCull(emission, true)), maxLight, overlay, 1, alarms ? 0.3f : 1, alarms ? 0.3f : 1, 1);
			}
		}


		matrices.pop();
		if (!entity.findTardis().get().getHandlers().getSonic().hasSonic(SonicHandler.HAS_EXTERIOR_SONIC)) return;
		ItemStack stack = entity.findTardis().get().getHandlers().getSonic().get(SonicHandler.HAS_EXTERIOR_SONIC);
		if (stack == null) return;
		matrices.push();
		matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f + exteriorVariant.sonicItemRotations()[0]), (float) entity.getPos().toCenterPos().x - entity.getPos().getX(), (float) entity.getPos().toCenterPos().y - entity.getPos().getY(), (float) entity.getPos().toCenterPos().z - entity.getPos().getZ());
		matrices.translate(exteriorVariant.sonicItemTranslations().x(), exteriorVariant.sonicItemTranslations().y(), exteriorVariant.sonicItemTranslations().z());
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(exteriorVariant.sonicItemRotations()[1]));
		matrices.scale(0.9f, 0.9f, 0.9f);
		int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
		matrices.pop();
	}

	@Override
	public boolean rendersOutsideBoundingBox(T blockEntity) {
		return true;
	}
}
