package loqor.ait.core.tardis.handler;

import java.util.ArrayList;
import java.util.List;

import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.pavatus.lib.data.CachedDirectedGlobalPos;
import dev.pavatus.lib.data.DirectedBlockPos;
import dev.pavatus.lib.data.DirectedGlobalPos;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.api.KeyedTardisComponent;
import loqor.ait.api.TardisEvents;
import loqor.ait.api.TardisTickable;
import loqor.ait.core.advancement.TardisCriterions;
import loqor.ait.core.engine.SubSystem;
import loqor.ait.core.item.ChargedZeitonCrystalItem;
import loqor.ait.core.tardis.handler.travel.TravelHandler;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.core.tardis.manager.ServerTardisManager;
import loqor.ait.core.tardis.util.TardisUtil;
import loqor.ait.core.util.WorldUtil;
import loqor.ait.data.Exclude;
import loqor.ait.data.properties.Property;
import loqor.ait.data.properties.Value;
import loqor.ait.data.properties.bool.BoolProperty;
import loqor.ait.data.properties.bool.BoolValue;
import loqor.ait.data.schema.desktop.TardisDesktopSchema;
import loqor.ait.registry.impl.CategoryRegistry;
import loqor.ait.registry.impl.DesktopRegistry;

public class InteriorChangingHandler extends KeyedTardisComponent implements TardisTickable {

    public static final Identifier CHANGE_DESKTOP = AITMod.id("change_desktop");

    private static final Property<Identifier> QUEUED_INTERIOR_PROPERTY = new Property<>(Property.Type.IDENTIFIER, "queued_interior", new Identifier(""));
    private static final BoolProperty QUEUED = new BoolProperty("queued");
    private static final BoolProperty REGENERATING = new BoolProperty("regenerating");

    private final Value<Identifier> queuedInterior = QUEUED_INTERIOR_PROPERTY.create(this);
    private final BoolValue queued = QUEUED.create(this);
    private final BoolValue regenerating = REGENERATING.create(this);

    @Exclude
    private List<ItemStack> restorationChestContents;

    public InteriorChangingHandler() {
        super(Id.INTERIOR);
    }

    @Override
    public void onLoaded() {
        queuedInterior.of(this, QUEUED_INTERIOR_PROPERTY);
        queued.of(this, QUEUED);
        regenerating.of(this, REGENERATING);
    }

    static {
        TardisEvents.DEMAT.register(tardis -> {
            if (tardis.isGrowth()
                    || tardis.interiorChangingHandler().queued().get())
                return TardisEvents.Interaction.FAIL;

            return TardisEvents.Interaction.PASS;
        });

        TardisEvents.MAT.register(tardis -> {
            if (!tardis.isGrowth())
                return TardisEvents.Interaction.PASS;

            tardis.getExterior().setType(CategoryRegistry.CAPSULE);
            return TardisEvents.Interaction.SUCCESS; // force mat even if checks fail
        });

        TardisEvents.LOSE_POWER.register(tardis -> tardis.interiorChangingHandler().queued.set(false));

        ServerPlayNetworking.registerGlobalReceiver(InteriorChangingHandler.CHANGE_DESKTOP,
                ServerTardisManager.receiveTardis(((tardis, server, player, handler, buf, responseSender) -> {
                    TardisDesktopSchema desktop = DesktopRegistry.getInstance().get(buf.readIdentifier());

                    if (tardis == null || desktop == null)
                        return;

                    // nuh uh no interior changing during flight
                    if (tardis.travel().getState() != TravelHandler.State.LANDED)
                        return;

                    tardis.interiorChangingHandler().queueInteriorChange(desktop);
                })));
    }

    public BoolValue queued() {
        return queued;
    }

    public BoolValue regenerating() {
        return regenerating;
    }

    public TardisDesktopSchema getQueuedInterior() {
        return DesktopRegistry.getInstance().get(queuedInterior.get());
    }

    public void queueInteriorChange(TardisDesktopSchema schema) {
        if (!this.canQueue())
            return;

        if (tardis.fuel().getCurrentFuel() < 5000) {
            for (PlayerEntity player : TardisUtil.getPlayersInsideInterior(tardis.asServer())) {
                player.sendMessage(
                        Text.translatable("tardis.message.interiorchange.not_enough_fuel").formatted(Formatting.RED),
                        true);
                return;
            }
        }
        if (tardis.subsystems().isEnabled()) {
            for (PlayerEntity player : TardisUtil.getPlayersInsideInterior(tardis.asServer())) {
                player.sendMessage(
                        Text.translatable("tardis.message.interiorchange.subsystems_enabled", tardis.subsystems().countEnabled()).formatted(Formatting.RED), false);
            }
        }

        AITMod.LOGGER.info("Queueing interior change for {} to {}", this.tardis, schema);

        this.queuedInterior.set(schema.id());
        this.queued.set(true);

        TravelHandler travel = this.tardis.travel();

        if (travel.getState() == TravelHandler.State.FLIGHT && !travel.isCrashing())
            travel.crash();

        restorationChestContents = new ArrayList<>();

        for (SubSystem system : tardis.subsystems().getEnabled()) {
            if (system == null)
                continue;

            restorationChestContents.addAll(system.toStacks());
            AITMod.LOGGER.debug("Storing Subsystem, {} ({}) => {}", system.getId(), system.isEnabled(), system.toStacks());
        }
    }

