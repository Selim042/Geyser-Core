package selim.geyser.core.shared;

public class RegistryKey implements Comparable<RegistryKey> {

	private final String namespace;
	private final String key;

	public RegistryKey(String namespace, String key) {
		this.namespace = namespace;
		this.key = key;
	}

	public RegistryKey(String fullKey) {
		String[] parts = fullKey.split(":");
		if (!fullKey.matches(".*:.*") || parts.length != 2)
			throw new IllegalArgumentException("fullKey must match \"namespace:key\"");
		this.namespace = parts[0];
		this.key = parts[1];
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getKey() {
		return this.key;
	}

	@Override
	public int hashCode() {
		return (31 * this.namespace.hashCode()) + this.key.hashCode();
	}

	@Override
	public String toString() {
		return this.namespace + ":" + this.key;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RegistryKey))
			return false;
		RegistryKey in = (RegistryKey) obj;
		return in.namespace.equals(this.namespace) && in.key.equals(this.key);
	}

	@Override
	public int compareTo(RegistryKey o) {
		return toString().compareTo(o.toString());
	}

}
