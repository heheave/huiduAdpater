package pipe.pipehandler;

import event.Event;
import pipe.PipeHandlerContext;
import pipe.Pipeline;

public interface PipeHandler<T extends Event> {

	Pipeline.OP handle(PipeHandlerContext<T> hc, T e) throws Exception;

}
