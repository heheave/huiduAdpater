package pipe.pipehandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.apache.log4j.Logger;

import cache.channelinfo.ChannelInfo;
import cache.channelinfo.ChannelInfoCache;
import cache.channelrealtimedata.ChannelRealtimeData;
import cache.channelrealtimedata.ChannelRealtimeDataCache;
import event.PacketEvent;
import net.sf.json.JSONObject;
import persistence.PersistenceData;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;
import util.IsNumericUtil;

public class ChannelRealtimeDataHandler implements PipeHandler<PacketEvent> {

	//private static final Logger log = Logger.getLogger(ChannelRealtimeDataHandler.class);

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		try {
			JSONObject jo = e.toJson();
			if (jo == null) {
				return OP.DROP;
			}
			String deviceType = e.getDeviceType();
			String deviceId = e.getDeviceId();
			String dataType = e.getInfoType();
			long timestamp = e.getTimestamp();
			int normal = jo.getInt("normal");

			JSONObject datas = jo.getJSONObject("datas");
			List<PersistenceData> pd = new ArrayList<PersistenceData>(datas.size());
			int const_max = 32767;
			int const_min = -32768;
			@SuppressWarnings("rawtypes")
			Iterator iterator = datas.keys();
			while (iterator.hasNext()) {
				String portidx = (String) iterator.next();
				String channelid = ChannelInfoCache.instance().getKey(deviceType, deviceId, dataType, portidx);
				String val = datas.getString(portidx);
				String transferredVal = val;

				String unit = "NULL";
				String desc = "NULL";
				String spotid = "NULL";
				String spotlocation = "NULL";
				String sceneid = "NULL";
				String channel_id = "NULL";
				if (ChannelInfoCache.instance().contains(channelid)) {
					// System.out.println("----------------------
					// matched!!!!!!!!!!!!!!!!!!");
					ChannelInfo channelInfo = ChannelInfoCache.instance().get(channelid);
					if (!"1".equals(channelInfo.getActive())) {
						// System.out.println("inactive!!!");
						continue;
					}
					// System.out.println("active!!!");
					String usermaxdata = channelInfo.getUsermaxdata();
					String usermindata = channelInfo.getUsermindata();
					unit = channelInfo.getUnit();
					desc = channelInfo.getDesc();
					spotid = channelInfo.getSpotid();
					spotlocation = channelInfo.getSpotlocation();
					sceneid = channelInfo.getSceneid();
					channel_id = channelInfo.get_id();
					// log.info("-------------channelid-->" + channelid + "
					// matched!-------------");
					// log.info("usermaxdata=" + usermaxdata);
					// log.info("usermindata=" + usermindata);
					// log.info("unit=" + unit);
					// log.info("desc=" + desc);
					// log.info("spotid=" + spotid);
					// log.info("spotlocation=" + spotlocation);
					// log.info("sceneid=" + sceneid);
					// log.info("channel_id=" + channel_id);
					// log.info("val=" + val + "; transferedval=" +
					// transferredVal);
					// log.info("-------------channelid-->" + channelid + "
					// matched!-------------");
					// if dataType is "AI" or "AO", then convert the value
					if (dataType != null && dataType.startsWith("A") && IsNumericUtil.isDouble(val)
							&& IsNumericUtil.isDouble(usermaxdata) && IsNumericUtil.isDouble(usermindata)) {
						double y = (Double.valueOf(usermaxdata) - Double.valueOf(usermindata))
								* (Double.valueOf(val) - const_min) / (const_max - const_min)
								+ Double.valueOf(usermindata);
						transferredVal = String.valueOf(y);
					}
				} else {
					// System.out.println("----------------------not
					// matched!!!!!!!!!!!!!!!!!!");
					continue;
				}

				ChannelRealtimeData crd = new ChannelRealtimeData();
				crd.setChannelid(channelid);
				crd.setTimestamp(timestamp);
				crd.setUnit(unit);
				crd.setDesc(desc);
				crd.setValue(transferredVal);
				crd.setNormal(normal);
				crd.setSpotid(spotid);
				crd.setSpotlocation(spotlocation);
				crd.setSceneid(sceneid);
				crd.setChannel_id(channel_id);
				ChannelRealtimeDataCache.instance().put(channelid, crd);
				pd.add(crd);
				// ObjectPersistenceWorker.instance().putObject(crd);
				// log.info("channelRealtimeData created! ====== channelid-->" +
				// channelid + ";timestamp-->" + timestamp
				// + ";unit-->" + unit + ";desc-->" + desc +
				// ";transferredVal-->" + transferredVal + ";normal-->"
				// + normal + ";spotid-->" + spotid + ";spotlocation-->" +
				// spotlocation + ";sceneid-->" + sceneid
				// + ";channel_id-->" + channel_id);
			}
			e.setRealDatas(pd);
			return OP.NEXT;
		} catch (Exception ex) {
			return OP.DROP;
		}
	}

}
