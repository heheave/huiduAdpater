package cache.deviceconf;

public class Conf618X implements Conf {

	private static final String deviceType = "618X";

	private final int t;

	private final int i;

	private final double max;

	private final double min;

	private final double k;

	private final double b;

	private final String u;

	private final String m;

	public Conf618X(int t, int i) {
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
				max = 10;
				min = -10;
				u = "V";
				m = "A_10V";
				break;

			case 1:
				max = 5;
				min = -5;
				u = "V";
				m = "A_5V";
				break;

			case 2:
				max = 5;
				min = -2;
				u = "V";
				m = "A_2_5V";
				break;

			case 3:
				max = 1;
				min = -1;
				u = "mV";
				m = "A_1V";
				break;

			case 4:
				max = 500;
				min = -500;
				u = "mV";
				m = "A_500MV";
				break;

			case 5:
				max = 100;
				min = -100;
				u = "mV";
				m = "A_100V";
				break;

			case 6:
				max = 20;
				min = -20;
				u = "mV";
				m = "A_20MV";
				break;

			case 7:
				max = 1370;
				min = 0;
				u = "¡æ";
				m = "T_K";
				break;

			case 8:
				max = 760;
				min = 0;
				u = "¡æ";
				m = "T_J";
				break;

			case 9:
				max = 1750;
				min = 500;
				u = "¡æ";
				m = "T_R";
				break;

			case 10:
				max = 1750;
				min = 500;
				u = "¡æ";
				m = "T_S";
				break;

			case 11:
				max = 1800;
				min = 500;
				u = "¡æ";
				m = "T_B";
				break;

			case 12:
				max = 1000;
				min = 0;
				u = "¡æ";
				m = "T_E";
				break;

			case 13:
				max = 400;
				min = -100;
				u = "¡æ";
				m = "T_T";
				break;

			case 14:
				max = MAX;
				min = MIN;
				u = "¡æ";
				m = "T_N";
				break;

			case 15:
				max = 20;
				min = 0;
				u = "mA";
				m = "I_0_20MA";
				break;

			case 16:
				max = 20;
				min = -4;
				u = "¡æ";
				m = "I_4_20MA";
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
