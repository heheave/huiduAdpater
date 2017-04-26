package ec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import v.V;

public abstract class AbastructEventConsumer<T> implements EventConsumer<T> {

	private static final Logger log = Logger.getLogger(AbastructEventConsumer.class);

	private final static int MAX_BUF_SIZE = V.MAX_BUF_SIZE;
	private final static int MAX_THROD_SIZE = (int) (MAX_BUF_SIZE * 0.8);
	private volatile boolean isAlive;
	private final Lock lock = new ReentrantLock();
	private final Condition producer = lock.newCondition();
	private final Condition consumer = lock.newCondition();
	private final List<T> buffer = new ArrayList<T>(MAX_BUF_SIZE);

	private final Runnable eventsConsumer = new Runnable() {
		public void run() {
			while (isAlive) {
				preConsumer();
			}
		}
	};

	protected AbastructEventConsumer() {
		isAlive = false;
	}

	private void preConsumer() {
		T e = null;
		try {
			lock.lock();
			while (buffer.isEmpty() && isAlive) {
				producer.signalAll();
				consumer.await();
			}
			if (isAlive) {
				e = buffer.remove(0);
				if (buffer.size() < MAX_THROD_SIZE) {
					producer.signal();
				}
				consumer.signal();
			}
		} catch (Exception ex) {
			if (!isAlive) {
				log.info("AbastructEventConsumer is not alive the consumer run is out");
			} else {
				log.error("LOCK ERROR", ex);
			}
		} finally {
			lock.unlock();
		}

		if (e != null) {
			try {
				consumer(e);
			} catch (Exception e1) {
				log.error("CONSUMER ERROR", e1);
			}
		}
	}

	protected abstract void consumer(T e) throws Exception;

	@Override
	public void start() {
		if (!isAlive) {
			isAlive = true;
			new Thread(eventsConsumer).start();
		}
		log.info("AbastructEventConsumer1 is started");
	}

	@Override
	public void stop() {
		isAlive = false;
		try {
			lock.lock();
			consumer.signalAll();
			producer.signalAll();
		} finally {
			lock.unlock();
		}
		log.info("AbastructEventConsumer1 is stopped");
	}

	@Override
	public boolean isAlive() {
		return isAlive;
	}

	@Override
	public void add(T e) {
		try {
			lock.lock();
			while (buffer.size() >= MAX_BUF_SIZE && isAlive) {
				consumer.signalAll();
				producer.await();
			}

			if (isAlive) {
				buffer.add(e);
				consumer.signal();
				producer.signal();
			}
		} catch (Exception ex) {
			if (!isAlive) {
				log.info("AbastructEventConsumer1 is not alive the additional e is dropped");
			} else {
				log.error("LOCK ERROR", ex);
			}
		} finally {
			lock.unlock();
		}
	}
}
