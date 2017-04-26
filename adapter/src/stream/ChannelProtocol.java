package stream;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import stream.Channel.State;

public abstract class ChannelProtocol {

	protected static final class CMD {
		public static final String ID = "ID";
		public static final String ID_ACK = "ID_ACK";
		public static final String STATUS = "STATUS";
		public static final String STATUS_ACK = "STATUS_ACK";
		public static final String INFO_ACK = "INFO_ACK";
		public static final String MES = "MES";
	}

	private static final Logger log = Logger.getLogger(ChannelProtocol.class);

	protected long id;

	protected Channel channel = null;

	// private static final Map<String, Method> nameToMethod = new
	// HashMap<String, Method>();
	//
	// static {
	// for (Method m : ChannelProtocol.class.getDeclaredMethods()) {
	// if (m.isAnnotationPresent(ChannelMethodAnnotation.class)) {
	// ChannelMethodAnnotation cna =
	// m.getAnnotation(ChannelMethodAnnotation.class);
	// nameToMethod.put(cna.name(), m);
	// }
	// }
	// }

	protected ChannelProtocol(long id) {
		this.id = id;
	}

	protected ChannelProtocol() {
		this(-1);
	}

	public State channelState() {
		return channel.state();
	}

	public void event(byte[] data) {
		JSONObject jo = JSONObject.fromObject(new String(data));
		System.out.println(jo.toString());
		String cmd = null;
		if (jo.containsKey("cmd")) {
			cmd = jo.getString("cmd");
		}
		try {
			trueEventTackle(cmd, jo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// this can gather the protocol methods in a class, but may cause more
	// overheads
	protected final void trueEventTackle(String cmd, JSONObject mes) throws IOException {
		// Method m = null;
		// if (cmd == null || !nameToMethod.containsKey(cmd)) {
		// m = nameToMethod.get(ChannelMethodAnnotation.UNKNOWN);
		// } else {
		// m = nameToMethod.get(cmd);
		// }
		// try {
		// m.invoke(this, mes);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		if (CMD.MES.equals(cmd)) {
			mes(mes);
		} else if (CMD.INFO_ACK.equals(cmd)) {
			info_ack(mes);
		} else if (CMD.STATUS.equals(cmd)) {
			status(mes);
		} else if (CMD.STATUS_ACK.equals(cmd)) {
			status_ack(mes);
		} else if (CMD.ID.equals(cmd)) {
			id(mes);
		} else if (CMD.ID_ACK.equals(CMD.ID_ACK)) {
			id_ack(mes);
		} else {
			error(mes);
		}
	}

	public void mes(String mes) throws IOException {
		JSONObject jo = new JSONObject();
		jo.put("cmd", CMD.MES);
		jo.put("id", id);
		jo.put("data", mes);
		mesOut(jo.toString(), true);
	}

	public void mesOut(String data, boolean immediate) throws IOException {
		if (data == null) {
			throw new IOException("Cannot send null data");
		} else if (id <= 0) {
			throw new IOException("SocketChannel was still not estabilshed");
		}

		if (immediate) {
			channel.send(data);
		} else {
			channel.sendAfter(data);
		}
	}

	public void mesOut(String str) throws IOException {
		mesOut(str, true);
	}

	public void mesBatchOut(Collection<String> strs) throws IOException {
		for (String str : strs)
			mesOut(str, false);
	}

	@ChannelMethodAnnotation
	protected final void error(JSONObject mes) {
		log.warn("unsupport mes: " + mes.toString());
	}

	@ChannelMethodAnnotation(name = "MES")
	protected abstract void mes(JSONObject mes) throws IOException;

	@ChannelMethodAnnotation(name = "INFO_ACK")
	protected abstract void info_ack(JSONObject mes) throws IOException;

	@ChannelMethodAnnotation(name = "STATUS")
	protected abstract void status(JSONObject mes) throws IOException;

	@ChannelMethodAnnotation(name = "STATUS_ACK")
	protected abstract void status_ack(JSONObject mes) throws IOException;

	@ChannelMethodAnnotation(name = "ID")
	protected abstract void id(JSONObject mes) throws IOException;

	@ChannelMethodAnnotation(name = "ID_ACK")
	protected abstract void id_ack(JSONObject mes) throws IOException;

}
