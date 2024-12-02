package loqor.ait;

import static dev.pavatus.planet.core.planet.Crater.CRATER_ID;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import dev.pavatus.module.ModuleRegistry;
import dev.pavatus.planet.core.planet.Crater;
import dev.pavatus.planet.core.planet.PlanetRegistry;
import dev.pavatus.register.Registries;
import dev.pavatus.register.api.RegistryEvents;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import loqor.ait.api.AITModInitializer;
import loqor.ait.core.*;
import loqor.ait.core.advancement.TardisCriterions;
import loqor.ait.core.commands.*;
import loqor.ait.core.config.AITConfig;
import loqor.ait.core.entities.ConsoleControlEntity;
import loqor.ait.core.item.component.AbstractTardisPart;
import loqor.ait.core.item.part.MachineItem;
import loqor.ait.core.likes.ItemOpinionRegistry;
import loqor.ait.core.lock.LockedDimensionRegistry;
import loqor.ait.core.screen_handlers.EngineScreenHandler;
import loqor.ait.core.sounds.flight.FlightSoundRegistry;
import loqor.ait.core.sounds.travel.TravelSoundRegistry;
import loqor.ait.core.tardis.manager.ServerTardisManager;
import loqor.ait.core.tardis.util.AsyncLocatorUtil;
import loqor.ait.core.tardis.util.NetworkUtil;
import loqor.ait.core.tardis.util.TardisUtil;
import loqor.ait.core.util.ServerLifecycleHooks;
import loqor.ait.core.util.StackUtil;
import loqor.ait.core.util.WorldUtil;
import loqor.ait.core.util.schedule.Scheduler;
import loqor.ait.core.world.LandingPadManager;
import loqor.ait.data.landing.LandingPadRegion;
import loqor.ait.data.schema.MachineRecipeSchema;
import loqor.ait.registry.impl.*;
import loqor.ait.registry.impl.console.ConsoleRegistry;
import loqor.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import loqor.ait.registry.impl.door.DoorRegistry;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;



public class AITMod implements ModInitializer {

    public static final String MOD_ID = "ait";
    public static final Logger LOGGER = LoggerFactory.getLogger("ait");
    public static final Random RANDOM = new Random();

    public static final AITConfig AIT_CONFIG = AITConfig.createAndLoad();
    public static final GameRules.Key<GameRules.BooleanRule> TARDIS_GRIEFING = GameRuleRegistry.register("tardisGriefing",
            GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    //Creative Inventory Tabs
    public static final OwoItemGroup AIT_ITEM_GROUP = OwoItemGroup
            .builder(new Identifier(AITMod.MOD_ID, "item_group"), () -> Icon.of(AITItems.TARDIS_ITEM))
            .disableDynamicTitle().build();


    public static final RegistryKey<PlacedFeature> CUSTOM_GEODE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            new Identifier(MOD_ID, "zeiton_geode"));

    public static final Crater CRATER = new Crater(ProbabilityConfig.CODEC);

    public static final ScreenHandlerType<EngineScreenHandler> ENGINE_SCREEN_HANDLER;

