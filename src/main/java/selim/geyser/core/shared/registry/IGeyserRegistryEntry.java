package selim.geyser.core.shared.registry;

import io.netty.buffer.ByteBuf;
import selim.geyser.core.shared.RegistryKey;

public interface IGeyserRegistryEntry<T extends IGeyserRegistryEntry<T>> {

	public RegistryKey getRegistryName();

	public T setRegistryName(RegistryKey key);

	public static interface IEntryData {

		public void toBytes(ByteBuf buf);

	}

}
