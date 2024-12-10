package dev.pavatus.gun;

import java.util.Optional;
import java.util.function.Consumer;

import dev.pavatus.gun.client.ScopeOverlay;
import dev.pavatus.gun.client.render.StaserBoltEntityRenderer;
import dev.pavatus.gun.core.entity.GunEntityTypes;
import dev.pavatus.gun.core.item.GunItems;
import dev.pavatus.module.Module;
import dev.pavatus.module.ModuleRegistry;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.datagen.datagen_providers.AITBlockTagProvider;
import loqor.ait.datagen.datagen_providers.AITItemTagProvider;
import loqor.ait.datagen.datagen_providers.AITLanguageProvider;
import loqor.ait.datagen.datagen_providers.AITRecipeProvider;

public class GunModule extends Module {
    private static final GunModule INSTANCE = new GunModule();

    public static final Identifier ID = AITMod.id("gun");


    @Override
    public void init() {
        this.getItemGroup().initialize();

        FieldRegistrationHandler.register(GunItems.class, AITMod.MOD_ID, false);
        FieldRegistrationHandler.register(GunEntityTypes.class, AITMod.MOD_ID, false);
    }

    @Override
    public void initClient() {
        HudRenderCallback.EVENT.register(new ScopeOverlay());
        EntityRendererRegistry.register(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, StaserBoltEntityRenderer::new);

        ModelPredicateProviderRegistry.register(GunItems.CULT_STASER_RIFLE, new Identifier("ads"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (livingEntity == null) return 0.0f;
                    if (itemStack.getItem() == GunItems.CULT_STASER_RIFLE && livingEntity.getMainHandStack().getItem() == GunItems.CULT_STASER_RIFLE) {
                        if (livingEntity instanceof PlayerEntity) {
                            boolean bl = MinecraftClient.getInstance().options.useKey.isPressed();
                            return bl ? 1.0f : 0.0f;
                        }
                    }
                    return 0.0F;
                });
        ModelPredicateProviderRegistry.register(GunItems.CULT_STASER, new Identifier("ads"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (livingEntity == null) return 0.0f;
                    if (itemStack.getItem() == GunItems.CULT_STASER && livingEntity.getMainHandStack().getItem() == GunItems.CULT_STASER) {
                        if (livingEntity instanceof PlayerEntity) {
                            boolean bl = MinecraftClient.getInstance().options.useKey.isPressed();
                            return bl ? 1.0f : 0.0f;
                        }
                    }
                    return 0.0F;
                });
    }

    @Override
    public Optional<Class<?>> getItemRegistry() {
        return Optional.of(GunItems.class);
    }

    @Override
    protected OwoItemGroup.Builder buildItemGroup() {
        return OwoItemGroup.builder(new Identifier(AITMod.MOD_ID, id().getPath()), () -> Icon.of(GunItems.CULT_STASER));
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public Optional<DataGenerator> getDataGenerator() {
        return Optional.of(new DataGenerator() {
            @Override
            public void lang(AITLanguageProvider provider) {
                provider.addTranslation(getItemGroup(), "AIT: Cult Weaponry");
            }

            @Override
            public void recipes(AITRecipeProvider provider) {

            }

            @Override
            public void blockTags(AITBlockTagProvider provider) {

            }

            @Override
            public void itemTags(AITItemTagProvider provider) {

            }

            @Override
            public void generateItemModels(ItemModelGenerator itemModelGenerator) {

            }

            @Override
            public void models(BlockStateModelGenerator generator) {

            }

            @Override
            public void advancements(Consumer<Advancement> consumer) {

            }
        });
    }

    public static GunModule instance() {
        return INSTANCE;
    }
    public static boolean isLoaded() {
        return ModuleRegistry.instance().get(ID) != null;
    }
}
