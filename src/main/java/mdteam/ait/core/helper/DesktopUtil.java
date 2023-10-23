package mdteam.ait.core.helper;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DesktopUtil {
    public static List<AbsoluteBlockPos> getNextAvailableInteriorSpot() {
        List<AbsoluteBlockPos> list = new ArrayList<>();

        AbsoluteBlockPos bottomLeft = generateRandomPosInTARDISDim();
        AbsoluteBlockPos topRight = new AbsoluteBlockPos(bottomLeft.toBlockPos().add(256,0,256), TardisUtil.getTardisDimension());

        list.add(bottomLeft);
        list.add(topRight);

        return list;
    }
    private static AbsoluteBlockPos generateRandomPosInTARDISDim() {
        Random random = new Random();

        int x = random.nextInt(100000);
        int z = random.nextInt(100000);
        AbsoluteBlockPos pos = new AbsoluteBlockPos(new BlockPos(x,0,z), TardisUtil.getTardisDimension());
        return pos;
    }
}

