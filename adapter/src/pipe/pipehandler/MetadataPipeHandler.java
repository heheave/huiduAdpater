package pipe.pipehandler;

import event.PacketEvent;
import pipe.PipeHandlerContext;
import pipe.Pipeline.OP;

public class MetadataPipeHandler implements PipeHandler<PacketEvent> {

	@Override
	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
		String topic = e.getTopic();
		if (topic == null) {
			return OP.DROP;
		}
		String[] infos = topic.split("/", 5);
		if (infos[2] != null) {
			e.setDeviceType(infos[2]);
		} else {
			return OP.DROP;
		}
		if (infos[3] != null) {
			e.setDeviceId(infos[3]);
		} else {
			return OP.DROP;
		}
		if (infos[4] != null) {
			e.setInfoType(infos[4]);
		} else {
			return OP.DROP;
		}
		e.setTimestamp(System.currentTimeMillis());
		return OP.NEXT;
	}

}
