package bus.bushandler;

import bus.Bus;
import event.Event;
import event.Event.CODE;
import util.IDGenUtil;

public class BusChangeBusHandler extends AbstractBusHandler {

	// private Map<String, Bus> stringToBus;
	private Bus one;

	private Bus two;

	public BusChangeBusHandler(int id, String name, String desc) {
		super(id, name, desc);
		one = null;
		two = null;
	}

	public BusChangeBusHandler() {
		this(IDGenUtil.getId(), "BusChangeBusHandler",
				"This event handler is used to change the bus which event is on");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		int id = bus.id();
		if (one != null && one.id() == id && two != null) {
			event.changeCode(CODE.PRE);
			two.putEvent(event);
		} else if (two != null && two.id() == id && one != null) {
			event.changeCode(CODE.PRE);
			one.putEvent(event);
		} else {
			event.changeCode(CODE.DROP);
			bus.putEvent(event);
		}
	}

	@Override
	public void initSet(String name, Object value) {
		if (one == null) {
			one = (Bus) value;
		} else if (two == null) {
			two = (Bus) value;
		} else {
			throw new IllegalArgumentException("No empty bus to set");
		}
	}

}
