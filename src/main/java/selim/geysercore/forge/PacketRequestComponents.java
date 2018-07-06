package selim.geysercore.forge;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestComponents implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler
			implements IMessageHandler<PacketRequestComponents, PacketSendComponents> {

		@Override
		public PacketSendComponents onMessage(PacketRequestComponents message, MessageContext ctx) {
			return new PacketSendComponents(GeyserCoreForge.getInstalledComponents());
		}

	}

}
