package loqor.ait.client.renderers.exteriors;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.profiler.Profiler;

import loqor.ait.AITMod;
import loqor.ait.api.TardisComponent;
import loqor.ait.api.link.v2.TardisRef;
import loqor.ait.client.models.exteriors.ExteriorModel;
import loqor.ait.client.models.exteriors.SiegeModeModel;
import loqor.ait.client.models.machines.ShieldsModel;
import loqor.ait.client.renderers.AITRenderLayers;
import loqor.ait.client.util.ClientLightUtil;
import loqor.ait.core.blockentities.ExteriorBlockEntity;
import loqor.ait.core.blocks.ExteriorBlock;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.handler.BiomeHandler;
import loqor.ait.core.tardis.handler.CloakHandler;
import loqor.ait.core.tardis.handler.OvergrownHandler;
import loqor.ait.data.DirectedGlobalPos;
import loqor.ait.data.datapack.DatapackConsole;
import loqor.ait.data.schema.exterior.ClientExteriorVariantSchema;
import loqor.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class ExteriorRenderer<T extends ExteriorBlockEntity> implements BlockEntityRenderer<T> {

    private static final Identifier SHIELDS = new Identifier(AITMod.MOD_ID, "textures/environment/shields.png");

    private static final SiegeModeModel SIEGE_MODEL = new SiegeModeModel(
            SiegeModeModel.getTexturedModelData().createModel());
    private static final ShieldsModel SHIELDS_MODEL = new ShieldsModel(
            ShieldsModel.getTexturedModelData().createModel());;

    private ClientExteriorVariantSchema variant;
    private ExteriorModel model;

    public ExteriorRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("exterior");

        profiler.push("find_tardis");
        TardisRef optionalTardis = entity.tardis();

        if (optionalTardis == null || optionalTardis.isEmpty())
            return;

        Tardis tardis = optionalTardis.get();
        profiler.swap("render");

        if (entity.getAlpha() > 0 || !tardis.<CloakHandler>handler(TardisComponent.Id.CLOAK).cloaked().get())
            this.renderExterior(profiler, tardis, entity, tickDelta, matrices, vertexConsumers, light, overlay);

        profiler.pop();

        profiler.pop();
    }

    private void renderExterior(Profiler profiler, Tardis tardis, T entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (tardis.siege().isActive()) {
            profiler.push("siege");

            matrices.push();
            matrices.translate(0.5f, 0.5f, 0.5f);
            SIEGE_MODEL.renderWithAnimations(entity, SIEGE_MODEL.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(tardis.siege().texture().get())),
                    light, overlay, 1, 1, 1, 1);

            matrices.pop();
            profiler.pop();
            return;
        }

        DirectedGlobalPos.Cached exteriorPos = tardis.travel().position();

        if (exteriorPos == null) {
            profiler.pop();
            return;
        }

        this.updateModel(tardis);

        BlockState blockState = entity.getCachedState();
        int k = blockState.get(ExteriorBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        final float alpha = entity.getAlpha();

        if (tardis.areVisualShieldsActive()) {
            profiler.push("shields");

            float delta = (tickDelta + MinecraftClient.getInstance().player.age) * 0.03f;
            VertexConsumer vertexConsumer = vertexConsumers
                    .getBuffer(RenderLayer.getEnergySwirl(SHIELDS, delta % 1.0F, (delta * 0.1F) % 1.0F));

            matrices.push();
            matrices.translate(0.5F, 0.0F, 0.5F);

            SHIELDS_MODEL.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, 0f,
                    0.25f, 0.5f, alpha);

            matrices.pop();
            profiler.pop();
        }

        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);

        Identifier texture = this.variant.texture();
        Identifier emission = this.variant.emission();

        if (MinecraftClient.getInstance().player == null) {
            profiler.pop();
            return;
        }

        float wrappedDegrees = MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getHeadYaw() + h);

        if (this.variant.equals(ClientExteriorVariantRegistry.DOOM)) {
            texture = DoomConstants.getTextureForRotation(wrappedDegrees, tardis);
            emission = DoomConstants.getEmissionForRotation(DoomConstants.getTextureForRotation(wrappedDegrees, tardis),
                    tardis);
        }

        matrices.multiply(
                RotationAxis.NEGATIVE_Y.rotationDegrees(!this.variant.equals(ClientExteriorVariantRegistry.DOOM)
                        ? h + 180f
                        : MinecraftClient.getInstance().player.getHeadYaw() + 180f
                                + ((wrappedDegrees > -135 && wrappedDegrees < 135) ? 180f : 0f)));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        if (model == null) {
            profiler.pop();
            return;
        }

        String name = tardis.stats().getName();
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            matrices.translate(0, 1.25f, -0.7f);
        }

        if (tardis.travel().antigravs().get() && tardis.flight().falling().get()) {
            float sinFunc = (float) Math.sin((MinecraftClient.getInstance().player.age / 400f * 220f) * 0.2f + 0.2f);
            matrices.translate(0, sinFunc, 0);
        }

        model.renderWithAnimations(entity, this.model.getPart(), matrices,
                vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1, 1,
                alpha);

        if (tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).isOvergrown()) {
            model.renderWithAnimations(entity, this.model.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(
                            tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).getOvergrownTexture())),
                    light, overlay, 1, 1, 1, alpha);
        }

        profiler.push("emission");
        boolean alarms = tardis.alarm().enabled().get();

        if (alpha > 0.105f && emission != null && !(emission.equals(DatapackConsole.EMPTY)))
            ClientLightUtil.renderEmissivable(tardis.fuel().hasPower(), model::renderWithAnimations, emission, entity,
                    this.model.getPart(), matrices, vertexConsumers, light, overlay, 1, alarms ? 0.3f : 1,
                    alarms ? 0.3f : 1, alpha);

        profiler.swap("biome");

        if (this.variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler handler = tardis.handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = handler.getBiomeKey().get(this.variant.overrides());

            if (alpha > 0.105f && (biomeTexture != null && !texture.equals(biomeTexture))) {
                model.renderWithAnimations(entity, this.model.getPart(), matrices,
                        vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(biomeTexture, false)),
                        light, overlay, 1, 1, 1, alpha);
            }
        }

        profiler.pop();
        matrices.pop();

        profiler.push("sonic");
        ItemStack stack = tardis.sonic().getExteriorSonic();

        if (stack == null || entity.getWorld() == null) {
            profiler.pop();
            return;
        }

        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f + h + this.variant.sonicItemRotations()[0]),
                (float) entity.getPos().toCenterPos().x - entity.getPos().getX(),
                (float) entity.getPos().toCenterPos().y - entity.getPos().getY(),
                (float) entity.getPos().toCenterPos().z - entity.getPos().getZ());
        matrices.translate(this.variant.sonicItemTranslations().x(), this.variant.sonicItemTranslations().y(),
                this.variant.sonicItemTranslations().z());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.variant.sonicItemRotations()[1]));
        matrices.scale(0.9f, 0.9f, 0.9f);

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove,
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();
        profiler.pop();
    }

    private void updateModel(Tardis tardis) {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.model();
        }
    }
}
