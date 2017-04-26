package unsed;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class FilePersistence_1 implements Persistence<PrintWriter, Object> {

	private static final Logger log = Logger.getLogger(FilePersistence_1.class);

	private static volatile String path;

	// private static final String DATAS_FILE_BASE = "snapshot/datas/";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

	private static PrintWriter pw = null;

	public PrintWriter getAccesor(File f) throws Exception {
		synchronized (FilePersistence_1.class) {
			if (!(f.getAbsolutePath().equals(path))) {
				closeAccesor(pw);
				path = f.getAbsolutePath();
			}
		}
		return getAccesor();
	}

	@Override
	public PrintWriter getAccesor(String filePath) throws Exception {
		synchronized (FilePersistence.class) {
			if (!(filePath.equals(path))) {
				closeAccesor(pw);
				path = filePath;
			}
		}
		return getAccesor();
	}

	public PrintWriter getAccesor() throws Exception {
		if (pw == null) {
			synchronized (FilePersistence.class) {
				if (pw == null && path != null) {
					pw = new PrintWriter(path);
				}
			}
		}
		return pw;
	}

	@Override
	public void closeAccesor(PrintWriter accesor) throws Exception {
		if (pw != null) {
			pw.close();
			pw = null;
		}
	}

	@Override
	public boolean persistence(PersistenceOpt po, Object event) throws Exception {
		List<Object> events = new ArrayList<Object>(1);
		events.add(event);
		return persistenceByBatch(po, events);
	}

	@Override
	public synchronized boolean persistenceByBatch(PersistenceOpt po, Collection<Object> objs) throws Exception {
		if (objs.isEmpty()) {
			return true;
		}
		FilePersistenceOpt fpo = (FilePersistenceOpt) po;
		Iterator<Object> iter = objs.iterator();
		Object obj = iter.next();
		JSONObject jo = JSONObject.fromObject(obj);
		long timestamp = jo.containsKey("timestamp") ? jo.getLong("timestamp") : 0;
		String dateStr = sdf.format(new Date(timestamp));
		// String fileName = DATAS_FILE_BASE + dateStr;
		File fileBase = new File(fpo.filePath);
		PrintWriter pw = null;
		try {
			if (!fileBase.exists()) {
				fileBase.mkdirs();
			}
			pw = getAccesor(new File(fpo.filePath, dateStr));
			String dataStr = obj.toString();
			pw.println(dataStr);
			while (iter.hasNext()) {
				obj = iter.next();
				dataStr = obj.toString();
				pw.println(dataStr);
			}
			pw.flush();
			return true;
		} catch (IOException e) {
			log.error("CREATE FILE ERROR", e);
			return false;
		} finally {
			closeAccesor(pw);
		}
	}

	@Override
	public void flush() {
		if (pw != null) {
			pw.flush();
		}
	}

	@Override
	public void close() {
		flush();
		try {
			closeAccesor(pw);
		} catch (Exception e) {
			// TODO
		}
	}

}
