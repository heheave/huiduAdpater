//package rmi;
//
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Logger;
//
//public class DefaultSocketManager implements SocketManager {
//
//	private static final Logger log = Logger.getLogger(DefaultSocketManager.class);
//
//	private final Map<String, Socket> sockets = new ConcurrentHashMap<String, Socket>();
//
//	@Override
//	public boolean isEmpty() {
//		return sockets.isEmpty();
//	}
//
//	@Override
//	public int size() {
//		return sockets.size();
//	}
//
//	@Override
//	public void add(String id, Socket socket) {
//		if (contains(id)) {
//			release(id);
//		}
//		sockets.put(id, socket);
//
//	}
//
//	@Override
//	public Socket get(String id) {
//		if (contains(id)) {
//			return sockets.get(id);
//		} else {
//			return null;
//		}
//	}
//
//	@Override
//	public boolean contains(String id) {
//		return sockets.containsKey(id);
//	}
//
//	@Override
//	public void release(String id) {
//		Socket s = get(id);
//		if (s != null) {
//			try {
//				if (!s.isClosed())
//					s.close();
//				s = null;
//			} catch (IOException e) {
//				log.error("COLSE SOCKET:" + id + " ERROR");
//			}
//			sockets.remove(id);
//		}
//	}
//
//	@Override
//	public void clear() {
//		for (Entry<String, Socket> entry : sockets.entrySet()) {
//			Socket s = entry.getValue();
//			if (s != null) {
//				try {
//					if (!s.isClosed())
//						s.close();
//					s = null;
//				} catch (IOException e) {
//					log.error("COLSE SOCKET:" + entry.getKey() + " ERROR");
//				}
//			}
//		}
//		sockets.clear();
//	}
//
//}
