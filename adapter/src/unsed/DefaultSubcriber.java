package unsed;
//package client;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import listener.ConnectionLostListener;
//import listener.MessageArrivedListener;
//
//import org.apache.log4j.Logger;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.MqttSecurityException;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//
//public class DefaultSubcriber implements Subcriber {
//
//	private static final Logger log = Logger.getLogger(DefaultSubcriber.class);
//
//	protected MqttClient client = null;
//	protected MessageArrivedListener mal = null;
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
//	static {
//		default_opt = new MqttConnectOptions();
//		default_opt.setCleanSession(false);
//		default_opt.setConnectionTimeout(10);
//		default_opt.setKeepAliveInterval(20);
//	}
//
//	private final Timer timer = new Timer();
//
//	public DefaultSubcriber(String host, String id, int peroid) {
//		try {
//			this.period = (peroid < DEFALUT_PERIOD) ? DEFALUT_PERIOD : peroid;
//			this.client = new MqttClient(host, id, new MemoryPersistence());
//		} catch (MqttException e) {
//			this.client = null;
//			e.printStackTrace();
//			throw new RuntimeException("cannot create MqttClient for host: "
//					+ host + " id: " + id);
//		}
//
//	}
//
//	public DefaultSubcriber(String host, String id) {
//		this(host, id, DEFALUT_PERIOD);
//	}
//
//	public DefaultSubcriber() {
//		this(DEFAULT_HOST, DEFAULT_ID, DEFALUT_PERIOD);
//	}
//
//	@Override
//	public void close() throws MqttException {
//		this.timer.cancel();
//		this.client.disconnect();
//		this.client.close();
//	}
//
//	public void connect() throws MqttSecurityException, MqttException {
//		this.connect(default_opt);
//	}
//
//	@Override
//	public void connect(MqttConnectOptions opt) throws MqttSecurityException,
//			MqttException {
//		this.client.setCallback(new MqttCallback() {
//
//			@Override
//			public void messageArrived(String arg0, MqttMessage arg1)
//					throws Exception {
//				if (mal != null) {
//					mal.messageArrived(arg0, arg1);
//				}
//			}
//
//			@Override
//			public void deliveryComplete(IMqttDeliveryToken arg0) {
//
//			}
//
//			@Override
//			public void connectionLost(Throwable arg0) {
//				if (cll != null) {
//					cll.connectionLost(arg0);
//				}
//			}
//		});
//		this.client.connect(opt);
//		this.unconnecttime = 0;
//		this.startHeartbeat();
//	}
//
//	@Override
//	public void disconnect() throws MqttException {
//		this.client.disconnect();
//	}
//
//	@Override
//	public boolean isConnected() {
//		return this.client.isConnected();
//	}
//
//	@Override
//	public void subcribe(String topic) throws MqttException {
//		if (!this.client.isConnected()) {
//			this.client.connect();
//		}
//		this.client.subscribe(topic);
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
//	public void addMessageArrivedListener(MessageArrivedListener mal) {
//		this.mal = mal;
//	}
//
//	@Override
//	public void addConnectionLostListener(ConnectionLostListener cll) {
//		this.cll = cll;
//	}
//
// }
