package mdteam.ait.core.helper;

import mdteam.ait.AITMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.io.Serializable;
import java.util.Iterator;

public class AbsoluteBlockPos implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient World dimension;
    private String dimensionValue;
    private String dimensionRegistry;

    private Direction direction;
    int x;
    int y;
    int z;

    public AbsoluteBlockPos(int x, int y, int z, Direction direction, World dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        setDimension(dimension);
        setDirection(direction);
    }
    public AbsoluteBlockPos(BlockPos pos, World dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), Direction.NORTH, dimension);
    }

    public AbsoluteBlockPos(BlockPos pos, Direction direction, World dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), direction, dimension);
    }

    public void setDimension(World dimension) {
        this.dimension = dimension;
        this.dimensionValue = this.dimension.getRegistryKey().getValue().toString();
        this.dimensionRegistry = this.dimension.getRegistryKey().getRegistry().toString();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    public World getDimension() {
        if(dimension == null) {
            dimension = AITMod.mcServer.getWorld(RegistryKey.of(RegistryKey.ofRegistry(new Identifier(dimensionRegistry)),new Identifier(dimensionValue)));
        }
        return dimension;
    }

    public Direction getDirection() {
        if(direction != null) {
            direction = Direction.byId(direction.getId());
        }
        return direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public AbsoluteBlockPos above() {
        return new AbsoluteBlockPos(new BlockPos(getX(), getY() + 1, getZ()), getDimension());
    }

    @Override
    public String toString() {
        return "AbsoluteBlockPos[ "+ getX() + " _ " + getY() + " _ " + getZ() + " ] | [ " + getDirection() + " _ " + getDimension() + " ]";
    }

}

