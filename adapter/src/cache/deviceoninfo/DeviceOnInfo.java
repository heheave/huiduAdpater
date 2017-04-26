package cache.deviceoninfo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DeviceOnInfo implements Serializable {

	public static final long IF_NO_MES_OFF_INTERVAL = 3 * 60 * 1000;

	// deviceFirstOn
	private final long FO;

	// deviceLastTimeOnBegin
	private long LNB;

	// deviceLastTimeOnEnd
	private long LNE;

	// deviceLastTimeOffBegin
	private long LFB;

	// deviceLastTimeOffEnd
	private long LFE;

	// deviceTotalOnTime
	private long TO;

	public DeviceOnInfo(long deviceFirstOn) {
		this.FO = deviceFirstOn;
		long now = System.currentTimeMillis();
		LNB = now;
		LNE = now;
		LFB = -1;
		LFE = -1;
		TO = 0;
	}

	public DeviceOnInfo() {
		this(System.currentTimeMillis());
	}

	public long getFO() {
		return FO;
	}

	public long getLNB() {
		return LNB;
	}

	public long getLNE() {
		return LNE;
	}

	public long getLFB() {
		return LFB;
	}

	public long getLFE() {
		return LFE;
	}

	public long getTO() {
		return TO;
	}

	public synchronized void pulse() {
		pulse(System.currentTimeMillis());
	}

	public synchronized void pulse(long now) {
		if (now - LNE < 0) {
			return;
		} else if (now - LNE <= IF_NO_MES_OFF_INTERVAL) {
			TO += (now - LNE);
			LNE = now;
		} else if (now - LNE > IF_NO_MES_OFF_INTERVAL) {
			LFB = LNE;
			LFE = now;
			LNB = now;
			LNE = now;
		}
	}

	public boolean isON() {
		long now = System.currentTimeMillis();
		if (now - LNE > IF_NO_MES_OFF_INTERVAL) {
			return false;
		}
		return true;
	}

	public synchronized DeviceOnInfo check() {
		long now = System.currentTimeMillis();
		if (now - LNE <= IF_NO_MES_OFF_INTERVAL) {
			TO += (now - LNE);
			LNE = now;
		} else if (now - LNE > IF_NO_MES_OFF_INTERVAL) {
			LFB = LNE;
			LFE = now;
		}
		return this;
	}

	// TODO for test
	public void show() {
		System.out.println("deviceFirstOn: " + FO + ",deviceLastTimeOnBegin: " + LNB + ",deviceLastTimeOnEnd: " + LNE
				+ ",deviceLastTimeOffBegin: " + LFB + ",deviceLastTimeOffEnd: " + LFE);
	}

}
