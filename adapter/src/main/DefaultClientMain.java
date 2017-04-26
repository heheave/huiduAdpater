package main;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import bus.Bus;
import bus.SychBusContext;
import bus.AsychBusContext;
import bus.bushandler.BusChangeBusHandler;
import bus.bushandler.CtrlCheckBusHandler;
import bus.bushandler.DataClassifyBusHandler;
import bus.bushandler.MesDropBusHandler;
import bus.bushandler.BusHandler;
import bus.bushandler.CacheAccessorBusHandler;
import bus.bushandler.DataPersistBusHandler;
import bus.bushandler.DataPubBusHandler;
import bus.bushandler.RCRetBusHandler;
import bus.bushandler.StateBusHandler;
import cache.channelinfo.ChannelInfoCache;
import cache.deviceoninfo.DeviceOnCache;
import rmi.RemoteControlListener;
import rmi.Server;
import socketserver.SocketServer;
import v.V;
import client.Contacter;
import client.DefaultMqttContacter;
import client.MessageArrivedListener;
import event.ControlEvent;
import event.Event.CODE;
import event.PacketEvent;
import pipe.PipesAsychBusHandler;
import pipe.PipesSychBusHandler;

public class DefaultClientMain implements ClientMain {

	private static final Logger log = Logger.getLogger(DefaultClientMain.class);

	private static final String DEFAULT_CONFIG_FILE_PATH = V.PIPELINE_HANDLER_INIT_FILE;

	private static final Contacter.MODE DEFAULT_MODE = Contacter.MODE.SP;
	private static final String DEFAULT_HOST = V.MQTT_HOST;
	private static final String DEFAULT_ID = UUID.randomUUID().toString().substring(0, 10);
	// private static final String DEFAULT_TOPIC = "client_default_topic";

	private static final int DEFAULT_PORT = 9999;

	private static Bus busEvent = null;
	private static Bus busCntl = null;

	private static StateBusHandler uppipes = null;
	private static StateBusHandler downpipes = null;

	private static StateBusHandler finisher = null;
	private static BusHandler droper = null;
	private static BusHandler classifier = null;

	private static BusHandler contactor = null;
	private static BusHandler remotecaller = null;
	private static BusHandler checker = null;
	// private static BusHandler configer = null;
	private static BusHandler cacheReader = null;
	private static BusHandler tapper = null;

	private static Contacter client = null;
	private static Server server = null;

	private static Map<String, String> RUN_PARA;

	public DefaultClientMain() {
		RUN_PARA = null;
	}

	public DefaultClientMain(Map<String, String> rp) {
		RUN_PARA = rp;
	}

	@Override
	public void start() throws Exception {
		if (RUN_PARA == null) {
			throw new RuntimeException("start sub main parameter should be null");
		}
		// context.init(DefaultConfig.getInstance(conf_path))

		String mode = RUN_PARA.containsKey(V.MODE) ? RUN_PARA.get(V.MODE) : null;
		Contacter.MODE MODE;
		if (mode == null) {
			log.info("mode is unspecified so use the default mode para: " + DEFAULT_MODE);
			MODE = DEFAULT_MODE;
		} else {
			if ("s".equals(mode) || "S".equals(mode)) {
				MODE = Contacter.MODE.S;
			} else if ("p".equals(mode) || "P".equals(mode)) {
				MODE = Contacter.MODE.P;
			} else if ("sp".equals(mode) || "SP".equals(mode)) {
				MODE = Contacter.MODE.SP;
			} else {
				throw new RuntimeException("illegal mqtt client mode: " + mode);
			}
		}

		String host = RUN_PARA.containsKey(V.HOST) ? RUN_PARA.get(V.HOST) : null;
		if (host == null) {
			log.info("host is unspecified so use the default host para: " + DEFAULT_HOST);
			host = DEFAULT_HOST;
		}

		String id = RUN_PARA.containsKey(V.ID) ? RUN_PARA.get(V.ID) : null;
		if (id == null) {
			log.info("id is unspecified so use the default id para: " + DEFAULT_ID);
			id = DEFAULT_ID;
		}

		String topic = RUN_PARA.containsKey(V.TOPIC) ? RUN_PARA.get(V.TOPIC) : null;
		if (topic == null) {
			throw new IllegalArgumentException("should specify at least a topic to sub");
		}
		String[] topics = topic.split(",|\\|");

		String conf_path = RUN_PARA.containsKey(V.CONF) ? RUN_PARA.get(V.CONF) : null;
		if (conf_path == null) {
			log.info("conf path is unspecified so use the default conf path para: " + DEFAULT_CONFIG_FILE_PATH);
			conf_path = DEFAULT_CONFIG_FILE_PATH;
		}

		String timerStr = RUN_PARA.containsKey(V.TIMER) ? RUN_PARA.get(V.TIMER) : null;
		int timer;

		try {
			timer = Integer.parseInt(timerStr);
		} catch (Exception e) {
			log.info("timer para is unspecified or cannot be casted to int so use the default timer");
			timer = 0;
		}

		String portStr = RUN_PARA.containsKey(V.PORT) ? RUN_PARA.get(V.PORT) : null;
		int port;
		try {
			port = Integer.parseInt(portStr);
		} catch (Exception e) {
			log.info("port para is unspecified or cannot be casted to int so use the default port: " + DEFAULT_PORT);
			port = DEFAULT_PORT;
		}

		// init all components
		// server = new RemoteControlServer(port);
		server = new SocketServer(port);
		client = new DefaultMqttContacter(MODE, host, id, timer);
		busEvent = new AsychBusContext();
		busCntl = new SychBusContext();
		uppipes = new PipesAsychBusHandler<PacketEvent>();
		downpipes = new PipesSychBusHandler<ControlEvent>();
		finisher = new DataPersistBusHandler();
		droper = new MesDropBusHandler();
		classifier = new DataClassifyBusHandler();
		contactor = new DataPubBusHandler();
		remotecaller = new RCRetBusHandler();
		checker = new CtrlCheckBusHandler();
		// configer = new ConfCacheBusHandler();
		cacheReader = new CacheAccessorBusHandler();
		tapper = new BusChangeBusHandler();
		// add listener
		server.setListener(new RemoteControlListener() {

			@Override
			public void cmd(String cmdId, String cmdType, String massage) {
				ControlEvent ce = new ControlEvent(cmdId, cmdType);
				ce.setData(massage.getBytes());
				ce.changeCode(CODE.PRE);
				busCntl.putEvent(ce);
			}
		});

		client.addMessageArrivedListener(new MessageArrivedListener() {
			@Override
			public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
				byte[] message = arg1.getPayload();
				PacketEvent pe = new PacketEvent(CODE.PRE);
				pe.setTopic(arg0);
				pe.setTempData(message);
				busEvent.putEvent(pe);
			}
		});

