package remote;

import remote.common.Args;

public class RMIHandlerFactory {
	public static RMIHandler getHandler(int id, Args args) {
		return new RMIHandler(id, args);
	}
}
