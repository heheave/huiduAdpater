package rmi;

public interface RemoteControlListener {

	void cmd(String cmdId, String cmdType, String massage);

}
