package pipe;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import bus.Bus;
import pipe.pipehandler.SkipMe;
import util.IDGenUtil;
import v.V;
import ec.AbastructEventConsumer;
import ec.EventConsumer;
import event.Event;

public class PipesAsychBusHandler<T extends Event> extends AbstractPipesBusHandler<T> {

	private static final Logger log = Logger.getLogger(PipesAsychBusHandler.class);

	private final List<Pipeline<T>> pipes = new ArrayList<Pipeline<T>>();

	private final ExecutorService es = Executors.newCachedThreadPool();

	private final EventConsumer<T> pec = new AbastructEventConsumer<T>() {
		@Override
		protected void consumer(final T e) {
			es.execute(new Runnable() {
				@Override
				public void run() {
					int pidx = e.getPipeId();
					pipes.get(pidx).headIn(bus, e);
				}
			});
		}
	};

	public PipesAsychBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public PipesAsychBusHandler() {
		this(IDGenUtil.getId(), "PipelinesAsychBusHandler", "It's the container of pipelines with cache");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handlerEvent(Bus context, Event event) {
		pec.add((T) event);
	}

	@Override
	public void start() {
		super.start();
		assert !pipes.isEmpty() : "please call init before start";
		pec.start();
		log.info("pipeline container" + id + " is started");
	}

	@Override
	public void initSet(String name, Object value) {
		if (name.equals(V.CONF_FILE)) {
			String filepath = (String) value;
			try {
				init(filepath);
			} catch (Exception e) {
				log.info("INIT_ERROR", e);
				throw new RuntimeException(e.getMessage());
			}
		} else {
			throw new RuntimeException("unknown name: " + name);
		}
	}

	@Override
	public void setBusOn(Bus bus) {
		assert !pipes.isEmpty() : "please call init before setBelongsTo";
		super.setBusOn(bus);
	}

	@SuppressWarnings("unchecked")
	private void init(String filepath) throws Exception {
		// TODO
		File f = new File(filepath);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(f);
		Element root = doc.getRootElement();
		Element pls = root.element("uppipes");
		for (Object pl : pls.elements("pipeline")) {
			Element ple = (Element) pl;
			Class<?> c = Class.forName(ple.attributeValue("class"));
			Constructor<?> con = c.getConstructor(this.getClass().getSuperclass());
			Object pl_o = con.newInstance(this);
			for (Object hc : ple.elements("handler")) {
				Class<?> hc_clz = Class.forName(((Element) hc).attributeValue("class"));
				SkipMe a = hc_clz.getMethods()[0].getAnnotation(SkipMe.class);
				boolean isSkipped = false;
				if (a != null) {
					log.info("Handler " + hc_clz.getName() + " will be skipped, because of " + a.reason());
					isSkipped = true;
				}
				Method m = c.getMethod("addHandler", hc_clz.getInterfaces()[0], boolean.class);
				m.invoke(pl_o, hc_clz.newInstance(), isSkipped);
			}
			pipes.add((Pipeline<T>) pl_o);
		}
	}

	@Override
	public void stop() {
		pec.stop();
		es.shutdown();
		log.info("pipeline container" + id + " is stoped");
	}

}
