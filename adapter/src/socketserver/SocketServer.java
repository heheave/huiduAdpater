package socketserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import event.ControlEvent;
import event.Event;
import net.sf.json.JSONObject;
import rmi.RemoteControlListener;
import rmi.Server;
import v.V;

public class SocketServer implements Server {

	private static final Logger log = Logger.getLogger(SocketServer.class);

	private static final int BUF_SIZE = 1024;

	private final Map<Integer, SelectionKey> skeys = new ConcurrentHashMap<Integer, SelectionKey>();

	private Selector selector;

	private ServerSocketChannel serverSocketChannel;

	private static final Charset charset = Charset.forName("utf-8");

	private final String host;

	private final int port;

	// private static final int TRY_TIMES = V.DEFAULT_SOCKET_TRY_TIMES;

	private volatile boolean isrun;

	private RemoteControlListener rcl;

	private final ExecutorService es = Executors.newCachedThreadPool();

	public SocketServer(RemoteControlListener rcl, String host, int port) throws IOException {
		this.rcl = rcl;
		this.host = host;
		this.port = port;
	}

	public SocketServer(String host, int port) throws IOException {
		this(null, host, port);
	}

	public SocketServer(int port) throws IOException {
		this(V.DEFAULT_SOCKET_SERVER_HOST, port);
	}

	public SocketServer() throws IOException {
		this(V.DEFAULT_SOCKET_SERVER_PORT);
	}

	private void callListener(int sk, String info) {
		try {
			JSONObject jo = JSONObject.fromObject(info);
			String cmdType = null;
			if (jo.containsKey("cmd")) {
				cmdType = jo.getString("cmd");
			}
			log.info(jo.toString());
			String mes = null;
			if (jo.containsKey("mes")) {
				mes = jo.getString("mes");
			}

			if (cmdType == null || mes == null || rcl == null) {
				retString(sk, "{\"error\":\"" + V.RESULT_NULL + "\"}");
			} else {
				rcl.cmd("" + sk, cmdType, mes);
			}
		} catch (Exception e) {
			retString(sk, "{\"error\":\"" + V.RESULT_ERROR + "\"}");
		}

	}

	private void retString(int sk, String mes) {
		SelectionKey skey = skeys.get(sk);
		if (skey != null) {
			try {
				skey.channel().configureBlocking(false);
				skey.channel().register(selector, SelectionKey.OP_WRITE, mes);
				selector.wakeup();
			} catch (Exception e) {
				e.printStackTrace();
				removeSelectionKey(skey);
			}
		}
	}

	@Override
	public void setListener(RemoteControlListener rcl) {
		this.rcl = rcl;
	}

	@Override
	public void remoteRet(ControlEvent retValue) {
		String result = V.RESULT_ERROR;
		Event.CODE code = retValue.getCode();
		JSONObject jo = new JSONObject();
		switch (code) {
		case DROP:
			result = V.RESULT_FAILURE;
			jo.put("error", result);
			break;
		case RC_RET:
			String retStr = retValue.getDataString();
			result = (retStr == null) ? V.RESULT_SUCCESS : retStr;
			jo.put("ret", result);
			break;
		default:
			result = V.RESULT_NULL;
			jo.put("error", result);
		}
		String ctrlId = retValue.getCtrlId();
		int sk = Integer.parseInt(ctrlId);
		retString(sk, jo.toString());
	}

	@Override
	public void start() throws Exception {
		if (!isrun) {
			isrun = true;
			new Thread() {
				public void run() {
					initSelector();
					service();
				}
			}.start();
		}
	}

	@Override
	public void stop() {
		isrun = false;
		try {
			if (selector != null) {
				selector.close();
			}
		} catch (IOException e) {
			log.error("close serversocket occurs an error");
		}
		es.shutdown();
		log.info("Server shutdown  successfully");
	}

