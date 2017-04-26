package remote.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SerObj  implements Serializable{
	
	private String name;
	
	private Serializable value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}
}
