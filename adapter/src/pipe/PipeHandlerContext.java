package pipe;

import bus.Bus;
import event.Event;

public interface PipeHandlerContext<T extends Event> {

	int id();

	PipeHandlerContext<T> getNext();

	PipeHandlerContext<T> getPre();

	Pipeline<T> getPipeline();

	void tackle(Bus c, T e);

}
