package pipe.pipehandler;

import org.apache.log4j.Logger;

import event.PacketEvent;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

public class ShowConfPipeHandler implements PipeHandler<PacketEvent> {
	
	private static final Logger log = Logger.getLogger(ShowConfPipeHandler.class);

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		log.info("Conf: " + hc.getPipeline().id() + ":" + hc.id() + "---" + e);
		return OP.NONE;
	}
}
