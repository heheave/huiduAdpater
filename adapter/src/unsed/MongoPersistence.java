package unsed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import event.PacketEvent;
import net.sf.json.JSONObject;
import v.V;

public class MongoPersistence implements Persistence<MongoClient, PacketEvent> {

	private MongoClient mc;

	private static final String DB_HOST = V.DB_HOST;
	private static final int DB_PORT = V.DB_PORT;
	private static final String DB_USER = V.DB_USER;
	private static final String DB_PASSWORD = V.DB_PASSWORD;

	@Override
	public MongoClient getAccesor(String dbname) throws Exception {
		if (mc == null) {
			synchronized (MongoPersistence.class) {
				if (mc == null) {
					MongoCredential credential = MongoCredential.createCredential(DB_USER, dbname,
							DB_PASSWORD.toCharArray());
					ServerAddress serverAddress = new ServerAddress(DB_HOST, DB_PORT);
					mc = new MongoClient(serverAddress, Arrays.asList(credential));
				}
			}
		}
		return mc;
	}

	@Override
	public void closeAccesor(MongoClient accesor) throws Exception {
		if (accesor != null) {
			accesor.close();
		}
	}

	@Override
	public void flush() {
		// TODO
	}

	@Override
	public void close() {
		flush();
		try {
			closeAccesor(mc);
		} catch (Exception e) {
			// TODO
		}
	}

	@Override
	public boolean persistence(PersistenceOpt po, PacketEvent event) throws Exception {
		List<PacketEvent> listDocuments = new ArrayList<PacketEvent>();
		listDocuments.add(event);
		return persistenceByBatch(po, listDocuments);
	}

	@Override
	public boolean persistenceByBatch(PersistenceOpt po, Collection<PacketEvent> events) throws Exception {
		MongoCollection<Document> collection = getTableColByMPO(po);
		if (collection == null) {
			return false;
		}
		List<Document> listDocuments = new ArrayList<Document>();
		for (PacketEvent pe : events) {
			int pipeIdx = pe.getPipeId();
			if (pipeIdx < 0) {
				continue;
			}
			Document d = getDocument(pe.toJson());
			listDocuments.add(d);
		}
		try {
			collection.insertMany(listDocuments);
			return true;
		} finally {
			listDocuments.clear();
		}
	}

	private MongoCollection<Document> getTableColByMPO(PersistenceOpt po) throws Exception {
		MongonPersistenceOpt mpo = (MongonPersistenceOpt) po;
		MongoClient mc = getAccesor(mpo.databaseName);
		MongoDatabase mdb = mc.getDatabase(mpo.databaseName);
		if (mdb == null) {
			return null;
		}
		MongoCollection<Document> collection = mdb.getCollection(mpo.tableName);
		if (collection == null) {
			return null;
		}
		return collection;
	}

	private Document getDocument(JSONObject jo) {
		if (jo == null) {
			return null;
		}
		Document result = new Document();
		@SuppressWarnings("unchecked")
		Set<Entry<String, Object>> entries = jo.entrySet();
		for (Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof JSONObject) {
				value = getDocument((JSONObject) value);
			}
			result.append(key, value);
		}
		return result;
	}
}
