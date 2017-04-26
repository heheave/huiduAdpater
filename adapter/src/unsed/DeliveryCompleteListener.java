package unsed;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public interface DeliveryCompleteListener {
	public void deliveryComplete(IMqttDeliveryToken arg0);
}
