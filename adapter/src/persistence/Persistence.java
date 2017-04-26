package persistence;

import java.util.Collection;

import event.PacketEvent;

public interface Persistence {
	
	// persist packets to persistence device
	public void persistence(final Collection<PacketEvent> packets) throws Exception;
}
