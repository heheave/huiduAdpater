package stream;

import java.io.IOException;

public interface Channel {

	public static enum State {
		PRE, RUNNING, READ_DOWN, WRITE_DOWN, STOP
	};

	void start() throws Exception;

	void stop() throws Exception;

	State state();

	void beforeWork();

	void afterWork();

	void packetIn(byte[] data) throws Exception;

	void send(String data) throws IOException;

	void send(byte[] data) throws IOException;

	void sendAfter(String data);

	void sendAfter(byte[] data);
}
