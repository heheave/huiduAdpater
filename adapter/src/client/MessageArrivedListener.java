package client;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MessageArrivedListener {
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception;
}
