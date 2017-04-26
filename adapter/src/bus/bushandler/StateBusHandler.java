package bus.bushandler;

import bus.Bus;

public interface StateBusHandler extends BusHandler {

	void setBusOn(Bus bus);

	Bus getBusOn();

	void start();

	void stop();
}
