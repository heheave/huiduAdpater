package cache.deviceconf;

public class Device614P extends AbastractDevice {

	public Device614P(String type) {
		super("614P");
	}

	@Override
	public synchronized Conf[] getT(int t) {
		if (t == 1) {
			Conf[] enabled = super.getT(0);
			Conf[] map = super.getT(1);
			if (enabled == null) {
				return map;
			} else if (map == null) {
				return null;
			} else {
				int looptime = enabled.length < map.length ? enabled.length : map.length;
				Conf[] result = new Conf[looptime];
				for (int i = 0; i < looptime; i++) {
					if (enabled[i].getI() == 0) {
						result[i] = OFF;
					} else {
						result[i] = map[i];
					}
				}
				return result;
			}
		} else {
			return super.getT(t);
		}
	}

	@Override
	public Conf[] get(String type) {
		if (type.equals("AI")) {
			return getT(1);
		} else if (type.equals("EN")) {
			return getT(0);
		} else {
			return getT(2);
		}
	}

}
