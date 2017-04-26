package bus.bushandler;

import bus.Bus;
import util.IDGenUtil;
import event.Event;
import event.Event.CODE;
import event.PacketEvent;

public class DataClassifyBusHandler extends AbstractBusHandler {

	public DataClassifyBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public DataClassifyBusHandler() {
		this(IDGenUtil.getId(), "DataClassifyBusHandler", "This event handler is used to classify the packet by type");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		if (event instanceof PacketEvent) {
			PacketEvent pe = (PacketEvent) event;
			String topic = pe.getTopic();
			// hard coding and it is not so elegant
			if (!topic.endsWith("CTRL")) {
				if (topic.endsWith("CFG")) {
					pe.setPipeId(1);
					// pe.setInfoType(V.CONF_COL);
				} else {
					pe.setPipeId(0);
					// pe.setInfoType(V.DATA_COL);
				}
				pe.changeCode(CODE.IN);
				bus.putEvent(pe);
			} else {
				event.changeCode(CODE.DROP);
				bus.putEvent(event);
			}
		} else {
			event.changeCode(CODE.DROP);
			bus.putEvent(event);
		}
	}
}
