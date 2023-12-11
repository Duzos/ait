package mdteam.ait.datagen;

import mdteam.ait.AITMod;
import mdteam.ait.core.AITBlocks;
import mdteam.ait.core.AITItems;
import mdteam.ait.core.AITSounds;
import mdteam.ait.datagen.datagen_providers.AITItemTagProvider;
import mdteam.ait.datagen.datagen_providers.AITLanguageProvider;
import mdteam.ait.datagen.datagen_providers.AITModelProvider;
import mdteam.ait.datagen.datagen_providers.AITSoundProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class AITModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		generateLanguages(pack);
		generateItemTags(pack); // fixme im not sure why this is being silly goofy
		generateBlockModels(pack);
		generateSoundData(pack);
	}

	public void generateSoundData(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) ->  {
			AITSoundProvider provider = new AITSoundProvider(output);

			provider.addSound("secret_music", AITSounds.SECRET_MUSIC);

			// TARDIS
			provider.addSound("tardis/demat", AITSounds.DEMAT);
			provider.addSound("tardis/mat", AITSounds.MAT);
			provider.addSound("tardis/hop_demat", AITSounds.HOP_DEMAT);
			provider.addSound("tardis/hop_mat", AITSounds.HOP_MAT);
			provider.addSound("tardis/fail_demat", AITSounds.FAIL_DEMAT);
			provider.addSound("tardis/fail_land", AITSounds.FAIL_MAT);
			provider.addSound("tardis/emergency_mat", AITSounds.EMERG_MAT);
			provider.addSound("tardis/eighth_demat", AITSounds.EIGHT_DEMAT);
			provider.addSound("tardis/eighth_mat", AITSounds.EIGHT_MAT);

			// Controls
			provider.addSound("controls/demat_lever_pull", AITSounds.DEMAT_LEVER_PULL);
			provider.addSound("controls/handbrake_lever_pull", AITSounds.HANDBRAKE_LEVER_PULL);

			return provider;
		})));
	}

	public void generateItemTags(FabricDataGenerator.Pack pack) {
		pack.addProvider(AITItemTagProvider::new);
	}

	public void generateBlockModels(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> {
			AITModelProvider aitModelProvider = new AITModelProvider(output);
			aitModelProvider.registerDirectionalBlock(AITBlocks.RADIO);
			aitModelProvider.registerDirectionalBlock(AITBlocks.CONSOLE);
			aitModelProvider.registerDirectionalBlock(AITBlocks.EXTERIOR_BLOCK);
			aitModelProvider.registerDirectionalBlock(AITBlocks.DOOR_BLOCK);

			//falloutModelProvider.registerSimpleBlock(AITBlocks.DEEPSLATE_URANIUM_ORE);
			return aitModelProvider;
		}));
	}

	public void generateLanguages(FabricDataGenerator.Pack pack) {
		generate_EN_US_Language(pack); // en_us (English US)
		generate_EN_UK_Language(pack); // en_uk (English UK)
		generate_FR_CA_Language(pack); // fr_ca (French Canadian)
		generate_FR_FR_Language(pack); // fr_fr (French France)
		generate_ES_AR_Language(pack); // es_ar (Spanish Argentina)
		generate_ES_CL_Language(pack); // es_cl (Spanish Chile)
		generate_ES_EC_Language(pack); // es_ec (Spanish Ecuador)
		generate_ES_ES_Language(pack); // es_es (Spanish Spain)
		generate_ES_MX_Language(pack); // es_mx (Spanish Mexico)
		generate_ES_UY_Language(pack); // es_uy (Spanish Uruguay)
		generate_ES_VE_Language(pack); // es_ve (Spanish Venezuela)
		generate_EN_AU_Language(pack); // en_au (English Australia)
		generate_EN_CA_Language(pack); // en_ca (English Canada)
		generate_EN_GB_Language(pack); // en_gb (English Great Britain)
		generate_EN_NZ_Language(pack); // en_nz (English New Zealand)
	}

	/**
	 * Adds English translations to the language file.
	 * @param output The data generator output.
	 * @param registriesFuture The registries future.
	 * @param languageType The language type.
	 * @return The AITLanguageProvider.
	 */
	public AITLanguageProvider addEnglishTranslations(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
		AITLanguageProvider aitLanguageProvider = new AITLanguageProvider(output, languageType);

		aitLanguageProvider.addTranslation(AITMod.AIT_ITEM_GROUP, "Adventures In Time");
		aitLanguageProvider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
		aitLanguageProvider.addTranslation(AITBlocks.DOOR_BLOCK, "Door");
		aitLanguageProvider.addTranslation(AITBlocks.CONSOLE, "Console");
		aitLanguageProvider.addTranslation(AITItems.IRON_KEY, "Iron Key");
		aitLanguageProvider.addTranslation(AITItems.GOLD_KEY, "Gold Key");
		aitLanguageProvider.addTranslation(AITItems.NETHERITE_KEY, "Netherite Key");
		aitLanguageProvider.addTranslation(AITItems.CLASSIC_KEY, "Classic Key");
		aitLanguageProvider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Remote");
		aitLanguageProvider.addTranslation(AITItems.MECHANICAL_SONIC_SCREWDRIVER, "Mechanical Sonic Screwdriver");
		aitLanguageProvider.addTranslation(AITItems.CORAL_SONIC_SCREWDRIVER, "Coral Sonic Screwdriver");
		aitLanguageProvider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITBlocks.RADIO, "Radio");
		aitLanguageProvider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterior");

		return aitLanguageProvider;
	}

	/**
	 * Adds French translations to the language file.
	 * @param output The data generator output.
	 * @param registriesFuture The registries future.
	 * @param languageType The language type.
	 * @return The AITLanguageProvider.
	 */
	public AITLanguageProvider addFrenchTranslations(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
		AITLanguageProvider aitLanguageProvider = new AITLanguageProvider(output, languageType);

		aitLanguageProvider.addTranslation(AITMod.AIT_ITEM_GROUP, "Adventures In Time");
		aitLanguageProvider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
		aitLanguageProvider.addTranslation(AITBlocks.DOOR_BLOCK, "Door");
		aitLanguageProvider.addTranslation(AITBlocks.CONSOLE, "Console");
		aitLanguageProvider.addTranslation(AITItems.IRON_KEY, "Iron Key");
		aitLanguageProvider.addTranslation(AITItems.GOLD_KEY, "Gold Key");
		aitLanguageProvider.addTranslation(AITItems.NETHERITE_KEY, "Netherite Key");
		aitLanguageProvider.addTranslation(AITItems.CLASSIC_KEY, "Classic Key");
		aitLanguageProvider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Remote");
		aitLanguageProvider.addTranslation(AITItems.MECHANICAL_SONIC_SCREWDRIVER, "Mechanical Sonic Screwdriver");
		aitLanguageProvider.addTranslation(AITItems.CORAL_SONIC_SCREWDRIVER, "Coral Sonic Screwdriver");
		aitLanguageProvider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITBlocks.RADIO, "Radio");
		aitLanguageProvider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterior");

		return aitLanguageProvider;
	}

	/**
	 * Adds Spanish translations to the language file.
	 * @param output The data generator output.
	 * @param registriesFuture The registries future.
	 * @param languageType The language type.
	 * @return The AITLanguageProvider.
	 */
	public AITLanguageProvider addSpanishTranslations(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, LanguageType languageType) {
		AITLanguageProvider aitLanguageProvider = new AITLanguageProvider(output, languageType);

		aitLanguageProvider.addTranslation(AITMod.AIT_ITEM_GROUP, "Adventures In Time");
		aitLanguageProvider.addTranslation(AITItems.TARDIS_ITEM, "TARDIS");
		aitLanguageProvider.addTranslation(AITBlocks.DOOR_BLOCK, "Door");
		aitLanguageProvider.addTranslation(AITBlocks.CONSOLE, "Console");
		aitLanguageProvider.addTranslation(AITItems.IRON_KEY, "Iron Key");
		aitLanguageProvider.addTranslation(AITItems.GOLD_KEY, "Gold Key");
		aitLanguageProvider.addTranslation(AITItems.NETHERITE_KEY, "Netherite Key");
		aitLanguageProvider.addTranslation(AITItems.CLASSIC_KEY, "Classic Key");
		aitLanguageProvider.addTranslation(AITItems.REMOTE_ITEM, "Stattenheim Remote");
		aitLanguageProvider.addTranslation(AITItems.MECHANICAL_SONIC_SCREWDRIVER, "Mechanical Sonic Screwdriver");
		aitLanguageProvider.addTranslation(AITItems.CORAL_SONIC_SCREWDRIVER, "Coral Sonic Screwdriver");
		aitLanguageProvider.addTranslation(AITItems.GOLD_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITItems.NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITItems.CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
		aitLanguageProvider.addTranslation(AITBlocks.RADIO, "Radio");
		aitLanguageProvider.addTranslation(AITBlocks.EXTERIOR_BLOCK, "Exterior");

		return aitLanguageProvider;
	}

	public void generate_EN_US_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_US))); // en_us (English US)
	}

	public void generate_EN_UK_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_UK))); // en_uk (English UK)
	}

	public void generate_FR_CA_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addFrenchTranslations(output, registriesFuture, LanguageType.FR_CA)))); // fr_ca (French Canadian)
	}

	public void generate_FR_FR_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addFrenchTranslations(output, registriesFuture, LanguageType.FR_FR)))); // fr_fr (French France)
	}

	public void generate_ES_AR_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_AR)))); // es_ar (Spanish Argentina)
	}

	public void generate_ES_CL_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_CL)))); // es_cl (Spanish Chile)
	}

	public void generate_ES_EC_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_EC)))); // es_ec (Spanish Ecuador)
	}

	public void generate_ES_ES_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_ES)))); // es_es (Spanish Spain)
	}

	public void generate_ES_MX_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_MX)))); // es_mx (Spanish Mexico)
	}

	public void generate_ES_UY_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_UY)))); // es_uy (Spanish Uruguay)
	}

	public void generate_ES_VE_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registriesFuture) -> addSpanishTranslations(output, registriesFuture, LanguageType.ES_VE)))); // es_ve (Spanish Venezuela)
	}

	public void generate_EN_AU_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_AU))); // en_au (English Australia)
	}

	public void generate_EN_CA_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_CA))); // en_ca (English Canada)
	}

	public void generate_EN_GB_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_GB))); // en_gb (English Great Britain)
	}

	public void generate_EN_NZ_Language(FabricDataGenerator.Pack pack) {
		pack.addProvider(((output, registriesFuture) -> addEnglishTranslations(output, registriesFuture, LanguageType.EN_NZ))); // en_nz (English New Zealand)
	}
}
