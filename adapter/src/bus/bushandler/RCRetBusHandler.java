package bus.bushandler;

import rmi.Server;
import util.IDGenUtil;
import v.V;

import org.apache.log4j.Logger;

import bus.Bus;
import event.ControlEvent;
import event.Event;

public class RCRetBusHandler extends AbstractBusHandler {

	private static final Logger log = Logger.getLogger(RCRetBusHandler.class);

	private Server server;

	public RCRetBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public RCRetBusHandler() {
		this(IDGenUtil.getId(), "RCRetBusHandler",
				"This handler is the gate of the user remote call, only the ControlEvent with ctrl id will be returned");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		if (event instanceof ControlEvent) {
			ControlEvent ce = (ControlEvent) event;
			if (!ce.getCtrlId().equals(ControlEvent.NULL_CTRLID)) {
				this.server.remoteRet(ce);
			}
		} else {
			log.error("unexpected event type: " + event.getClass().getName());
			event = null;
		}
	}

	@Override
	public void initSet(String name, Object value) {
		if (name.equals(V.REMOTECALL)) {
			if (value instanceof Server) {
				this.server = (Server) value;
			} else {
				throw new RuntimeException("contactor value should be contactor type");
			}
		} else {
			throw new RuntimeException("unknown name: " + name);
		}
	}

}
