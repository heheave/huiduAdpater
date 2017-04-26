package bus.bushandler;

import bus.Bus;
import component.Component;
import event.Event;

public interface BusHandler extends Component {
	void initSet(String name, Object value);

	void handlerEvent(final Bus bus, Event event);
}
