package bus;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import bus.bushandler.BusHandler;
import util.IDGenUtil;
import ec.AbastructEventConsumer;
import event.Event;
import event.Event.CODE;

public class AsychBusContext extends AbastructEventConsumer<Event> implements Bus {

	private static final Logger log = Logger.getLogger(AsychBusContext.class);

	private final int id;

	private final String name;

	private final String desc;

	private final Map<Event.CODE, BusHandler> eventhandlers = new HashMap<Event.CODE, BusHandler>();

	public AsychBusContext(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}

	public AsychBusContext() {
		this(IDGenUtil.getId(), "EventBusContext", "It's the event bus and it tackle the event circularly, "
				+ "according to the different event code, " + "call it's corresponding handler to handler the event");
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String desc() {
		return desc;
	}

	@Override
	public void start() {
		super.start();
		log.info("bus" + id + " has been started");
	}

	@Override
	public void stop() {
		super.stop();
		log.info("bus" + id + " has been stoped");
	}

	@Override
	public void register(CODE code, BusHandler eh) {
		if (contain(code)) {
			remove(code);
		}
		eventhandlers.put(code, eh);
		log.info("eventhandler" + eh.id() + ": " + eh.name() + " has conncted to the bus" + id + " " + name
				+ " whose desc is: " + eh.desc());
	}

	@Override
	public void remove(CODE code) {
		eventhandlers.remove(code);
		log.info("remove all the handler which handle " + code);
	}

	@Override
	public boolean contain(CODE code) {
		return eventhandlers.containsKey(code);
	}

	@Override
	protected void consumer(Event e) {
		Event.CODE code = e.getCode();
		if (eventhandlers.containsKey(code)) {
			eventhandlers.get(code).handlerEvent(this, e);
		} else {
			log.error("Unknown evnet code: " + code);
		}
	}

	@Override
	public void putEvent(Event e) {
		super.add(e);
	}
}
