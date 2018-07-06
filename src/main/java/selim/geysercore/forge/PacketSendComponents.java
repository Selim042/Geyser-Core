package selim.geysercore.forge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import selim.geysercore.shared.EnumComponent;

public class PacketSendComponents implements IMessage {

	private final List<EnumComponent> components = new ArrayList<>();

	public PacketSendComponents() {}

	public PacketSendComponents(EnumComponent... components) {
		for (EnumComponent component : components)
			this.components.add(component);
	}

	public PacketSendComponents(Collection<EnumComponent> components) {
		this.components.addAll(components);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int numComponents = buf.readInt();
		for (int i = 0; i < numComponents; i++) {
			String name = ByteBufUtils.readUTF8String(buf);
			try {
				this.components.add(EnumComponent.valueOf(name));
			} catch (IllegalArgumentException e) {
				GeyserCoreForge.LOGGER.error("illegal EnumComponent, " + name);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.components.size());
		for (EnumComponent component : this.components)
			ByteBufUtils.writeUTF8String(buf, component.name());
	}

	public static class Handler implements IMessageHandler<PacketSendComponents, IMessage> {

		@Override
		public IMessage onMessage(PacketSendComponents message, MessageContext ctx) {
			return null;
		}

	}

}
