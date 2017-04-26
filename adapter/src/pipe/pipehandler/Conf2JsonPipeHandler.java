package pipe.pipehandler;

import net.sf.json.JSONObject;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;
import event.PacketEvent;

public class Conf2JsonPipeHandler implements PipeHandler<PacketEvent> {

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		try {
			JSONObject jo = e.toJson();
			if (jo == null) {
				return OP.DROP;
			}
			JSONObject temp = new JSONObject();
			temp.put("devicetype", e.getDeviceType());
			temp.put("deviceid", e.getDeviceId());
			temp.put("timestamp", "" + e.getTimestamp());

			if (jo.containsKey("t")) {
				temp.put("t", jo.remove("t"));
			} else {
				temp.put("t", -1);
			}
			int count = jo.size();
//			JSONObject data = new JSONObject();
//			int count = 0;
//			@SuppressWarnings("unchecked")
//			Set<Entry<String, Object>> entries = jo.entrySet();
//			for (Entry<String, Object> entry : entries) {
//				String key = entry.getKey();
//				if (IsNumericUtil.isInteger(key)) {
//					data.put(key, entry.getValue());
//					count++;
//				}
//			}
			temp.put("portnum", count);
			temp.put("datas", jo);
			e.setJson(temp);

			return OP.NEXT;
		} catch (Exception ex) {
			return OP.DROP;
		}
	}

}
