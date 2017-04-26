package bus.bushandler;

import java.util.Collection;
import org.apache.log4j.Logger;
import bus.Bus;
import util.IDGenUtil;
import ec.AbastructBatchEventConsumer;
import event.Event;
import event.PacketEvent;
import persistence.AdapterPersistence;

public class DataPersistBusHandler extends AbastructBatchEventConsumer<PacketEvent> implements StateBusHandler {

	private static final Logger log = Logger.getLogger(DataPersistBusHandler.class);

	private final int id;
	private final String name;
	private final String desc;

	private AdapterPersistence ap = new AdapterPersistence();

	private Bus bus;

	public DataPersistBusHandler(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}

	public DataPersistBusHandler() {
		this(IDGenUtil.getId(), "DataPersistBusHandler", "It's insert the data into mongodb by batch");
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
	protected void consumerOnce(final Collection<PacketEvent> packets) throws Exception {
		log.info("batch size is: " + packets.size());
		ap.persistence(packets);
		// log.info("batch size is: " + empty.size());
		// try {
		// // MongoUtil.batchInsertToDB(empty);
		// mongodb.persistenceByBatch(mongoPo, empty);
		// } catch (Exception e) {
		// int tryTime;
		// for (tryTime = 0; tryTime < 3; tryTime++) {
		// try {
		// filedb.persistenceByBatch(filePo, empty);
		// break;
		// } catch (Exception e1) {
		// continue;
		// }
		// }
		// if (tryTime >= 3) {
		// throw e;
		// }
		// }
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
	public void initSet(String name, Object value) {
		throw new RuntimeException("this method shouldn't be called");
	}

	@Override
	public void start() {
		super.start();
		ap.start();
		assert this.bus != null : "bus shouldn't be null";
		log.info("finisher is started");
	}

	@Override
	public void stop() {
		super.stop();
		try {
			ap.stop();
		} catch (Exception e) {
			log.error("stop adapter persistence error", e);
		}
		log.info("finisher is stoped");
	}

	@Override
	public void handlerEvent(Bus bus, Event event) {
		add((PacketEvent) event);
	}

}
