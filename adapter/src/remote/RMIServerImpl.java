package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import remote.common.Args;
import remote.common.RMICall;
import remote.common.Ret;
import remote.common.Ret.RetStatus;

@SuppressWarnings("serial")
public class RMIServerImpl extends UnicastRemoteObject implements RMICall {
	private static final int MAX_CONSUMER_THREAD = 2;
	private final AtomicInteger ids;
	private final Set<Integer> handlersSet;
	private final BlockingQueue<RMIHandler> handlersQueue;
	private volatile boolean isAlive;
	private final ExecutorService es = Executors.newFixedThreadPool(MAX_CONSUMER_THREAD);
	private volatile boolean isHasRun;

	public RMIServerImpl() throws RemoteException {
		super();
		handlersSet = Collections.synchronizedSet(new HashSet<Integer>());
		handlersQueue = new ArrayBlockingQueue<>(1000);
		ids = new AtomicInteger(0);
	}

	public void start() {
		isAlive = true;
		for (int i = 0; i < MAX_CONSUMER_THREAD; i++) {
			es.submit(run);
		}
	}

	public void stop() {
		isAlive = false;
		// run may wait
		if (handlersQueue.isEmpty()) {
			handlersQueue.offer(RMIHandlerFactory.getHandler(-1, null));
		} else {
			while (handlersQueue.isEmpty()) {
				try {
					RMIHandler h = handlersQueue.take();
					synchronized (h) {
						h.notify();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		handlersSet.clear();
		handlersQueue.clear();
		es.shutdown();
	}

	private final Runnable run = new Runnable() {
		@Override
		public void run() {
			try {
				while (isAlive) {
					RMIHandler h = handlersQueue.take();
					int id = h.getId();
					if (handlersSet.contains(id)) {
						handlersSet.remove(id);
						h.tackle();
					} else {
						System.out.println("Handler " + id + " has been removed!");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			isHasRun = false;
		}
	};

	@Override
	public Ret call(Args args) throws RemoteException {
		if (isAlive && !isHasRun) {
			es.submit(run);
		}

		Ret ret;
		if (args == null) {
			ret = new Ret(RetStatus.ERROR);
			ret.setError(new NullPointerException("Args is null"));
		} else {
			try {
				int id = ids.getAndIncrement();
				RMIHandler handler = RMIHandlerFactory.getHandler(id, args);
				if (handler != null) {
					synchronized (handler) {
						if (handlersQueue.isEmpty()) {
							handler.tackle();
							ret = handler.getRet();
						} else {
							handlersSet.add(id);
							if (handlersQueue.offer(handler)) {
								handler.wait();
								ret = handler.getRet();
							} else {
								handlersSet.remove(id);
								ret = new Ret(RetStatus.ERROR);
							}
							ret.setError(new Exception("Server is busy"));
						}
					}
				} else {
					ret = new Ret(RetStatus.ERROR);
					ret.setError(new NullPointerException("Handler is null"));
				}
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
				ret = new Ret(RetStatus.ERROR);
				ret.setError(e);
			}
		}
		return ret;
	}
}
