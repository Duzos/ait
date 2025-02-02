package loqor.ait.core.blockentities;


import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import loqor.ait.api.link.v2.block.InteriorLinkableBlockEntity;
import loqor.ait.core.AITBlockEntityTypes;
import loqor.ait.core.AITSounds;
import loqor.ait.core.item.blueprint.Blueprint;
import loqor.ait.core.item.blueprint.BlueprintItem;
import loqor.ait.core.item.blueprint.BlueprintSchema;

public class FabricatorBlockEntity extends InteriorLinkableBlockEntity {
    private Blueprint blueprint;

    public FabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FABRICATOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void useOn(BlockState state, World world, boolean sneaking, PlayerEntity player) {
        if (world.isClient()) return;
        if (!this.isValid()) return;

        ItemStack hand = player.getMainHandStack();
        // accept new blueprint
        if (!this.hasBlueprint() && hand.getItem() instanceof BlueprintItem) {
            BlueprintSchema schema = BlueprintItem.getSchema(hand);
            if (schema == null) return;

            this.setBlueprint(schema.create());
            // hand.decrement(1);
            world.playSound(null, this.getPos(), AITSounds.FABRICATOR_START, SoundCategory.BLOCKS, 1, 1);
            return;
        }

        // try to insert items into the fabricator
        if (this.hasBlueprint()) {
            Blueprint blueprint = this.getBlueprint().get();
            if (blueprint.tryAdd(hand)) {
                // world.playSound(null, this.getPos(), AITSounds.DING, SoundCategory.BLOCKS, 1, 1);
                this.syncChanges();

                if (blueprint.isComplete()) {
                    world.playSound(null, this.getPos(), AITSounds.FABRICATOR_END, SoundCategory.BLOCKS, 1, 1);
                }

                return;
            }

            // try to craft the blueprint
            Optional<ItemStack> output = blueprint.tryCraft();
            if (output.isPresent()) {
                ItemStack stack = output.get();
                player.getInventory().offerOrDrop(stack);
                // world.playSound(null, this.getPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1);
                this.setBlueprint(null, true);
            }
        }
    }

    public boolean isValid() {
        if (!this.hasWorld()) return false;

        return this.getWorld().getBlockState(this.getPos().down()).isOf(Blocks.SMITHING_TABLE);
    }

    public Optional<Blueprint> getBlueprint() {
        return Optional.ofNullable(this.blueprint);
    }
    public boolean hasBlueprint() {
        return this.getBlueprint().isPresent();
    }

    /**
     * attempts to set the blueprint of this fabricator
     * @return false if this fabricator already has a blueprint
     */
    public boolean setBlueprint(Blueprint blueprint, boolean force) {
        if (!force && this.hasBlueprint()) return false;
        this.blueprint = blueprint;
        this.syncChanges();
        return true;
    }
    /**
     * attempts to set the blueprint of this fabricator
     * @return false if this fabricator already has a blueprint
     */
    public boolean setBlueprint(Blueprint blueprint) {
        return this.setBlueprint(blueprint, false);
    }

    /**
     * @return the itemstack that should be displayed in the fabricator's renderer
     */
    public ItemStack getShowcaseStack() {
        if (this.hasBlueprint()) {
            if (this.blueprint.isComplete()) return this.blueprint.getOutput();

            // cycle through the requirements based off world ticks
            int index = (int) (world.getTime() / 20 % this.blueprint.getRequirements().size());
            return this.blueprint.getRequirements().get(index);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.blueprint = null;
        if (nbt.contains("Blueprint")) {
            blueprint = new Blueprint(nbt.getCompound("Blueprint"));
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (blueprint != null) {
            nbt.put("Blueprint", blueprint.toNbt());
        }
        nbt.putBoolean("HasBlueprint", this.hasBlueprint());
    }
    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    protected void syncChanges() {
        if (this.getWorld().isClient()) return;

        ServerWorld world = (ServerWorld) this.getWorld();
        world.getChunkManager().markForUpdate(this.getPos());
        this.markDirty();
    }
}
