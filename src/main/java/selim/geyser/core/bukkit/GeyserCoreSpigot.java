package selim.geyser.core.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import selim.geyser.core.bukkit.network.NetworkHandler;
import selim.geyser.core.bukkit.network.packets.RequestComponentsPacket;
import selim.geyser.core.bukkit.network.packets.SendComponentsPacket;
import selim.geyser.core.shared.EnumComponent;
import selim.geyser.core.shared.GeyserCoreInfo;
import selim.geyser.core.shared.IGeyserCorePlugin;
import selim.geyser.core.shared.IGeyserPlugin;

public class GeyserCoreSpigot extends JavaPlugin implements Listener,
		// PluginMessageListener,
		IGeyserCorePlugin {

	protected static Logger LOGGER;
	protected static GeyserCoreSpigot INSTANCE;
	protected static NetworkHandler NETWORK;

	@Override
	public void onEnable() {
		LOGGER = this.getLogger();
		INSTANCE = this;
		PluginManager manager = this.getServer().getPluginManager();
		// Bukkit.getMessenger().registerOutgoingPluginChannel(this,
		// GeyserCoreInfo.CHANNEL);
		manager.registerEvents(this, this);
		// Bukkit.getMessenger().registerIncomingPluginChannel(this,
		// GeyserCoreInfo.CHANNEL, this);
		NETWORK = NetworkHandler.registerChannel(this, GeyserCoreInfo.CHANNEL);
		NETWORK.registerPacket(GeyserCoreInfo.PacketDiscrimators.REQUEST_COMPONENTS,
				RequestComponentsPacket.class, RequestComponentsPacket.Handler.class);
		NETWORK.registerPacket(GeyserCoreInfo.PacketDiscrimators.SEND_COMPONENTS,
				SendComponentsPacket.class, new SendComponentsPacket.Handler());
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

	public static List<EnumComponent> getRequiredComponents() {
		return new LinkedList<>(REQUIRED_COMPONENTS);
	}

	public static Logger getGeyserLogger() {
		return LOGGER;
	}

	public static boolean isComponentRequired(EnumComponent component) {
		return REQUIRED_COMPONENTS.contains(component);
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin plugin = event.getPlugin();
		if (plugin instanceof IGeyserCorePlugin) {
			IGeyserCorePlugin geyserCorePlugin = (IGeyserCorePlugin) plugin;
			for (EnumComponent component : geyserCorePlugin.providedComponents())
				if (!CORE_PLUGINS.containsKey(component)) {
					CORE_PLUGINS.put(component, geyserCorePlugin);
					LOGGER.info(String.format(GeyserCoreInfo.GEYSER_WELCOME_MESSAGE, plugin.getName(),
							component.name().toLowerCase()));
				}
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
				if (player.getListeningPluginChannels().contains(GeyserCoreInfo.CHANNEL))
					NETWORK.sendPacket(player, new RequestComponentsPacket());
				else {
					kickPlayerForMissing(player, REQUIRED_COMPONENTS);
					// TODO: Should it announce?
					// player.sendMessage("This server has " +
					// GeyserCoreInfo.NAME + " installed. "
					// + "If you install the client companion Forge mod,
					// "
					// + "you can see any custom recipes from Spigot
					// plugins
					// installed on the server in JEI.");
					// TextComponent base = new TextComponent("You can
					// find the
					// mod ");
					// TextComponent link = new TextComponent("here.");
					// link.setUnderlined(true);
					// link.setColor(ChatColor.BLUE);
					// link.setClickEvent(new
					// ClickEvent(Action.OPEN_URL,
					// GeyserCoreInfo.DOWNLOAD_LINK));
					// base.addExtra(link);
					// player.spigot().sendMessage(base);
				}
			}
		}, ping);
	}

	// @Override
	// public void onPluginMessageReceived(String channel, Player player,
	// byte[] message) {
	// if (message.length < 1)
	// return;
	// switch ((char) message[0]) {
	// case GeyserCoreInfo.PacketDiscrimators.SEND_COMPONENTS:
	// List<EnumComponent> components = parseComponents(message);
	// List<EnumComponent> missingComponents = new ArrayList<>();
	// for (EnumComponent component : REQUIRED_COMPONENTS)
	// if (!components.contains(component))
	// missingComponents.add(component);
	// if (missingComponents.isEmpty())
	// PLAYER_DATA.put(player, components);
	// else {
	// kickPlayerForMissing(player, missingComponents);
	// break;
	// }
	// String compList = "";
	// for (EnumComponent c : components)
	// compList += c.name().toLowerCase() + ", ";
	// compList = compList.substring(0, compList.length() - 2);
	// LOGGER.log(Level.INFO, player.getName()
	// + " has connected with the following Geyser components: "
	// + compList);
	// break;
	// }
	// }

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
