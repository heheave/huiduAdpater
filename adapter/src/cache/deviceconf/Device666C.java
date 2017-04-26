package cache.deviceconf;

public class Device666C extends AbastractDevice {

	public Device666C(String type) {
		super("666C");
	}

	@Override
	public synchronized Conf[] get(String type) {
		return getT(0);
	}

}
