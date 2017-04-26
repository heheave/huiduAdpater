package pipe.pipehandler;

import org.apache.log4j.Logger;

import event.ControlEvent;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

public class ShowCtrlPipeHandler implements PipeHandler<ControlEvent> {

	private static final Logger log = Logger.getLogger(ShowCtrlPipeHandler.class);

	@Override
	public OP handle(PipeHandlerContext<ControlEvent> hc, ControlEvent e) throws Exception {
		log.info("Ctrl: " + hc.getPipeline().id() + ":" + hc.id() + "---" + e.getDataString());
		return OP.NEXT;
	}
}