		// init set
		remotecaller.initSet(V.REMOTECALL, server);
		contactor.initSet(V.CONTACTOR, client);
		uppipes.initSet(V.CONF_FILE, conf_path);
		downpipes.initSet(V.CONF_FILE, conf_path);
		tapper.initSet("first", busEvent);
		tapper.initSet("second", busCntl);

		// setBelongs
		uppipes.setBusOn(busEvent);
		finisher.setBusOn(busEvent);
		downpipes.setBusOn(busCntl);

		// register
		busEvent.register(CODE.IN, uppipes);
		busEvent.register(CODE.OUT, finisher);
		busEvent.register(CODE.DROP, droper);
		busEvent.register(CODE.PRE, classifier);

		busCntl.register(CODE.IN, downpipes);
		busCntl.register(CODE.PRE, checker);
		busCntl.register(CODE.OUT, contactor);
		busCntl.register(CODE.RC_RET, remotecaller);
		busCntl.register(CODE.DROP, remotecaller);
		// busCntl.register(CODE.GET, configer);
		busCntl.register(CODE.GET, cacheReader);

		busEvent.register(CODE.CROSS, tapper);
		busCntl.register(CODE.CROSS, tapper);

		// start
		busEvent.start();
		busCntl.start();
		finisher.start();
		uppipes.start();
		downpipes.start();
		server.start();

		client.connect();

		// make sure the singleton object has initialized firstly
		ChannelInfoCache.instance();
		DeviceOnCache.instance();

		for (String t : topics) {
			if (!t.trim().isEmpty()) {
				client.subcribe(t);
			}
		}
		log.info("starting program success...");
	}

	@Override
	public void stop() throws Exception {
		try {
			if (client != null)
				client.close();
		} catch (Exception e) {
			log.error("CLOSE MQTT ERROR", e);
		}
		if (server != null)
			server.stop();
		if (downpipes != null)
			downpipes.stop();
		if (uppipes != null)
			uppipes.stop();
		if (finisher != null)
			finisher.stop();
		if (busCntl != null)
			busCntl.stop();
		if (busEvent != null)
			busEvent.stop();

		// make sure the singleton object
		if (DeviceOnCache.instance() != null)
			DeviceOnCache.instance().save();

	}

	@Override
	public void setPara(Map<String, String> paras) {
		RUN_PARA = paras;
	}

	// private static Registry createRegistry(int port) {
	// Registry registry = null;
	// try {
	// registry = LocateRegistry.getRegistry(port);
	// registry.list();
	// } catch (final Exception e) {
	// try {
	// registry = LocateRegistry.createRegistry(port);
	// } catch (final Exception ee) {
	// ee.printStackTrace();
	// }
	// }
	// return registry;
	// }

	// TODO just for gongbohui
	// private void getAllDeviceConf() {
	// String[] deviceType_sense1 = { "678C", "678C", "618X", "618X", "614P" };
	// String[] deviceId_sense1 = { "16091076", "16091078", "16091086",
	// "16091087", "16091046" };
	// int[] _666Conf = { 0, 0, 0, 0, 0, 0, 0, 0 };
	// ConfCache.instance().updateConf("16091068", "666C", 0, _666Conf);
	// ConfCache.instance().updateConf("16091069", "666C", 0, _666Conf);
	// String mes = "{\"m\":-1,\"r\":1}";
	// for (int i = 0; i < deviceType_sense1.length; i++) {
	// String topic = "Witium/DistrIO/" + deviceType_sense1[i] + "/" +
	// deviceId_sense1[i] + "/CTRL";
	// log.info("topic: " + topic);
	// for (int t = 0; t < 3; t++) {
	// try {
	// client.publish(topic, mes);
	// break;
	// } catch (Exception e) {
	// e.printStackTrace();
	// continue;
	// }
	// }
	// }
	// String[] deviceId_sense1 = { "16091076", "16091051", "16091041",
	// "16091086", "16091045" };
	// }
}
