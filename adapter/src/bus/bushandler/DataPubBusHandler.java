package bus.bushandler;

import org.apache.log4j.Logger;

import client.Contacter;
import util.IDGenUtil;
import v.V;
import bus.Bus;
import event.ControlEvent;
import event.Event;
import event.Event.CODE;

public class DataPubBusHandler extends AbstractBusHandler {

	private static final Logger log = Logger.getLogger(DataPubBusHandler.class);

	private Contacter client;

	public DataPubBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public DataPubBusHandler() {
		this(IDGenUtil.getId(), "DataPubBusHandler", "This component is used for pub message to client");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		if (event instanceof ControlEvent) {
			ControlEvent ce = (ControlEvent) event;
			String topic = ce.getTopic();
			String message = ce.getDataString();
			if (topic != null && message != null) {
				try {
					this.client.publish(topic, message);
					ce.changeCode(CODE.RC_RET);
					ce.setData((String) null);
					bus.putEvent(ce);
					return;
				} catch (Exception e) {
					log.error("unable to pub message: " + message + " to topic: " + topic);
				}
			}
		}
		event.changeCode(CODE.DROP);
		bus.putEvent(event);

	}

	@Override
	public void initSet(String name, Object value) {
		if (name.equals(V.CONTACTOR)) {
			if (value instanceof Contacter) {
				this.client = (Contacter) value;
			} else {
				throw new RuntimeException("contactor value should be contactor type");
			}
		} else {
			throw new RuntimeException("unknown name: " + name);
		}
	}

}
