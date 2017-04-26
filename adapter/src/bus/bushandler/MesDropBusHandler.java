package bus.bushandler;

import org.apache.log4j.Logger;

import bus.Bus;
import util.IDGenUtil;
import event.ControlEvent;
import event.Event;
import event.PacketEvent;

public class MesDropBusHandler extends AbstractBusHandler {

	private static final Logger log = Logger.getLogger(MesDropBusHandler.class);

	public MesDropBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public MesDropBusHandler() {
		this(IDGenUtil.getId(), "MesDropBusHandler", "This event handler is used to handler mes which is droped");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		if (event instanceof PacketEvent) {
			PacketEvent pe = (PacketEvent) event;
			log.error("a packet is droped by pipeline " + pe.getPipeId() + " and its code is " + pe.getCode()
					+ " and its message is " + pe);
			pe = null;
		} else if (event instanceof ControlEvent) {
			ControlEvent ce = (ControlEvent) event;
			log.error("a control is droped by pipeline " + ce.getPipeId() + " and its ctrl is " + ce.getCtrlId()
					+ " and cmd is " + ce.getCmd() + " and its message is " + ce.getDataString());
			ce.setData((byte[]) null);
			ce = null;
		} else {
			log.error(
					"a event is droped and its code is " + event.getCode() + " and its message is " + event.toString());
			event = null;
		}

	}
}
