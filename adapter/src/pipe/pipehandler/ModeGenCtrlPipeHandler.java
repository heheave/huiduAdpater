package pipe.pipehandler;

import event.ControlEvent;
import net.sf.json.JSONObject;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;
import util.GenModeUtil;

public class ModeGenCtrlPipeHandler implements PipeHandler<ControlEvent> {

	@Override
	public OP handle(PipeHandlerContext<ControlEvent> hc, ControlEvent e) throws Exception {
		String data = e.getDataString();
		if (data == null) {
			return OP.DROP;
		}
		JSONObject jo = JSONObject.fromObject(data);
		String deviceType = jo.remove("devicetype").toString();
		String dataType = jo.remove("datatype").toString();
		String mode = jo.remove("mode").toString();
		int m = GenModeUtil.getMode(deviceType, dataType, mode);
		jo.put("m", m);
		System.out.println(jo.toString());
		e.setData(jo.toString());
		return OP.NEXT;
	}

}
