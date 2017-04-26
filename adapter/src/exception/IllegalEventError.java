package exception;

import event.Event;

@SuppressWarnings("serial")
public class IllegalEventError extends RuntimeException {

	private final Event e;

	public IllegalEventError(Event e) {
		this.e = e;
	}

	@Override
	public String toString() {
		return "Illegal event: event code is " + e.getCode() + " and event content is " + e.toString();
	}

}
