package cache.deviceconf;

public class Device650C extends AbastractDevice {

	public Device650C(String type) {
		super("650C");
	}

	@Override
	public synchronized Conf[] get(String type) {
		if (type.equals("DO")) {
			return getT(0);
		} else {
			return getT(1);
		}
	}

}
