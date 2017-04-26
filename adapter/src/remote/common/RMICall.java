package remote.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMICall extends Remote {
	static enum Type {
		NONE, CONTROL, ACCESS
	}

	Ret call(Args args) throws RemoteException;
}
