package main;

import java.awt.EventQueue;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import util.Parser;
import v.V;
import exception.ParserError;

public class Main {

	private static final Logger log = Logger.getLogger(Main.class);

	private static final int ERROR_VAR_XML = -1;

	private static final int ILLEGAL_COMMAND = -2;

	private static final int ILLEGAL_PARAMETER = -3;

	private static final int CLIENT_ERROR = -4;

	private static final int CLIENT_NULL = -5;

	private static final int MODE_ERROR = -6;

	private static ClientMain cm = null;

	public static void main(String[] args) {

		try {
			Class.forName("v.V");
		} catch (Exception e) {
			System.err.println("variable xml parse error");
			System.exit(ERROR_VAR_XML);
		}

		// String paras = "";
		// if (args.length < 1) {
		// log.error("use program name -ptype pvalue to run");
		// System.exit(ILLEGAL_COMMAND);
		// } else {
		// for (String arg : args) {
		// paras += arg + " ";
		// }
		// }
		//

		PropertyConfigurator.configure(V.LOG_PATH);

		String paras = "-MODE sp -tp Witium/DistrIO/+/+/+";

		log.info("paras" + paras);
		Map<String, String> RUN_PARA = null;
		try {
			RUN_PARA = Parser.parser(null, paras.trim());
		} catch (ParserError iae) {
			log.error("ILLEGAL_PARAMETER", iae);
			System.exit(ILLEGAL_PARAMETER);
		}
		log.info("run parameters are listed in below:");
		for (Entry<String, String> entry : RUN_PARA.entrySet()) {
			log.info(entry.getKey() + " : " + entry.getValue());
		}

		if (!RUN_PARA.containsKey(V.MODE)) {
			log.error("you need to specify running mode");
			System.exit(MODE_ERROR);
		} else {
			cm = new DefaultClientMain(RUN_PARA);
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (cm != null) {
					try {
						cm.start();
					} catch (Exception e) {
						log.error("start client error", e);
						System.exit(CLIENT_ERROR);
					}
				} else {
					System.exit(CLIENT_NULL);
				}
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("before stoping client stoping other items");
				if (cm != null) {
					try {
						cm.stop();
						log.info("waiting for other components stopping");
						Thread.sleep(10000);
						log.info("client is stopped");
					} catch (Exception e) {
						cm = null;
						log.error("stop client error");
					}
				}
			}
		});
	}

}
