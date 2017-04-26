package event;

public interface Event {

	enum CODE {
		PRE, IN, OUT, DROP, RC_RET, NONE, GET, CROSS
	};

	CODE getCode();

	void changeCode(CODE code);

	int getPipeId();

	void setPipeId(int pipeid);
}