	private void initSelector() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().setReuseAddress(true);
			// ������ģʽ
			serverSocketChannel.configureBlocking(false);
			// serverSocketChannel.socket()����һ���͵�ǰ�ŵ��������socket
			serverSocketChannel.socket().bind(new InetSocketAddress(port), 100);
			// ע��������Ӿ����¼�
			// ע���¼���᷵��һ��SelectionKey�������Ը���ע���¼����
			// ��SelectionKey�������Selector��all-keys�����У������Ӧ���¼�����
			// ��SelectionKey�������Selector��selected-keys������
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Server establish error!");
			System.exit(-1);
		}

		log.info("Server start up and listening on " + host + ":" + port);
	}

	public void service() {
		int emptyLoopTime = 0;
		while (isrun) {
			try {
				int selCnt = selector.select();
				if (selCnt > 0) {
					emptyLoopTime = 0;
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectedKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey selectionKey = iterator.next();
						iterator.remove();
						int ops = selectionKey.interestOps();
						selectionKey.interestOps(ops & ~SelectionKey.OP_READ & ~SelectionKey.OP_WRITE);
						tackle(selectionKey);
					}
				} else {
					emptyLoopTime++;
					if (emptyLoopTime > 10000) {
						log.warn("An JDK select bug occurred");
						// TODO
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void addSelectionKey(SelectionKey selectionKey) {
		if (skeys.containsKey(selectionKey.hashCode())) {
			removeSelectionKey(skeys.get(selectionKey.hashCode()));
		}
		skeys.put(selectionKey.hashCode(), selectionKey);
	}

	private void removeSelectionKey(SelectionKey selectionKey) {
		if (selectionKey != null) {
			if (skeys.containsKey(selectionKey.hashCode())) {
				skeys.remove(selectionKey.hashCode());
			}
			try {
				selectionKey.cancel();
				selectionKey.channel().close();
			} catch (IOException e) {
				log.info("close SelectionKey channel error");
			}
		}
	}

	private void tackle(final SelectionKey selectionKey) {
		if (selectionKey.isAcceptable()) {
			// System.out.println("isAcceptable:" + selectionKey.hashCode());
			try {
				ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
				SocketChannel sc = (SocketChannel) ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			} catch (IOException e) {
				e.printStackTrace();
				removeSelectionKey(selectionKey);
			}
		} else if (selectionKey.isReadable()) {
			es.submit(new Runnable() {
				@Override
				public void run() {
					Readable(selectionKey);
				}
			});
		} else if (selectionKey.isWritable()) {
			es.submit(new Runnable() {
				@Override
				public void run() {
					Writable(selectionKey);
				}
			});
		}
	}

	private void Readable(SelectionKey selectionKey) {
		try {
			SocketChannel sc = (SocketChannel) selectionKey.channel();
			ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
			int num = sc.read(buffer);
			if (num > 0) {
				buffer.flip();
				String request = charset.decode(buffer).toString();
				System.out.println(request);
				addSelectionKey(selectionKey);
				callListener(selectionKey.hashCode(), request);
			} else {
				throw new IOException("READ ERROR");
			}
		} catch (IOException e) {
			e.printStackTrace();
			removeSelectionKey(selectionKey);
		}
	}

	private void Writable(SelectionKey selectionKey) {
		try {
			selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
			String answer = (String) selectionKey.attachment();
			SocketChannel sc = (SocketChannel) selectionKey.channel();
			// System.out.println("answer: " + selectionKey.attachment());
			ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
			buffer.clear();
			buffer.put(charset.encode(answer));
			buffer.flip();
			while (buffer.hasRemaining()) {
				sc.write(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			removeSelectionKey(selectionKey);
		}
	}

	public static void main(String[] args) {
		SocketServer ss = null;
		try {
			ss = new SocketServer(9999);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ss != null) {
			try {
				ss.start();
			} catch (Exception e) {
				e.printStackTrace();
				if (ss != null) {
					ss.stop();
				}
			}
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ss.stop();
	}
}
