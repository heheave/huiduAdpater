package cache.deviceconf;

public class Device966C extends AbastractDevice {

	public Device966C(String type) {
		super("966C");
	}

	@Override
	public synchronized Conf[] get(String type) {
		return getT(0);
	}

}
