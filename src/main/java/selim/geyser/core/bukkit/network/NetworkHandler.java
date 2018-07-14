package selim.geyser.core.bukkit.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import selim.geyser.core.shared.Pair;

public class NetworkHandler implements PluginMessageListener {

	private final static List<NetworkHandler> HANDLERS = new LinkedList<>();

	private final Plugin plugin;
	private final String channelName;
	private final HashMap<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> PACKETS = new HashMap<>();

	private NetworkHandler(Plugin plugin, String channelName) {
		this.plugin = plugin;
		this.channelName = channelName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends GeyserPacket> boolean registerPacket(char discrim, Class<T> packet) {
		return registerPacket(discrim, packet, (GeyserPacketHandler) null);
	}

	public <T extends GeyserPacket> boolean registerPacket(char discrim, Class<T> packet,
			Class<? extends GeyserPacketHandler<? extends T, ? extends GeyserPacket>> handler) {
		try {
			GeyserPacketHandler<? extends T, ? extends GeyserPacket> handlerInstance = handler
					.newInstance();
			return registerPacket(discrim, packet, handlerInstance);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public <T extends GeyserPacket> boolean registerPacket(char discrim, Class<T> packet,
			GeyserPacketHandler<? extends T, ? extends GeyserPacket> handler) {
		if (PACKETS.containsKey(discrim))
			return false;
		Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>> pair = Pair
				.of(packet, handler);
		PACKETS.put(discrim, pair);
		return true;
	}

	public void sendPacket(Player player, GeyserPacket packet) {
		Entry<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> info = getPacketInfo(
				packet.getClass());
		if (info == null)
			return;
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(info.getKey());
		packet.toBytes(buf);
		player.sendPluginMessage(this.plugin, this.channelName, buf.array());
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(this.channelName))
			return;
		ByteBuf buf = Unpooled.copiedBuffer(message);
		char discrim = (char) buf.readByte();
		System.out.println(discrim);
		Entry<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> info = getPacketInfo(
				discrim);
		if (info == null)
			return;
		try {
			Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>> pair = info
					.getValue();
			GeyserPacket packet = pair.left().newInstance();
			packet.fromBytes(buf);
			@SuppressWarnings("rawtypes")
			GeyserPacketHandler handler = pair.right();
			if (handler != null) {
				@SuppressWarnings("unchecked")
				GeyserPacket ret = handler.handle(player, packet);
				if (ret != null)
					sendPacket(player, (GeyserPacket) ret);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private Entry<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> getPacketInfo(
			char c) {
		for (Entry<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> e : PACKETS
				.entrySet())
			if (e.getKey() == c)
				return e;
		return null;
	}

	private Entry<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> getPacketInfo(
			Class<? extends GeyserPacket> packet) {
		for (Entry<Character, Pair<Class<? extends GeyserPacket>, GeyserPacketHandler<? extends GeyserPacket, ? extends GeyserPacket>>> e : PACKETS
				.entrySet())
			if (e.getValue().left().equals(packet))
				return e;
		return null;
	}

	public static NetworkHandler registerChannel(Plugin plugin, String channelName) {
		NetworkHandler handler = new NetworkHandler(plugin, channelName);
		Messenger messenger = Bukkit.getMessenger();
		messenger.registerOutgoingPluginChannel(plugin, channelName);
		messenger.registerIncomingPluginChannel(plugin, channelName, handler);
		HANDLERS.add(handler);
		return handler;
	}

}
