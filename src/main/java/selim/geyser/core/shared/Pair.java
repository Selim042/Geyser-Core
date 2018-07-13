package selim.geyser.core.shared;

public class Pair<L, R> {

	private final L left;
	private final R right;

	private Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L left() {
		return this.left;
	}

	public R right() {
		return this.right;
	}

	public static <L, R> Pair<L, R> of(L left, R right) {
		return new Pair<>(left, right);
	}

}
