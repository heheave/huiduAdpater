//package rmi;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import org.apache.log4j.Logger;
//
//import net.sf.json.JSONObject;
//import v.V;
//import event.ControlEvent;
//import event.Event;
//
//public class RemoteControlServer implements Server {
//
//	private static final Logger log = Logger.getLogger(RemoteControlServer.class);
//
//	private static final AtomicInteger ai = new AtomicInteger();
//
//	private final SocketManager sm = new DefaultSocketManager();
//
//	private ServerSocket serverSocket = null;
//
//	private static final String host = V.DEFAULT_SOCKET_SERVER_HOST;
//
//	private static final int port = V.DEFAULT_SOCKET_SERVER_PORT;
//
//	private static final int TRY_TIMES = V.DEFAULT_SOCKET_TRY_TIMES;
//
//	private volatile boolean isrun;
//
//	private RemoteControlListener rcl;
//
//	private final ExecutorService es = Executors.newCachedThreadPool();
//
//	private final Runnable runListen = new Runnable() {
//		@Override
//		public void run() {
//			while (isrun) {
//				try {
//					final Socket socket = serverSocket.accept();
//					es.submit(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								tackle(socket);
//							} catch (Exception e) {
//								log.error("TACKLE REQUEST ERROR", e);
//							} finally {
//								if (socket != null)
//									try {
//										socket.close();
//									} catch (IOException e) {
//										log.error("close socket error");
//									}
//							}
//						}
//					});
//				} catch (Exception e) {
//					continue;
//				}
//			}
//		}
//	};
//
//	public RemoteControlServer(RemoteControlListener rcl, String host, int port) throws IOException {
//		this.rcl = rcl;
//		serverSocket = new ServerSocket(port);
//
//	}
//
//	public RemoteControlServer(String host, int port) throws IOException {
//		this(null, host, port);
//	}
//
//	public RemoteControlServer(int port) throws IOException {
//		this(host, port);
//	}
//
//	public RemoteControlServer() throws IOException {
//		this(host, port);
//	}
//
//	private void tackle(Socket socket) throws Exception {
//		JSONObject jo = JSONObject.fromObject(getString(socket));
//		String cmdType = null;
//		if (jo.containsKey("cmd")) {
//			cmdType = jo.getString("cmd");
//		}
//		log.info(jo.toString());
//		String mes = null;
//		if (jo.containsKey("mes")) {
//			mes = jo.getString("mes");
//		}
//
//		if (cmdType == null || mes == null || rcl == null) {
//			retString(socket, "{\"status\":\"" + V.RESULT_NULL + "\"}");
//			socket.close();
//		} else {
//			String cntrlId = cmdType + ai.getAndIncrement();
//			sm.add(cntrlId, socket);
//			rcl.cmd(cntrlId, cmdType, mes);
//		}
//
//	}
//
//	private String getString(Socket s) throws IOException {
//		if (s == null || s.isInputShutdown() || s.isClosed()) {
//			return null;
//		}
//		StringBuffer sb = new StringBuffer();
//		InputStream is = s.getInputStream();
//		byte[] buf = new byte[1024];
//		int len;
//		while ((len = is.read(buf, 0, buf.length)) > 0) {
//			sb.append(new String(buf, 0, len));
//		}
//		return sb.toString();
//	}
//
//	private void retString(Socket s, String mes) throws IOException {
//		if (s == null || s.isOutputShutdown() || s.isClosed() || mes == null) {
//			return;
//		}
//
//		OutputStream os = s.getOutputStream();
//		os.write(mes.getBytes());
//		os.flush();
//	}
//
//	@Override
//	public void setListener(RemoteControlListener rcl) {
//		this.rcl = rcl;
//	}
//
//	@Override
//	public void remoteRet(ControlEvent retValue) {
//		String result = V.RESULT_ERROR;
//		Event.CODE code = retValue.getCode();
//		switch (code) {
//		case DROP:
//			result = V.RESULT_FAILURE;
//			break;
//		case RC_RET:
//			String retStr = retValue.getDataString();
//			result = (retStr == null) ? V.RESULT_SUCCESS : retStr;
//			break;
//		default:
//			result = V.RESULT_NULL;
//		}
//		JSONObject jo = new JSONObject();
//		jo.put("ret", result);
//		String ctrlId = retValue.getCtrlId();
//		Socket s = sm.get(ctrlId);
//		for (int i = 0; i < TRY_TIMES; i++) {
//			try {
//				retString(s, jo.toString());
//				break;
//			} catch (IOException e) {
//				continue;
//			}
//		}
//		sm.release(ctrlId);
//	}
//
//	@Override
//	public void start() {
//		isrun = true;
//		new Thread(runListen).start();
//	}
//
//	@Override
//	public void stop() {
//		isrun = false;
//		try {
//			serverSocket.close();
//		} catch (IOException e) {
//			log.error("close serversocket occurs an error");
//		}
//		if (!sm.isEmpty()) {
//			sm.clear();
//		}
//		es.shutdown();
//	}
//
//}
