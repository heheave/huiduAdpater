package ec;

import java.util.Collection;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import v.V;

public abstract class AbastructBatchEventConsumer<T> implements EventConsumer<T> {

	private static final Logger log = Logger.getLogger(AbastructBatchEventConsumer.class);

	private final Lock lock = new ReentrantLock();

	private final ArrayBlockingQueue<T> dataEvents1;

	private final ArrayBlockingQueue<T> dataEvents2;

	private volatile ArrayBlockingQueue<T> used;

	private volatile ArrayBlockingQueue<T> empty;

	private volatile boolean isrun;

	private final int CAMPACITY;

	private final int PERIOD;

	// To avoid batch operators being done at the same time
	private final int BEGIN_DELAY;

	private final Timer timer = new Timer();
	private final Runnable run = new Runnable() {

		@Override
		public void run() {
			// use try-catch to guarantee that the lock can be released
			// successfully when it occurs an exception.
			try {
				// only allow consuming in empty
				consumerOnce(empty);
				// clear all elements in empty
				// log.info("successfully done a batch");
			} catch (Exception e) {
				log.error("consumer once occurs error and empty is clear");
			} finally {
				empty.clear();
			}
		}

	};

	protected AbastructBatchEventConsumer(int comapcity, int period) {
		CAMPACITY = comapcity;
		PERIOD = period;
		dataEvents1 = new ArrayBlockingQueue<T>(CAMPACITY);
		dataEvents2 = new ArrayBlockingQueue<T>(CAMPACITY);
		used = dataEvents1;
		empty = dataEvents2;
		BEGIN_DELAY = new Random().nextInt(PERIOD / 1000) * 1000 + (PERIOD >> 2);
	}

	protected AbastructBatchEventConsumer() {
		this(V.BATCH_CAMPACITY, V.BATCH_PERIOD);
	}

	abstract protected void consumerOnce(Collection<T> batch) throws Exception;

	@Override
	public void start() {
		isrun = true;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isrun)
					flush(true);
			}

		}, BEGIN_DELAY, PERIOD);
	}

	@Override
	public void add(T e) {
		if (!used.offer(e)) {
			log.info("used queue is full a packet is droped:" + e);
		}
	}

	@Override
	public boolean isAlive() {
		return isrun;
	}

	public void stop() {
		flush(true);
		isrun = false;
		timer.cancel();
	}

	// flush is time overhead
	// but we have no idea to release it
	// don't touch these codes because of it's complex due to synchronizing
	public void flush(boolean checkUsed) {
		boolean check = checkUsed ? (!used.isEmpty() && empty.isEmpty()) : empty.isEmpty();
		if (check) {
			lock.lock();
			// to avoid multiply swapping operation
			check = checkUsed ? (!used.isEmpty() && empty.isEmpty()) : empty.isEmpty();
			if (check) {
				ArrayBlockingQueue<T> temp = used;
				used = empty;
				empty = temp;
				new Thread(run).start();
			}
			lock.unlock();
		}
	}
}
