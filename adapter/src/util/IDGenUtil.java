package util;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenUtil {
	private static final AtomicInteger ai = new AtomicInteger();

	public static int getId() {
		return ai.getAndIncrement();
	}
}
