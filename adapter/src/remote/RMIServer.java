package remote;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
	public static void main(String[] args) {
		try {
			RMIServerImpl caller = new RMIServerImpl();
			caller.start();
			LocateRegistry.createRegistry(12345);
			Naming.bind("rmi://localhost:12345/RMICall", caller);
			System.out.println("RMI Server is starting and waiting for connecting ...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
