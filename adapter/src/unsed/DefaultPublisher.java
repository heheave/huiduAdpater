package unsed;
//package client;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import listener.ConnectionLostListener;
//import listener.DeliveryCompleteListener;
//
//import org.apache.log4j.Logger;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
//import org.eclipse.paho.client.mqttv3.MqttSecurityException;
//import org.eclipse.paho.client.mqttv3.MqttTopic;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//
//import bus.Bus;
//import util.IDGenUtil;
//import event.Event;
//import event.PacketEvent;
//import eventhandler.EventHandler;
//
//public class DefaultPublisher implements Publisher, EventHandler {
//
//	private static final Logger log = Logger.getLogger(DefaultPublisher.class);
//
//	protected MqttClient client = null;
//	protected DeliveryCompleteListener dcl = null;
//	protected ConnectionLostListener cll = null;
//
//	protected static final MqttConnectOptions default_opt;
//
//	private static final String DEFAULT_HOST = "tcp://114.55.92.31:1883";
//	private static final String DEFAULT_ID = "default_client_subcriber";
//
//	private volatile int unconnecttime = 0;
//
//	private static final int DEFALUT_PERIOD = 8000;
//
//	private int period;
//
//	private final int id;
//
//	private final String name;
//
//	private final String desc;
//
//	static {
//		default_opt = new MqttConnectOptions();
//		default_opt.setCleanSession(false);
//		default_opt.setConnectionTimeout(10);
//		default_opt.setKeepAliveInterval(20);
//	}
//
//	private final Timer timer = new Timer();
//
//	public DefaultPublisher(int id, String name, String desc, String host,
//			String mid, int peroid) {
//		this.id = id;
//		this.name = name;
//		this.desc = desc;
//		try {
//			this.period = (peroid < DEFALUT_PERIOD) ? DEFALUT_PERIOD : peroid;
//			this.client = new MqttClient(host, mid, new MemoryPersistence());
//		} catch (MqttException e) {
//			this.client = null;
//			e.printStackTrace();
//			throw new RuntimeException("cannot create MqttClient for host: "
//					+ host + " id: " + id);
//		}
//	}
//
//	public DefaultPublisher(String host, String id, int peroid) {
//		this(IDGenUtil.getId(), "DefaultPublisher",
//				"This is pub handler, pub the packet to broker", host, id,
//				peroid);
//	}
//
//	public DefaultPublisher() {
//		this(DEFAULT_HOST, DEFAULT_ID, DEFALUT_PERIOD);
//	}
//
//	@Override
//	public int id() {
//		return this.id;
//	}
//
//	@Override
//	public String name() {
//		return this.name;
//	}
//
//	@Override
//	public String desc() {
//		return this.desc;
//	}
//
//	@Override
//	public void connect(MqttConnectOptions arg0) throws MqttSecurityException,
//			MqttException {
//		this.client.setCallback(new MqttCallback() {
//
//			@Override
//			public void messageArrived(String arg0, MqttMessage arg1)
//					throws Exception {
//
//			}
//
//			@Override
//			public void deliveryComplete(IMqttDeliveryToken arg0) {
//				if (dcl != null) {
//					dcl.deliveryComplete(arg0);
//				}
//			}
//
//			@Override
//			public void connectionLost(Throwable arg0) {
//				if (cll != null) {
//					cll.connectionLost(arg0);
//				}
//			}
//		});
//		this.client.connect(arg0);
//		this.unconnecttime = 0;
//		this.startHeartbeat();
//	}
//
//	public void connect() throws MqttSecurityException, MqttException {
//		this.connect(default_opt);
//		this.startHeartbeat();
//	}
//
//	@Override
//	public void disconnect() throws MqttException {
//		this.timer.cancel();
//		this.client.disconnect();
//		this.client.close();
//	}
//
//	@Override
//	public boolean isConnected() {
//		return this.client.isConnected();
//	}
//
//	@Override
//	public void close() throws MqttException {
//		this.client.close();
//	}
//
//	@Override
//	public void setTimeout(long time) {
//		this.client.setTimeToWait(time);
//	}
//
//	@Override
//	public void publish(String topic, String msg) throws MqttException,
//			MqttPersistenceException {
//		MqttMessage mqtt_msg = new MqttMessage();
//		mqtt_msg.setPayload(msg.getBytes());
//		mqtt_msg.setQos(1);
//		mqtt_msg.setRetained(false);
//		if (!this.client.isConnected()) {
//			this.client.connect();
//		}
//		MqttTopic mt_topic = client.getTopic(topic);
//		MqttDeliveryToken token = mt_topic.publish(mqtt_msg);
//		token.waitForCompletion(1000);
//	}
//
//	@Override
//	public void addDeliveryComplete(DeliveryCompleteListener dcl) {
//		this.dcl = dcl;
//
//	}
//
//	@Override
//	public void addConnectionLostListener(ConnectionLostListener cll) {
//		this.cll = cll;
//	}
//
//	private void startHeartbeat() {
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if (client != null && !client.isConnected()) {
//					try {
//						client.connect();
//					} catch (MqttException e) {
//						unconnecttime++;
//						if (unconnecttime >= 3) {
//							log.error("try to reconnect mqtt brocker failed "
//									+ unconnecttime + " times");
//							unconnecttime = 0;
//						}
//					}
//				}
//			}
//		}, 1000, period);
//	}
//
//	@Override
//	public void handlerEvent(Bus bus, Event event) {
//		PacketEvent pe = (PacketEvent) event;
//		String topic = pe.getTopic();
//		log.info(topic);
//		try {
//			this.publish(topic, pe.getDataString());
//		} catch (Exception e) {
//			log.error("MQTT PUB ERROR", e);
//		}
//	}
//
//}
