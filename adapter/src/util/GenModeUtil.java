package util;

public class GenModeUtil {
	public static int getMode(String deviceType, String dataType, String mode) {
		StringBuffer sb = new StringBuffer();
		sb.append(deviceType);
		sb.append('_');
		sb.append(dataType);
		sb.append('_');
		sb.append(mode);
		String key = sb.toString();
		if (("666C_DS_IOPUT").equals(key) || ("966C_DS_IOPUT").equals(key)) {
			return 0;
		} else if (("666C_DS_SAFEB").equals(key) || ("966C_DS_SAFEB").equals(key)) {
			return 1;
		} else if (("678C_DI_MODE").equals(key) || ("978C_DI_MODE").equals(key)) {
			return 0;
		} else if (("678C_DO_MODE").equals(key) || ("978C_DO_MODE").equals(key)) {
			return 1;
		} else if (("678C_DI_STATUS").equals(key) || ("978C_DI_STATUS").equals(key)) {
			return 2;
		} else if (("678C_DI_SAFEB").equals(key) || ("678C_DO_SAFEB").equals(key) || ("978C_DI_SAFEB").equals(key)
				|| ("978C_DO_SAFEB").equals(key)) {
			return 3;
		} else {
			return -1;
		}
	}
}
