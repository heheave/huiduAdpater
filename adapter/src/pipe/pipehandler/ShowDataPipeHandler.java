package pipe.pipehandler;

import org.apache.log4j.Logger;

import event.PacketEvent;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

public class ShowDataPipeHandler implements PipeHandler<PacketEvent> {

	private static final Logger log = Logger.getLogger(ShowDataPipeHandler.class);

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		log.info("Data: " + hc.getPipeline().id() + ":" + hc.id() + "---" + e);
		return OP.NEXT;
	}
}
