package remote.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Ret implements Serializable{
	public static enum RetStatus {
		NONE, SUCCESS, ERROR, FAILURE
	}
	
	private RetStatus status;
	
	private SerObj retObj;
	
	private Exception error;
	
	public Ret(RetStatus status, SerObj retObj, Exception error) {
		this.status = status;
		this.retObj = retObj;
		this.error = error;
	}
	
	public Ret(RetStatus status) {
		this(status, null, null);
	}
	
	public Ret() {
		this(RetStatus.NONE);
	}

	public RetStatus getStatus() {
		return status;
	}

	public SerObj getRetObj() {
		return retObj;
	}

	public void setStatus(RetStatus status) {
		this.status = status;
	}

	public void setRetObj(SerObj retObj) {
		this.retObj = retObj;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}
	
}
