package cache.deviceconf;

public class Conf966C implements Conf {

	private static final String deviceType = "966C";

	private final int t;

	private final int i;

	private final double max;

	private final double min;

	private final double k;

	private final double b;

	private final String u;

	private final String m;

	public Conf966C(int t, int i) {
		this.t = t;
		this.i = i;
		max = 1;
		min = 0;
		k = 1;
		b = 0;
		u = "";
		m = "DS";
	}

	@Override
	public String getType() {
		return deviceType;
	}

	@Override
	public double getK() {
		return k;
	}

	@Override
	public double getB() {
		return b;
	}

	@Override
	public int getI() {
		return i;
	}

	@Override
	public String getU() {
		return u;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public String getM() {
		return m;
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public double getMin() {
		return min;
	}

}
