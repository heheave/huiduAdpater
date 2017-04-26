package stream;

import stream.Channel.State;

public class StreamClient {

	private static StreamClient stream = null;

	private static final ClientSocketChanel scs = new ClientSocketChanel();

	private final String host;

	private final int port;

	private StreamClient(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		scs.initChanel(host, port);
	}

	public static StreamClient instance() {
		if (stream == null) {
			synchronized (StreamClient.class) {
				if (stream == null) {
					try {
						stream = new StreamClient("localhost", 6789);
					} catch (Exception e) {
						return null;
					}
				}
			}
		}
		return stream;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public State channelState() {
		return scs.channelState();
	}

	public void send(byte[] bytes) throws Exception {
		scs.mes(new String(bytes));
	}

	public void send(String str) throws Exception {
		scs.mes(str);
	}
	
	public static void main(String[] args) {
		StreamClient.instance();
	}
}
