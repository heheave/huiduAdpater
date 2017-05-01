package persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import event.PacketEvent;
import persistence.PersistenceAccessorFactory.DBTYPE;
import v.V;

public class AdapterPersistence implements Persistence {

	private static final Logger log = Logger.getLogger(AdapterPersistence.class);

	private DbAccessorConf mongDbConf;

	private FileAccessorConf fileConf;

	private PersistenceAccessor dbPa;

	private PersistenceAccessor filePa;

	private static final PersistenceOpt datasDbOpt = new MongoPersistenceOpt(V.DB_NAME, V.DATAS_TABLE_NAME);
	private static final PersistenceOpt datasFileOpt = new FilePersistenceOpt(
			V.RECOVERY_SAVE_BASE_DIR + V.DATAS_TABLE_NAME, null);

	private static final PersistenceOpt channelDbOpt = new MongoPersistenceOpt(V.DB_NAME,
			V.CHANNEL_REALTIME_TABLE_NAME);
	private static final PersistenceOpt channelFileOpt = new FilePersistenceOpt(
			V.RECOVERY_SAVE_BASE_DIR + V.CHANNEL_REALTIME_TABLE_NAME, null);

	public AdapterPersistence() {
		initConf();
	}

	private void initConf() {
		mongDbConf = new DbAccessorConf();
		mongDbConf.setDbType(DBTYPE.MONGO);
		mongDbConf.setDbHost(V.DB_HOST);
		mongDbConf.setDbPort(V.DB_PORT);
		mongDbConf.setDbName(V.DB_NAME);
		mongDbConf.setUser(V.DB_USER);
		mongDbConf.setPassword(V.DB_PASSWORD);

		fileConf = new FileAccessorConf();
		fileConf.setDateFormate("yyyy-MM-dd_HH_mm_ss");
	}

	public void start() {
		dbPa = PersistenceAccessorFactory.getAccessor(mongDbConf);
		filePa = PersistenceAccessorFactory.getAccessor(fileConf);
		if (dbPa == null || filePa == null) {
			throw new RuntimeException("DbPersistence or FilePersistence initial failed");
		}
	}

	public void stop() throws Exception {
		// now this will not throw exception so stop it first
		if (filePa != null) {
			filePa.close();
		}
		filePa = null;

		if (dbPa != null) {
			dbPa.close();
		}
		dbPa = null;

	}

	@Override
	public void persistence(final Collection<PacketEvent> pevents) throws Exception {
		if (pevents.isEmpty()) {
			return;
		}

		Exception ec = null;
		try {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Collection<PersistenceData> datas = (Collection) pevents;
			persistence1(datasDbOpt, channelFileOpt, datas);
		} catch (Exception e) {
			ec = e;
		} finally {
			pevents.clear();
		}

		// predict size is four times
		List<PersistenceData> realTimeDatas = new ArrayList<PersistenceData>(pevents.size() << 2);
		for (PacketEvent pe : pevents) {
			List<PersistenceData> realTimeOne = pe.getRealDatas();
			if (realTimeOne != null && !realTimeOne.isEmpty()) {
				realTimeDatas.addAll(realTimeOne);
			}
		}

		try {
			persistence1(channelDbOpt, datasFileOpt, realTimeDatas);
		} catch (Exception e) {
			if (ec == null) {
				ec = e;
			}
		} finally {
			realTimeDatas.clear();
		}

		if (ec != null) {
			throw ec;
		}
	}

	private void persistence1(PersistenceOpt opt1, PersistenceOpt opt2, Collection<PersistenceData> pdata)
			throws Exception {
		log.info("batch size is: " + pdata.size());
		try {
			log.info("first try to persist datas to db");
			dbPa.persistenceBatch(opt1, pdata);
			log.info("successfully persisted to db");
		} catch (Exception e) {
			log.warn("persist to db error, second try to persist datas to file", e);
			filePa.persistenceBatch(opt2, pdata);
			log.info("successfully persisted to file");
		}
	}

}
