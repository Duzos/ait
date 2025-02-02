package loqor.ait.registry.impl.console.variant;

import java.util.ArrayList;
import java.util.List;

import dev.pavatus.lib.register.unlockable.UnlockableRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.joml.Vector3f;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import loqor.ait.AITMod;
import loqor.ait.data.datapack.DatapackConsole;
import loqor.ait.data.datapack.DatapackExterior;
import loqor.ait.data.schema.console.ConsoleTypeSchema;
import loqor.ait.data.schema.console.ConsoleVariantSchema;
import loqor.ait.data.schema.console.variant.alnico.AlnicoVariant;
import loqor.ait.data.schema.console.variant.alnico.BlueAlnicoVariant;
import loqor.ait.data.schema.console.variant.copper.CopperVariant;
import loqor.ait.data.schema.console.variant.coral.*;
import loqor.ait.data.schema.console.variant.crystalline.CrystallineVariant;
import loqor.ait.data.schema.console.variant.crystalline.CrystallineZeitonVariant;
import loqor.ait.data.schema.console.variant.hartnell.HartnellVariant;
import loqor.ait.data.schema.console.variant.hartnell.KeltHartnellVariant;
import loqor.ait.data.schema.console.variant.hartnell.MintHartnellVariant;
import loqor.ait.data.schema.console.variant.hartnell.WoodenHartnellVariant;
import loqor.ait.data.schema.console.variant.renaissance.*;
import loqor.ait.data.schema.console.variant.steam.*;
import loqor.ait.data.schema.console.variant.toyota.ToyotaBlueVariant;
import loqor.ait.data.schema.console.variant.toyota.ToyotaLegacyVariant;
import loqor.ait.data.schema.console.variant.toyota.ToyotaVariant;

public class ConsoleVariantRegistry extends UnlockableRegistry<ConsoleVariantSchema> {
    private static ConsoleVariantRegistry INSTANCE;

    protected ConsoleVariantRegistry() {
        super(DatapackConsole::fromInputStream, null, "console_variants", "console", true);
    }

    public static ConsoleVariantSchema registerStatic(ConsoleVariantSchema schema) {
        return ConsoleVariantRegistry.getInstance().register(schema);
    }

    @Override
    public ConsoleVariantSchema fallback() {
        return null;
    }

    @Override
    public void syncToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(REGISTRY.size());

        for (ConsoleVariantSchema schema : REGISTRY.values()) {
            if (schema instanceof DatapackConsole variant) {
                buf.encodeAsJson(DatapackConsole.CODEC, variant);
                continue;
            }

            buf.encodeAsJson(DatapackConsole.CODEC, new DatapackConsole(schema.id(), schema.parent().id(),
                    DatapackExterior.DEFAULT_TEXTURE, DatapackExterior.DEFAULT_TEXTURE, List.of(), new Vector3f(), List.of(), new Vector3f(),
                    false));
        }

