package cache.channelinfo;

public class ChannelInfo {
	private String channelid;
	private String usermaxdata;
	private String usermindata;
	private String unit;
	private String desc;
	private String spotid;
	private String spotlocation;
	private String sceneid;
	private String _id;
	private String active;

	public ChannelInfo() {
	}

	public ChannelInfo(String channelid, String usermaxdata, String usermindata, String unit, String desc,
			String spotid, String spotlocation, String sceneid, String _id, String active) {
		this.channelid = channelid;
		this.usermaxdata = usermaxdata;
		this.usermindata = usermindata;
		this.unit = unit;
		this.desc = desc;
		this.spotid = spotid;
		this.spotlocation = spotlocation;
		this.sceneid = sceneid;
		this._id = _id;
		this.active = active;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
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

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public String getUsermaxdata() {
		return usermaxdata;
	}

	public void setUsermaxdata(String usermaxdata) {
		this.usermaxdata = usermaxdata;
	}

	public String getUsermindata() {
		return usermindata;
	}

	public void setUsermindata(String usermindata) {
		this.usermindata = usermindata;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

}
