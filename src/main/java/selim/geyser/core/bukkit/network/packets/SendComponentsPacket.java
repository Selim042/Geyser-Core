package selim.geyser.core.bukkit.network.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import selim.geyser.core.bukkit.GeyserCoreSpigot;
import selim.geyser.core.bukkit.network.GeyserPacket;
import selim.geyser.core.bukkit.network.GeyserPacketHandler;
import selim.geyser.core.shared.EnumComponent;
import selim.geyser.core.shared.GeyserCoreInfo;
import selim.geyser.core.shared.SharedByteBufUtils;

public class SendComponentsPacket extends GeyserPacket {

	private static final Map<Player, List<EnumComponent>> PLAYER_DATA = new HashMap<>();

	private List<EnumComponent> components = null;

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.components = parseComponents(buf.array());
	}

	public static class Handler extends GeyserPacketHandler<SendComponentsPacket, GeyserPacket> {

		@Override
		public GeyserPacket handle(Player player, SendComponentsPacket packet) {
			List<EnumComponent> components = packet.components;
			List<EnumComponent> missingComponents = new ArrayList<>();

			String compList = "";
			for (EnumComponent c : components)
				compList += c.name().toLowerCase() + ", ";
			compList = compList.substring(0, compList.length() - 2);
			GeyserCoreSpigot.getGeyserLogger().log(Level.INFO, player.getName()
					+ " has connected with the following Geyser components: " + compList);

			for (EnumComponent component : GeyserCoreSpigot.getRequiredComponents())
				if (!components.contains(component))
					missingComponents.add(component);
			if (missingComponents.isEmpty())
				PLAYER_DATA.put(player, components);
			else {
				kickPlayerForMissing(player, missingComponents);
				return null;
			}
			return null;
		}

	}

	private static List<EnumComponent> parseComponents(byte[] message) {
		ByteBuf buf = Unpooled.copiedBuffer(message);
		buf.readByte();
		List<EnumComponent> components = new LinkedList<>();
		int numComponents = buf.readInt();
		for (int i = 0; i < numComponents; i++) {
			String name = SharedByteBufUtils.readUTF8String(buf);
			try {
				components.add(EnumComponent.valueOf(name));
			} catch (IllegalArgumentException e) {
				GeyserCoreSpigot.getGeyserLogger().log(Level.WARNING,
						"client tried sending illegal EnumComponent, " + name
								+ ", this could be because the Geyser Core plugin is out of date");
			}
		}
		return components;
	}

	private static void kickPlayerForMissing(Player player, List<EnumComponent> missingComponents) {
		String missingString = "";
		for (EnumComponent component : missingComponents)
			missingString += component + ", ";
		if (missingString.length() == 0)
			return;
		missingString = missingString.substring(0, missingString.length() - 2);
		player.kickPlayer(
				"This servers requires that you have the following Geyser components installed: "
						+ missingString + "\nYou can find more information here: "
						+ GeyserCoreInfo.GEYSER_INFO_URL);
	}

}
