package unsed;

import java.util.Collection;

import org.apache.log4j.Logger;

import ec.AbastructBatchEventConsumer;
import v.V;

public class ObjectPersistenceWorker extends AbastructBatchEventConsumer<Object> {

	private final Logger log = Logger.getLogger(ObjectPersistenceWorker.class);

	private final Persistence<?, Object> mongodb = new MongoPersistence_1();

	private final MongonPersistenceOpt mongoPo = new MongonPersistenceOpt(V.DB_NAME, V.CHANNEL_REALTIME_TABLE_NAME);

	private final Persistence<?, Object> filedb = new FilePersistence_1();

	private final FilePersistenceOpt filePo = new FilePersistenceOpt(V.RECOVERY_SAVE_BASE_DIR + V.CHANNEL_TABLE_NAME,
			null);

	private static ObjectPersistenceWorker objectPersistenceWorker;

	private ObjectPersistenceWorker() {
		// private the constructor
	}

	public static ObjectPersistenceWorker instance() {
		if (objectPersistenceWorker == null) {
			synchronized (ObjectPersistenceWorker.class) {
				if (objectPersistenceWorker == null) {
					objectPersistenceWorker = new ObjectPersistenceWorker();
					objectPersistenceWorker.start();
				}
			}
		}
		return objectPersistenceWorker;
	}

	@Override
	protected void consumerOnce(Collection<Object> batch) throws Exception {
		try {
			log.info("batch size is: " + batch.size());
			mongodb.persistenceByBatch(mongoPo, batch);
		} catch (Exception e) {
			int tryTime;
			for (tryTime = 0; tryTime < 3; tryTime++) {
				try {
					filedb.persistenceByBatch(filePo, batch);
					break;
				} catch (Exception e1) {
					continue;
				}
			}
			if (tryTime >= 3) {
				throw e;
			}
		}
	}

	public void putObject(Object obj) {
		add(obj);
	}

	@Override
	public void stop() {
		super.stop();
		mongodb.close();
		filedb.close();
	}

}
