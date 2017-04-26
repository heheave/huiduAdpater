package util;

public class ByteUtil {
	public static int getNotAddOne(int a) {
		short sa = (short) a;
		if (sa < 0) {
			int ia = (~a) + 1;
			sa = (short) ia;
		}
		return sa;
	}
}
