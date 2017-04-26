package cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DefaultCacheImpl<T> implements MemCache<T> {

	// private static final Logger log =
	// Logger.getLogger(DefaultCacheImpl.class);

	private static final String DEFAULT_KEY = "DEFAULT_KEY_INFO";

	private static final int MAX_CAMPACITY = 15;

	private final BlockingQueue<StringBuffer> unused = new ArrayBlockingQueue<>(MAX_CAMPACITY);

	protected final Map<String, T> cacheData = new ConcurrentHashMap<String, T>();

	protected DefaultCacheImpl() {
		for (int i = 0; i < MAX_CAMPACITY; i++) {
			unused.add(new StringBuffer());
		}
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getKey(String first, String... others) {
		StringBuffer sb = null;
		try {
			sb = unused.take();
			sb.append(first);
			for (String v : others) {
				sb.append('_');
				sb.append(v);
			}
			return sb.toString();
		} catch (InterruptedException e) {
			return DEFAULT_KEY;
		} finally {
			if (sb != null) {
				sb.setLength(0);
				unused.add(sb);
			}
			// log.info("key: ---" + showKey(first, others));
		}
	}

	// for test
	// private String showKey(String first, String... others) {
	// String re = first;
	// for (String v : others) {
	// re += '_';
	// re += v;
	// }
	// return re;
	// }

	@Override
	public void put(String key, T value) {
		cacheData.put(key, value);
	}

	@Override
	public T get(String key) {
		return cacheData.get(key);
	}

	@Override
	public T delete(String key) {
		return cacheData.remove(key);
	}

	@Override
	public boolean contains(String key) {
		return cacheData.containsKey(key);
	}

	@Override
	public int size() {
		return cacheData.size();
	}

	// TODO this method is just for testing
	public synchronized void showMap() {
		for (Entry<String, T> entry : cacheData.entrySet()) {
			System.out.println("key: " + entry.getKey() + ",value: " + entry.getValue());
		}
	}

	@Override
	public void load() throws Exception {
		// this method will be called in the constructor
	};

	@Override
	public void save() throws Exception {
		// this method will be called to tail cleaning
	};

}
