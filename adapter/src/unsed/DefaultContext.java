package unsed;
//package context;
//
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Logger;
//
//import config.Config;
//import config.V;
//import pipeline.Pipeline;
//import util.IDGenUtil;
//import ec.AbastructEventConsumer;
//import event.ControlEvent;
//import event.Event;
//import event.PacketEvent;
//import event.Event.CODE;
//import gate.Gate;
//
//public class DefaultContext extends AbastructEventConsumer<Event> implements
//		Context {
//
//	private static final Logger log = Logger.getLogger(DefaultContext.class);
//
//	private final int id;
//
//	private volatile static State state;
//
//	private static ConcurrentHashMap<Integer, Pipeline> pipes;
//
//	private static Gate dispatcher;
//
//	private static Gate finisher;
//
//	private static volatile int running;
//
//	public DefaultContext() {
//		this.id = IDGenUtil.getId();
//		state = State.INITING;
//	}
//
//	@Override
//	public void init(Config c) throws Exception {
//		if (c.contians(V.DIS_ID)) {
//			Object dis = c.get(V.DIS_ID);
//			if (dis instanceof Gate) {
//				dispatcher = (Gate) dis;
//			}
//		} else {
//			throw new IllegalArgumentException("config contians no dispatcher");
//		}
//
//		if (c.contians(V.FIN_ID)) {
//			Object fin = c.get(V.FIN_ID);
//			if (fin instanceof Gate) {
//				finisher = (Gate) fin;
//			}
//		} else {
//			throw new IllegalArgumentException("config contians no finisher");
//		}
//
//		if (c.contians(V.PIPES_ID)) {
//			Object ps = c.get(V.PIPES_ID);
//			if (ps instanceof List<?>) {
//				List<?> pslist = (List<?>) ps;
//				if (pslist.isEmpty()) {
//					throw new IllegalArgumentException(
//							"pipelines cannot be empty");
//				}
//				pipes = new ConcurrentHashMap<Integer, Pipeline>();
//				for (Object p : pslist) {
//					if (p instanceof Pipeline) {
//						Pipeline temp_pipe = (Pipeline) p;
//						temp_pipe.setContext(this);
//						pipes.put(temp_pipe.id(), temp_pipe);
//					} else {
//						throw new IllegalArgumentException("illegal pipeline");
//					}
//				}
//			}
//		} else {
//			throw new IllegalArgumentException("config contians no pipes");
//		}
//
//		dispatcher.setBelongsTo(this);
//		finisher.setBelongsTo(this);
//
//		state = State.INITED;
//
//	}
//
//	@Override
//	public void consumer(Event e) {
//		switch (e.getCode()) {
//		case IN:
//			dispatcher.data(DefaultContext.this, (PacketEvent) e);
//			break;
//		case NEXT:
//			PacketEvent pe = (PacketEvent) e;
//			int idx = pe.getPipeId();
//			if (!pipes.containsKey(idx)) {
//				log.error("dispatcher dispatch a illegal index: " + idx
//						+ " pack is dropped");
//				pe.changeCode(Event.CODE.DROP);
//				DefaultContext.this.notify(pe);
//			} else {
//				pipes.get(idx).asyFirst(DefaultContext.this, pe);
//			}
//			break;
//		case OUT:
//			finisher.data(DefaultContext.this, (PacketEvent) e);
//			break;
//		case RUN_ACK:
//			running++;
//			run1();
//			break;
//		case STOP_ACK:
//			running--;
//			stop1();
//			break;
//		default:
//			log.error("an event is dropped its message is: "
//					+ ((PacketEvent) e));
//			e = null;
//			break;
//		}
//	};
//
//	// notice the start up order;
//	@Override
//	public void start() {
//		if (state.equals(State.INITED)) {
//			// it's just like a bus
//			// if we start it firstly then
//			// the events can be transfered on it
//			state = State.RUNNING;
//			super.start();
//			running = 0;
//			run1();
//		} else {
//			throw new RuntimeException("context still uninited");
//		}
//	}
//
//	private void startupPipeline() {
//		for (Entry<Integer, Pipeline> entry : pipes.entrySet()) {
//			entry.getValue().startup();
//		}
//	}
//
//	private void run1() {
//		if (running == 0) {
//			assert finisher != null;
//			finisher.control(this, new ControlEvent(CODE.RUN));
//		} else if (running == 1) {
//			startupPipeline();
//			assert dispatcher != null;
//			dispatcher.control(this, new ControlEvent(CODE.RUN));
//		} else {
//			log.info("context is starting");
//		}
//	}
//
//	// notice the shut down order;
//	@Override
//	public void stop() {
//		stop1();
//	}
//
//	private void shutdownPipes() {
//		for (Entry<Integer, Pipeline> entry : pipes.entrySet()) {
//			entry.getValue().shutdown();
//		}
//	}
//
//	public void stop1() {
//		if (running == 2) {
//			assert dispatcher != null;
//			dispatcher.control(this, new ControlEvent(CODE.STOP));
//		} else if (running == 1) {
//			shutdownPipes();
//			assert finisher != null;
//			finisher.control(this, new ControlEvent(CODE.STOP));
//		} else {
//			// stop the bus lastly
//			super.stop();
//			state = State.STOPED;
//			log.info("context is stopped");
//		}
//	}
//
//	@Override
//	public State getState() {
//		return state;
//	}
//
//	@Override
//	public void notify(Event e) {
//		add(e);
//	}
//
//	@Override
//	public Set<Integer> getPipeInfo() {
//		return pipes.keySet();
//	}
//
//	@Override
//	public int id() {
//		return this.id;
//	}
//
//}
