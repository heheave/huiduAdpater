package cache.deviceconf;

public class Conf650C implements Conf {

	private final static String deviceType = "650C";

	private final int t;

	private final int i;

	private final double max;

	private final double min;

	private final double k;

	private final double b;

	private final String u;

	private final String m;

	public Conf650C(int t, int i) {
		this.t = t;
		switch (t) {
		case 0:
			switch (i) {
			case 0:
				max = 1;
				min = 0;
				k = 1;
				b = 0;
				u = "";
				m = "Normal Out";
				break;

			default:
				max = 0;
				min = 0;
				k = 0;
				b = 0;
				u = "";
				m = "OFF";
				break;
			}
			break;

		case 1:
			max = -1;
			min = -1;
			k = 0;
			b = 0;
			u = "";
			m = "Status Out";
			break;

		default:
			max = 0;
			min = 0;
			k = 0;
			b = 0;
			u = "";
			m = "OFF";
			break;
		}
		this.i = m.equals("OFF") ? -1 : i;
	}

	@Override
	public String getType() {
		return deviceType;
	}

	public static String getDevicetype() {
		return deviceType;
	}

	public int getT() {
		return t;
	}

	public int getI() {
		return i;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public double getK() {
		return k;
	}

	public double getB() {
		return b;
	}

	public String getU() {
		return u;
	}

	public String getM() {
		return m;
	}

}
