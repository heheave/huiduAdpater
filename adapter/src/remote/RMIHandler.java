package remote;

import java.util.NoSuchElementException;

import remote.common.Args;
import remote.common.RMICall;
import remote.common.Ret;
import remote.common.Ret.RetStatus;
import remote.common.SerObj;

public class RMIHandler {

	private final Integer id;

	private final Args args;

	private final Ret ret;

	public RMIHandler(int id, Args args) {
		this.id = id;
		this.args = args;
		this.ret = new Ret();
	}

	public int getId() {
		return id;
	}

	public Ret getRet() throws NoSuchElementException {
		return ret;
	}

	public void tackle() throws NoSuchElementException {
		try {
			SerObj type = args.get("TYPE");
			if (type == null) {
				throw new NoSuchElementException("No such arg TYPE");
			}
			//System.out.println(type.getValue());
			RMICall.Type retType = (RMICall.Type) type.getValue();
			SerObj so = null;
			if (retType.equals(RMICall.Type.CONTROL)) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				so = new SerObj();
				so.setName("RET_V");
				so.setValue("CONTROL¡¡SUCCESS");
			} else if (retType.equals(RMICall.Type.ACCESS)) {
				so = new SerObj();
				so.setName("RET_V");
				so.setValue("ACCESS¡¡SUCCESS");
			} else {
				so = new SerObj();
				so.setName("RET_V");
				so.setValue("NONE OP¡¡SUCCESS");
			}
			ret.setStatus(RetStatus.SUCCESS);
			ret.setRetObj(so);
		} catch (Exception e) {
			ret.setStatus(RetStatus.ERROR);
			ret.setError(e);
		} finally {
			synchronized (this) {
				notify();
			}
		}
	}
}
