package stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cache.deviceoninfo.DeviceOnCache;
import net.sf.json.JSONObject;

public class ClientSocketChanel extends ChannelProtocol {

	protected final void initChanel(String host, int port) throws Exception {
		channel = new SocketChannel(host, port) {

			@Override
			public void beforeWork() {
				System.out.println("Socket channel is beginning to work");
			}

			@Override
			public void afterWork() {
				System.out.println("Socket channel is ending to work");
			}

			@Override
			public void packetIn(byte[] data) throws Exception {
				event(data);
			}
		};
		channel.start();
		// //For Test
		// new Thread() {
		// public void run() {
		// try {
		// System.out.println("begin to
		// sleep--------------------------------------------------------------");
		// Thread.sleep(30000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(
		// "after sleep-------------------------------stop the
		// channel-------------------------------");
		// ((SocketChannel) channel).testStop();
		// }
		// }.start();
	}

	@Override
	protected void id(JSONObject mes) throws IOException {
		long id = -1;
		if (mes.containsKey("id")) {
			id = (Long) mes.get("id");
		}
		this.id = id;
		JSONObject jo = new JSONObject();
		jo.put("cmd", CMD.ID_ACK);
		jo.put("id", this.id);
		mesOut(jo.toString(), true);
	}

	@Override
	protected void mes(JSONObject mes) throws IOException {
		throw new IOException("Unsupported mes in client end");
	}

	@Override
	protected void info_ack(JSONObject mes) throws IOException {
		throw new IOException("Unsupported info_ack in client end");
	}

	@Override
	protected void status(JSONObject mes) throws IOException {
		JSONObject jo = new JSONObject();
		jo.put("cmd", CMD.STATUS_ACK);
		jo.put("id", id);
		jo.put("status", "OK");
		mesOut(jo.toString(), true);
		DeviceOnCache doc = DeviceOnCache.instance();
		List<String> infos = new ArrayList<String>();
		for (String di : doc.onInfos()) {
			System.out.println(di);
			jo = new JSONObject();
			jo.put("cmd", CMD.INFO_ACK);
			jo.put("id", id);
			jo.put("info", di);
			infos.add(jo.toString());
		}
		mesBatchOut(infos);
	}

	@Override
	protected void status_ack(JSONObject mes) throws IOException {
		throw new IOException("Unsupported status_ack in client end");
	}

	@Override
	protected void id_ack(JSONObject mes) throws IOException {
		throw new IOException("Unsupported id_ack in client end");
	}

}
