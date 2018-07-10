package selim.geyser.core.forge;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import selim.geyser.core.shared.EnumComponent;
import selim.geyser.core.shared.GeyserCoreInfo;

@Mod(modid = GeyserCoreInfo.ID, name = GeyserCoreInfo.NAME, version = GeyserCoreInfo.VERSION,
		clientSideOnly = true)
public class GeyserCoreForge {

	@Mod.Instance(value = GeyserCoreInfo.ID)
	public static GeyserCoreForge instance;
	public static final Logger LOGGER = LogManager.getLogger(GeyserCoreInfo.ID);
	public static SimpleNetworkWrapper network;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(GeyserCoreInfo.CHANNEL);
		network.registerMessage(PacketRequestComponents.Handler.class, PacketRequestComponents.class,
				GeyserCoreInfo.PacketDiscrimators.REQUEST_COMPONENTS, Side.CLIENT);
		network.registerMessage(PacketSendComponents.Handler.class, PacketSendComponents.class,
				GeyserCoreInfo.PacketDiscrimators.SEND_COMPONENTS, Side.CLIENT);

		FMLInterModComms.sendMessage(GeyserCoreInfo.ID, GeyserCoreInfo.IMC_SEND_KEY,
				EnumComponent.CORE.toString());
	}

	private static final List<EnumComponent> INSTALLED_COMPONENTS = new LinkedList<>();

	public static List<EnumComponent> getInstalledComponents() {
		return INSTALLED_COMPONENTS;
	}

	@EventHandler
	public void imcCallback(FMLInterModComms.IMCEvent event) {
		for (final FMLInterModComms.IMCMessage imcMessage : event.getMessages()) {
			if (imcMessage.key.equals(GeyserCoreInfo.ID + ":components")) {
				String modid = imcMessage.getSender();
				if (imcMessage.isStringMessage()) {
					String value = imcMessage.getStringValue();
					for (String s : value.split(":")) {
						try {
							EnumComponent component = EnumComponent.valueOf(s);
							if (component == null)
								continue;
							INSTALLED_COMPONENTS.add(component);
							ModContainer container = getModContainer(modid);
							if (container != null)
								LOGGER.info(String.format(GeyserCoreInfo.GEYSER_WELCOME_MESSAGE,
										container.getName(), component.name().toLowerCase()));
						} catch (IllegalArgumentException e) {}
					}
				}
			}
		}
	}

	private static ModContainer getModContainer(String id) {
		for (ModContainer container : Loader.instance().getActiveModList())
			if (container != null && container.getModId().equals(id))
				return container;
		return null;
	}

}
