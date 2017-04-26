package unsed;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public interface Publisher {

	void close() throws MqttException;

	void connect() throws MqttSecurityException, MqttException;

	void connect(MqttConnectOptions opt) throws MqttSecurityException,
			MqttException;

	void disconnect() throws MqttException;

	boolean isConnected();

	void setTimeout(long time);

	void publish(String topic, String msg) throws MqttException,
			MqttPersistenceException;

	void addDeliveryComplete(DeliveryCompleteListener dcl);

	void addConnectionLostListener(ConnectionLostListener cll);
}
