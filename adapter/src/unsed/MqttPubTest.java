package unsed;
//package mqtttest;
//
//import java.util.UUID;
//
//import listener.DeliveryCompleteListener;
//import net.sf.json.JSONObject;
//
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttException;
//
//import client.DefaultPublisher;
//import client.Publisher;
//
//public class MqttPubTest {
//
//	public static final String HOST = "tcp://114.55.92.31:1883";
//
//	public static final String ID = "server_publisher";
//
//	public static final String TOPIC = "sub_test_topic";
//
//	public static MqttClient client;
//
//	public static volatile long id = 0;
//
//	public static void main(String[] args) throws MqttException {
//		// client=new MqttClient(HOST,ID,new MemoryPersistence());
//		// MqttConnectOptions options=new MqttConnectOptions();
//		// options.setCleanSession(true);
//		// //options.setUserName(userName);
//		// //options.setPassword();
//		// options.setConnectionTimeout(10);
//		// options.setKeepAliveInterval(20);
//		// client.setCallback(new MqttCallback() {
//		//
//		// @Override
//		// public void messageArrived(String arg0, MqttMessage arg1) throws
//		// Exception {
//		// System.out.println("---connect success---");
//		// //sendMessage(client, TOPIC);
//		// }
//		//
//		// @Override
//		// public void deliveryComplete(IMqttDeliveryToken arg0) {
//		// System.out.println("---deliver success---");
//		// // TODO Auto-generated method stub
//		// }
//		//
//		// @Override
//		// public void connectionLost(Throwable arg0) {
//		// System.out.println("---connect lost---");
//		// // TODO Auto-generated method stub
//		// }
//		// });
//		//
//		// MqttTopic topic = client.getTopic(TOPIC);
//		//
//		// MqttMessage message = new MqttMessage();
//		// message.setQos(1);
//		// message.setRetained(true);
//		// System.out.println(message.isRetained()+"------ratained状态");
//		// message.setPayload("这是一个测试程序".getBytes());
//		// client.connect(options);
//		//
//		// MqttDeliveryToken token=topic.publish(message);
//		// token.waitForCompletion();
//		int max_thread_num = 1000;
//		Thread[] threads = new Thread[max_thread_num];
//		for (int i = 0; i < max_thread_num; i++) {
//			threads[i] = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Publisher pub = new DefaultPublisher(HOST, UUID
//							.randomUUID().toString(), 8000);
//					pub.addDeliveryComplete(new DeliveryCompleteListener() {
//
//						@Override
//						public void deliveryComplete(IMqttDeliveryToken arg0) {
//							// System.out.println("deliveryComplete");
//						}
//					});
//
//					try {
//						for (int i = 0; i < 10; i++) {
//							JSONObject jo = new JSONObject();
//							jo.put("id", id++);
//							jo.put("uid", UUID.randomUUID().toString());
//							jo.put("name", "小明");
//							jo.put("age", "23");
//							jo.put("gen", "男");
//							pub.publish(TOPIC, jo.toString());
//							Thread.sleep(100);
//						}
//					} catch (MqttException me) {
//						me.printStackTrace();
//					} catch (InterruptedException ie) {
//						ie.printStackTrace();
//					}
//				}
//			});
//		}
//
//		for (int i = 0; i < max_thread_num; i++) {
//			threads[i].start();
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
// }
