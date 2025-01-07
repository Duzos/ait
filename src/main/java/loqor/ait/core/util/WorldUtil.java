package loqor.ait.core.util;

import java.util.ArrayList;
import java.util.List;

import dev.drtheo.stp.SeamlessTp;
import dev.pavatus.config.AITConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;

import loqor.ait.AITMod;
import loqor.ait.api.TardisEvents;
import loqor.ait.api.WorldWithTardis;
import loqor.ait.core.AITDimensions;
import loqor.ait.core.tardis.ServerTardis;
import loqor.ait.core.tardis.handler.travel.TravelHandlerBase;
import loqor.ait.core.tardis.util.NetworkUtil;
import loqor.ait.core.world.TardisServerWorld;
import loqor.ait.data.DirectedGlobalPos;
import loqor.ait.mixin.server.EnderDragonFightAccessor;

@SuppressWarnings("deprecation")
public class WorldUtil {

    private static final List<Identifier> blacklisted = new ArrayList<>();
    private static List<ServerWorld> worlds;
    private static final int SAFE_RADIUS = 3;

    private static ServerWorld OVERWORLD;
    private static ServerWorld TIME_VORTEX;

    public static void init() {
        for (String id : AITMod.CONFIG.SERVER.WORLDS_BLACKLIST) {
            blacklisted.add(Identifier.tryParse(id));
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> worlds = getDimensions(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> worlds = null);

        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD)
                OVERWORLD = null;

            if (world.getRegistryKey() == AITDimensions.TIME_VORTEX_WORLD)
                TIME_VORTEX = null;
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD)
                OVERWORLD = world;

            if (world.getRegistryKey() == AITDimensions.TIME_VORTEX_WORLD)
                TIME_VORTEX = world;
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            OVERWORLD = server.getOverworld();
            TIME_VORTEX = server.getWorld(AITDimensions.TIME_VORTEX_WORLD);

