package bus.bushandler;

import bus.Bus;
import cache.channelinfo.ChannelInfo;
import cache.channelinfo.ChannelInfoCache;
import cache.channelrealtimedata.ChannelRealtimeDataCache;
import cache.deviceoninfo.DeviceOnCache;
import event.ControlEvent;
import event.Event;
import event.Event.CODE;
import net.sf.json.JSONObject;
import util.IDGenUtil;
import v.V;

public class CacheAccessorBusHandler extends AbstractBusHandler {

	private static final String DEVICE_ON_INFO_CACHE_READ = V.GET_DEVICE_ON_INFO;
	private static final String DEVICE_REALTIME_INFO_CACHE_READ = V.GET_DEVICE_REALTIME_INFO;
	private static final String CHANNEL_RULE_CACHE_ENTRY_UPDATE = V.CHANNEL_RULE_CACHE_ENTRY_UPDATE;

	private final DeviceOnCache doc = DeviceOnCache.instance();
	private final ChannelInfoCache cic = ChannelInfoCache.instance();
	private final ChannelRealtimeDataCache crdc = ChannelRealtimeDataCache.instance();

	public CacheAccessorBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public CacheAccessorBusHandler() {
		super(IDGenUtil.getId(), "CacheReadBusHandler", "This bus handler is used to read the cache in momery");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		if (event instanceof ControlEvent) {
			ControlEvent ce = (ControlEvent) event;
			String cmd = ce.getCmd();
			if (DEVICE_ON_INFO_CACHE_READ.equals(cmd)) {
				deviceOnInfoCacheRead(ce);
			} else if (DEVICE_REALTIME_INFO_CACHE_READ.equals(cmd)) {
				channelRealtimeCacheRead(ce);
			} else if (CHANNEL_RULE_CACHE_ENTRY_UPDATE.equals(cmd)) {
				channelRealtimeCacheUpdate(ce);
			}
		}
		event.changeCode(CODE.RC_RET);
		bus.putEvent(event);
	}

	private void channelRealtimeCacheUpdate(ControlEvent ce) {
		String infos = ce.getDataString();
		JSONObject jo = JSONObject.fromObject(infos);
		String channelId = jo.containsKey("channelid") ? jo.getString("channelid") : "NULL";
		String userMaxData = jo.containsKey("usermaxdata") ? jo.getString("usermaxdata") : "NULL";
		String userMinData = jo.containsKey("usermindata") ? jo.getString("usermindata") : "NULL";
		String userUnit = jo.containsKey("userunit") ? jo.getString("userunit") : "NULL";
		String userDesc = jo.containsKey("userdesc") ? jo.getString("userdesc") : "NULL";
		String spotId = jo.containsKey("spotid") ? jo.getString("spotid") : "NULL";
		String spotLocation = jo.containsKey("spotlocation") ? jo.getString("spotlocation") : "NULL";
		String sceneId = jo.containsKey("sceneid") ? jo.getString("sceneid") : "NULL";
		String _id = jo.containsKey("_id") ? jo.getString("_id") : "NULL";
		String active = jo.containsKey("active") ? jo.getString("active") : "1";
		Object cio = null;
		if (channelId.equals("NULL")) {
			String deviceType = jo.containsKey("devicetype") ? jo.getString("devicetype") : "NULL";
			String deviceId = jo.containsKey("deviceid") ? jo.getString("deviceid") : "NULL";
			String dataType = jo.containsKey("datatype") ? jo.getString("datatype") : "NULL";
			String portIdx = jo.containsKey("portidx") ? jo.getString("portidx") : "NULL";
			channelId = cic.getKey(deviceType, deviceId, dataType, portIdx);
		}
		if (!active.equals("1")) {
			cio = cic.delete(channelId);
		} else {
			cio = cic.get(channelId);
			if (cio != null) {
				synchronized (cio) {
					ChannelInfo ci = (ChannelInfo) cio;
					ci.setUsermaxdata(userMaxData);
					ci.setUsermindata(userMinData);
					ci.setUnit(userUnit);
					ci.setDesc(userDesc);
					ci.set_id(_id);
					ci.setActive(active);
					ci.setChannelid(channelId);
					ci.setSceneid(sceneId);
					ci.setSpotid(spotId);
					ci.setSpotlocation(spotLocation);
				}
			} else {
				ChannelInfo ci = new ChannelInfo(channelId, userMaxData, userMinData, userUnit, userDesc, spotId,
						spotLocation, sceneId, _id, active);
				cio = ci;
				cic.put(channelId, ci);
			}
		}
		if (cio != null)
			ce.setData(JSONObject.fromObject(cio).toString());
		else
			ce.setData("{}");
	}

	private void channelRealtimeCacheRead(ControlEvent ce) {
		String infos = ce.getDataString();
		JSONObject jo = JSONObject.fromObject(infos);
		String channelId = jo.containsKey("channelid") ? jo.getString("channelid") : "NULL";
		// System.out.println("channelid is : *****************************" +
		// channelId);
		Object crd = null;
		if (channelId.equals("NULL")) {
			String deviceType = jo.containsKey("devicetype") ? jo.getString("devicetype") : "NULL";
			String deviceId = jo.containsKey("deviceid") ? jo.getString("deviceid") : "NULL";
			String dataType = jo.containsKey("datatype") ? jo.getString("datatype") : "NULL";
			String portIdx = jo.containsKey("portidx") ? jo.getString("portidx") : "NULL";
			System.out.println(crdc.getKey(deviceType, deviceId, dataType, portIdx));
			crd = crdc.get(crdc.getKey(deviceType, deviceId, dataType, portIdx));
		} else {
			crd = crdc.get(crdc.getKey(channelId));
		}
		if (crd != null) {
			ce.setData(JSONObject.fromObject(crd).toString());
		} else {
			ce.setData("{}");
		}
	}

	private void deviceOnInfoCacheRead(ControlEvent ce) {
		String infos = ce.getDataString();
		JSONObject jo = JSONObject.fromObject(infos);
		String channelId = jo.containsKey("channelid") ? jo.getString("channelid") : "NULL";
		// System.out.println("channelid is : *****************************" +
		// channelId);
		Object doi = null;
		if (channelId.equals("NULL")) {
			String deviceType = jo.containsKey("devicetype") ? jo.getString("devicetype") : "NULL";
			String deviceId = jo.containsKey("deviceid") ? jo.getString("deviceid") : "NULL";
			String dataType = jo.containsKey("datatype") ? jo.getString("datatype") : "NULL";
			String portIdx = jo.containsKey("portidx") ? jo.getString("portidx") : "NULL";
			doi = doc.get(doc.getKey(deviceType, deviceId, dataType, portIdx));
		} else {
			doi = doc.get(doc.getKey(channelId));
		}
		if (doi != null) {
			ce.setData(JSONObject.fromObject(doi).toString());
		} else {
			ce.setData("{}");
		}
	}

}
