package selim.geyser.core.bukkit.network.packets;

import io.netty.buffer.ByteBuf;
import selim.geyser.core.bukkit.network.GeyserPacket;
import selim.geyser.core.shared.SharedByteBufUtils;
import selim.geyser.core.shared.registry.GeyserRegistry;

public class SyncRegistryPacket extends GeyserPacket {

	private final GeyserRegistry<?> registry;

	public SyncRegistryPacket(GeyserRegistry<?> registry) {
		this.registry = registry;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		SharedByteBufUtils.writeUTF8String(buf, registry.getRegistryKey().toString());
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

}
