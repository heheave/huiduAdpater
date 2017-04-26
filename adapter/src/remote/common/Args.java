package remote.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Args implements Serializable{
	private  transient Map<String, SerObj> argsObjs;
	
	public Args() {
		this.argsObjs = new HashMap<>();
	}
	
	public SerObj get(String name) {
		return argsObjs.get(name);
	}
	
	public void add(SerObj so) {
		if (so == null) {
			throw new NullPointerException("Args cannot be null");
		} 
		argsObjs.put(so.getName(), so);
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException { 
		oos.defaultWriteObject();
		oos.writeInt(argsObjs.size());
		for (SerObj so: argsObjs.values()) {
			oos.writeObject(so);
		}
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException { 
		ois.defaultReadObject();
		int size = ois.readInt();
		argsObjs = new HashMap<String, SerObj>();
		for (int i = 0; i < size; i++) {
			SerObj so = (SerObj)ois.readObject();
			argsObjs.put(so.getName(), so);
		}
	}
	
}
