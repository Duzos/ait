package loqor.ait.core.tardis.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import loqor.ait.AITMod;
import loqor.ait.api.Nameable;
import loqor.ait.api.TardisComponent;
import loqor.ait.api.TardisTickable;
import loqor.ait.core.AITSounds;
import loqor.ait.core.advancement.TardisCriterions;
import loqor.ait.core.tardis.ServerTardis;
import loqor.ait.core.tardis.util.TardisUtil;
import loqor.ait.data.Loyalty;
import loqor.ait.registry.impl.DesktopRegistry;
import loqor.ait.registry.impl.SonicRegistry;
import loqor.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class LoyaltyHandler extends TardisComponent implements TardisTickable {
    private final Map<UUID, Loyalty> data;

    public LoyaltyHandler(HashMap<UUID, Loyalty> data) {
        super(Id.LOYALTY);
        this.data = data;
    }

    public LoyaltyHandler() {
        this(new HashMap<>());
    }

    public Map<UUID, Loyalty> data() {
        return this.data;
    }

    public Loyalty get(PlayerEntity player) {
        return this.data.getOrDefault(player.getUuid(), new Loyalty(Loyalty.Type.NEUTRAL));
    }

    public Loyalty set(ServerPlayerEntity player, Loyalty loyalty) {
        this.data.put(player.getUuid(), loyalty);
        this.unlock(player, loyalty);

        this.sync();
        return loyalty;
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 40 != 0)
            return;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior((ServerTardis) tardis)) {
            Loyalty loyalty = this.get(player);

            if (!loyalty.isOf(Loyalty.Type.NEUTRAL))
                continue;

            if (AITMod.RANDOM.nextInt(0, 20) != 14)
                continue;

            this.addLevel(player, 1);
        }
    }

    public void update(ServerPlayerEntity player, Function<Loyalty, Loyalty> consumer) {
        Loyalty current = this.get(player);
        current = consumer.apply(current);

        this.set(player, current);
    }

    public void unlock(ServerPlayerEntity player, Loyalty loyalty) {
        ServerTardis tardis = (ServerTardis) this.tardis;

        boolean playSound = ConsoleVariantRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema));
        playSound = DesktopRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema)) || playSound;
        playSound = ExteriorVariantRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema)) || playSound;
        playSound = SonicRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema)) || playSound;

        if (playSound)
            player.getServerWorld().playSound(null, player.getBlockPos(), AITSounds.LOYALTY_UP,
                    SoundCategory.PLAYERS, 0.2F, 1.0F);

        if (loyalty.isOf(Loyalty.Type.PILOT)) {
            TardisCriterions.REACH_PILOT.trigger(player);
        } else if (loyalty.isOf(Loyalty.Type.OWNER)) {
            TardisCriterions.REACH_OWNER.trigger(player);
        }
    }

    private void playUnlockEffects(ServerPlayerEntity player, Nameable nameable) {

        Text nameText = nameable.text().copy();
        Text unlockedMessage = Text.translatable("message.ait.unlocked");

        Text finalMessage = ((MutableText) nameText)
                .append(Text.literal(" "))
                .append(unlockedMessage)
                .append(Text.literal("!"))
                .formatted(Formatting.BOLD, Formatting.ITALIC, Formatting.GOLD);

        player.sendMessage(finalMessage, false);
    }

    public void addLevel(ServerPlayerEntity player, int level) {
        this.update(player, loyalty -> loyalty.add(level));
    }

    public void subLevel(ServerPlayerEntity player, int level) {
        this.addLevel(player, -level);
    }

    public ServerPlayerEntity getLoyalPlayerInside() {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return null;

        ServerPlayerEntity highest = null;
        int highestLoyalty = 0;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(serverTardis)) {
            if (highest == null) {
                highest = player;
                highestLoyalty = this.get(highest).level();
                continue;
            }

            int found = this.get(player).level();

            if (found > highestLoyalty) {
                highest = player;
                highestLoyalty = found;
            }
        }

        return highest;
    }

    public void sendMessageToPilot(Text text) {
        ServerPlayerEntity player = this.getLoyalPlayerInside();

        if (player == null)
            return;

        player.sendMessage(text, true);
    }
}
