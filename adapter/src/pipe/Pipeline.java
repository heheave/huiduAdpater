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

	void headIn(Bus c, T e);

	void addHandler(PipeHandler<T> hc, boolean isSkipped);

}