    private void changeInterior() {
        tardis.getDesktop().changeInterior(this.getQueuedInterior(), true, true)
                .thenRun(() -> {
                    this.queued.set(false);
                    this.regenerating.set(false);

                    tardis.engine().unlinkEngine();

                    if (tardis.hasGrowthExterior()) {
                        TravelHandler travel = tardis.travel();

                        travel.autopilot(true);
                        travel.forceDemat();
                    } else {
                        tardis.removeFuel(5000 * tardis.travel().instability());
                    }

                    TardisUtil.sendMessageToLinked(tardis.asServer(), Text.translatable("tardis.message.interiorchange.success", tardis.stats().getName(), tardis.getDesktop().getSchema().name()));
                    createChestAtInteriorDoor(restorationChestContents);
                }).execute();
    }

    private void warnPlayers() {
        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(this.tardis.asServer())) {
            player.sendMessage(Text.translatable("tardis.message.interiorchange.warning").formatted(Formatting.RED),
                    true);
            if (!tardis.isGrowth())
                TardisCriterions.REDECORATE.trigger(player);
        }
    }

    private void createChestAtInteriorDoor(List<ItemStack> contents) {
        if (contents == null || contents.isEmpty()) {
            AITMod.LOGGER.debug("No contents to save in recovery chest for {}", this.tardis);
            return;
        }

        DirectedBlockPos door = this.tardis.getDesktop().getDoorPos();
        CachedDirectedGlobalPos safe = WorldUtil.locateSafe(CachedDirectedGlobalPos.create(this.tardis.asServer().getInteriorWorld(), door.getPos().offset(door.toMinecraftDirection(), 2), door.getRotation()), TravelHandlerBase.GroundSearch.MEDIAN, true);

        // set block to chest
        if (!(safe.getWorld().getBlockState(safe.getPos()).isAir())) {
            AITMod.LOGGER.error("Failed to create recovery chest at {} for {}", safe, this.tardis);
            return;
        }

        safe.getWorld().setBlockState(safe.getPos(), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, door.toMinecraftDirection().getOpposite()), 3);

        // set chest contents
        ChestBlockEntity chest = (ChestBlockEntity) safe.getWorld().getBlockEntity(safe.getPos());
        List<ItemStack> overflow = new ArrayList<>(contents);

        for (int i = 0; i < 27 && !overflow.isEmpty(); i++) {
            chest.setStack(i, overflow.remove(0));
        }

        AITMod.LOGGER.debug("Created recovery chest at {} for {}", safe, this.tardis);

        if (!overflow.isEmpty())
            createChestAtInteriorDoor(overflow);
    }

    @Override
    public void tick(MinecraftServer server) {
        boolean isQueued = this.queued.get();

        if (server.getTicks() % 10 == 0 && this.tardis.isGrowth()) {
            this.generateInteriorWithItem();

            if (!isQueued) {
                if (this.tardis.door().isClosed()) {
                    this.tardis.door().openDoors();
                } else {
                    this.tardis.door().setLocked(false);
                }
            }
        }

        if (!isQueued)
            return;

        if (!this.canQueue()) {
            this.queued.set(false);
            this.regenerating.set(false);

            tardis.alarm().enabled().set(false);
            return;
        }

        if (!TardisUtil.isInteriorEmpty(tardis.asServer())) {
            warnPlayers();
            return;
        }

        if (!this.regenerating.get()) {
            tardis.getDesktop().startQueue(true);
            Scheduler.get().runTaskLater(this::changeInterior, TimeUnit.SECONDS, 5);

            this.regenerating.set(true);
        }
    }

    protected void generateInteriorWithItem() {
        TardisUtil.getEntitiesInInterior(this.tardis, 50).stream()
                .filter(entity -> entity instanceof ItemEntity item
                        && (item.getStack().getItem() == Items.NETHER_STAR || isChargedCrystal(item.getStack()))
                        && entity.isTouchingWater())
                .forEach(entity -> {
                    DirectedGlobalPos position = this.tardis.travel().position();

                    if (position == null)
                        return;

                    this.tardis.setFuelCount(8000);

                    entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                            SoundCategory.BLOCKS, 10.0F, 0.75F);
                    entity.getWorld().playSound(null, position.getPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                            SoundCategory.BLOCKS, 10.0F, 0.75F);

                    this.queueInteriorChange(DesktopRegistry.getInstance().getRandom(this.tardis));

                    if (this.queued.get())
                        entity.discard();
                });
    }

    private boolean isChargedCrystal(ItemStack stack) {
        if (!(stack.getItem() instanceof ChargedZeitonCrystalItem))
            return false;

        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(ChargedZeitonCrystalItem.FUEL_KEY))
            return false;

        return nbt.getDouble(ChargedZeitonCrystalItem.FUEL_KEY) >= ChargedZeitonCrystalItem.MAX_FUEL;
    }

    private boolean canQueue() {
        return tardis.isGrowth() || tardis.fuel().hasPower() || tardis.crash().isToxic();
    }
}
