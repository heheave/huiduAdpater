package v;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class V {

	private static final String V_XML_PATH = "file/var.xml";

	private static final Map<String, String> XML_VAR_MAP = new HashMap<String, String>();

	static {
		try {
			readFromXml();
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void readFromXml() throws DocumentException {
		File f = new File(V_XML_PATH);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(f);
		Element root = doc.getRootElement();
		List<?> vs = root.elements("v");
		for (Object obj : vs) {
			if (obj instanceof Element) {
				Element ve = (Element) obj;
				String name = ve.attributeValue("name");
				Object value = ve.getData();
				if (name != null) {
					XML_VAR_MAP.put(name.trim(), value.toString().trim());
				}
			}
		}

	}

	private static int getInt(String key) {
		try {
			String e = XML_VAR_MAP.get(key);
			if (e != null) {
				return Integer.parseInt(e.toString());
			} else {
				return 0;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private static long getLong(String key) {
		try {
			String e = XML_VAR_MAP.get(key);
			if (e != null) {
				return Long.parseLong(e.toString());
			} else {
				return 0;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private static String getString(String key) {
		String e = XML_VAR_MAP.get(key);
		if (e != null) {
			return e;
		} else {
			return "NULL";
		}
	}

	// ec const parameter
	// for batch
	public static final int TIME_INTERVAL_TO_SLEEP = getInt("TIME_INTERVAL_TO_SLEEP");
	public static final int BATCH_CAMPACITY = getInt("BATCH_CAMPACITY");
	public static final int BATCH_PERIOD = getInt("BATCH_PERIOD");
	public static final int MAX_BUF_SIZE = getInt("MAX_BUF_SIZE");

	// db const parameter
	public static final String DB_HOST = getString("DB_HOST");
	public static final int DB_PORT = getInt("DB_PORT");
	public static final String DB_USER = getString("DB_USER");
	public static final String DB_PASSWORD = getString("DB_PASSWORD");
	public static final String DB_NAME = getString("DB_NAME");
	public static final String CHANNEL_TABLE_NAME = getString("CHANNEL_TABLE_NAME");
	public static final String DATAS_TABLE_NAME = getString("DATAS_TABLE_NAME");
	public static final String CHANNEL_REALTIME_TABLE_NAME = getString("CHANNEL_REALTIME_TABLE_NAME");
	public static final String RECOVERY_SAVE_BASE_DIR = getString("RECOVERY_SAVE_BASE_DIR");
	public static final String ON_INFO_SAVE_FILE = getString("ON_INFO_SAVE_FILE");
	// used for initSet
	public static final String CONF_FILE = "conf_file";
	public static final String CONTACTOR = "contactor";
	public static final String REMOTECALL = "server";
	// used for socket server
	public static final String DEFAULT_SOCKET_SERVER_HOST = getString("DEFAULT_SOCKET_SERVER_HOST");
	public static final int DEFAULT_SOCKET_SERVER_PORT = getInt("DEFAULT_SOCKET_SERVER_PORT");
	public static final int DEFAULT_SOCKET_TRY_TIMES = getInt("DEFAULT_SOCKET_TRY_TIMES");
	// used for log
	public static final String LOG_PATH = getString("LOG_PATH");
	// used for pipeline handler init
	public static final String PIPELINE_HANDLER_INIT_FILE = "file/conf.xml";
	// used for mqtt client
	public static final String MQTT_HOST = getString("MQTT_HOST");
	public static final String MQTT_CNT_ID = getString("MQTT_CNT_ID");
	public static final int MQTT_HEARTBEAT_CHECK_PERIOD = getInt("MQTT_HEARTBEAT_CHECK_PERIOD");
	public static final int MQTT_MAX_SUB_TOPIC = getInt("MQTT_MAX_SUB_TOPIC");
	public static final int MQTT_LOG_ATFER_RETRY_TIME = getInt("MQTT_LOG_ATFER_RETRY_TIME");

	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	public static final String ID = "ID";
	public static final String MODE = "MODE";
	public static final String TOPIC = "TP";
	public static final String CONF = "CONF";
	public static final String TIMER = "T";

	public static final String RESULT_NULL = "NULL";
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAILURE = "FAILURE";
	public static final String RESULT_ERROR = "ERROR";

	// used for ctrl check
	public static final String CTRL_DEVICE_INFO_PREDIX = "CTRL";
	public static final String GET_DEVICE_INFO_PREDIX = "GET";
	public static final String GET_DEVICE_ON_INFO = GET_DEVICE_INFO_PREDIX + "_DOI";
	public static final String GET_DEVICE_REALTIME_INFO = GET_DEVICE_INFO_PREDIX + "_DRTI";
	public static final String CHANNEL_RULE_CACHE_ENTRY_UPDATE = GET_DEVICE_INFO_PREDIX + "_CR_UPD";
	public static final String CTRL_DEVICE_CHANNEL = CTRL_DEVICE_INFO_PREDIX + "_CNL";
	public static final String CTRL_GET_DEVICE_CONF = CTRL_DEVICE_INFO_PREDIX + "_GETF";

	static {
		XML_VAR_MAP.clear();
	}

}
