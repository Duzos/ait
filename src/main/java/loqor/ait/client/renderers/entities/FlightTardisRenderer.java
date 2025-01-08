package loqor.ait.client.renderers.entities;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

import loqor.ait.api.TardisComponent;
import loqor.ait.client.models.exteriors.ExteriorModel;
import loqor.ait.client.models.machines.ShieldsModel;
import loqor.ait.client.renderers.AITRenderLayers;
import loqor.ait.core.entities.FlightTardisEntity;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.TardisExterior;
import loqor.ait.core.tardis.handler.BiomeHandler;
import loqor.ait.data.schema.exterior.ClientExteriorVariantSchema;

public class FlightTardisRenderer extends EntityRenderer<FlightTardisEntity> {

    private ExteriorModel model;

    public FlightTardisRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FlightTardisEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        TardisExterior exterior = tardis.getExterior();
        ClientExteriorVariantSchema exteriorVariant = exterior.getVariant().getClient();

        if (exteriorVariant == null)
            return;

        if (this.getModel(tardis) == null)
            return;

        Vec3d vec3d = entity.getRotationVec(tickDelta);
        Vec3d vec3d2 = entity.lerpVelocity(tickDelta);

        double d = vec3d2.horizontalLengthSquared();
        double e = vec3d.horizontalLengthSquared();

        matrices.push();
        if (d > 0.0 && e > 0.0) {
            double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
            double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
            double v = Math.signum(m) * Math.acos(l);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) v));
        }

        if (entity.getPlayer() != null && !entity.getPlayer().isOnGround()) {
            if (!tardis.door().isOpen()) {
                this.model.getPart().setAngles((float) 0, ((entity.getRotation(tickDelta)) * 4), 0);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) (entity.getVelocity().horizontalLength() * 45f)));
            } else {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-180f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) (-entity.getVelocity().horizontalLength() * 45f)));
            }
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                    RotationPropertyHelper.toDegrees(tardis.travel().position().getRotation())
            ));
        }

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));

        this.model.renderEntity(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        if (exteriorVariant.emission() != null && tardis.engine().hasPower()) {
            boolean alarms = tardis.alarm().enabled().get();
            this.model.renderEntity(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(getEmission(tardis), true)), LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, alarms ? 0.3f : 1, alarms ? 0.3f : 1, 1);
        }

        BiomeHandler biome = tardis.handler(TardisComponent.Id.BIOME);
        Identifier biomeTexture = biome.getBiomeKey().get(exteriorVariant.overrides());

        if (biomeTexture != null && !this.getTexture(entity).equals(biomeTexture))
            model.renderEntity(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(biomeTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        int maxLight = 0xF000F0;

        matrices.pop();
        if (tardis.areVisualShieldsActive()) {
            matrices.push();

            float delta = ((tickDelta + entity.age) * 0.03f);
            ShieldsModel shieldsModel = new ShieldsModel(ShieldsModel.getTexturedModelData().createModel());
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(new Identifier("textures/misc/forcefield.png"), delta % 1.0F, (delta * 0.1F) % 1.0F));
            shieldsModel.render(matrices, vertexConsumer, maxLight, OverlayTexture.DEFAULT_UV, 0f, 0.25f, 0.5f, 1f);
            matrices.pop();
        }
    }

    private ExteriorModel getModel(Tardis tardis) {
        if (model == null)
            model = tardis.getExterior().getVariant().getClient().model();

        return model;
    }

    public Identifier getEmission(Tardis tardis) {
        return tardis.getExterior().getVariant().getClient().emission();
    }

    @Override
    public Identifier getTexture(FlightTardisEntity entity) {
        if (entity.tardis() == null || entity.tardis().isEmpty())
            return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE; // random texture just so i dont crash

        return entity.tardis().get().getExterior().getVariant().getClient().texture();
    }
}