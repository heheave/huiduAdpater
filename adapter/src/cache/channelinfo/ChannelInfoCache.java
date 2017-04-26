package cache.channelinfo;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cache.DefaultCacheImpl;
import v.V;

public class ChannelInfoCache extends DefaultCacheImpl<ChannelInfo> {

	private static ChannelInfoCache cic = null;

	private ChannelInfoCache() {
	}

	@Override
	public void load() throws Exception {
		init();
	}

	private synchronized void init() {
		MongoCredential credential = MongoCredential.createCredential(V.DB_USER, V.DB_NAME,
				V.DB_PASSWORD.toCharArray());
		ServerAddress serverAddress = new ServerAddress(V.DB_HOST, V.DB_PORT);
		MongoClient mc = new MongoClient(serverAddress, Arrays.asList(credential));
		MongoDatabase mdb = mc.getDatabase(V.DB_NAME);
		MongoCollection<Document> collection = mdb.getCollection("channel");
		for (Document document : collection.find()) {
			// System.out.println(document);
			String channelid = document.containsKey("channelid") ? document.getString("channelid") : "NULL";
			String usermaxdata = document.containsKey("usermaxdata") ? document.getString("usermaxdata") : null;
			String usermindata = document.containsKey("usermindata") ? document.getString("usermindata") : null;
			String unit = document.containsKey("userunit") ? document.getString("userunit") : null;
			String desc = document.containsKey("userdesc") ? document.getString("userdesc") : null;
			String spotid = document.containsKey("spotid") ? document.getString("spotid") : null;
			String spotlocation = document.containsKey("spotlocation") ? document.getString("spotlocation") : null;
			String sceneid = document.containsKey("sceneid") ? document.getString("sceneid") : null;
			String _id = document.containsKey("_id") ? document.get("_id").toString() : null;
			String active = document.containsKey("active") ? document.getString("active") : null;
			ChannelInfo channelInfo = new ChannelInfo(channelid, usermaxdata, usermindata, unit, desc, spotid,
					spotlocation, sceneid, _id, active);
			put(channelid, channelInfo);
		}
		mc.close();
	}

	public static ChannelInfoCache instance() {
		if (cic == null) {
			synchronized (ChannelInfoCache.class) {
				if (cic == null) {
					cic = new ChannelInfoCache();
				}
			}
		}
		return cic;
	}

}
