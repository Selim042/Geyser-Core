package selim.geyser.core.bukkit.network;

import io.netty.buffer.ByteBuf;

public abstract class GeyserPacket {

	public abstract void toBytes(ByteBuf buf);

	public abstract void fromBytes(ByteBuf buf);

}
