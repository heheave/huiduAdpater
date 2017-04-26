package rmi;

import event.ControlEvent;

public interface Server {

	void start() throws Exception;

	void stop();

	void setListener(RemoteControlListener rcl);

	void remoteRet(ControlEvent retValue);

}