            // blacklist all tardises
            for (ServerWorld world : getDimensions(server)) {
                if (TardisServerWorld.isTardisDimension(world)) blacklist(world);
            }
        });

        if (!AITMod.CONFIG.SERVER.STP_PRELOADING)
            return;

        TardisEvents.SYNC_TARDIS.register(WorldWithTardis.forSync((player, tardisSet) -> {
            for (ServerTardis tardis : tardisSet) {
                if (player.getServerWorld() == tardis.getInteriorWorld())
                    continue;

                BlockPos doorPos = tardis.getDesktop().doorPos().getPos();
                SeamlessTp.preloadAll(player, tardis.getInteriorWorld(), new ChunkPos(doorPos));
            }
        }));

        TardisEvents.UNLOAD_TARDIS.register(WorldWithTardis.forDesync((player, tardisSet) -> {
            for (ServerTardis tardis : tardisSet) {
                SeamlessTp.unload(player, tardis.getInteriorWorld());
            }
        }));

        TardisEvents.ENTER_TARDIS.register((tardis, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player) || !tardis.travel().isLanded())
                return;

            DirectedGlobalPos.Cached cached = tardis.travel().position();
            SeamlessTp.preloadAll(player, cached.getWorld(), new ChunkPos(cached.getPos()));
        });

        // When a TARDIS exterior gets placed it doesn't trigger SYNC_TARDIS!
        TardisEvents.CREATE_TARDIS.register(tardis -> {
            ServerWorld world = tardis.getInteriorWorld();
            ChunkPos doorPos = new ChunkPos(tardis.getDesktop().doorPos().getPos());

            NetworkUtil.getSubscribedPlayers(tardis).forEach(player
                    -> SeamlessTp.preloadAll(player, world, doorPos));
        });
    }

    public static ServerWorld getOverworld() {
        return OVERWORLD;
    }

    public static ServerWorld getTimeVortex() {
        return TIME_VORTEX;
    }

    public static List<ServerWorld> getDimensions(MinecraftServer server) {
        List<ServerWorld> worlds = new ArrayList<>();

        for (ServerWorld world : server.getWorlds()) {
            if (isOpen(world.getRegistryKey()))
                worlds.add(world);
        }

        return worlds;
    }

    public static boolean isOpen(RegistryKey<World> world) {
        for (Identifier blacklisted : blacklisted) {
            if (world.getValue().equals(blacklisted))
                return false;
        }

        return true;
    }

    public static void blacklist(RegistryKey<World> world) {
        blacklisted.add(world.getValue());
    }

    public static void blacklist(World world) {
        blacklist(world.getRegistryKey());
    }

    public static int worldIndex(ServerWorld world) {
        for (int i = 0; i < worlds.size(); i++) {
            if (world == worlds.get(i))
                return i;
        }

        return -1;
    }

    public static List<ServerWorld> getOpenWorlds() {
        return worlds;
    }

    public static DirectedGlobalPos.Cached locateSafe(DirectedGlobalPos.Cached cached,
            TravelHandlerBase.GroundSearch vSearch, boolean hSearch) {
        ServerWorld world = cached.getWorld();
        BlockPos pos = cached.getPos();

        if (isSafe(world, pos))
            return cached;

        if (hSearch) {
            BlockPos temp = findSafeXZ(world, pos, SAFE_RADIUS);

            if (temp != null)
                return cached.pos(temp);
        }

        int x = pos.getX();
        int z = pos.getZ();

        int y = switch (vSearch) {
            case CEILING -> findSafeTopY(world, pos);
            case FLOOR -> findSafeBottomY(world, pos);
            case MEDIAN -> findSafeMedianY(world, pos);
            case NONE -> pos.getY();
        };

        return cached.pos(x, y, z);
    }

    private static BlockPos findSafeXZ(ServerWorld world, BlockPos original, int radius) {
        BlockPos.Mutable pos = original.mutableCopy();

        int minX = pos.getX() - radius;
        int maxX = pos.getX() + radius;

        int minZ = pos.getZ() - radius;
        int maxZ = pos.getZ() + radius;

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                pos.setX(x).setZ(z);

                if (isSafe(world, pos))
                    return pos;
            }
        }

        return null;
    }

    private static int findSafeMedianY(ServerWorld world, BlockPos pos) {
        BlockPos upCursor = pos;
        BlockState floorUp = world.getBlockState(upCursor.down());
        BlockState curUp = world.getBlockState(upCursor);
        BlockState aboveUp = world.getBlockState(upCursor.up());

        BlockPos downCursor = pos;
        BlockState floorDown = world.getBlockState(downCursor.down());
        BlockState curDown = world.getBlockState(downCursor);
        BlockState aboveDown = world.getBlockState(downCursor.up());

        while (true) {
            boolean canGoUp = upCursor.getY() < world.getTopY();
            boolean canGoDown = downCursor.getY() > world.getBottomY();

            if (!canGoUp && !canGoDown)
                return pos.getY();

            if (canGoUp) {
                if (isSafe(floorUp, curUp, aboveUp))
                    return upCursor.getY() - 1;

                upCursor = upCursor.up();

                floorUp = curUp;
                curUp = aboveUp;
                aboveUp = world.getBlockState(upCursor);
            }

            if (canGoDown) {
                if (isSafe(floorDown, curDown, aboveDown))
                    return downCursor.getY() + 1;

                downCursor = downCursor.down();

                curDown = aboveDown;
                aboveDown = floorDown;
                floorDown = world.getBlockState(downCursor);
            }
        }
    }

    private static int findSafeBottomY(ServerWorld world, BlockPos pos) {
        BlockPos cursor = pos.withY(world.getBottomY() + 2);

        BlockState floor = world.getBlockState(cursor.down());
        BlockState current = world.getBlockState(cursor);
        BlockState above = world.getBlockState(cursor.up());

        while (true) {
            if (cursor.getY() > world.getTopY())
                return pos.getY();

            if (isSafe(floor, current, above))
                return cursor.getY() - 1;

            cursor = cursor.up();

            floor = current;
            current = above;
            above = world.getBlockState(cursor);
        }
    }

    private static int findSafeTopY(ServerWorld world, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();

        return world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z))
                .sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x & 15, z & 15) + 1;
    }

    private static boolean isSafe(BlockState floor, BlockState block1, BlockState block2) {
        return isFloor(floor) && !block1.blocksMovement() && !block2.blocksMovement();
    }

    private static boolean isSafe(BlockState block1, BlockState block2) {
        return !block1.blocksMovement() && !block2.blocksMovement();
    }

    private static boolean isFloor(BlockState floor) {
        return floor.blocksMovement();
    }

    private static boolean isSafe(World world, BlockPos pos) {
        BlockState floor = world.getBlockState(pos.down());

        if (!isFloor(floor))
            return false;

        BlockState curUp = world.getBlockState(pos);
        BlockState aboveUp = world.getBlockState(pos.up());

        return isSafe(curUp, aboveUp);
    }

    public static void onBreakHalfInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleBlockHalf = state.get(Properties.DOUBLE_BLOCK_HALF);

        if (doubleBlockHalf != DoubleBlockHalf.UPPER)
            return;

        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.isOf(state.getBlock())
                && blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            BlockState withFluid = blockState.getFluidState().isOf(Fluids.WATER)
                    ? Blocks.WATER.getDefaultState()
                    : Blocks.AIR.getDefaultState();

            world.setBlockState(blockPos, withFluid, 35);
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
        }
    }

    public static boolean isEndDragonDead() {
        ServerWorld end = ServerLifecycleHooks.get().getWorld(World.END);

        if (end == null)
            return true;

        return ((EnderDragonFightAccessor) end.getEnderDragonFight()).getDragonKilled();
    }

    public static void teleportToWorld(ServerPlayerEntity player, ServerWorld target, Vec3d pos, float yaw, float pitch) {
        //player.teleport(target, pos.x, pos.y, pos.z, yaw, pitch);
        SeamlessTp.teleport(player, target, pos, yaw, pitch);
        player.addExperience(0);

        player.getStatusEffects().forEach(effect -> player.networkHandler.sendPacket(
                new EntityStatusEffectS2CPacket(player.getId(), effect)));
    }
}
