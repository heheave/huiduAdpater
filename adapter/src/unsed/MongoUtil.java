//package unsed;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import net.sf.json.JSONObject;
//import unsed.PacketEvent;
//
////import org.apache.log4j.Logger;
//import org.bson.Document;
//
//import v.V;
//
//import com.mongodb.MongoClient;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//public class MongoUtil {
//
//	// private static final Logger log = Logger.getLogger(MongoUtil.class);
//
//	private static MongoClient mc;
//
//	private static final String DB_HOST = V.DB_HOST;
//	private static final int DB_PORT = V.DB_PORT;
//	private static final String DB_NAME = V.DB_NAME;
//
//	// private static final String DATA_COL = V.DATA_COL;
//	//
//	// private static final String CONF_COL = V.CONF_COL;
//
//	private static MongoClient checkMongoClient() {
//		if (mc == null) {
//			synchronized (MongoUtil.class) {
//				if (mc == null) {
//					mc = new MongoClient(DB_HOST, DB_PORT);
//				}
//			}
//		}
//		return mc;
//	}
//
//	public static void closeMongoClient(MongoClient mc) {
//		if (mc != null) {
//			mc.close();
//		}
//	}
//
//	private static boolean batchInsertIntoCollection(MongoDatabase mdb, String colName, List<Document> documentsCol)
//			throws Exception {
//		MongoCollection<Document> collection = mdb.getCollection(colName);
//		collection.insertMany(documentsCol);
//		return true;
//	}
//
//	private static Document getDocument(JSONObject jo) {
//		if (jo == null) {
//			return null;
//		}
//		Document result = new Document();
//		@SuppressWarnings("unchecked")
//		Set<Entry<String, Object>> entries = jo.entrySet();
//		for (Entry<String, Object> entry : entries) {
//			String key = entry.getKey();
//			Object value = entry.getValue();
//			if (value instanceof JSONObject) {
//				value = getDocument((JSONObject) value);
//			}
//			result.append(key, value);
//		}
//		return result;
//	}
//
//	public static boolean batchInsertToDB(final Collection<PacketEvent> pevents) throws Exception {
//		MongoClient mc = checkMongoClient();
//		if (mc == null) {
//			throw new RuntimeException("Cann't connect to remote db");
//		}
//		Map<String, List<Document>> mapDocuments = new HashMap<String, List<Document>>();
//		for (PacketEvent pe : pevents) {
//			int pipeIdx = pe.getPipeId();
//			if (pipeIdx < 0) {
//				continue;
//			}
//			String type = "datas";
//			Document d = getDocument(pe.getJo());
//			System.out.println(d);
//			if (mapDocuments.containsKey(type)) {
//				if (d != null)
//					mapDocuments.get(type).add(d);
//			} else {
//				List<Document> temp = new ArrayList<Document>();
//				if (d != null)
//					temp.add(d);
//				mapDocuments.put(type, temp);
//			}
//
//			if (pe.hasProccessed()) {
//				String typePro = "dataprocess";
//				d = getDocument(pe.getJod());
//				if (mapDocuments.containsKey(typePro)) {
//					if (d != null)
//						mapDocuments.get(typePro).add(d);
//				} else {
//					List<Document> temp = new ArrayList<Document>();
//					if (d != null)
//						temp.add(d);
//					mapDocuments.put(typePro, temp);
//				}
//			}
//		}
//
//		MongoDatabase mdb = mc.getDatabase(DB_NAME);
//		if (mdb == null) {
//			return false;
//		}
//		boolean result = true;
//		try {
//			for (Entry<String, List<Document>> entry : mapDocuments.entrySet()) {
//				result &= batchInsertIntoCollection(mdb, entry.getKey(), entry.getValue());
//			}
//		} finally {
//			mapDocuments.clear();
//		}
//		return result;
//	}
//}
