package unsed;
//package mqtttest;
//
//import java.util.Set;
//
//import listener.MessageArrivedListener;
//import net.sf.json.JSONObject;
//
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.bson.Document;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//import util.MongoUtil;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//import config.Config;
//import config.DefaultConfig;
//import context.Context;
//import context.DefaultContext;
//import client.DefaultSubcriber;
//import client.Subcriber;
//import event.Event.CODE;
//import event.PacketEvent;
//
//public class MqttSubTest {
//
//	public static final Logger log = Logger.getLogger(MqttSubTest.class);
//
//	public static final String HOST = "tcp://114.55.92.31:1883";
//
//	public static final String ID = "client_subcriber";
//
//	public static final String TOPIC = "test_topic";
//
//	public static MqttClient client;
//
//	public static void main(String[] args) throws MqttException {
//
//		Subcriber sub = new DefaultSubcriber(HOST, ID);
//
//		PropertyConfigurator.configure("file/log4j.properties");
//		// Logger log = Logger.getLogger(MqttSubTest.class);
//		final Context context = new DefaultContext();
//
//		Config f = null;
//		try {
//			f = DefaultConfig.getInstance("file/conf.xml");
//			if (f != null)
//				context.init(f);
//		} catch (Exception e) {
//			log.error("CONF_ERR", e);
//		}
//		// f.add(V.DIS_ID, new DefaultDispatcher());
//		// f.add(V.FIN_ID, new DefaultFinisher());
//		// List<Pipeline> pipes = new ArrayList<Pipeline>();
//		// DefaultPipeline dp1 = new DefaultPipeline(context);
//		// DefaultPipeline dp2 = new DefaultPipeline(context);
//		// // System.out.println(dp1.id() + "---" + dp2.id());
//		// dp1.addHandler(new ShowEventHandler());
//		// dp2.addHandler(new ShowEventHandler());
//		// dp2.addHandler(new ShowEventHandler());
//		// // DefaultPipeline dp3=new DefaultPipeline(context);
//		// pipes.add(dp1);
//		// pipes.add(dp2);
//		// f.add(DefaultContext.V, pipes);
//		// try {
//		// context.init(f);
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// log.error("init error", e);
//		// }
//		// context.start(1000);
//
//		// final PacketEvent pd = new PacketEvent(CODE.IN);
//
//		sub.addMessageArrivedListener(new MessageArrivedListener() {
//
//			@Override
//			public void messageArrived(String arg0, MqttMessage arg1)
//					throws Exception {
//				// System.out.println(arg0);
//				if (arg1 != null) {
//					byte[] hehe = arg1.getPayload();
//					// insertIntoMongo(new String(hehe));
//					PacketEvent pd = new PacketEvent(CODE.IN);
//					pd.setData(hehe);
//					// insertIntoMongo(s);
//					context.notify(pd);
//				}
//			}
//		});
//
//		sub.subcribe(TOPIC);
//	}
//
//	public static void insertIntoMongo(String hehe) {
//		MongoDatabase mdb = MongoUtil.getDatabase("mqtt_test_db");
//		if (mdb == null) {
//			throw (new NullPointerException());
//		}
//
//		JSONObject jo = JSONObject.fromObject(hehe);
//		if (jo == null) {
//			System.out.println("parse from string error");
//			return;
//		}
//		Document document = new Document();
//		@SuppressWarnings("unchecked")
//		Set<String> keySet = jo.keySet();
//		for (String key : keySet) {
//			document.append(key, jo.get(key));
//		}
//
//		System.out.println("create first collection success");
//
//		MongoCollection<Document> collection = mdb
//				.getCollection("mqtt_test_collection");
//		// Document document = new Document();
//		// document.append("name", "小科");
//		// document.append("age", "23");
//		// document.append("height", "173cm");
//		// document.append("weight", "60kg");
//		// document.append("gen", "男");
//		// Document school = new Document();
//		// school.append("name", "上海交通大学123");
//		// school.append("addr", "上海市闵行区东川路800号");
//		// document.append("school", school);
//		collection.insertOne(document);
//	}
//}
