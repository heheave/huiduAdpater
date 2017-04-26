package cache.deviceconf;

public class Conf614P implements Conf {

	private static final String deviceType = "618X";

	private final int t;

	private final int i;

	private final double max;

	private final double min;

	private final double k;

	private final double b;

	private final String u;

	private final String m;

	public Conf614P(int t, int i) {
		this.t = t;
		switch (t) {
		case 0:
			max = -1;
			min = -1;
			k = 0;
			b = 0;
			u = "";
			m = "Enabled";
			break;

		case 1:
			switch (i) {
			case 0:
				max = 150;
				min = -50;
				u = "¡æ";
				m = "PT100_1_385";
				break;

			case 1:
				max = 100;
				min = 0;
				u = "¡æ";
				m = "PT100_2_385";
				break;

			case 2:
				max = 200;
				min = 0;
				u = "¡æ";
				m = "PT100_3_385";
				break;

			case 3:
				max = 400;
				min = 0;
				u = "¡æ";
				m = "PT100_4_385";
				break;

			case 4:
				max = 200;
				min = -200;
				u = "¡æ";
				m = "PT100_5_385";
				break;

			case 5:
				max = 150;
				min = -50;
				u = "¡æ";
				m = "PT100_1_392";
				break;

			case 6:
				max = 100;
				min = 0;
				u = "¡æ";
				m = "PT100_2_392";
				break;

			case 7:
				max = 200;
				min = 0;
				u = "¡æ";
				m = "PT100_3_392";
				break;

			case 8:
				max = 400;
				min = 0;
				u = "¡æ";
				m = "PT100_4_392";
				break;

			case 9:
				max = 200;
				min = -200;
				u = "¡æ";
				m = "PT100_5_392";
				break;

			case 10:
				max = 160;
				min = -40;
				u = "¡æ";
				m = "PT1000";
				break;

			default:
				max = 0;
				min = 0;
				u = "";
				m = "OFF";
				break;
			}
			double max_sub_min = (Math.abs(min) < Math.abs(max)) ? Math.abs(max) * 2 : Math.abs(min) * 2;
			k = max_sub_min * DK;
			b = 0;
			break;

		case 2:
			max = -1;
			min = -1;
			k = 0;
			b = 0;
			u = "za";
			m = "Pt";
			break;

		default:
			max = 0;
			min = 0;
			k = 0;
			b = 0;
			u = "";
			m = "OFF";
		}
		this.i = m.equals("OFF") ? -1 : i;
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
