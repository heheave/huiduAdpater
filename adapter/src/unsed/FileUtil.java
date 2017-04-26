//package unsed;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.SimpleDateFormat;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Iterator;
//
//import org.apache.log4j.Logger;
//
//import event.PacketEvent;
//
//
//public class FileUtil {
//
//	private static final Logger log = Logger.getLogger(FileUtil.class);
//
//	private static final String DATAS_FILE_BASE = "snapshot/datas/";
//
//	private static final String DATAPROCESS_FILE_BASE = "snapshot/dataprocess/";
//
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
//
//	private static PrintWriter pw = null;
//
//	private static PrintWriter pw1 = null;
//	
//	static{
//		File dataFileDir = new File(DATAS_FILE_BASE);
//		if(!dataFileDir.exists()){
//			dataFileDir.mkdirs();
//		}
//		File dataprocessFileDir = new File(DATAPROCESS_FILE_BASE);
//		if(!dataprocessFileDir.exists()){
//			dataprocessFileDir.mkdirs();
//		}
//	}
//
//	private FileUtil() {
//		// private the constructor
//	}
//
//	public synchronized static void dataSaveToFile(Collection<PacketEvent> empty) {
//		if (empty.isEmpty()) {
//			return;
//		}
//		Iterator<PacketEvent> iter = empty.iterator();
//		PacketEvent pe = iter.next();
//		String dateStr = sdf.format(new Date(pe.getTimestamp()));
//		System.out.println(dateStr);
//		File file = new File(DATAS_FILE_BASE + dateStr);
//		File file1 = new File(DATAPROCESS_FILE_BASE + dateStr);
//		try {
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//
//			if (!file1.exists()) {
//				file1.createNewFile();
//			}
//			pw = new PrintWriter(file);
//			pw1 = new PrintWriter(file1);
//			String dataStr = pe.getJo() != null ? pe.getJo().toString() : "";
//			String dataprocessStr = pe.getJod() != null ? pe.getJod().toString() : "";
//			pw.println(dataStr);
//			pw1.println(dataprocessStr);
//			while (iter.hasNext()) {
//				pe = iter.next();
//				dataStr = pe.getJo() != null ? pe.getJo().toString() : "";
//				dataprocessStr = pe.getJod() != null ? pe.getJod().toString() : "";
//				pw.println(dataStr);
//				pw1.println(dataprocessStr);
//			}
//			pw.flush();
//			pw1.flush();
//		} catch (IOException e) {
//			log.error("CREATE FILE ERROR", e);
//		} finally {
//			if (pw != null) {
//				pw.close();
//			}
//
//			if (pw1 != null) {
//				pw1.close();
//			}
//		}
//
//	}
//
//	public static void flushUnflushed() {
//		if (pw != null) {
//			pw.close();
//		}
//
//		if (pw1 != null) {
//			pw1.close();
//		}
//	}
//}
