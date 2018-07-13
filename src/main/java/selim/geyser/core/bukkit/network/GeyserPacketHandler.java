package selim.geyser.core.bukkit.network;

import org.bukkit.entity.Player;

public abstract class GeyserPacketHandler<IN extends GeyserPacket, OUT extends GeyserPacket> {

	public abstract OUT handle(Player player, IN packet);

}
