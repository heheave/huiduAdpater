package cache.deviceconf;

import util.ByteUtil;

public interface Conf {

	int MAX = ByteUtil.getNotAddOne(0x7FFF);

	int MIN = ByteUtil.getNotAddOne(0x8000);

	double DK = 1.0 / (MAX - MIN);

	String getType();

	int getT();

	int getI();

	String getU();

	String getM();

	double getMax();

	double getMin();

	double getK();

	double getB();

}
