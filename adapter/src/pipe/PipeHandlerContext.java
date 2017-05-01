package pipe;

import bus.Bus;
import event.Event;

public interface PipeHandlerContext<T extends Event> {

	int id();

	PipeHandlerContext<T> pre();

	PipeHandlerContext<T> next();

	void setPre(PipeHandlerContext<T> pre);

	void setNext(PipeHandlerContext<T> next);

	boolean skipped();

	Pipeline<T> getPipeline();

	void tackle(Bus c, T e);

}
