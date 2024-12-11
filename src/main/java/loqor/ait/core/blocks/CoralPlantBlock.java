package loqor.ait.core.blocks;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import loqor.ait.api.TardisComponent;
import loqor.ait.core.AITBlocks;
import loqor.ait.core.AITSounds;
import loqor.ait.core.advancement.TardisCriterions;
import loqor.ait.core.blockentities.CoralBlockEntity;
import loqor.ait.core.blocks.types.HorizontalDirectionalBlock;
import loqor.ait.core.tardis.ServerTardis;
import loqor.ait.core.tardis.dim.TardisDimension;
import loqor.ait.core.tardis.handler.FuelHandler;
import loqor.ait.core.tardis.manager.ServerTardisManager;
import loqor.ait.core.tardis.manager.TardisBuilder;
import loqor.ait.core.world.RiftChunkManager;
import loqor.ait.data.DirectedGlobalPos;
import loqor.ait.data.schema.exterior.variant.growth.CoralGrowthVariant;
import loqor.ait.registry.impl.DesktopRegistry;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;

@SuppressWarnings("deprecation")
public class CoralPlantBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    private final VoxelShape DEFAULT = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);

    public static final IntProperty AGE = Properties.AGE_7;
    public static final BooleanProperty HAS_SMS = BooleanProperty.of("has_sms");

    public CoralPlantBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
                this.getDefaultState().with(AGE, 0).with(HAS_SMS, false)
        );
    }

    protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public final boolean isMature(BlockState blockState) {
        return this.getAge(blockState) >= this.getMaxAge();
    }
    public static boolean hasSms(BlockState state) {
        return state.get(HAS_SMS);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(AITBlocks.ENGINE_CORE_BLOCK.asItem()) && !hasSms(state)) {
            if (world.isClient()) return ActionResult.SUCCESS;

            // If the player is holding an engine core block, set the has_sms property to true
            world.setBlockState(pos, state.with(HAS_SMS, true));
            stack.decrement(1);

            world.playSound(null, pos, AITSounds.SIEGE_DISABLE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            tryCreate((ServerWorld) world, pos, state);

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                if (!(world.getBlockState(pos.down()).getBlock() instanceof SoulSandBlock)) {
                    world.breakBlock(pos, true);
                    return;
                }

                world.setBlockState(pos, state.with(AGE, i + 1), 2);
            }
        }

        tryCreate(world, pos, state);
    }

    private boolean tryCreate(ServerWorld world, BlockPos pos, BlockState state) {
        if (!this.isMature(state))
            return false;

        if (!hasSms(state)) {
            world.playSound(null, pos, AITSounds.SIEGE_ENABLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return false;
        }

        if (TardisDimension.isTardisDimension(world)) {
            this.createConsole(world, pos);
            return true;
        }

        if (world.getBlockEntity(pos) instanceof CoralBlockEntity coral)
            this.createTardis(world, pos, coral.creator);

        return true;
    }

    private void createConsole(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, AITBlocks.CONSOLE.getDefaultState());
    }

    private void createTardis(ServerWorld world, BlockPos pos, UUID creatorId) {
        if (!(world.getPlayerByUuid(creatorId) instanceof ServerPlayerEntity player))
            return;

        TardisBuilder builder = new TardisBuilder().at(DirectedGlobalPos.Cached.create(world, pos, (byte) 0))
                .owner(player)
                .<FuelHandler>with(TardisComponent.Id.FUEL, fuel -> fuel.setCurrentFuel(0))
                .exterior(ExteriorVariantRegistry.getInstance().get(CoralGrowthVariant.REFERENCE))
                .desktop(DesktopRegistry.DEFAULT_CAVE);

        ServerTardis created = ServerTardisManager.getInstance()
                .create(builder);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(placer instanceof ServerPlayerEntity player))
            return;

        if (!(world.getBlockState(pos.down()).getBlock() instanceof SoulSandBlock)
                || (!RiftChunkManager.isRiftChunk((ServerWorld) world, pos)
                        && !TardisDimension.isTardisDimension((ServerWorld) world))) {
            world.breakBlock(pos, !placer.isPlayer() || !player.isCreative());
            return;
        }

        if (world.getBlockEntity(pos) instanceof CoralBlockEntity coral) {
            coral.creator = player.getUuid();
            coral.markDirty();
        }

        TardisCriterions.PLACE_CORAL.trigger(player);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return DEFAULT;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return AITBlocks.CORAL_PLANT.asItem().getDefaultStack();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(FACING).add(HAS_SMS);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoralBlockEntity(pos, state);
    }
}
