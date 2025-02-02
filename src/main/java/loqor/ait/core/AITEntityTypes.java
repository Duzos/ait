package loqor.ait.core;

import dev.pavatus.lib.container.AssignedName;
import dev.pavatus.lib.container.impl.EntityContainer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import loqor.ait.core.entities.*;

public class AITEntityTypes implements EntityContainer {

    @AssignedName("control_entity")
    public static final EntityType<ConsoleControlEntity> CONTROL_ENTITY_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, ConsoleControlEntity::new).dimensions(EntityDimensions.changing(0.125f, 0.125f))
            .build();

    @AssignedName("falling_tardis")
    public static final EntityType<FallingTardisEntity> FALLING_TARDIS_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, FallingTardisEntity::new).dimensions(EntityDimensions.changing(0.98f, 0.98f))
            .build();

    @AssignedName("flight_tardis")
    public static final EntityType<FlightTardisEntity> FLIGHT_TARDIS_TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, FlightTardisEntity::new).dimensions(EntityDimensions.changing(0.98f, 0.98f))
            .build();

    public static final EntityType<GallifreyFallsPaintingEntity> GALLIFREY_FALLS_PAINTING_TYPE = FabricEntityTypeBuilder
            .<GallifreyFallsPaintingEntity>create(SpawnGroup.MISC, GallifreyFallsPaintingEntity::new)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build();

    public static final EntityType<CobbledSnowballEntity> COBBLED_SNOWBALL_TYPE = FabricEntityTypeBuilder
            .<CobbledSnowballEntity>create(SpawnGroup.MISC, CobbledSnowballEntity::new)
            .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeBlocks(4).trackedUpdateRate(10).build();
}
