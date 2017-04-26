package unsed;
//package main;
//
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//
//import bus.Bus;
//import bus.EventBusContext;
//import pipeline.AbstractEventPipelines;
//import pipeline.EventPipelinesWithoutCache;
//import rmi.RemoteControlServer;
//import unsed.Publisher;
//import client.Contacter;
//import client.DefaultMqttContacter;
//import config.V;
//import event.Event;
//import event.PacketEvent;
//import eventhandler.EventHandler;
//import eventhandler.StateEventHandler;
//
//public class PubMain implements ClientMain {
//
//	private static final int CONFIG_ERROR = -1;
//
//	private static final String DEFAULT_CONFIG_FILE_PATH = "file/conf_pub.xml";
//
//	private static final String DEFAULT_HOST = "tcp://114.55.92.31:1883";
//	private static final int DEFAULT_PORT = 9999;
//	private static final String DEFAULT_ID = "default_client_subcriber";
//	private static final int DEFAULT_TIMER = 8000;
//
//	private static final Logger log = Logger.getLogger(PubMain.class);
//
//	private static Bus bus = null;
//	private static StateEventHandler ep = null;
//	private static Contacter pub = null;
//	private static RemoteControlServer remoteSvr = null;
//
//	private static Map<String, String> RUN_PARA;
//
//	public PubMain() {
//		RUN_PARA = null;
//	}
//
//	public PubMain(Map<String, String> rp) {
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
//		ep = new EventPipelinesWithoutCache<PacketEvent>();
//		ep.setBelongsTo(bus);
//		try {
//			String conf_path = RUN_PARA.containsKey(V.CONF) ? RUN_PARA
//					.get(V.CONF) : null;
//			if (conf_path == null) {
//				conf_path = DEFAULT_CONFIG_FILE_PATH;
//			}
//			log.info("config file path is: " + conf_path);
//			ep.initSet(V.CONF_FILE, conf_path);
//		} catch (Exception e) {
//			log.error("init context error check your config file", e);
//			System.exit(CONFIG_ERROR);
//		}
//
//		String host = RUN_PARA.containsKey(V.HOST) ? RUN_PARA.get(V.HOST)
//				: null;
//		if (host == null) {
//			host = DEFAULT_HOST;
//		}
//
//		String id = RUN_PARA.containsKey(V.ID) ? RUN_PARA.get(V.ID) : null;
//		if (id == null) {
//			id = DEFAULT_ID;
//		}
//
//		String timerStr = RUN_PARA.containsKey(V.TIMER) ? RUN_PARA.get(V.TIMER)
//				: null;
//		int timer;
//		if (timerStr == null) {
//			timer = DEFAULT_TIMER;
//		} else {
//			try {
//				timer = Integer.parseInt(timerStr);
//			} catch (Exception e) {
//				log.info("your timer para cannot be casted to int so use the default timer: "
//						+ DEFAULT_TIMER);
//				timer = DEFAULT_TIMER;
//			}
//		}
//
//		String portStr = RUN_PARA.containsKey(V.PORT) ? RUN_PARA.get(V.PORT)
//				: null;
//		int port;
//		if (portStr == null) {
//			port = DEFAULT_PORT;
//		} else {
//			try {
//				port = Integer.parseInt(portStr);
//			} catch (Exception e) {
//				log.info("your port para cannot be casted to int so use the default port: "
//						+ DEFAULT_PORT);
//				port = DEFAULT_PORT;
//			}
//		}
//		System.out.println(port);
//
//		pub = new DefaultMqttContacter(host, id, timer);
//		remoteSvr = new RemoteControlServer(bus, pub);
//
//		Registry registry = createRegistry(port);
//		registry.rebind(V.REMOTE_SERVER, remoteSvr);
//
//		bus.register(Event.CODE.IN, ep);
//		bus.register(Event.CODE.OUT, (EventHandler) pub);
//
//		bus.start();
//		pub.connect();
//		ep.start();
//		remoteSvr.start();
//
//	}
//
//	@Override
//	public void stop() throws Exception {
//		remoteSvr.stop();
//		ep.stop();
//		pub.close();
//		bus.stop();
//	}
//
//	@Override
//	public void setPara(Map<String, String> paras) {
//		RUN_PARA = paras;
//	}
//
//	private static Registry createRegistry(int port) {
//		Registry registry = null;
//		try {
//			registry = LocateRegistry.getRegistry(port);
//			registry.list();
//		} catch (final Exception e) {
//			try {
//				registry = LocateRegistry.createRegistry(port);
//			} catch (final Exception ee) {
//				ee.printStackTrace();
//			}
//		}
//		return registry;
//	}
// }
