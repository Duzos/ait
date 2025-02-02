package loqor.ait.core.tardis.handler;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import loqor.ait.AITMod;
import loqor.ait.api.TardisComponent;
import loqor.ait.core.tardis.manager.ServerTardisManager;
import loqor.ait.core.tardis.util.TardisUtil;
import loqor.ait.data.hum.Hum;
import loqor.ait.registry.impl.HumRegistry;

public class ServerHumHandler extends TardisComponent {
    public static final Identifier SEND = AITMod.id("send_hum");
    public static final Identifier RECEIVE = AITMod.id("receive_hum");
    private Hum current;

    static {
        ServerPlayNetworking.registerGlobalReceiver(ServerHumHandler.RECEIVE,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    Hum hum = HumRegistry.getInstance().get(buf.readIdentifier());

                    if (tardis == null || hum == null)
                        return;

                    tardis.<ServerHumHandler>handler(TardisComponent.Id.HUM).setHum(hum);
                }));
    }

    public ServerHumHandler() {
        super(Id.HUM);
    }

    public Hum getHum() {
        if (current == null) {
            this.current = HumRegistry.getInstance().getRandom();
        }

        return this.current;
    }

    public void setHum(Hum hum) {
        this.current = hum;

        this.updateClientHum();
    }

    private void updateClientHum() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(this.current.sound().getId());

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(this.tardis.asServer())) {
            ServerPlayNetworking.send(player, SEND, buf);
        }
    }
}
