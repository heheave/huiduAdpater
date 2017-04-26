package cache.deviceoninfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cache.DefaultCacheImpl;
import net.sf.json.JSONObject;
import util.ObjectToFileUtil;
import v.V;

public class DeviceOnCache extends DefaultCacheImpl<DeviceOnInfo> {

	private static final Logger log = Logger.getLogger(DeviceOnCache.class);

	private static final String SAVE_ON_INFO_PATH = V.ON_INFO_SAVE_FILE;

	private static final DeviceOnCache doc = new DeviceOnCache();

	@SuppressWarnings("serial")
	static final class DeviceOnEntry implements Serializable {

		private String key;

		private DeviceOnInfo value;

		public DeviceOnEntry(String key, DeviceOnInfo value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public DeviceOnInfo getValue() {
			return value;
		}
	}

	private DeviceOnCache() {
		try {
			load();
		} catch (Exception e) {
			log.error("Load Data from error");
		}
	}

	public static DeviceOnCache instance() {
		return doc;
	}

	@Override
	public void load() throws Exception {
		Object obj = ObjectToFileUtil.derializeFromFile(SAVE_ON_INFO_PATH);
		if (obj != null) {
			DeviceOnEntry[] does = (DeviceOnEntry[]) obj;
			if (does != null) {
				for (DeviceOnEntry doe : does) {
					put(doe.key, doe.value);
				}
			}
		}
	}

	@Override
	public void save() {
		File f = new File(SAVE_ON_INFO_PATH);
		try {
			if (!f.exists() || !f.isFile()) {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				f.createNewFile();
			}
			if (f.exists()) {
				DeviceOnEntry[] does = new DeviceOnEntry[super.cacheData.size()];
				int count = 0;
				for (Entry<String, DeviceOnInfo> e : super.cacheData.entrySet()) {
					DeviceOnEntry doe = new DeviceOnEntry(e.getKey(), e.getValue());
					does[count] = doe;
					count++;
				}
				ObjectToFileUtil.serializeToFile(does, SAVE_ON_INFO_PATH);
			}
		} catch (Exception e) {
			if (f.exists()) {
				f.delete();
			}
		} finally {
			super.cacheData.clear();
		}

	}

	@Override
	public DeviceOnInfo get(String key) {
		DeviceOnInfo doi = super.get(key);
		if (doi != null) {
			return doi.check();
		} else {
			return null;
		}
	}

	public void deviceOn(String key, long timestamp) {
		DeviceOnInfo deviceOnInfo = get(key);
		if (deviceOnInfo != null) {
			deviceOnInfo.pulse(timestamp);
		} else {
			deviceOnInfo = new DeviceOnInfo(timestamp);
			deviceOnInfo.pulse(timestamp);
			put(key, deviceOnInfo);
		}
	}

	public List<String> onInfos() {
		Map<String, DeviceOnInfo> onInfos = cacheData;
		List<String> infos = new ArrayList<String>(onInfos.size());
		for (Entry<String, DeviceOnInfo> entry : onInfos.entrySet()) {
			String device = entry.getKey();
			DeviceOnInfo info = entry.getValue();
			if (info.isON()) {
				JSONObject dijo = new JSONObject();
				dijo.put("device", device);
				dijo.put("info", JSONObject.fromObject(info).toString());
				infos.add(dijo.toString());
			}
		}
		return infos;
	}

	// public static void main(String[] args) {
	// DeviceOnCache doc = DeviceOnCache.instance();
	// System.out.println("load");
	// String key1 = doc.getKey("966C_77777777_DS_1");
	// String key2 = doc.getKey("966C_77777777_DS_3");
	// String key3 = doc.getKey("966C_77777777_DS_5");
	// doc.put(key1, new DeviceOnInfo());
	// doc.put(key2, new DeviceOnInfo());
	// doc.put(key3, new DeviceOnInfo());
	// System.out.println("save");
	// try {
	// doc.save();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// System.out.println("load");
	// try {
	// doc.load();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
