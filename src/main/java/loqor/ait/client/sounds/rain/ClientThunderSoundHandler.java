package loqor.ait.client.sounds.rain;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

import loqor.ait.client.sounds.LoopingSound;
import loqor.ait.client.sounds.PositionedLoopingSound;
import loqor.ait.client.util.ClientTardisUtil;
import loqor.ait.core.AITSounds;
import loqor.ait.tardis.base.TardisComponent;
import loqor.ait.tardis.data.ExteriorEnvironmentHandler;
import loqor.ait.tardis.data.travel.TravelHandlerBase;
import loqor.ait.tardis.util.SoundHandler;
import loqor.ait.tardis.wrapper.client.ClientTardis;

public class ClientThunderSoundHandler extends SoundHandler {

    public static LoopingSound THUNDER_SOUND;

    public LoopingSound getThunderSound(ClientTardis tardis) {
        if (THUNDER_SOUND == null)
            THUNDER_SOUND = this.createThunderSound(tardis);

        return THUNDER_SOUND;
    }

    private LoopingSound createThunderSound(ClientTardis tardis) {
        if (tardis == null || tardis.getDesktop().doorPos().getPos() == null)
            return null;

        return new PositionedLoopingSound(AITSounds.THUNDER, SoundCategory.WEATHER,
                tardis.getDesktop().doorPos().getPos(), 0.1f);
    }

    public static ClientThunderSoundHandler create() {
        ClientThunderSoundHandler handler = new ClientThunderSoundHandler();

        handler.generate(ClientTardisUtil.getCurrentTardis());
        return handler;
    }

    private void generate(ClientTardis tardis) {
        if (THUNDER_SOUND == null)
            THUNDER_SOUND = createThunderSound(tardis);

        this.ofSounds(THUNDER_SOUND);
    }

    private boolean shouldPlaySounds(ClientTardis tardis) {
        return tardis != null && tardis.travel().getState() == TravelHandlerBase.State.LANDED
                && tardis.<ExteriorEnvironmentHandler>handler(TardisComponent.Id.ENVIRONMENT).isThundering();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.sounds == null)
            this.generate(tardis);

        if (this.shouldPlaySounds(tardis)) {
            this.startIfNotPlaying(this.getThunderSound(tardis));
        } else {
            this.stopSound(THUNDER_SOUND);
        }
    }
}