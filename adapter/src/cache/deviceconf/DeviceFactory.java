package cache.deviceconf;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceFactory {

	private static final String BASE_PATH = "cache.deviceconf.Device";

	private static final Map<String, Device> devMap = new ConcurrentHashMap<String, Device>();

	public static Device getDevice(String deviceType) {
		if (devMap.containsKey(deviceType)) {
			return devMap.get(deviceType);
		} else {
			try {
				String className = BASE_PATH + deviceType;
				Constructor<?> c = Class.forName(className).getConstructor(String.class);
				Device d = (Device) c.newInstance(deviceType);
				devMap.put(deviceType, d);
				return d;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
