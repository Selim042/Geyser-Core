package selim.geyser.core.shared;

public class GeyserCoreInfo {

	public static final String ID = "geysercore";
	public static final String NAME = "Geyser Core";
	public static final String VERSION = "1.1.0";
	public static final String CHANNEL = "geyser_core";
	// TODO: Put an actual page here
	public static final String GEYSER_INFO_URL = "http://myles-selim.us/mods/geyser/";
	public static final String IMC_SEND_KEY = ID + ":components";
	public static final String GEYSER_WELCOME_MESSAGE = "Welcome to the party %s, and thanks for providing the %s.";

	public static class PacketDiscrimators {
		public static final char REQUEST_COMPONENTS = (char) 'r';
		public static final char SEND_COMPONENTS = (char) 's';
	}

}
