package pipe.pipehandler;

import net.sf.json.JSONObject;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;
import cache.deviceconf.ConfCache;
import event.PacketEvent;

public class UpdateConfPipeHandler implements PipeHandler<PacketEvent> {

	private final ConfCache cc = ConfCache.instance();

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		try {
			String deviceId = e.getDeviceId();
			String deviceType = e.getDeviceType();
			JSONObject jo = e.toJson();
			if (deviceId == null || deviceType == null || jo == null) {
				return OP.DROP;
			}
			JSONObject jodatas = jo.getJSONObject("datas");
			if (jodatas == null) {
				return OP.DROP;
			}
			int t = (Integer) jo.get("t");
			int portnum = (Integer) jo.get("portnum");
			if (t >= 0 && portnum > 0) {
				int[] portdatas = new int[portnum];
				for (int i = 0; i < portnum; i++) {
					String key_i = "" + i;
					if (jodatas.containsKey(key_i))
						portdatas[i] = (Integer) jodatas.get(key_i);
					else
						portdatas[i] = -1;
				}
				cc.updateConf(deviceId, deviceType, t, portdatas);
			}

			return OP.NEXT;

		} catch (Exception ex) {
			ex.printStackTrace();
			return OP.DROP;
		}
	}

}
