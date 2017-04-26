package pipe;

import org.apache.log4j.Logger;

import bus.Bus;
import util.IDGenUtil;
import event.Event;
import event.Event.CODE;
import pipe.pipehandler.PipeHandler;

public final class DefaultPipeHandlerContext<T extends Event> implements PipeHandlerContext<T> {

	private static final Logger log = Logger.getLogger(DefaultPipeHandlerContext.class);

	private final int id = IDGenUtil.getId();
	private Pipeline<T> pipeline;
	private PipeHandler<T> handler;

	public DefaultPipeHandlerContext(Pipeline<T> pipeline, PipeHandler<T> handler) {
		if (pipeline == null) {
			throw new IllegalArgumentException("pipeline connot be null");
		}
		this.pipeline = pipeline;
		this.handler = handler;
	}

	public DefaultPipeHandlerContext(Pipeline<T> pipeline) {
		this(pipeline, null);
	}

	@Override
	public int id() {
		return this.id;
	}

	@Override
	public Pipeline<T> getPipeline() {
		return this.pipeline;
	}

	@Override
	public PipeHandlerContext<T> getNext() {
		return this.pipeline.getNextHandlerContext(this);
	}

	@Override
	public PipeHandlerContext<T> getPre() {
		// TODO Auto-generated method stub
		// now we just support next
		return null;
	}

	public void tackle(Bus c, T e) {
		if (handler == null) {
			return;
		}
		try {
			Pipeline.OP op_result = handler.handle(this, e);
			switch (op_result) {
			case NEXT:
				next_tackle(c, e);
				break;
			case END:
				end_tackle(c, e);
				break;
			case NONE:
				break;
			default:
				drop_tackle(c, e);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("HANDLER ERROR", ex);
			drop_tackle(c, e);
		}
	}

	private void next_tackle(Bus b, T e) {
		PipeHandlerContext<T> hc_next = this.pipeline.getNextHandlerContext(this);
		if (hc_next != null) {
			hc_next.tackle(b, e);
		} else {
			end_tackle(b, e);
		}
	}

	private void end_tackle(Bus b, T e) {
		e.changeCode(CODE.OUT);
		b.putEvent(e);
		// log.info("pipe out timestamp: " + System.currentTimeMillis());
	}

	private void drop_tackle(Bus b, T e) {
		e.changeCode(CODE.DROP);
		b.putEvent(e);
	}

}
