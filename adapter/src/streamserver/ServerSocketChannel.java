package streamserver;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import stream.ChannelProtocol;
import stream.SocketChannel;

public class ServerSocketChannel extends ChannelProtocol {

	private static final Logger log = Logger.getLogger(ServerSocketChannel.class);

	private final StreamServer serverBelongsTo;

	private final Timer timer = new Timer();

	private static final int TIMEOUT = 60000;

	private final TimerTask timeoutTask = new TimerTask() {
		public void run() {
			stop();
		}
	};

	public ServerSocketChannel(StreamServer serverBelongsTo, long id, Socket socket) throws Exception {
		super(id);
		this.serverBelongsTo = serverBelongsTo;
		channel = new SocketChannel(socket) {
			@Override
			public void beforeWork() {
				beforeService();
			}

			@Override
			public void afterWork() {
				afterService();
			}

			@Override
			public void packetIn(byte[] data) throws Exception {
				event(data);
			}
		};
		channel.start();
	}

	public void ping() {
		JSONObject jo = new JSONObject();
		jo.put("cmd", CMD.STATUS);
		try {
			mesOut(jo.toString(), true);
			// channel.sendAfter(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void beforeService() {
		System.out.println("service is starting, first set the slave id");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		id();
		timer.schedule(timeoutTask, TIMEOUT);
	}

	public void afterService() {
		System.out.println("service is stop, next remove the id");
		serverBelongsTo.deleteMaster(id);
	}

	public void stop() {
		try {
			channel.stop();
		} catch (Exception e) {
			// TODO
		}
	}

	private void id() {
		JSONObject jo = new JSONObject();
		jo.put("cmd", CMD.ID);
		jo.put("id", id);
		try {
			mesOut(jo.toString(), true);
		} catch (IOException e) {
			// TODO
		}
	}

	@Override
	protected void mes(JSONObject jo) throws IOException {
		System.out.println("MES: " + jo.toString());
	}

	@Override
	protected void info_ack(JSONObject jo) throws IOException {
		if (jo.containsKey("id")) {
			long id = (Long) jo.get("id");
			if (this.id == id) {
				serverBelongsTo.updateMaster(id);
				log.info(jo.toString());
				return;
			}
		}
		stop();
	}

	@Override
	protected void status(JSONObject jo) throws IOException {
		throw new IOException("Unsupported status in server end");
	}

	@Override
	protected void status_ack(JSONObject jo) throws IOException {
		System.out.println("status_ack: " + jo.toString());
	}

	@Override
	protected void id(JSONObject jo) throws IOException {
		throw new IOException("Unsupported id in server end");
	}

	@Override
	protected void id_ack(JSONObject jo) throws IOException {
		if (jo.containsKey("id")) {
			long id = (Long) jo.get("id");
			if (this.id == id) {
				timeoutTask.cancel();
				timer.cancel();
				serverBelongsTo.addMaster(id, this);
				return;
			}
		}
		stop();
	}

}
