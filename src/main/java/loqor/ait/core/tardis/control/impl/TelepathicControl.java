package loqor.ait.core.tardis.control.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NameTagItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import loqor.ait.api.link.LinkableItem;
import loqor.ait.core.AITSounds;
import loqor.ait.core.item.HypercubeItem;
import loqor.ait.core.item.KeyItem;
import loqor.ait.core.item.SonicItem;
import loqor.ait.core.likes.ItemOpinion;
import loqor.ait.core.likes.ItemOpinionRegistry;
import loqor.ait.core.lock.LockedDimensionRegistry;
import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.control.Control;
import loqor.ait.core.tardis.handler.SiegeHandler;
import loqor.ait.core.tardis.handler.distress.DistressCall;
import loqor.ait.core.tardis.util.AsyncLocatorUtil;
import loqor.ait.data.DirectedGlobalPos;
import loqor.ait.data.Loyalty;

public class TelepathicControl extends Control {

    private static final int RADIUS = 256;

    public TelepathicControl() {
        super("telepathic_circuit");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        boolean security = tardis.stats().security().get();

        if (!KeyItem.hasMatchingKeyInInventory(player, tardis) && security)
            return false;

        ItemStack held = player.getMainHandStack();
        Item type = held.getItem();

        if (type == Items.BRICK) {
            tardis.siege().texture().set(SiegeHandler.BRICK_TEXTURE);
            return false;
        }

        if (type == Items.STONE) {
            tardis.siege().texture().set(SiegeHandler.DEFAULT_TEXTURRE);
            return false;
        }

        if (type instanceof LinkableItem linker) {
            if (linker instanceof SonicItem)
                return false;

            linker.link(held, tardis);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS,
                    1.0F, 1.0F);
            return true;
        }

        if (type instanceof NameTagItem) {
            if (!held.hasCustomName())
                return false;

            tardis.stats().setName(held.getName().getString());
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1F, 1.0F);

            if (!player.isCreative())
                held.decrement(1);

            return true;
        }

        if (type instanceof HypercubeItem) {
            // todo - cleanup
            DistressCall call = HypercubeItem.getCall(held, world.getServer().getTicks());

            if (call == null) {
                // create new call
                call = DistressCall.create(tardis, held.hasCustomName() ? held.getName().getString() : "SOS", true);
                HypercubeItem.setCall(held, call);
            }

            if (call.canSend(tardis.getUuid())) {
                call.send(tardis.getUuid(), held);
            }

            // receive and process call

            call.summon(tardis, held);

            return true;
        }

        if (held.isOf(Items.NETHER_STAR) && tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT)) {
            tardis.selfDestruct().boom();
            if (!(tardis.selfDestruct().isQueued())) return false;

            if (!player.isCreative())
                held.decrement(1);
            return true;
        }

        if (LockedDimensionRegistry.tryUnlockDimension(player, held, tardis.asServer())) return true;

        ItemOpinion opinion = ItemOpinionRegistry.getInstance().get(held.getItem()).orElse(null);
        if (opinion != null && tardis.opinions().contains(opinion) && (player.experienceLevel >= opinion.cost() || player.isCreative())) {
            opinion.apply(tardis.asServer(), player);

            player.getServerWorld().playSound(null, console, AITSounds.GROAN, SoundCategory.AMBIENT, 0.25f, 1f);
            player.getServerWorld().spawnParticles((opinion.likes()) ? ParticleTypes.HEART : ParticleTypes.ANGRY_VILLAGER, console.toCenterPos().getX(),
                    console.toCenterPos().getY() + 1, console.toCenterPos().getZ(), 1, 0f, 1F, 0f, 5.0F);

            return true;
        }

        Text text = Text.translatable("tardis.message.control.telepathic.choosing");
        player.sendMessage(text, true);

        DirectedGlobalPos.Cached globalPos = tardis.travel().position();

        locateStructureOfInterest(player, tardis, globalPos.getWorld(), globalPos.getPos());
        return true;
    }

    public static void locateStructureOfInterest(ServerPlayerEntity player, Tardis tardis, ServerWorld world,
            BlockPos source) {
        if (world.getRegistryKey() == World.NETHER) {
            getStructureViaChunkGen(player, tardis, world, source, RADIUS, StructureKeys.FORTRESS);
        } else if (world.getRegistryKey() == World.END) {
            getStructureViaChunkGen(player, tardis, world, source, RADIUS, StructureKeys.END_CITY);
        } else if (world.getRegistryKey() == World.OVERWORLD) {
            getStructureViaWorld(player, tardis, world, source, RADIUS, StructureTags.VILLAGE);
        } else {
            Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            // get a list of all the registry entries
            List<RegistryEntry<Structure>> structures = new ArrayList<>();

            for (int i = 0; i < registry.size() - 1; i++) {
                structures.add(registry.getEntry(i).orElseThrow());
            }

            locateWithChunkGenAsync(player, tardis, RegistryEntryList.of(structures), world, source, RADIUS);
        }
    }

    public static void getStructureViaChunkGen(ServerPlayerEntity player, Tardis tardis, ServerWorld world,
            BlockPos pos, int radius, RegistryKey<Structure> key) {
        Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);

        if (registry.getEntry(key).isPresent())
            locateWithChunkGenAsync(player, tardis, RegistryEntryList.of(registry.getEntry(key).get()), world, pos,
                    radius);
    }

    public static void getStructureViaWorld(ServerPlayerEntity player, Tardis tardis, ServerWorld world, BlockPos pos,
            int radius, TagKey<Structure> key) {
        locateWithWorldAsync(player, tardis, key, world, pos, radius);
    }

    @Override
    public boolean requiresPower() {
        return false;
    }

    @Override
    public long getDelayLength() {
        return 5 * 1000L;
    }

    public static void locateWithChunkGenAsync(ServerPlayerEntity player, Tardis tardis,
            RegistryEntryList<Structure> structureList, ServerWorld world, BlockPos center, int radius) {
        AsyncLocatorUtil.locate(world, structureList, center, radius, false).thenOnServerThread(pos -> {
            BlockPos newPos = pos != null ? pos.getFirst() : null;
            if (newPos != null) {
                tardis.travel().forceDestination(cached -> cached.pos(newPos.withY(75)));
                tardis.removeFuel(500 * tardis.travel().getHammerUses());
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.success"), true);
            } else {
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.failed"), true);
            }
        });
    }

    public static void locateWithWorldAsync(ServerPlayerEntity player, Tardis tardis, TagKey<Structure> structureTagKey,
            ServerWorld world, BlockPos center, int radius) {
        AsyncLocatorUtil.locate(world, structureTagKey, center, radius, false).thenOnServerThread(pos -> {
            if (pos != null) {
                tardis.travel().forceDestination(cached -> cached.pos(pos.withY(75)));
                tardis.removeFuel(500 * tardis.travel().instability());
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.success"), true);
            } else {
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.failed"), true);
            }
        });
    }

}
