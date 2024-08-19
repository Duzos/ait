package loqor.ait.tardis.data.landing;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class LandingPadRegion {

    public static final Codec<LandingPadRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("chunk").forGetter(o -> o.getChunk().toLong()),
            Codec.INT.fieldOf("y").forGetter(LandingPadRegion::getDefaultY),
            Codec.list(LandingPadSpot.CODEC).fieldOf("spots").forGetter(o -> o.spots)
    ).apply(instance, LandingPadRegion::create));

    private static final int CHUNK_LENGTH = 16;

    private static final int MAX_SPOTS = CHUNK_LENGTH * CHUNK_LENGTH / 4;
    private static final int MAX_PER_ROW = CHUNK_LENGTH / 2;

    private final ChunkPos chunk;
    private final List<LandingPadSpot> spots;

    private final int defaultY;

    private static LandingPadRegion create(long chunk, int y, List<LandingPadSpot> spots) {
        return new LandingPadRegion(new ChunkPos(chunk), y, spots);
    }

    private LandingPadRegion(ChunkPos chunk, int y, List<LandingPadSpot> spots) {
        this.chunk = chunk;
        this.spots = spots;

        this.defaultY = y;
        this.createAllSpots();
    }

    public LandingPadRegion(ChunkPos pos, int y) {
        this(pos, y, new ArrayList<>());
    }

    public @Nullable LandingPadSpot getFreeSpot() {
        for (LandingPadSpot spot : this.spots) {
            if (spot.isOccupied())
                continue;

            return spot;
        }

        return null; // unreachable
    }

    private LandingPadSpot createSpot() {
        if (this.spots.isEmpty())
            return new LandingPadSpot(new BlockPos(
                    this.chunk.getStartX() + 1, this.defaultY, this.chunk.getStartZ() + 1
            ));

        // FIXME mid code
        float rowCount = ((float) this.spots.size() / MAX_PER_ROW);
        boolean isNewRow = rowCount == Math.round(rowCount);

        if (!isNewRow)
            return new LandingPadSpot(this.spots.get(this.spots.size() - 1)
                    .getPos().add(2, 0, 0));

        return new LandingPadSpot(this.spots.get(0)
                .getPos().add(0, 0, 2)
        );
    }

    private LandingPadSpot generateSpot() {
        LandingPadSpot created = this.createSpot();
        this.spots.add(created);

        return created;
    }

    public Collection<LandingPadSpot> getSpots() {
        return this.spots;
    }

    public int getDefaultY() {
        return defaultY;
    }

    public ChunkPos getChunk() {
        return chunk;
    }

    private boolean isFull() {
        return this.spots.size() >= MAX_SPOTS;
    }

    private void createAllSpots() {
        int toCreate = MAX_SPOTS - this.spots.size();

        for (int i = 0; i < toCreate; i++) {
            this.generateSpot();
        }
    }

    @Override
    public String toString() {
        return "LandingPadRegion{" +
                "chunk=" + chunk +
                ", spots=" + spots +
                ", defaultY=" + defaultY +
                '}';
    }
}