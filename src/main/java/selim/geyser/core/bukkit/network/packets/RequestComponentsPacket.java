package selim.geyser.core.bukkit.network.packets;

import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import selim.geyser.core.bukkit.network.GeyserPacket;
import selim.geyser.core.bukkit.network.GeyserPacketHandler;

public class RequestComponentsPacket extends GeyserPacket {

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	public static class Handler extends GeyserPacketHandler<RequestComponentsPacket, GeyserPacket> {

		@Override
		public GeyserPacket handle(Player player, RequestComponentsPacket packet) {
			return null;
		}

	}

}
