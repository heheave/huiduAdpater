package pipe;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pipe.pipehandler.SkipMe;
import util.IDGenUtil;
import v.V;
import bus.Bus;
import event.Event;

public class PipesSychBusHandler<T extends Event> extends AbstractPipesBusHandler<T> {

	private static final Logger log = Logger.getLogger(PipesSychBusHandler.class);

	private Pipeline<T> pipe;

	public PipesSychBusHandler(int id, String name, String desc) {
		super(id, name, desc);
	}

	public PipesSychBusHandler() {
		this(IDGenUtil.getId(), "EventPippelinesSingle",
				"This pipelines container contains only one pipeline and without cache");
	}

	@Override
	public void start() {
		super.start();
		assert pipe != null : "pipe in container+" + id + " shouldn't be null";
		log.info("pipeline container" + id + " is started");
	}

	@Override
	public void stop() {
		log.info("pipeline container" + id + " is stoped");
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
		assert pipe != null : "please call init before setBelongsTo";
		super.setBusOn(bus);
	}

	@SuppressWarnings("unchecked")
	private void init(String filepath) throws Exception {
		// TODO
		File f = new File(filepath);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(f);
		Element root = doc.getRootElement();
		Element pls = root.element("downpipes");
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
			pipe = (Pipeline<T>) pl_o;
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handlerEvent(Bus bus, Event event) {
		pipe.headIn(bus, (T) event);
	}

}
