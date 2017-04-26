package pipe.pipehandler;

import cache.deviceoninfo.DeviceOnCache;
import event.PacketEvent;
import net.sf.json.JSONObject;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

public class UpdateDeviceonPipeHandler implements PipeHandler<PacketEvent> {

	private final DeviceOnCache deviceOnCache = DeviceOnCache.instance();

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		String id = e.getDeviceId();
		String type = e.getDeviceType();
		String dataType = e.getInfoType();
		long timestamp = e.getTimestamp();
		JSONObject datas = e.toJson().getJSONObject("datas");
		for (Object k : datas.keySet()) {
			String key = deviceOnCache.getKey(type, id, dataType, k.toString());
			deviceOnCache.deviceOn(key, timestamp);
		}
		return OP.NEXT;
	}
}
