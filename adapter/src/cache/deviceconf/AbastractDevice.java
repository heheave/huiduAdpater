package cache.deviceconf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbastractDevice implements Device {

	protected static final Conf OFF = new Conf() {

		@Override
		public String getType() {
			return null;
		}

		@Override
		public int getT() {
			return -1;
		}

		@Override
		public int getI() {
			return -1;
		}

		@Override
		public String getU() {
			return "";
		}

		@Override
		public String getM() {
			return "OFF";
		}

		@Override
		public double getMax() {
			return 0;
		}

		@Override
		public double getMin() {
			return 0;
		}

		@Override
		public double getK() {
			return 0;
		}

		@Override
		public double getB() {
			return 0;
		}

	};

	protected final String deviceType;

	protected final Map<Integer, Conf[]> confs;

	protected AbastractDevice(String type) {
		this.deviceType = type;
		this.confs = new ConcurrentHashMap<Integer, Conf[]>();
	}

	protected synchronized boolean insert(String type, int t, int[] portConfNums) {
		if (!type.equals(deviceType) || portConfNums == null || portConfNums.length < 1) {
			return false;
		}
		int portnums = portConfNums.length;
		Conf[] portConfs = new Conf[portnums];

		for (int i = 0; i < portnums; i++) {
			portConfs[i] = ConfFactory.getConf(deviceType, t, portConfNums[i]);
		}
		confs.put(t, portConfs);
		return true;
	}

	public Conf[] getT(int t) {
		return confs.get(t);
	}

}
