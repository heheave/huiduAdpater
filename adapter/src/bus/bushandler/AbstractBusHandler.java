package bus.bushandler;

abstract public class AbstractBusHandler implements BusHandler {

	private final int id;

	private final String name;

	private final String desc;

	protected AbstractBusHandler(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public int id() {
		return this.id;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String desc() {
		return this.desc;
	}

	@Override
	public void initSet(String name, Object value) {
		throw new RuntimeException("this method shouldn't be called");
	}

}
