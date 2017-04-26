package bus;

import bus.bushandler.BusHandler;
import component.Component;

import event.Event;

public interface Bus extends Component{

	void putEvent(Event e);
	
	void register(Event.CODE code, BusHandler eh);

	void remove(Event.CODE code);

	boolean contain(Event.CODE code);

	void start();

	void stop();

}
