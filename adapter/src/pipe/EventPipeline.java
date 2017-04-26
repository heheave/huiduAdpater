package pipe;

import java.util.concurrent.ConcurrentHashMap;

import bus.Bus;
import util.IDGenUtil;
import event.Event;
import event.Event.CODE;
import pipe.pipehandler.PipeHandler;

public class EventPipeline<T extends Event> implements Pipeline<T> {

	// private static final Logger log = Logger.getLogger(EventPipeline.class);

	private final int id;

	private final String name;

	private final String desc;

	private final AbstractPipesBusHandler<T> belongsTo;

	private final ConcurrentHashMap<Integer, PipeHandlerContext<T>> next_map = new ConcurrentHashMap<Integer, PipeHandlerContext<T>>();

	private volatile PipeHandlerContext<T> head = null;

	private volatile PipeHandlerContext<T> tail = null;

	public EventPipeline(int id, String name, String desc, AbstractPipesBusHandler<T> aep) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.belongsTo = aep;
	}

	public EventPipeline(AbstractPipesBusHandler<T> aep) {
		this(IDGenUtil.getId(), "DefaultPipeline", "It's a pipeline", aep);
	}

	@Override
	public int id() {
		return this.id;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String desc() {
		return this.desc;
	}

	@Override
	public void addHandler(PipeHandler<T> hc) {
		PipeHandlerContext<T> dhc = new DefaultPipeHandlerContext<T>(this, hc);
		// System.out.println(hc.getClass().getName());
		if (head == null) {
			head = dhc;
			tail = dhc;
		} else {
			next_map.put(tail.id(), dhc);
			tail = dhc;
		}
	}

	@Override
	public PipeHandlerContext<T> getNextHandlerContext(PipeHandlerContext<T> hc) {
		if (next_map.containsKey(hc.id())) {
			return next_map.get(hc.id());
		}
		return null;
	}

	@Override
	public void first(final Bus b, final T e) {
		e.setPipeId(id);
		if (head == null) {
			e.changeCode(CODE.OUT);
			b.putEvent(e);
		} else {
			head.tackle(b, e);
		}
	}

	@Override
	public AbstractPipesBusHandler<T> getContainer() {
		return belongsTo;
	}

}
