package cache.deviceconf;

public class Device640X extends AbastractDevice {

	public Device640X(String type) {
		super("640X");
	}

	@Override
	public synchronized Conf[] get(String type) {
		if (type.equals("DI")) {
			return getT(0);
		} else {
			return getT(1);
		}
	}

}
