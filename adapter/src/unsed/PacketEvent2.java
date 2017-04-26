package unsed;

import event.Event;
import event.Event.CODE;
import net.sf.json.JSONObject;

public class PacketEvent2 implements Event {

	private int pipeid;

	private CODE code;

	private String topic;

	private String deviceId;

	private String deviceType;

	private String infoType;

	private long timestamp;

	private JSONObject jo;

	private JSONObject jod;

	public PacketEvent2(int pipeid, CODE code) {
		this.pipeid = pipeid;
		this.code = code;
		this.topic = null;
		this.deviceId = null;
		this.deviceType = null;
		this.infoType = null;
		this.timestamp = -1;
		this.jo = null;
		this.jod = null;
	}

	public PacketEvent2(CODE code) {
		this(-1, code);
	}

	public PacketEvent2() {
		this(CODE.NONE);
	}

	public int getPipeId() {
		return this.pipeid;
	}

	@Override
	public void setPipeId(int pipeid) {
		this.pipeid = pipeid;
	}

	@Override
	public CODE getCode() {
		return this.code;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void changeCode(CODE code) {
		this.code = code;
	}

	public JSONObject getJo() {
		return jo;
	}

	public void setJo(JSONObject jo) {
		this.jo = jo;
	}

	public JSONObject getJod() {
		return jod;
	}

	public void setJod(JSONObject jod) {
		this.jod = jod;
	}

	public void setDataJo(String str) {
		if (str == null) {
			this.jo = null;
		} else {
			try {
				this.jo = JSONObject.fromObject(str);
			} catch (Exception e) {
				this.jo = null;
			}
		}
	}

	public void setDataJod(String str) {
		if (str == null) {
			this.jod = null;
		} else {
			try {
				this.jod = JSONObject.fromObject(str);
			} catch (Exception e) {
				this.jod = null;
			}
		}
	}

	public boolean hasProccessed() {
		return this.jod != null;
	}

	public void setDataJo(byte[] data) {
		if (data == null) {
			this.jo = null;
		} else {
			setDataJo(new String(data));
		}
	}

	public void setDataJod(byte[] data) {
		if (data == null) {
			this.jod = null;
		} else {
			setDataJod(new String(data));
		}
	}

	public String getDataString() {
		StringBuffer sb = new StringBuffer();
		if (jo != null) {
			sb.append("jo: ");
			sb.append(jo.toString());
		}

		if (jod != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append("jod: ");
			sb.append(jod.toString());
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return topic + " : " + getDataString();
	}

}
