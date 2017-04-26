package pipe.pipehandler;

import event.PacketEvent;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;
import stream.StreamClient;

public class RemoteStreamPipeHandler implements PipeHandler<PacketEvent> {
	private static final StreamClient stream = null;

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		if (e.toJson() != null && stream != null) {
			stream.send(e.toJson().toString());
		}
		return OP.NEXT;
	}
}
