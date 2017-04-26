package pipe;

import bus.Bus;
import bus.bushandler.StateBusHandler;
import event.Event;

abstract public class AbstractPipesBusHandler<T extends Event> implements StateBusHandler {

	protected final int id;

	protected final String name;

	protected final String desc;

	protected Bus bus;

	protected AbstractPipesBusHandler(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public int id() {
		return this.id;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String desc() {
		return this.desc;
	}

	@Override
	public void setBusOn(Bus bus) {
		this.bus = bus;
	}

	@Override
	public Bus getBusOn() {
		return this.bus;
	}

	@Override
	public void start() {
		assert bus != null : "bus belongs to shouldn't be null";
	}
}