        ServerPlayNetworking.send(player, this.packet, buf);
    }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        PacketByteBuf copy = PacketByteBufs.copy(buf);
        ClientConsoleVariantRegistry.getInstance().readFromServer(copy);

        REGISTRY.clear();

        this.defaults();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            DatapackConsole variant = buf.decodeAsJson(DatapackConsole.CODEC);
            if (!variant.wasDatapack())
                continue;

            this.register(variant);
        }

        AITMod.LOGGER.info("Read {} console variants from server", size);
    }

    public static ConsoleVariantRegistry getInstance() {
        if (INSTANCE == null) {
            AITMod.LOGGER.debug("ConsoleVariantRegistry was not initialized, Creating a new instance");
            INSTANCE = new ConsoleVariantRegistry();
        }

        return INSTANCE;
    }

    public static List<ConsoleVariantSchema> withParent(ConsoleTypeSchema parent) {
        List<ConsoleVariantSchema> list = new ArrayList<>();

        for (ConsoleVariantSchema schema : ConsoleVariantRegistry.getInstance().REGISTRY.values()) {
            if (schema.parent().equals(parent))
                list.add(schema);
        }

        return list;
    }

    public static ConsoleVariantSchema HARTNELL;
    public static ConsoleVariantSchema HARTNELL_WOOD;
    public static ConsoleVariantSchema HARTNELL_KELT;
    public static ConsoleVariantSchema HARTNELL_MINT;
    public static ConsoleVariantSchema CORAL;
    public static ConsoleVariantSchema CORAL_BLUE;
    public static ConsoleVariantSchema CORAL_WHITE;
    public static ConsoleVariantSchema CORAL_SITH;
    public static ConsoleVariantSchema CORAL_DECAYED;
    public static ConsoleVariantSchema TOYOTA;
    public static ConsoleVariantSchema TOYOTA_BLUE;
    public static ConsoleVariantSchema TOYOTA_LEGACY;
    public static ConsoleVariantSchema ALNICO;
    public static ConsoleVariantSchema ALNICO_BLUE;
    public static ConsoleVariantSchema STEAM;
    public static ConsoleVariantSchema STEAM_CHERRY;
    public static ConsoleVariantSchema STEAM_STEEL;
    public static ConsoleVariantSchema STEAM_GILDED;
    public static ConsoleVariantSchema STEAM_COPPER;
    public static ConsoleVariantSchema STEAM_PLAYPAL;
    public static ConsoleVariantSchema HUDOLIN;
    public static ConsoleVariantSchema COPPER;
    public static ConsoleVariantSchema BOREALIS;
    public static ConsoleVariantSchema CRYSTALLINE;
    public static ConsoleVariantSchema CRYSTALLINE_ZEITON;
    public static ConsoleVariantSchema RENAISSANCE;
    public static ConsoleVariantSchema RENAISSANCE_TOKAMAK;
    public static ConsoleVariantSchema RENAISSANCE_FIRE;
    public static ConsoleVariantSchema RENAISSANCE_IDENTITY;
    public static ConsoleVariantSchema RENAISSANCE_INDUSTRIOUS;


    @Override
    protected void defaults() {
        // Hartnell variants
        HARTNELL = registerStatic(new HartnellVariant());
        HARTNELL_KELT = registerStatic(new KeltHartnellVariant());
        HARTNELL_MINT = registerStatic(new MintHartnellVariant());
        HARTNELL_WOOD = registerStatic(new WoodenHartnellVariant()); // fixme this texture is awful - make tright remake
                                                                        // it

        // Coral variants
        CORAL = registerStatic(new CoralVariant());
        CORAL_BLUE = registerStatic(new BlueCoralVariant());
        CORAL_WHITE = registerStatic(new WhiteCoralVariant());
        CORAL_SITH = registerStatic(new CoralSithVariant());
        CORAL_DECAYED = registerStatic(new CoralDecayedVariant());

        // Toyota variants
        TOYOTA = registerStatic(new ToyotaVariant());
        TOYOTA_BLUE = registerStatic(new ToyotaBlueVariant());
        TOYOTA_LEGACY = registerStatic(new ToyotaLegacyVariant());

        // Alnico variants
        ALNICO = registerStatic(new AlnicoVariant());
        ALNICO_BLUE = registerStatic(new BlueAlnicoVariant());

        // Steam variants
        STEAM = registerStatic(new SteamVariant());
        STEAM_CHERRY = registerStatic(new SteamCherryVariant());
        STEAM_STEEL = registerStatic(new SteamSteelVariant());
        STEAM_GILDED = registerStatic(new SteamGildedVariant());
        STEAM_PLAYPAL = registerStatic(new SteamPlaypalVariant());
        STEAM_COPPER = registerStatic(new SteamCopperVariant());

        // Hudolin variants (why am i adding this???)
        // HUDOLIN = registerStatic(new HudolinVariant());

        // Copper variants
        COPPER = registerStatic(new CopperVariant());

        // Borealis variants
        // BOREALIS = registerStatic(new BorealisVariant());

        // Crystalline variants
        CRYSTALLINE = registerStatic(new CrystallineVariant());
        CRYSTALLINE_ZEITON = registerStatic(new CrystallineZeitonVariant());

        // Renaissance variants
        RENAISSANCE = registerStatic(new RenaissanceVariant());
        RENAISSANCE_TOKAMAK = registerStatic(new RenaissanceTokamakVariant());
        RENAISSANCE_FIRE = registerStatic(new RenaissanceFireVariant());
        RENAISSANCE_IDENTITY = registerStatic(new RenaissanceIdentityVariant());
        RENAISSANCE_INDUSTRIOUS = registerStatic(new RenaissanceIndustriousVariant());

    }
}
