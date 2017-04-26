package unsed;
//package pipeline;
//
//import java.io.File;
//import java.lang.reflect.Method;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//import org.apache.log4j.Logger;
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//
//import config.V;
//import bus.Bus;
//import util.IDGenUtil;
//import event.Event;
//
//public class EventPipelinesWithoutCache<T extends Event> extends
//		AbstractEventPipelines<T> {
//
//	private static final Logger log = Logger
//			.getLogger(EventPipelinesWithoutCache.class);
//
//	// for pipelines without cache we only support one pipeline
//	private Pipeline<T> pipe = null;
//
//	private final Lock lock = new ReentrantLock();
//
//	public EventPipelinesWithoutCache(int id, String name, String desc) {
//		super(id, name, desc);
//	}
//
//	public EventPipelinesWithoutCache() {
//		this(IDGenUtil.getId(), "EventPipelinesWithoutCache",
//				"It's the container of pipelines without cache");
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void handlerEvent(Bus context, Event event) {
//		try {
//			lock.lock();
//			pipe.syFirst(bus, (T) event);
//		} finally {
//			lock.unlock();
//		}
//
//	}
//
//	@Override
//	public void start() {
//		super.start();
//		assert pipe != null : "please call init before start";
//		log.info("pipeline container is started");
//	}
//
//	@Override
//	public void initSet(String name, Object value) {
//		if (name.equals(V.CONF_FILE)) {
//			String filepath = (String) value;
//			try {
//				init(filepath);
//			} catch (Exception e) {
//				log.info("INIT_ERROR", e);
//				throw new RuntimeException(e.getMessage());
//			}
//		} else {
//			throw new RuntimeException("unknown name: " + name);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	private void init(String filepath) throws Exception {
//		// TODO
//		log.info(filepath);
//		File f = new File(filepath);
//		SAXReader reader = new SAXReader();
//		Document doc = reader.read(f);
//		Element root = doc.getRootElement();
//		Element pls = root.element("pipelines");
//		for (Object pl : pls.elements("pipeline")) {
//			Element ple = (Element) pl;
//			Class<?> c = Class.forName(ple.attributeValue("class"));
//			Object pl_o = c.newInstance();
//			for (Object hc : ple.elements("handler")) {
//				Class<?> hc_clz = Class.forName(((Element) hc)
//						.attributeValue("class"));
//				Method m = c.getMethod("addHandler", hc_clz.getInterfaces());
//				m.invoke(pl_o, hc_clz.newInstance());
//			}
//			pipe = (Pipeline<T>) pl_o;
//			break;
//		}
//	}
//
//	@Override
//	public void stop() {
//		log.info("pipeline container is stoped");
//	}
//}
