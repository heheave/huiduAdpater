package bus.bushandler;

import bus.Bus;
import util.IDGenUtil;
import v.V;
import event.ControlEvent;
import event.Event;
import event.Event.CODE;

public class CtrlCheckBusHandler extends AbstractBusHandler {

	public CtrlCheckBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public CtrlCheckBusHandler() {
		this(IDGenUtil.getId(), "CtrlCheckBusHandler", "This event handler is used to check the control command");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		// check something
		Event checked = check(event);
		bus.putEvent(checked);
	}

	private Event check(Event event) {
		if (event instanceof ControlEvent) {
			ControlEvent ce = (ControlEvent) event;
			String cmdType = ce.getCmd();
			if (cmdType != null) {
				if (cmdType.startsWith(V.CTRL_DEVICE_INFO_PREDIX)) {
					ce.changeCode(CODE.IN);
				} else if (cmdType.startsWith(V.GET_DEVICE_INFO_PREDIX)) {
					ce.changeCode(CODE.GET);
				} else {
					event.changeCode(CODE.DROP);
				}
			} else {
				event.changeCode(CODE.DROP);
			}
		}
		return event;
	}
}
