package pipe.pipehandler;

import event.ControlEvent;
import net.sf.json.JSONObject;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

public class MetaCtrlPipeHandler implements PipeHandler<ControlEvent> {

	@Override
	public OP handle(PipeHandlerContext<ControlEvent> hc, ControlEvent e) throws Exception {
		String data = e.getDataString();
		if (data == null) {
			return OP.DROP;
		}
		JSONObject jo = JSONObject.fromObject(data);
		String channelId = jo.containsKey("channelid") ? jo.getString("channelid") : null;
		String deviceType = null;
		String deviceId = null;
		String dataType = null;
		String portIdx = null;
		String value = null;
		String mode = null;
		if (channelId == null) {
			deviceType = jo.containsKey("devicetype") ? jo.getString("devicetype") : null;
			deviceId = jo.containsKey("deviceid") ? jo.getString("deviceid") : null;
			portIdx = jo.containsKey("portidx") ? jo.getString("portidx") : null;
		} else {
			String[] infos = channelId.split("_", 4);
			deviceType = infos[0];
			deviceId = infos[1];
			dataType = infos[2];
			portIdx = infos[3];
		}
		value = jo.containsKey("value") ? jo.getString("value") : null;
		mode = jo.containsKey("ctrltype") ? jo.getString("ctrltype") : null;
		if (deviceType == null || deviceId == null || portIdx == null || value == null || mode == null) {
			return OP.DROP;
		}
		JSONObject ctrlJo = new JSONObject();
		ctrlJo.put("devicetype", deviceType);
		ctrlJo.put("datatype", dataType);
		ctrlJo.put("" + portIdx, Integer.parseInt(value));
		ctrlJo.put("mode", mode);
		e.setTopic("Witium/DistrIO/" + deviceType + "/" + deviceId + "/CTRL");
		e.setData(ctrlJo.toString());
		System.out.println(e.getTopic() + "---" + e.getDataString());
		return OP.NEXT;
	}

}
