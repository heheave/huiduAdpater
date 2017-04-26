package util;

import org.apache.log4j.Logger;

import v.V;

import com.mongodb.MongoClient;

public class DBFactory {

	private static final Logger log = Logger.getLogger(DBFactory.class);

	public enum DBTYPE {
		MONGO, MYSQL
	}

	private static final String DB_HOST = V.DB_HOST;

	private static final int DB_PORT = V.DB_PORT;

	// private static final String DB_NAME = V.DB_NAME;

	public static Object getAccessor(DBTYPE type) {
		Object result = null;
		try {
			switch (type) {
			case MONGO: {
				
				result = new MongoClient(DB_HOST, DB_PORT);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			log.error("CREATE DB CNT ERROR", e);
		}
		return result;
	}
	
	public static void closeAccessor(Object accessor,DBTYPE type ) {
		try {
			switch (type) {
			case MONGO: {
				((MongoClient)accessor).close();
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			log.error("CLOSE DB CNT ERROR", e);
		}
	}
}
