package loqor.ait.core;



import static loqor.ait.core.AITItems.isUnlockedOnThisDay;

import java.util.Calendar;

import dev.pavatus.lib.container.impl.BlockEntityContainer;
import dev.pavatus.planet.core.PlanetBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.block.entity.BlockEntityType;

import loqor.ait.core.blockentities.*;
import loqor.ait.core.blockentities.control.RedstoneControlBlockEntity;
import loqor.ait.core.blocks.PowerConverterBlock;
import loqor.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import loqor.ait.core.engine.link.block.FluidLinkBlockEntity;

public class AITBlockEntityTypes implements BlockEntityContainer {
    public static BlockEntityType<SnowGlobeBlockEntity> SNOW_GLOBE_BLOCK_ENTITY_TYPE;

    public static BlockEntityType<ExteriorBlockEntity> EXTERIOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ExteriorBlockEntity::new, AITBlocks.EXTERIOR_BLOCK).build();
    public static BlockEntityType<DoorBlockEntity> DOOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(DoorBlockEntity::new, AITBlocks.DOOR_BLOCK).build();
    public static BlockEntityType<ConsoleBlockEntity> CONSOLE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ConsoleBlockEntity::new, AITBlocks.CONSOLE).build();
    public static BlockEntityType<ConsoleGeneratorBlockEntity> CONSOLE_GENERATOR_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ConsoleGeneratorBlockEntity::new, AITBlocks.CONSOLE_GENERATOR).build();
    public static BlockEntityType<CoralBlockEntity> CORAL_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(CoralBlockEntity::new, AITBlocks.CORAL_PLANT).build();
    public static BlockEntityType<MonitorBlockEntity> MONITOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(MonitorBlockEntity::new, AITBlocks.MONITOR_BLOCK).build();
    public static BlockEntityType<DetectorBlockEntity> DETECTOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(DetectorBlockEntity::new, AITBlocks.DETECTOR_BLOCK).build();
    public static BlockEntityType<ArtronCollectorBlockEntity> ARTRON_COLLECTOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ArtronCollectorBlockEntity::new, AITBlocks.ARTRON_COLLECTOR_BLOCK).build();
    public static BlockEntityType<PlaqueBlockEntity> PLAQUE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(PlaqueBlockEntity::new, AITBlocks.PLAQUE_BLOCK).build();
    public static BlockEntityType<EngineBlockEntity> ENGINE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(EngineBlockEntity::new, AITBlocks.ENGINE_BLOCK).build();
    public static BlockEntityType<WallMonitorBlockEntity> WALL_MONITOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(WallMonitorBlockEntity::new, AITBlocks.WALL_MONITOR_BLOCK).build();
    public static BlockEntityType<EngineCoreBlockEntity> ENGINE_CORE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(EngineCoreBlockEntity::new, AITBlocks.ENGINE_CORE_BLOCK).build();
    public static BlockEntityType<MachineCasingBlockEntity> MACHINE_CASING_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(MachineCasingBlockEntity::new, AITBlocks.MACHINE_CASING).build();
    public static BlockEntityType<FabricatorBlockEntity> FABRICATOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(FabricatorBlockEntity::new, AITBlocks.FABRICATOR).build();
    public static BlockEntityType<EnvironmentProjectorBlockEntity> ENVIRONMENT_PROJECTOR_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(EnvironmentProjectorBlockEntity::new, AITBlocks.ENVIRONMENT_PROJECTOR).build();
    public static BlockEntityType<WaypointBankBlockEntity> WAYPOINT_BANK_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(WaypointBankBlockEntity::new, AITBlocks.WAYPOINT_BANK).build();

    public static final BlockEntityType<AITRadioBlockEntity> AIT_RADIO_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(AITRadioBlockEntity::new, AITBlocks.RADIO).build();
    public static BlockEntityType<RedstoneControlBlockEntity> REDSTONE_CONTROL_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
            .create(RedstoneControlBlockEntity::new, AITBlocks.REDSTONE_CONTROL_BLOCK).build();

    public static final BlockEntityType<FlagBlockEntity> FLAG_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(FlagBlockEntity::new, PlanetBlocks.FLAG).build();
    public static BlockEntityType<FluidLinkBlockEntity> FLUID_LINK_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
            .create(FluidLinkBlockEntity::new, AITBlocks.CABLE_BLOCK).build();
    public static BlockEntityType<ZeitonCageBlockEntity> ZEITON_CAGE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
            .create(ZeitonCageBlockEntity::new, AITBlocks.ZEITON_CAGE).build();
    public static BlockEntityType<PowerConverterBlock.BlockEntity> POWER_CONVERTER_BLOCK_TYPE = FabricBlockEntityTypeBuilder
            .create(PowerConverterBlock.BlockEntity::new, AITBlocks.POWER_CONVERTER).build();
    public static BlockEntityType<GenericStructureSystemBlockEntity> GENERIC_SUBSYSTEM_BLOCK_TYPE = FabricBlockEntityTypeBuilder
            .create(GenericStructureSystemBlockEntity::new, AITBlocks.GENERIC_SUBSYSTEM).build();

    // TODO ADVENT might have to make this work like the block as well
    static {
        if (isUnlockedOnThisDay(Calendar.DECEMBER, 30)) {
            SNOW_GLOBE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder
                    .create(SnowGlobeBlockEntity::new, AITBlocks.SNOW_GLOBE).build();
        }
    }
}