    static {
        ENGINE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "engine"),
                EngineScreenHandler::new);
    }

    public static final String BRANCH;

    static {
        // ait-1.x.x.xxx-1.20.1-xxxx-xxxx
        String version = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        // get the last part of the version string after the -
        BRANCH = version.substring(version.lastIndexOf("-") + 1);
    }

    public static boolean isUnsafeBranch() {
        return !BRANCH.equals("release");
    }

    @Override
    public void onInitialize() {
        NetworkUtil.init();
        Scheduler.init();
        AsyncLocatorUtil.setupExecutorService();

        ConsoleRegistry.init();
        CreakRegistry.init();
        SequenceRegistry.init();
        MoodEventPoolRegistry.init();
        LandingPadManager.init();
        ControlRegistry.init();

        // For all the addon devs
        FabricLoader.getInstance().invokeEntrypoints("ait-main", AITModInitializer.class,
                AITModInitializer::onInitializeAIT);

        RegistryEvents.INIT.register((registries, isClient) -> {
            if (isClient) return;

            registries.register(SonicRegistry.getInstance());
            registries.register(DesktopRegistry.getInstance());
            registries.register(ConsoleVariantRegistry.getInstance());
            registries.register(MachineRecipeRegistry.getInstance());
            registries.register(TravelSoundRegistry.getInstance());
            registries.register(FlightSoundRegistry.getInstance());
            registries.register(ExteriorVariantRegistry.getInstance());
            registries.register(CategoryRegistry.getInstance());
            registries.register(TardisComponentRegistry.getInstance());
            registries.register(PlanetRegistry.getInstance());
            registries.register(LockedDimensionRegistry.getInstance());
            registries.register(HumRegistry.getInstance());
            registries.register(ItemOpinionRegistry.getInstance());
            registries.register(ModuleRegistry.instance());
        });

        Registries.getInstance().subscribe(Registries.InitType.COMMON);
        DoorRegistry.init();
        AITStatusEffects.init();

        // ServerVortexDataHandler.init();
        ServerLifecycleHooks.init();

        AITArgumentTypes.register();

        AITSounds.init();
        FieldRegistrationHandler.register(AITItems.class, MOD_ID, false);
        FieldRegistrationHandler.register(AITBlocks.class, MOD_ID, false);
        FieldRegistrationHandler.register(AITBlockEntityTypes.class, MOD_ID, false);
        FieldRegistrationHandler.register(AITEntityTypes.class, MOD_ID, false);
        FieldRegistrationHandler.register(AITPaintings.class, MOD_ID, false);

        // important to init after items registration
        BlueprintRegistry.init();

        WorldUtil.init();
        TardisUtil.init();

        ServerTardisManager.init();
        TardisCriterions.init();

        entityAttributeRegister();

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                CUSTOM_GEODE_PLACED_KEY);

        Registry.register(net.minecraft.registry.Registries.FEATURE, CRATER_ID, CRATER);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            TeleportInteriorCommand.register(dispatcher);
            SummonTardisCommand.register(dispatcher);
            SetLockedCommand.register(dispatcher);
            GetInsideTardisCommand.register(dispatcher);
            FuelCommand.register(dispatcher);
            SetRepairTicksCommand.register(dispatcher);
            RiftChunkCommand.register(dispatcher);
            TriggerMoodRollCommand.register(dispatcher);
            SetNameCommand.register(dispatcher);
            GetNameCommand.register(dispatcher);
            GetCreatorCommand.register(dispatcher);
            SetMaxSpeedCommand.register(dispatcher);
            SetSiegeCommand.register(dispatcher);
            LinkCommand.register(dispatcher);
            RemoveCommand.register(dispatcher);
            PermissionCommand.register(dispatcher);
            LoyaltyCommand.register(dispatcher);
            UnlockCommand.register(dispatcher);
            DataCommand.register(dispatcher);
            TravelDebugCommand.register(dispatcher);
            VersionCommand.register(dispatcher);
            SafePosCommand.register(dispatcher);
            ListCommand.register(dispatcher);
            LoadCommand.register(dispatcher);
            DebugCommand.register(dispatcher);
        }));


        ServerPlayNetworking.registerGlobalReceiver(TardisUtil.REGION_LANDING_CODE,
                (server, player, handler, buf, responseSender) -> {
                    BlockPos pos = buf.readBlockPos();
                    String landingCode = buf.readString();

                    server.execute(() -> {
                        LandingPadRegion region = LandingPadManager.getInstance((ServerWorld) player.getWorld()).getRegionAt(pos);

                        if (region == null)
                            return;

                        region.setLandingCode(landingCode);
                        LandingPadManager.Network.syncTracked(LandingPadManager.Network.Action.ADD, player.getServerWorld(),
                                new ChunkPos(player.getBlockPos()));
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(MachineItem.MACHINE_DISASSEMBLE,
                (server, player, handler, buf, responseSender) -> {
                    ItemStack machine = buf.readItemStack();

                    Optional<MachineRecipeSchema> schema = MachineRecipeRegistry.getInstance().findMatching(machine);

                    if (schema.isEmpty())
                        return;

                    // this should ALWAYS be executed on the main thread
                    server.execute(() -> {
                        MachineItem.disassemble(player, machine, schema.get());

                        StackUtil.playBreak(player);
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(AbstractTardisPart.DISASSEMBLE,
                (server, player, handler, buf, responseSender) -> {
                    ItemStack machine = buf.readItemStack();

                    Optional<MachineRecipeSchema> schema = MachineRecipeRegistry.getInstance().findMatching(machine);

                    if (schema.isEmpty())
                        return;

                    // this should ALWAYS be executed on the main thread
                    server.execute(() -> {
                        AbstractTardisPart.disassemble(player, machine, schema.get());

                        StackUtil.playBreak(player);
                    });
                });

        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            AIT_CONFIG.save();
            AsyncLocatorUtil.shutdownExecutorService();
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> NetworkUtil.send(player, new Identifier(AITMod.MOD_ID, "change_world"), PacketByteBufs.create()));

        AIT_ITEM_GROUP.initialize();
    }

    public void entityAttributeRegister() {
        FabricDefaultAttributeRegistry.register(AITEntityTypes.CONTROL_ENTITY_TYPE,
                ConsoleControlEntity.createDummyAttributes());
    }

    public static final Identifier OPEN_SCREEN = new Identifier(AITMod.MOD_ID, "open_screen");
    public static final Identifier OPEN_SCREEN_TARDIS = new Identifier(AITMod.MOD_ID, "open_screen_tardis");
    public static final Identifier OPEN_SCREEN_CONSOLE = new Identifier(AITMod.MOD_ID, "open_screen_console");

    public static void openScreen(ServerPlayerEntity player, int id, UUID tardis) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        buf.writeUuid(tardis);
        ServerPlayNetworking.send(player, OPEN_SCREEN_TARDIS, buf);
    }

    public static void openScreen(ServerPlayerEntity player, int id, UUID tardis, BlockPos console) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        buf.writeUuid(tardis);
        buf.writeBlockPos(console);

        ServerPlayNetworking.send(player, OPEN_SCREEN_CONSOLE, buf);
    }
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
