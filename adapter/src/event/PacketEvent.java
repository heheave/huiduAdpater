package event;

import java.util.List;

import net.sf.json.JSONObject;
import persistence.PersistenceData;

public class PacketEvent implements Event, PersistenceData {

	private int pipeid;

	private CODE code;

	private String topic;

	private byte[] tempData;

	private JSONObject json;

	private String deviceId;

	private String deviceType;

	private String infoType;

	private long timestamp;

	private List<PersistenceData> realDatas;

	public PacketEvent(int pipeid, CODE code) {
		this.pipeid = pipeid;
		this.code = code;
		this.topic = null;
		this.deviceId = null;
		this.deviceType = null;
		this.infoType = null;
		this.timestamp = -1;
	}

	public PacketEvent(CODE code) {
		this(-1, code);
	}

	public PacketEvent() {
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

	public List<PersistenceData> getRealDatas() {
		return realDatas;
	}

	public void setRealDatas(List<PersistenceData> realDatas) {
		this.realDatas = realDatas;
	}

	public void changeCode(CODE code) {
		this.code = code;
	}

	public void setTempData(byte[] tempData) {
		this.tempData = tempData;
	}

	public void setJson(JSONObject json) {
		if (json != null) {
			this.tempData = null;
			this.json = json;
		}
	}

	@Override
	public JSONObject toJson() {
		if (json == null && tempData != null) {
			try {
				json = JSONObject.fromObject(new String(tempData));
			} catch (Exception e) {
				json = null;
			} finally {
				tempData = null;
			}
		}
		return json;
	}

	@Override
	public String toString() {
		return topic + " : " + toJson();
	}

}
