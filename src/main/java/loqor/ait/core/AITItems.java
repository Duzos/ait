package loqor.ait.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.pavatus.lib.container.impl.ItemContainer;
import dev.pavatus.lib.datagen.util.NoEnglish;
import dev.pavatus.lib.item.AItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.util.Rarity;

import loqor.ait.AITMod;
import loqor.ait.core.engine.SubSystem;
import loqor.ait.core.engine.item.SubSystemItem;
import loqor.ait.core.item.*;
import loqor.ait.core.item.blueprint.BlueprintItem;
import loqor.ait.core.item.blueprint.BlueprintRegistry;
import loqor.ait.core.item.blueprint.BlueprintSchema;
import loqor.ait.core.item.control.GenericControlBlockItem;
import loqor.ait.core.item.link.AbstractLinkItem;
import loqor.ait.core.item.link.FluidLinkItem;
import loqor.ait.core.item.link.MercurialLinkItem;
import loqor.ait.core.item.part.MachinePartItem;

public class AITItems extends ItemContainer {

    // TODO ADVENT ITEMS GO UP HERE AND DECLARED IN THE STATIC METHOD AT THE BOTTOM
    public static Item COBBLED_SNOWBALL;
    public static Item HOT_CHOCOLATE_POWDER;
    public static Item HOT_CHOCOLATE;
    public static Item MUG;
    public static Item SANTA_HAT;
    public static final FoodComponent ZEITON_DUST_FOOD = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1000, 3), 1.0F)
            .statusEffect(new StatusEffectInstance(AITStatusEffects.ZEITON_HIGH, 500, 1), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WITHER, 500, 1), 0.5F)
            .build();

    // TARDIS
    @NoEnglish
    public static final Item TARDIS_ITEM = new TardisItemBuilder(
            new AItemSettings().group(AITItemGroups.MAIN).fireproof().maxCount(1));

    @NoEnglish
    public static final Item SIEGE_ITEM = new SiegeTardisItem(new FabricItemSettings().fireproof());

    // Functional Items
    @NoEnglish
    public static final Item REMOTE_ITEM = new RemoteItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    @NoEnglish
    public static final Item ARTRON_COLLECTOR = new ArtronCollectorItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item RIFT_SCANNER = new RiftScannerItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item GEIGER_COUNTER = new RiftScannerItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item HAMMER = new HammerItem(3, -2.4F,
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(600));
    public static final Item RESPIRATOR = new RenderableArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(80), true);
    public static final Item FACELESS_RESPIRATOR = new RenderableArmorItem(ArmorMaterials.IRON,
            ArmorItem.Type.HELMET, new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(80),
            true);
    public static final Item HYPERCUBE = new HypercubeItem(new AItemSettings().maxCount(1).group(AITItemGroups.MAIN));
    public static final Item HAZANDRA = new InteriorTeleporterItem(new AItemSettings().group(AITItemGroups.MAIN));

    // Keys/Key Templates
    public static final Item IRON_KEY = new KeyItem(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item GOLD_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.UNCOMMON).group(AITItemGroups.MAIN), KeyItem.Protocols.SNAP);
    public static final Item NETHERITE_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.RARE).group(AITItemGroups.MAIN).fireproof(), KeyItem.Protocols.SNAP,
            KeyItem.Protocols.HAIL);
    public static final Item CLASSIC_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.EPIC).group(AITItemGroups.MAIN), KeyItem.Protocols.SNAP,
            KeyItem.Protocols.HAIL);

    // Creative only skeleton key
    public static final Item SKELETON_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.EPIC).group(AITItemGroups.MAIN), KeyItem.Protocols.SKELETON,
            KeyItem.Protocols.HAIL, KeyItem.Protocols.SNAP);
    @NoEnglish
    public static final Item GOLD_KEY_UPGRADE_SMITHING_TEMPLATE = new KeySmithingTemplateItem(
            new AItemSettings().group(AITItemGroups.MAIN), "Gold Key", "Gold Nugget");
    @NoEnglish
    public static final Item NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE = new KeySmithingTemplateItem(
            new AItemSettings().group(AITItemGroups.MAIN), "Netherite Key", "Netherite Scrap");
    @NoEnglish
    public static final Item CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE = new KeySmithingTemplateItem(
            new AItemSettings().group(AITItemGroups.MAIN), "Classic Key", "Amethyst Shard");
    //public static final Item SONIC_SCREWDRIVER = new SonicItem(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item SONIC_SCREWDRIVER = new SonicItem2(new AItemSettings().group(AITItemGroups.MAIN));

    // Crafting items
    public static final Item ZEITON_SHARD = new ZeitonShardItem(new AItemSettings());
    public static final Item CHARGED_ZEITON_CRYSTAL = new ChargedZeitonCrystalItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item ZEITON_DUST = new Item(
            new AItemSettings().food(ZEITON_DUST_FOOD));

    // Machine parts
    public static final Item ARTRON_FLUID_LINK = new FluidLinkItem(AbstractLinkItem.Type.ARTRON,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item DATA_FLUID_LINK = new FluidLinkItem(AbstractLinkItem.Type.DATA,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item VORTEX_FLUID_LINK = new FluidLinkItem(AbstractLinkItem.Type.VORTEX,
            new AItemSettings().group(AITItemGroups.FABRICATOR));

    public static final Item ARTRON_MERCURIAL_LINK = new MercurialLinkItem(AbstractLinkItem.Type.ARTRON,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item DATA_MERCURIAL_LINK = new MercurialLinkItem(AbstractLinkItem.Type.DATA,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item VORTEX_MERCURIAL_LINK = new MercurialLinkItem(AbstractLinkItem.Type.VORTEX,
            new AItemSettings().group(AITItemGroups.FABRICATOR));

    public static final Item CONDENSER = new MachinePartItem(MachinePartItem.Type.CONDENSER,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item MANIPULATOR = new MachinePartItem(MachinePartItem.Type.MANIPULATOR,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item BULB = new MachinePartItem(MachinePartItem.Type.BULB,
            new AItemSettings() .group(AITItemGroups.FABRICATOR));
    public static final Item INDUCTOR = new MachinePartItem(MachinePartItem.Type.INDUCTOR,
            new AItemSettings().group(AITItemGroups.FABRICATOR));

    // Components
    public static final Item DEMATERIALIZATION_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.DEMAT);

    public static final Item SHIELDS_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.SHIELDS);
    public static final Item BACKUP_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.EMERGENCY_POWER);
    public static final Item GRAVITATIONAL_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.GRAVITATIONAL);
    public static final Item CHAMELEON_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.CHAMELEON);
    public static final Item DESPERATION_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.DESPERATION);
    public static final Item STABILISERS = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.STABILISERS);
    public static final Item LIFE_SUPPORT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.LIFE_SUPPORT);

    @NoEnglish
    public static final Item GALLIFREY_FALLS_PAINTING = new AITDecorationItem(AITEntityTypes.GALLIFREY_FALLS_PAINTING_TYPE, new AItemSettings().group(AITItemGroups.MAIN));

    // Blueprint
    public static final Item BLUEPRINT = new BlueprintItem(
            new AItemSettings().rarity(Rarity.EPIC));

    // Waypoint-related
    public static final Item WAYPOINT_CARTRIDGE = new WaypointItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));

    // Music discs
    @NoEnglish
    public static final Item DRIFTING_MUSIC_DISC = new MusicDiscItem(1, AITSounds.DRIFTING_MUSIC,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 169);

    @NoEnglish
    public static final Item WONDERFUL_TIME_IN_SPACE_MUSIC_DISC = new MusicDiscItem(1, AITSounds.WONDERFUL_TIME_IN_SPACE,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 73);

    @NoEnglish
    public static final Item MERCURY_MUSIC_DISC = new MusicDiscItem(11, AITSounds.MERCURY_MUSIC,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 216);



    // Refreshments
    @NoEnglish
    public static final Item MUG_EMPTY = new MugEmptyItem(new AItemSettings());
    @NoEnglish
    public static final Item COFFEE = new CoffeeItem(new AItemSettings());
    @NoEnglish
    public static final Item TEA = new TeaItem(new AItemSettings());
    @NoEnglish
    public static final Item LATTE = new LatteItem(new AItemSettings());
    @NoEnglish
    public static final Item MILK = new MilkItem(new AItemSettings());
    @NoEnglish
    public static final Item WATER = new WaterItem(new AItemSettings());
    @NoEnglish
    public static final Item ICE_COFFEE = new IceCoffeeItem(new AItemSettings());
    @NoEnglish
    public static final Item COCO_MILK = new CocoMilkItem(new AItemSettings());

    public static final Item IRON_GOAT_HORN = new TardisGoatHorn(new AItemSettings().group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS);

    public static final Item GOLD_GOAT_HORN = new TardisGoatHorn(new AItemSettings().rarity(Rarity.UNCOMMON).group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS);

    public static final Item CLASSIC_GOAT_HORN = new TardisGoatHorn(new AItemSettings().rarity(Rarity.EPIC).group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS, TardisGoatHorn.Protocols.HAIL);

    public static final Item NETHERITE_GOAT_HORN = new TardisGoatHorn(new AItemSettings().rarity(Rarity.RARE).group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS, TardisGoatHorn.Protocols.HAIL);

    // Block controls

     public static final Item REDSTONE_CONTROL = new
             GenericControlBlockItem(AITBlocks.REDSTONE_CONTROL_BLOCK, new
     AItemSettings().group(AITItemGroups.MAIN));

     // Advent Items

    static {
        if (isUnlockedOnThisDay(Calendar.DECEMBER, 27)) {
            SANTA_HAT = new RenderableArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(80), true);
        }

        if (isUnlockedOnThisDay(Calendar.DECEMBER, 29)) {
            COBBLED_SNOWBALL = new CobbledSnowballItem(new AItemSettings().group(AITItemGroups.MAIN).maxCount(16));
        }
       if (isUnlockedOnThisDay(Calendar.JANUARY, 2)) {
            HOT_CHOCOLATE_POWDER = new Item(new AItemSettings().group(AITItemGroups.MAIN).food(ZEITON_DUST_FOOD));
            HOT_CHOCOLATE = new HotChocolateItem(new AItemSettings().group(AITItemGroups.MAIN));
            MUG = new Item(new AItemSettings().group(AITItemGroups.MAIN));
        }
        /*if (isUnlockedAdvent2024(4)) {
            // TODO SONIC CANDY CANE
        }*/
    }

    public static boolean isUnlockedOnThisDay(int month, int day) {
        return getAdventDates(month, Calendar.JANUARY, day, 6);
    }

    public static boolean isUnlockedAdvent2024(int day) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        // Check if the year is 2024 and the date is within December 26th to January 6th
        if (year == 2024) {
            return getAdventDates(Calendar.DECEMBER, Calendar.JANUARY, 26, 6);
        } else {
            // If the year is greater than 2024, always return true
            return true;
        }
    }

    public static boolean getAdventDates(int monthBegin, int monthEnd, int dayBegin, int dayEnd) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Handle the case where the date range spans across two years
        if (monthBegin == Calendar.DECEMBER && monthEnd == Calendar.JANUARY) {
            return (currentMonth == Calendar.DECEMBER && currentDay >= dayBegin) || (currentMonth == Calendar.JANUARY && currentDay <= dayEnd);
        } else {
            return currentMonth >= monthBegin && currentMonth <= monthEnd && currentDay >= dayBegin && currentDay <= dayEnd;
        }
    }

    public static boolean isInAdvent() {

        return getAdventDates(Calendar.DECEMBER, Calendar.JANUARY, 26, 6);
    }

    public static List<Item> get() {
        List<Item> list = new ArrayList<>();

        for (Item item : Registries.ITEM) {
            if (Registries.ITEM.getId(item).getNamespace().equalsIgnoreCase(AITMod.MOD_ID)) {
                list.add(item);
            }
        }

        return list;
    }

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.MUSIC_DISC_RELIC, DRIFTING_MUSIC_DISC);
            entries.addAfter(DRIFTING_MUSIC_DISC, MERCURY_MUSIC_DISC);
            entries.addAfter(MERCURY_MUSIC_DISC, WONDERFUL_TIME_IN_SPACE_MUSIC_DISC);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.addAfter(Items.MILK_BUCKET, COFFEE);
            entries.addAfter(COFFEE, TEA);
            entries.addAfter(TEA, LATTE);
            entries.addAfter(LATTE, MILK);
            entries.addAfter(MILK, WATER);
            entries.addAfter(WATER, ICE_COFFEE);
            entries.addAfter(ICE_COFFEE, COCO_MILK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.BUCKET, MUG_EMPTY);
        });

        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, AITItemGroups.FABRICATOR.id())).register(entries -> {
            for (BlueprintSchema schema : BlueprintRegistry.getInstance().toList()) {
                entries.add(BlueprintItem.createStack(schema));
            }
        });
    }
}
