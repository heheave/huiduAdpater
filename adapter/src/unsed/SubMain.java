package unsed;
//package main;
//
//import java.util.Map;
//
//import listener.MessageArrivedListener;
//
//import org.apache.log4j.Logger;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//import bus.Bus;
//import bus.EventBusContext;
//import pipeline.EventPipelinesWithCache;
//import unsed.Subcriber;
//import client.Contacter;
//import client.DefaultMqttContacter;
//import config.V;
//import event.PacketEvent;
//import event.Event.CODE;
//import eventhandler.EventClassifyHandler;
//import eventhandler.EventDBHandler;
//import eventhandler.EventDropHandler;
//import eventhandler.EventHandler;
//import eventhandler.StateEventHandler;
//
//public class SubMain implements ClientMain {
//
//	private static final Logger log = Logger.getLogger(SubMain.class);
//
//	private static final String DEFAULT_CONFIG_FILE_PATH = "file/conf.xml";
//
//	private static final String DEFAULT_HOST = "tcp://114.55.92.31:1883";
//	private static final String DEFAULT_ID = "default_client_subcriber";
//	private static final String DEFAULT_TOPIC = "sub_test_topic";
//
//	private static Bus bus = null;
//
//	private static StateEventHandler pipes = null;
//
//	private static StateEventHandler finisher = null;
//	private static EventHandler droper = null;
//	private static EventHandler classifier = null;
//
//	private static Contacter sub = null;
//
//	private static Map<String, String> RUN_PARA;
//
//	public SubMain() {
//		RUN_PARA = null;
//	}
//
//	public SubMain(Map<String, String> rp) {
//		RUN_PARA = rp;
//	}
//
//	@Override
//	public void start() throws Exception {
//		if (RUN_PARA == null) {
//			throw new RuntimeException(
//					"start sub main parameter should be null");
//		}
//		bus = new EventBusContext();
//		pipes = new EventPipelinesWithCache<PacketEvent>();
//		finisher = new EventDBHandler();
//		droper = new EventDropHandler();
//		classifier = new EventClassifyHandler();
//
//		String conf_path = RUN_PARA.containsKey(V.CONF) ? RUN_PARA.get(V.CONF)
//				: null;
//		if (conf_path == null) {
//			log.info("conf path is unspecified so use the default conf path para: "
//					+ DEFAULT_CONFIG_FILE_PATH);
//			conf_path = DEFAULT_CONFIG_FILE_PATH;
//		}
//		log.info("config file path is: " + conf_path);
//		// context.init(DefaultConfig.getInstance(conf_path));
//
//		pipes.initSet(V.CONF_FILE, conf_path);
//
//		String host = RUN_PARA.containsKey(V.HOST) ? RUN_PARA.get(V.HOST)
//				: null;
//		if (host == null) {
//			log.info("host is unspecified so use the default host para: "
//					+ DEFAULT_HOST);
//			host = DEFAULT_HOST;
//		}
//
//		String id = RUN_PARA.containsKey(V.ID) ? RUN_PARA.get(V.ID) : null;
//		if (id == null) {
//			log.info("id is unspecified so use the default id para: "
//					+ DEFAULT_ID);
//			id = DEFAULT_ID;
//		}
//
//		String topic = RUN_PARA.containsKey(V.TOPIC) ? RUN_PARA.get(V.TOPIC)
//				: null;
//		if (topic == null) {
//			log.info("topic is unspecified so use the default topic para: "
//					+ DEFAULT_TOPIC);
//			topic = DEFAULT_TOPIC;
//		}
//
//		String timerStr = RUN_PARA.containsKey(V.TIMER) ? RUN_PARA.get(V.TIMER)
//				: null;
//		log.info("timer:  ----------------    " + timerStr);
//		int timer;
//
//		try {
//			timer = Integer.parseInt(timerStr);
//		} catch (Exception e) {
//			log.info("timer para cannot be casted to int so use the default timer");
//			timer = 0;
//		}
//
//		sub = new DefaultMqttContacter(host, id, timer);
//		pipes.setBelongsTo(bus);
//		finisher.setBelongsTo(bus);
//		bus.register(CODE.IN, pipes);
//		bus.register(CODE.OUT, finisher);
//		bus.register(CODE.DROP, droper);
//		bus.register(CODE.PRE, classifier);
//
//		sub.addMessageArrivedListener(new MessageArrivedListener() {
//			@Override
//			public void messageArrived(String arg0, MqttMessage arg1)
//					throws Exception {
//				System.out.println(arg0);
//				byte[] message = arg1.getPayload();
//				PacketEvent pe = new PacketEvent(CODE.PRE);
//				pe.setTopic(arg0);
//				pe.setData(message);
//				bus.putEvent(pe);
//			}
//		});
//
//		bus.start();
//		finisher.start();
//		pipes.start();
//
//		sub.connect();
//		sub.connect();
//		// sub.subcribe(topic);
//		sub.subcribe("+/+/+");
//
//	}
//
//	@Override
//	public void stop() {
//		try {
//			sub.close();
//		} catch (Exception e) {
//			log.error("CLOSE MQTT ERROR", e);
//		}
//		pipes.stop();
//		finisher.stop();
//		bus.stop();
//	}
//
//	@Override
//	public void setPara(Map<String, String> paras) {
//		RUN_PARA = paras;
//	}
//
//}
