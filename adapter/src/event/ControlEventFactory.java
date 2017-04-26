package event;

import event.Event.CODE;

public final class ControlEventFactory {

	public static ControlEvent getRequestConfControlEvent(String deviceType, String deviceId) {
		ControlEvent ce = null;
		if (deviceType != null && deviceId != null) {
			String topic = "Witium/DistrIO/" + deviceType + "/" + deviceId + "/CTRL";
			String mes = "{\"m\":-1,\"r\":1}";
			ce = new ControlEvent("reqconf", CODE.NONE);
			ce.setTopic(topic);
			ce.setData(mes.getBytes());
		}
		return ce;
	}

}
