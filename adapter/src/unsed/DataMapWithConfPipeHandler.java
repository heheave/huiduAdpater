//package unsed;
//
//import net.sf.json.JSONObject;
//import pipe.PipeHandlerContext;
//import pipe.Pipeline.OP;
//import pipe.pipehandler.PipeHandler;
//import util.ByteUtil;
//import cache.deviceconf.AbastractDevice;
//import cache.deviceconf.Conf;
//import cache.deviceconf.ConfCache;
//import event.ControlEventFactory;
//import event.Event;
//import event.Event.CODE;
//import event.PacketEvent;
//
//public class DataMapWithConfPipeHandler implements PipeHandler<PacketEvent> {
//
//	private final ConfCache cc = ConfCache.instance();
//
//	@Override
//	public OP handle(PipeHandlerContext<PacketEvent> hc, PacketEvent e) throws Exception {
//		try {
//			String deviceId = e.getDeviceId();
//			JSONObject jo = e.toJson();
//			if (deviceId == null || jo == null) {
//				return OP.DROP;
//			}
//			AbastractDevice cd = cc.get(deviceId);
//			Conf[] cis = null;
//			if (cd != null && (cis = cd.get(e.getInfoType())) != null) {
//				JSONObject jod = new JSONObject();
//				jod.put("devicetype", e.getDeviceType());
//				jod.put("deviceid", e.getDeviceId());
//				jod.put("datatype", e.getInfoType());
//				jod.put("timestamp", e.getTimestamp());
//				jod.put("normal", jo.get("normal"));
//				int portnum = (Integer) jo.get("portnum");
//				jod.put("portnum", portnum);
//				JSONObject jodatas = jo.getJSONObject("datas");
//				JSONObject joddatas = new JSONObject();
//				int i = 0;
//				int loopTime = cis.length < portnum ? cis.length : portnum;
//				while (i < loopTime) {
//					Conf ci = cis[i];
//					String pi = "" + i;
//					int vi = ByteUtil.getNotAddOne((Integer) jodatas.get(pi));
//					double ki = ci.getK();
//					double bi = ci.getB();
//					String ui = ci.getU();
//					double vid = ki * vi + bi;
//					JSONObject portdatas = new JSONObject();
//					portdatas.put("value", vid);
//					portdatas.put("desc", ci.getM());
//					portdatas.put("unit", ui);
//					joddatas.put(pi, portdatas);
//					i++;
//				}
//				jod.put("datas", joddatas);
//				e.setJod(jod);
//				return OP.NEXT;
//			} else {
//				Event ce = ControlEventFactory.getRequestConfControlEvent(e.getDeviceType(), deviceId);
//				ce.changeCode(CODE.CROSS);
//				hc.getPipeline().getContainer().getBusOn().putEvent(ce);
//				return OP.NEXT;
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return OP.DROP;
//		}
//	}
//
//}
