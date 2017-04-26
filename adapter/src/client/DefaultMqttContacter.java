package client;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import v.V;

public class DefaultMqttContacter implements Contacter {

	private static final Logger log = Logger.getLogger(DefaultMqttContacter.class);

	protected MqttClient client = null;
	private MessageArrivedListener mal = null;
	private DeliveryCompleteListener dcl = null;
	private ConnectionLostListener cll = null;

	protected static final MqttConnectOptions default_opt;
	protected volatile MqttConnectOptions last_opt;

	private static final String DEFAULT_HOST = V.MQTT_HOST;
	private static final String DEFAULT_ID = V.MQTT_CNT_ID;

	private volatile int unconnecttime = 0;

	private volatile boolean isschedule = false;

	private static final int DEFALUT_PERIOD = V.MQTT_HEARTBEAT_CHECK_PERIOD;

	private static final int MAX_SUB_TOPIC = V.MQTT_MAX_SUB_TOPIC;

	private static final int LOG_ATFER_RETRY_TIME = V.MQTT_LOG_ATFER_RETRY_TIME;

	private final String host;
	private final String id;
	private final int period;

	private final MODE mode;
	private final boolean canPub;
	private final boolean canSub;

	private final Queue<String> topics = new ArrayBlockingQueue<String>(MAX_SUB_TOPIC);

	static {
		default_opt = new MqttConnectOptions();
		default_opt.setCleanSession(false);
		default_opt.setConnectionTimeout(10);
		default_opt.setKeepAliveInterval(20);
	}

	private final Timer timer = new Timer();

	public DefaultMqttContacter(MODE mode, String host, String id, int peroid) {
		this.mode = mode;
		this.host = host;
		this.id = id;
		this.period = (peroid < DEFALUT_PERIOD) ? DEFALUT_PERIOD : peroid;

		switch (mode) {
		case SP:
			canSub = true;
			canPub = true;
			break;
		case S:
			canSub = true;
			canPub = false;
			break;
		case P:
			canPub = true;
			canSub = false;
			break;
		default:
			canSub = false;
			canPub = false;
		}
	}

	private void initClient() {
		try {
			this.client = new MqttClient(host, id, new MemoryPersistence());
		} catch (MqttException e) {
			this.client = null;
			throw new RuntimeException("mqtt client is null");
		}
	}

	public DefaultMqttContacter(MODE mode, String host, String id) {
		this(mode, host, id, DEFALUT_PERIOD);
	}

	public DefaultMqttContacter(String host, String id) {
		this(MODE.SP, host, id);
	}

	public DefaultMqttContacter() {
		this(DEFAULT_HOST, DEFAULT_ID);
	}

	@Override
	public void close() throws MqttException {
		stopHeartbeat();
		this.disconnect();
		this.client.close();
	}

	public void connect() throws MqttSecurityException, MqttException {
		this.connect(default_opt);
	}

	@Override
	public void connect(MqttConnectOptions opt) throws MqttSecurityException, MqttException {
		if (this.client == null) {
			initClient();
		} else if (this.client.isConnected()) {
			this.client.disconnect();
		}
		if (opt == null) {
			opt = default_opt;
		}
		this.client.setCallback(new MqttCallback() {

			@Override
			public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
				if (mal != null) {
					mal.messageArrived(arg0, arg1);
				}
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {
				if (dcl != null) {
					dcl.deliveryComplete(arg0);
				}
			}

			@Override
			public void connectionLost(Throwable arg0) {
				if (cll != null) {
					cll.connectionLost(arg0);
				}
			}
		});

		this.client.connect(opt);
		last_opt = opt;
		this.unconnecttime = 0;
		this.startHeartbeat();
	}

	@Override
	public void disconnect() throws MqttException {
		this.client.disconnect();
		this.last_opt = null;
	}

	@Override
	public boolean isConnected() {
		return this.client.isConnected();
	}

	@Override
	public void setTimeout(long time) {
		this.client.setTimeToWait(time);
	}

	@Override
	public void subcribe(String topic) throws IllegalAccessException, MqttException {
		if (!canSub) {
			throw new IllegalAccessException("Can't sub topic '" + topic + "' under mode " + mode);
		}
		if (!this.client.isConnected()) {
			this.client.connect();
		}
		this.client.subscribe(topic);
		if (!this.topics.offer(topic)) {
			log.info("Only support subscribe max 10 topics");
		}
	}

	@Override
	public void publish(String topic, String msg)
			throws IllegalAccessException, MqttException, MqttPersistenceException {
		if (!canSub) {
			throw new IllegalAccessException("Can't pub mes '" + msg + "' to topic '" + topic + "' under mode " + mode);
		}
		if (!this.client.isConnected()) {
			this.client.connect();
		}
		MqttMessage mm = new MqttMessage();
		mm.setPayload(msg.getBytes());
		this.client.publish(topic, mm);
	}

	private void startHeartbeat() {
		if (!isschedule) {
			isschedule = true;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (isschedule && client != null && !client.isConnected() && last_opt != null) {
						try {
							client.connect(last_opt);
							for (String topic : topics) {
								client.subscribe(topic);
							}
						} catch (MqttException e) {
							e.printStackTrace();
							unconnecttime++;
							if (unconnecttime >= LOG_ATFER_RETRY_TIME) {
								log.error("try to reconnect mqtt brocker failed " + unconnecttime + " times");
								unconnecttime = 0;
							}
						}
					}
				}
			}, 1000, period);
		}
	}

	private void stopHeartbeat() {
		if (isschedule) {
			timer.cancel();
		}
		unconnecttime = 0;
		isschedule = false;
	}

	@Override
	public void addMessageArrivedListener(MessageArrivedListener mal) {
		if (canSub)
			this.mal = mal;
	}

	@Override
	public void addConnectionLostListener(ConnectionLostListener cll) {
		this.cll = cll;
	}

	@Override
	public void addDeliveryComplete(DeliveryCompleteListener dcl) {
		if (canPub)
			this.dcl = dcl;
	}

	@Override
	public void removeMessageArrivedListener() {
		this.mal = null;
	}

	@Override
	public void removeDeliveryComplete() {
		this.dcl = null;
	}

	@Override
	public void removeConnectionLostListener() {
		this.cll = null;
	}

}
