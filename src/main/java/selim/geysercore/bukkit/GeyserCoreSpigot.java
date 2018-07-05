package selim.geysercore.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import selim.geysercore.shared.EnumComponent;
import selim.geysercore.shared.GeyserCoreInfo;
import selim.geysercore.shared.IGeyserCorePlugin;
import selim.geysercore.shared.IGeyserPlugin;

public class GeyserCoreSpigot extends JavaPlugin
		implements Listener, PluginMessageListener, IGeyserCorePlugin {

	@Override
	public void onEnable() {
		PluginManager manager = this.getServer().getPluginManager();
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, GeyserCoreInfo.CHANNEL);
		manager.registerEvents(this, this);
		Bukkit.getMessenger().registerIncomingPluginChannel(this, GeyserCoreInfo.CHANNEL, this);
	}

	@Override
	public EnumComponent[] providedComponents() {
		return new EnumComponent[] { EnumComponent.CORE };
	}

	private int getPing(Player player) {
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			this.getLogger().log(Level.INFO, "Unable to get ping for " + player.getDisplayName()
					+ ", encountered a " + e.getClass().getName());
			e.printStackTrace();
			return -1;
		}
	}

	private static final Map<EnumComponent, IGeyserCorePlugin> CORE_PLUGINS = new HashMap<>();
	private static final List<EnumComponent> REQUIRED_COMPONENTS = new LinkedList<>();
	private static final Map<Player, List<EnumComponent>> PLAYER_DATA = new HashMap<>();

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin plugin = event.getPlugin();
		if (plugin instanceof IGeyserCorePlugin) {
			IGeyserCorePlugin geyserCorePlugin = (IGeyserCorePlugin) plugin;
			for (EnumComponent component : geyserCorePlugin.providedComponents())
				if (!CORE_PLUGINS.containsKey(component))
					CORE_PLUGINS.put(component, geyserCorePlugin);
		}
		if (plugin instanceof IGeyserPlugin) {
			IGeyserPlugin geyserPlugin = (IGeyserPlugin) plugin;
			for (EnumComponent component : geyserPlugin.requiredComponents())
				if (geyserPlugin.requiredOnClient(component))
					REQUIRED_COMPONENTS.add(component);
		}
	}

	public static IGeyserCorePlugin getCorePlugin(EnumComponent component) {
		if (!CORE_PLUGINS.containsKey(component))
			return null;
		return CORE_PLUGINS.get(component);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		int ping = getPing(player);
		if (ping <= 0)
			ping = 40;
		else
			ping = ping / 25;
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {

			}
		}, ping);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (message.length < 1)
			return;
		switch ((char) message[0]) {
		case GeyserCoreInfo.PacketDiscrimators.SEND_COMPONENTS:
			PLAYER_DATA.put(player, parseComponents(message));
			break;
		}
	}

	private List<EnumComponent> parseComponents(byte[] message) {
		ByteBuf buf = Unpooled.copiedBuffer(message);
		buf.readChar();
		List<EnumComponent> components = new LinkedList<>();
		int numComponents = buf.readInt();
		for (int i = 0; i < numComponents; i++)
			try {
				components.add(EnumComponent.valueOf(BukkitByteBufUtils.readUTF8String(buf)));
			} catch (IllegalArgumentException e) {}
		return components;
	}

}
