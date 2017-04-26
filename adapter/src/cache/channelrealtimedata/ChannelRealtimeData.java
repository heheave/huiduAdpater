package cache.channelrealtimedata;

import net.sf.json.JSONObject;
import persistence.PersistenceData;

public class ChannelRealtimeData implements PersistenceData {
	private String channelid;
	private String value;
	private long timestamp;
	private String unit;
	private String desc;
	private int normal;
	private String spotid;
	private String spotlocation;
	private String sceneid;
	private String channel_id;

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public String getSpotid() {
		return spotid;
	}

	public void setSpotid(String spotid) {
		this.spotid = spotid;
	}

	public String getSpotlocation() {
		return spotlocation;
	}

	public void setSpotlocation(String spotlocation) {
		this.spotlocation = spotlocation;
	}

	public String getSceneid() {
		return sceneid;
	}

	public void setSceneid(String sceneid) {
		this.sceneid = sceneid;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getNormal() {
		return normal;
	}

	public void setNormal(int normal) {
		this.normal = normal;
	}

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public JSONObject toJson() {
		return JSONObject.fromObject(this);
	}

}
