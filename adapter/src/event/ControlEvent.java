package event;

public class ControlEvent implements Event {

	public static final String NULL_CTRLID = "-1";

	private final String ctrlId;

	private final String cmd;

	private String topic;

	private CODE code;

	private String status;

	private byte[] data;

	public ControlEvent(String ctrlId, String cmd, CODE code) {
		this.ctrlId = ctrlId;
		this.cmd = cmd;
		this.code = code;
	}

	public ControlEvent(String cmd, CODE code) {
		this(NULL_CTRLID, cmd, code);
	}

	public ControlEvent(String ctrlId, String cmd) {
		this(ctrlId, cmd, CODE.NONE);
	}

	public ControlEvent() {
		this(null, CODE.NONE);
	}

	public String getCtrlId() {
		return ctrlId;

	}

	public String getCmd() {
		return cmd;

	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public CODE getCode() {
		return this.code;
	}

	@Override
	public void changeCode(CODE code) {
		this.code = code;
	}

	@Override
	public int getPipeId() {
		return -1;
	}

	@Override
	public void setPipeId(int pipeId) {
		// TODO
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setData(String str) {
		if (str == null) {
			this.data = null;
		} else {
			this.data = str.getBytes();
		}
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getDataString() {
		if (data != null) {
			return new String(this.data);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return ctrlId + "(" + cmd + ")" + " : " + getDataString();
	}
}
