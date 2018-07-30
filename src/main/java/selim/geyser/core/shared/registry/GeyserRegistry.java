package selim.geyser.core.shared.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import selim.geyser.core.shared.RegistryKey;

public class GeyserRegistry<T extends IGeyserRegistryEntry<T>> {

	private final RegistryKey key;
	private final Map<RegistryKey, T> map = new HashMap<>();

	public GeyserRegistry(RegistryKey key) {
		this.key = key;
	}

	public void register(T entry) {
		RegistryKey key = entry.getRegistryName();
		if (map.containsKey(key))
			throw new IllegalArgumentException("entry with key " + key + " already registered");
		map.put(key, entry);
	}

	public T getEntry(RegistryKey key) {
		if (!map.containsKey(key))
			return null;
		return map.get(key);
	}

	public Set<RegistryKey> getKeys() {
		return Collections.unmodifiableSet(map.keySet());
	}

	public Collection<T> getValues() {
		return Collections.unmodifiableCollection(map.values());
	}

	public RegistryKey getRegistryKey() {
		return this.key;
	}

}
