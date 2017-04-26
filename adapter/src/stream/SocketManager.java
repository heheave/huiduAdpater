package stream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import stream.Channel.State;

public abstract class SocketManager {

	private volatile State state;

	private final Socket socket;

	private final Runnable runService = new Runnable() {
		public void run() {
			try {
				beforeService();
				service();
				afterService();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						System.out.println("close socket error");
					}
				}
			}
		}
	};

	public SocketManager(Socket socket) {
		this.socket = socket;
		this.state = State.PRE;
	}

	protected void service() {
		byte[] buf = new byte[2048];
		DataInputStream di = null;
		while (!state.equals(State.READ_DOWN) && !state.equals(State.STOP)) {
			if (!socket.isClosed()) {
				int psize = 0;
				try {
					di = new DataInputStream(socket.getInputStream());
					socket.setSoTimeout(2000);
					psize = di.readInt();
					if (psize > 0) {
						socket.setSoTimeout(0);
						// System.out.println(psize);
						int rsize = 0;
						while (rsize < psize) {
							int a = di.read(buf, rsize, psize - rsize);
							rsize += a;
						}
						try {
							trigerData(Arrays.copyOf(buf, psize));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (SocketTimeoutException ste) {
					continue;
				} catch (Exception e) {
					state = State.STOP;
				}
			} else {
				state = State.READ_DOWN;
			}

		}

	}

	private synchronized final void sendData(byte[] data) throws IOException {
		if (data == null) {
			throw new IOException("send data connot be null and its size should be between 1 and 1024");
		} else {
			if (!socket.isClosed() && !state.equals(State.STOP) && !state.equals(State.WRITE_DOWN)) {
				try {
					int length = data.length;
					DataOutputStream os = new DataOutputStream(socket.getOutputStream());
					// try three times
					for (int i = 0; i < 3; i++) {
						try {
							os.writeInt(length);
							os.write(data, 0, length);
							os.flush();
							break;
						} catch (IOException e) {
							continue;
						}
					}
				} catch (Exception e) {
					state = state.equals(State.READ_DOWN) ? State.STOP : State.WRITE_DOWN;
				}
			} else {
				throw new IOException("SocketOutputStream error");
			}
		}
	}

	public void start() {
		if (state.equals(State.PRE)) {
			new Thread(runService).start();
			state = State.RUNNING;
		}
	}

	public void stop() {
		state = State.STOP;
	}

	public void send(byte[] data) throws IOException {
		sendData(data);
	}

	public State state() {
		return state;
	}

	public final void send(String data) throws Exception {
		if (data == null) {
			throw new IOException("send type and data connot be null");
		} else {
			sendData(data.getBytes());
		}
	}

	public abstract void beforeService();

	public abstract void afterService();

	public abstract void trigerData(byte[] data) throws Exception;

}
