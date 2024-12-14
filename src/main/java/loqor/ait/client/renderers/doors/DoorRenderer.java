package loqor.ait.client.renderers.doors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import loqor.ait.AITMod;
import loqor.ait.api.TardisComponent;
import loqor.ait.client.boti.BOTI;
import loqor.ait.client.models.boti.BotiPortalModel;
import loqor.ait.client.models.doors.DoomDoorModel;
import loqor.ait.client.models.doors.DoorModel;
import loqor.ait.client.renderers.AITRenderLayers;
import loqor.ait.client.util.ClientLightUtil;
import loqor.ait.compat.DependencyChecker;
import loqor.ait.core.blockentities.DoorBlockEntity;
import loqor.ait.core.blocks.DoorBlock;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.handler.BiomeHandler;
import loqor.ait.core.tardis.handler.DoorHandler;
import loqor.ait.core.tardis.handler.OvergrownHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.data.DirectedGlobalPos;
import loqor.ait.data.schema.exterior.ClientExteriorVariantSchema;
import loqor.ait.registry.impl.door.ClientDoorRegistry;
import loqor.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class DoorRenderer<T extends DoorBlockEntity> implements BlockEntityRenderer<T> {

    private ClientExteriorVariantSchema variant;
    private DoorModel model;

    public DoorRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("door");

        profiler.push("render");

        if (entity.getWorld().getRegistryKey().equals(World.OVERWORLD)) {
            BlockState blockState = entity.getCachedState();
            float k = blockState.get(DoorBlock.FACING).asRotation();
            matrices.push();
            matrices.translate(0.5, 1.5, 0.5);
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k + 180f));
            ClientDoorRegistry.CAPSULE.model().render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucent(ClientExteriorVariantRegistry.CAPSULE_DEFAULT.texture())), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
            profiler.pop();
            return;
        }

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        this.renderDoor(profiler, tardis, entity, matrices, vertexConsumers, light, overlay);
        profiler.pop();

        profiler.pop();
    }

    private void renderDoorBoti(DoorBlockEntity door, ClientExteriorVariantSchema variant, @Nullable Identifier interiorTexture, Profiler profiler, Tardis tardis, T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        profiler.push("boti");
        BOTI.renderInteriorDoorBoti(door, variant, matrices, new Identifier(AITMod.MOD_ID, "textures/painting/texture.png"), model, BotiPortalModel.getTexturedModelData().createModel(), light);
        profiler.pop();
    }

    private void renderDoor(Profiler profiler, Tardis tardis, T entity, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (tardis.siege().isActive())
            return;

        this.updateModel(tardis);

        BlockState blockState = entity.getCachedState();
        float k = blockState.get(DoorBlock.FACING).asRotation();

        Identifier texture = this.variant.texture();

        if (this.variant.equals(ClientExteriorVariantRegistry.DOOM))
            texture = tardis.door().isOpen() ? DoomDoorModel.DOOM_DOOR_OPEN : DoomDoorModel.DOOM_DOOR;

        if (DependencyChecker.hasPortals() && tardis.travel().getState() == TravelHandlerBase.State.LANDED
                && tardis.door().getDoorState() != DoorHandler.DoorStateEnum.CLOSED) {
            DirectedGlobalPos.Cached globalPos = tardis.travel().position();

            BlockPos pos = globalPos.getPos();
            World world = globalPos.getWorld();

            if (world != null) {
                int lightConst = 524296;
                int i = world.getLightLevel(LightType.SKY, pos);
                int j = world.getLightLevel(LightType.BLOCK, pos);

                light = (i + j > 15
                        ? (15 * 2) + (j > 0 ? 0 : -5)
                        : world.isNight()
                                ? (i / 15) + j > 0 ? j + 13 : j
                                : i + (world.getRegistryKey().equals(World.NETHER) ? j * 2 : j))
                        * lightConst;
            }
        }

        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        model.renderWithAnimations(entity, model.getPart(), matrices,
                vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1,
                1 /* 0.5f */, 1);

        if (tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).isOvergrown())
            model.renderWithAnimations(entity, model.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(
                            tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).getOvergrownTexture())),
                    light, overlay, 1, 1, 1, 1);

        profiler.push("emission");

        boolean alarms = tardis.alarm().enabled().get();

        if (!variant.equals(ClientExteriorVariantRegistry.DOOM))
            ClientLightUtil.renderEmissivable(tardis.fuel().hasPower(), model::renderWithAnimations,
                this.variant.emission(), entity, model.getPart(), matrices, vertexConsumers, light, overlay, 1,
                alarms ? 0.3f : 1, alarms ? 0.3f : 1, 1);

        profiler.swap("biome");

        if (this.variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler biome = tardis.handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = biome.getBiomeKey().get(this.variant.overrides());

            if (biomeTexture != null && !texture.equals(biomeTexture)) {
                model.renderWithAnimations(entity, model.getPart(), matrices,
                        vertexConsumers.getBuffer(AITRenderLayers.getEntityCutoutNoCullZOffset(biomeTexture)), light,
                        overlay, 1, 1, 1, 1);
            }
        }

        if (tardis.door().isOpen())
            this.renderDoorBoti(entity, variant, null, profiler, tardis, entity, matrices, vertexConsumers, light, overlay);

        matrices.pop();
        profiler.pop();
    }

    private void updateModel(Tardis tardis) {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.getDoor().model();
        }
    }
}
