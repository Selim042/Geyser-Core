package selim.geysercore.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import selim.geysercore.shared.GeyserCoreInfo;

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
	}

}
