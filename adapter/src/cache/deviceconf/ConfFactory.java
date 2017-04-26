package cache.deviceconf;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfFactory {

	private static final String BASE_PATH = "cache.deviceconf.Conf";

	private static final Map<String, Conf> confMap = new ConcurrentHashMap<String, Conf>();

	public static Conf getConf(String deviceType, int t, int i) {
		String key = deviceType + "_" + t + "_" + i;
		if (confMap.containsKey(key)) {
			return confMap.get(key);
		} else {
			try {
				String className = BASE_PATH + deviceType;
				Class<?> clz = Class.forName(className);
				Constructor<?> con = clz.getConstructor(int.class, int.class);
				Conf result = (Conf) con.newInstance(t, i);
				confMap.put(key, result);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
