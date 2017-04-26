package cache.deviceconf;

public class Device678C extends AbastractDevice {

	public Device678C(String type) {
		super("678C");
	}

	@Override
	public synchronized Conf[] get(String type) {
		if (type.equals("DI")) {
			return getT(0);
		} else if (type.equals("DO")) {
			return getT(1);
		} else if (type.equals("SI")) {
			return getT(2);
		} else {
			return getT(3);
		}
	}
}
