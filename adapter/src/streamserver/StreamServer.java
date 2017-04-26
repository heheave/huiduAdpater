package streamserver;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StreamServer {

	private static final Logger log = Logger.getLogger(StreamServer.class);

	private volatile boolean isrun;

	private ServerSocket serverSocket;

	private static final int PERIOD_PING_TIME = 30000;

	private static final int PORT = 6789;

	private static final int MAX_SLAVE_NUM = 50;

	private Map<Long, ServerSocketChannel> mmap = new ConcurrentHashMap<Long, ServerSocketChannel>();

	private final Runnable runListen = new Runnable() {
		@Override
		public void run() {
			while (isrun) {
				try {
					Socket socket = serverSocket.accept();
					try {
						if (mmap.size() <= MAX_SLAVE_NUM) {
							long id = System.currentTimeMillis();
							while (mmap.containsKey(id)) {
								id++;
							}
							new ServerSocketChannel(StreamServer.this, id, socket);
						} else {
							socket.close();
						}
					} catch (IOException ie) {
						log.info("CLOSE SOCKET ERROR", ie);
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
	};

	private final Timer timer = new Timer();

	private final TimerTask runPing = new TimerTask() {
		@Override
		public void run() {
			for (Entry<Long, ServerSocketChannel> entry : mmap.entrySet()) {
				entry.getValue().ping();
			}
		}
	};

	public StreamServer() {
		isrun = false;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (Exception e) {
			serverSocketClose();
		}
	}

	public void start() {
		if (serverSocket == null) {
			System.exit(-1);
		}
		if (!isrun) {
			isrun = true;
			new Thread(runListen).start();
			timer.schedule(runPing, PERIOD_PING_TIME, PERIOD_PING_TIME);
		}
		log.info("StreamServer started");
	}

	public void stop() {
		isrun = false;
		for (Entry<Long, ServerSocketChannel> entry : mmap.entrySet()) {
			entry.getValue().stop();
		}
		serverSocketClose();
		runPing.cancel();
		timer.cancel();
		log.info("StreamServer stopped");
	}

	private void serverSocketClose() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverSocket = null;
		}
	}

	public void addMaster(long id, ServerSocketChannel master) {
		mmap.put(id, master);
		log.info("A machine which id is " + id + " is connected to master");
	}

	public void deleteMaster(long id) {
		mmap.remove(id);
		log.info("A machine which id is " + id + " is disconnected to master");
	}

	public void updateMaster(long id) {

	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("file/log4j.properties");
		final StreamServer kas = new StreamServer();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				kas.start();
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				kas.stop();
			}
		});
	}

}
