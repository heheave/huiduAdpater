package exception;

@SuppressWarnings("serial")
public class ParserError extends RuntimeException {

	private String type;

	private String error_token;

	public ParserError(String type, String error_token) {
		this.type = type;
		this.error_token = error_token;
	}

	@Override
	public String toString() {
		return type + " parser error near: " + error_token;
	}

}
