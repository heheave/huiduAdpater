package pipe;

import bus.Bus;
import component.Component;
import event.Event;
import pipe.pipehandler.PipeHandler;

public interface Pipeline<T extends Event> extends Component {

	enum OP {
		NEXT, END, DROP, NONE
	}

	AbstractPipesBusHandler<T> getContainer();

	void first(Bus c, T e);

	PipeHandlerContext<T> getNextHandlerContext(PipeHandlerContext<T> hc);

	void addHandler(PipeHandler<T> hc);

}
