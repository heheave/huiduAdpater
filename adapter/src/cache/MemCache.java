package cache;

public interface MemCache<T> {

	public void load() throws Exception;

	public void save() throws Exception;

	public void put(String key, T value);

	public T get(String key);

	public T delete(String key);

	boolean contains(String key);

	int size();
}
