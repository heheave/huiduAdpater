package unsed;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public interface Subcriber {

	void close() throws MqttException;

	void connect() throws MqttSecurityException, MqttException;

	void connect(MqttConnectOptions opt) throws MqttSecurityException,
			MqttException;

	void disconnect() throws MqttException;

	boolean isConnected();

	void subcribe(String topic) throws MqttException;

	void addMessageArrivedListener(MessageArrivedListener mal);

	void addConnectionLostListener(ConnectionLostListener cll);
}
