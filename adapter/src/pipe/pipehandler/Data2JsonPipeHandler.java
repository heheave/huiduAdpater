package pipe.pipehandler;

import net.sf.json.JSONObject;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

import event.PacketEvent;

public class Data2JsonPipeHandler implements PipeHandler<PacketEvent> {

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
			temp.put("timestamp", e.getTimestamp());
			temp.put("datatype", e.getInfoType());
			if (jo.containsKey("s")) {
				temp.put("normal", jo.remove("s"));
			} else {
				temp.put("normal", 0);
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
			// while (true) {
			// String inKey = "" + count;
			// if (jo.containsKey("" + inKey)) {
			// data.put(inKey, jo.get(inKey));
			// count++;
			// } else {
			// break;
			// }
			// }
			temp.put("portnum", count);
			temp.put("datas", jo);
			e.setJson(temp);

			return OP.NEXT;
		} catch (Exception ex) {
			return OP.DROP;
		}
	}

}
