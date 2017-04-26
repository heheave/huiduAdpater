package stream;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class SocketChannel implements Channel {

	private final Queue<byte[]> unsuccess = new ArrayBlockingQueue<byte[]>(1000);

	private final Timer timer = new Timer();

	private volatile TimerTask uss = null;

	private static final int SLEEP_AFTER_SEND_FAILURE = 500;

	private static final int US_SEND = 8000;

	private static final long US_SEND_8 = (long) (US_SEND * 0.8);

	private SocketManager sm;

	private Socket socket;

	private final String host;

	private final int port;

	private final boolean isSlave;

	private volatile boolean isRecnt;

	private volatile boolean isActive;

	public SocketChannel(Socket socket) {
		this.host = socket.getInetAddress().getHostName();
		this.port = socket.getPort();
		this.isSlave = false;
		this.isRecnt = false;
		this.isActive = false;
		this.socket = socket;
	}

	public SocketChannel(String host, int port) {
		this.host = host;
		this.port = port;
		this.isSlave = true;
		this.isRecnt = false;
		this.isActive = false;
		this.socket = null;
	}

	private void connect() throws IOException {
		if (socket == null) {
			if (isSlave)
				socket = new Socket(InetAddress.getByName(host), port);
			else
				throw new IOException("Socket cannot be null in master mode");
		}

		sm = new SocketManager(socket) {
			@Override
			public void beforeService() {
				beforeWork();
			}

			@Override
			public void afterService() {
				try {
					SocketChannel.this.stop();
				} catch (Exception e) {
					// TODO
				}
				afterWork();
			}

			@Override
			public void trigerData(byte[] data) throws Exception {
				packetIn(data);
			}

		};
		sm.start();
	}

	private void reconnect() throws IOException {
		if (socket != null) {
			socket.isClosed();
		}
		if (sm != null) {
			sm.stop();
		}
		if (!isSlave) {
			return;
		}
		socket = new Socket(InetAddress.getByName(host), port);
		sm = new SocketManager(socket) {
			@Override
			public void beforeService() {
				beforeWork();
			}

			@Override
			public void afterService() {
				afterWork();
			}

			@Override
			public void trigerData(byte[] data) throws Exception {
				packetIn(data);
			}

		};
		sm.start();
	}

	public void start() throws IOException {
		isActive = true;
		connect();
	}

	// For test
	public void testStop() {
		unsuccess.clear();
		sm.stop();
	}

	@Override
	public void stop() throws Exception {
		isActive = false;
		sm.stop();
		if (uss != null) {
			uss.cancel();
		}
		timer.cancel();
	}

	@Override
	public State state() {
		return sm.state();
	}

	public void sendAfter(byte[] data) {
		if (unsuccess.offer(data)) {
			startUnunsuccessConsuming();
		} else {
			unsuccess.clear();
		}
	}

	public void sendAfter(String str) {
		if (str != null)
			sendAfter(str.getBytes());
	}

	@Override
	public void send(byte[] str) throws IOException {
		if (!isActive || str == null) {
			throw new IOException("SocketChannel has been stopped or send data is null");
		}
		if (sm != null) {
			try {
				sm.send(str);
				return;
			} catch (Exception e) {
				// System.out.println("--------------------------------------------------");
				if (isSlave) {
					if (!isRecnt) {
						synchronized (this) {
							// System.out.println("--------------------------------------------------");
							if (!isRecnt && (sm == null || !sm.state().equals(State.RUNNING))) {
								isRecnt = true;
								try {
									reconnect();
								} catch (IOException e1) {
									try {
										Thread.sleep(SLEEP_AFTER_SEND_FAILURE);
									} catch (InterruptedException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
								} finally {
									isRecnt = false;
								}
							}
						}
					}
				} else {
					if (sm != null) {
						sm.stop();
					}
					return;
				}
			}

		}
		unsuccess.add(str);
	}

	private void startUnunsuccessConsuming() {
		// System.out.println("sdfadsfasdfsafdasdfasdfsadfsadfsdaf");
		if (uss == null) {
			synchronized (unsuccess) {
				if (uss == null) {
					System.out.println("size-------size------size-----size------size------:" + unsuccess.size());
					uss = new TimerTask() {
						public void run() {
							int size = unsuccess.size();
							System.out.println("consumer-------consumer------consumer-----consumer------consumer------:"
									+ unsuccess.size());
							long opt = US_SEND_8 / size;
							for (int i = 0; i < size; i++) {
								try {
									long bt = System.currentTimeMillis();
									send(unsuccess.poll());
									long et = System.currentTimeMillis();
									if (et - bt < opt) {
										try {
											Thread.sleep(opt - (et - bt));
										} catch (InterruptedException e) {
											continue;
										}
									}
								} catch (IOException e) {
									try {
										Thread.sleep(opt >> 1);
									} catch (InterruptedException iet) {
										// TODO
									}
									continue;
								}
							}
							if (unsuccess.isEmpty()) {
								uss.cancel();
								uss = null;
							}
						}
					};
					timer.scheduleAtFixedRate(uss, US_SEND, US_SEND);
				}
			}
		}
	}

	public void send(String str) throws IOException {
		send(str.getBytes());
	}
}
