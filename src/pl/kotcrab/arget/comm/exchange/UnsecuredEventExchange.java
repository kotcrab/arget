
package pl.kotcrab.arget.comm.exchange;

public class UnsecuredEventExchange implements Exchange {
	public enum Type {
		SERVER_FULL, SERVER_SHUTTING_DOWN, KICKED
	};

	public Type type;

	public UnsecuredEventExchange () {
	}

	public UnsecuredEventExchange (Type type) {
		this.type = type;
	}
}
