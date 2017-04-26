package ec;

public interface EventConsumer<T> {

	void start();

	void stop();

	boolean isAlive();
	
	void add(T e);

}
