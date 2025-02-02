package loqor.ait.client.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;

import loqor.ait.client.sounds.alarm.ClientAlarmHandler;
import loqor.ait.client.sounds.console.ClientConsoleAmbientSoundsHandler;
import loqor.ait.client.sounds.drifting.ClientDriftingSoundHandler;
import loqor.ait.client.sounds.fall.ClientFallSoundHandler;
import loqor.ait.client.sounds.flight.ClientFlightHandler;
import loqor.ait.client.sounds.hum.ClientCreakHandler;
import loqor.ait.client.sounds.hum.ClientHumHandler;
import loqor.ait.client.sounds.lava.ClientLavaSoundHandler;
import loqor.ait.client.sounds.rain.ClientRainSoundHandler;
import loqor.ait.client.sounds.rain.ClientThunderSoundHandler;
import loqor.ait.client.sounds.sonic.SonicSoundHandler;
import loqor.ait.client.sounds.vortex.ClientVortexSoundsHandler;

/**
 * A class for playing + managing our custom sounds on the client, right now
 * TODO - refactor SoundHandler etc
 */
@Environment(EnvType.CLIENT)
public class ClientSoundManager {
    private static ClientHumHandler hum;
    private static ClientAlarmHandler alarm;
    private static ClientFlightHandler flight;
    private static ClientCreakHandler creak;
    private static ClientVortexSoundsHandler vortexSounds;
    private static ClientRainSoundHandler rainSound;
    private static ClientThunderSoundHandler thunderSound;
    private static ClientLavaSoundHandler lavaSound;
    private static SonicSoundHandler sonicSound;
    private static ClientFallSoundHandler fallSound;
    private static ClientDriftingSoundHandler driftingSound;
    private static ClientConsoleAmbientSoundsHandler ambientSound;

    public static ClientHumHandler getHum() {
        if (hum == null)
            hum = ClientHumHandler.create();

        return hum;
    }

    public static ClientAlarmHandler getAlarm() {
        if (alarm == null)
            alarm = ClientAlarmHandler.create();

        return alarm;
    }

    public static ClientFlightHandler getFlight() {
        if (flight == null)
            flight = ClientFlightHandler.create();

        return flight;
    }

    public static ClientCreakHandler getCreaks() {
        if (creak == null)
            creak = ClientCreakHandler.create();

        return creak;
    }

    public static ClientVortexSoundsHandler getVortexSounds() {
        if (vortexSounds == null)
            vortexSounds = ClientVortexSoundsHandler.create();

        return vortexSounds;
    }

    public static ClientConsoleAmbientSoundsHandler getAmbientSounds() {
        if (ambientSound == null)
            ambientSound = ClientConsoleAmbientSoundsHandler.create();

        return ambientSound;
    }

    public static ClientRainSoundHandler getRainSound() {
        if (rainSound == null)
            rainSound = ClientRainSoundHandler.create();

        return rainSound;
    }

    public static ClientThunderSoundHandler getThunderSound() {
        if (thunderSound == null)
            thunderSound = ClientThunderSoundHandler.create();

        return thunderSound;
    }

    public static ClientLavaSoundHandler getLavaSound() {
        if (lavaSound == null)
            lavaSound = ClientLavaSoundHandler.create();

        return lavaSound;
    }

    public static SonicSoundHandler getSonicSound() {
        if (sonicSound == null)
            sonicSound = new SonicSoundHandler();

        return sonicSound;
    }

    public static ClientFallSoundHandler getFallSound() {
        if (fallSound == null)
            fallSound = ClientFallSoundHandler.create();

        return fallSound;
    }

    public static ClientDriftingSoundHandler getDriftingSound() {
        if (driftingSound == null)
            driftingSound = ClientDriftingSoundHandler.create();

        return driftingSound;
    }

    public static void tick(MinecraftClient client) {
        if (getAlarm() != null)
            getAlarm().tick(client);

        if (getHum() != null)
            getHum().tick(client);

        if (getFlight() != null)
            getFlight().tick(client);

        if (getCreaks() != null)
            getCreaks().tick(client);

        if (getVortexSounds() != null)
            getVortexSounds().tick(client);

        if (getAmbientSounds() != null)
            getAmbientSounds().tick(client);

        if (getRainSound() != null)
            getRainSound().tick(client);

        if (getThunderSound() != null)
            getThunderSound().tick(client);

        if (getLavaSound() != null)
            getLavaSound().tick(client);

        if (getFallSound() != null)
            getFallSound().tick(client);

        if (getDriftingSound() != null)
            getDriftingSound().tick(client);
    }
}
