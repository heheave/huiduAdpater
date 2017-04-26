package exception;

@SuppressWarnings("serial")
public class UnIDError extends RuntimeException {
	private final long tid;

	public UnIDError(long id) {
		this.tid = id;
	}

	@Override
	public String toString() {
		return "UNID, thread: " + tid;
	}

}
