package bus.bushandler;

import bus.Bus;
import cache.deviceconf.AbastractDevice;
import cache.deviceconf.Conf;
import cache.deviceconf.ConfCache;
import util.IDGenUtil;
import event.ControlEvent;
import event.Event;
import event.Event.CODE;
import net.sf.json.JSONObject;

public class ConfCacheBusHandler extends AbstractBusHandler {

	private final ConfCache cc = ConfCache.instance();

	public ConfCacheBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public ConfCacheBusHandler() {
		this(IDGenUtil.getId(), "ConfCacheBusHandler", "This event handler is used to get the device's conf");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		if (event instanceof ControlEvent) {
			ControlEvent ce = (ControlEvent) event;
			String deviceId = ce.getTopic();
			AbastractDevice ad = cc.get(deviceId);
			JSONObject jo = new JSONObject();
			if (ad != null) {
				Conf[] tconf = null;
				int i = 0;
				while ((tconf = ad.getT(i)) != null) {
					JSONObject jt = new JSONObject();
					for (int ti = 0; ti < tconf.length; ti++) {
						JSONObject jp = new JSONObject();
						jp.put("i", tconf[ti].getI());
						jp.put("max", tconf[ti].getMax());
						jp.put("min", tconf[ti].getMin());
						jp.put("unit", tconf[ti].getU());
						jp.put("desc", tconf[ti].getM());
						jt.put("" + ti, jp);
					}
					jo.put("t" + i, jt);
					i++;
				}
				ce.setData(jo.toString());
			} else {
				ce.setData(jo.toString());
			}
			ce.changeCode(CODE.RC_RET);
		} else {
			event.changeCode(CODE.DROP);
		}
		bus.putEvent(event);
	}
}
