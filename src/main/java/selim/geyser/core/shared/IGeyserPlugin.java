package selim.geyser.core.shared;

public interface IGeyserPlugin {

	public EnumComponent[] requiredComponents();

	public boolean requiredOnClient(EnumComponent component);

}
