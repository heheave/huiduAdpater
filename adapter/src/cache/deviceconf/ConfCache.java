package cache.deviceconf;

import cache.DefaultCacheImpl;

public class ConfCache extends DefaultCacheImpl<AbastractDevice> {

	public static final ConfCache cc = new ConfCache();

	private ConfCache() {
		super();
	}

	public static ConfCache instance() {
		return cc;
	}

	public void updateConf(String deviceId, String deviceType, int t, int[] confDatas) {
		if (deviceId == null || deviceType == null || confDatas == null || confDatas.length <= 0) {
			return;
		}
		if (cc.contains(deviceId)) {
			AbastractDevice cd = cc.get(deviceId);
			cd.insert(deviceType, t, confDatas);
		} else {
			AbastractDevice cd = (AbastractDevice) DeviceFactory.getDevice(deviceType);
			if (cd != null) {
				cd.insert(deviceType, t, confDatas);
				synchronized (this) {
					if (!cc.contains(deviceId)) {
						cc.put(deviceId, cd);
					} else {
						cc.get(deviceId).insert(deviceType, t, confDatas);
					}
				}
			}
		}

	}
}
