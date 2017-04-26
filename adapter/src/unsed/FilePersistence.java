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

import event.PacketEvent;

public class FilePersistence implements Persistence<PrintWriter, PacketEvent> {

	private static final Logger log = Logger.getLogger(FilePersistence.class);

	private static volatile String path;

	// private static final String DATAS_FILE_BASE = "snapshot/datas/";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

	private static PrintWriter pw = null;

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
	public boolean persistence(PersistenceOpt po, PacketEvent event) throws Exception {
		List<PacketEvent> events = new ArrayList<PacketEvent>(1);
		events.add(event);
		return persistenceByBatch(po, events);
	}

	@Override
	public synchronized boolean persistenceByBatch(PersistenceOpt po, Collection<PacketEvent> events) throws Exception {
		if (events.isEmpty()) {
			return true;
		}
		FilePersistenceOpt fpo = (FilePersistenceOpt) po;
		Iterator<PacketEvent> iter = events.iterator();
		PacketEvent pe = iter.next();
		String dateStr = sdf.format(new Date(pe.getTimestamp()));
		// String fileName = DATAS_FILE_BASE + dateStr;
		File fileBase = new File(fpo.filePath);
		PrintWriter pw = null;
		try {
			if (!fileBase.exists()) {
				fileBase.mkdirs();
			}
			pw = getAccesor(fpo.filePath + File.separatorChar + dateStr);
			String dataStr = pe.toJson() != null ? pe.toJson().toString() : "";
			pw.println(dataStr);
			while (iter.hasNext()) {
				pe = iter.next();
				dataStr = pe.toJson() != null ? pe.toJson().toString() : "";
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
