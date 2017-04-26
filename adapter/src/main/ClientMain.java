package main;

import java.util.Map;

public interface ClientMain {

	void setPara(Map<String, String> paras);

	void start() throws Exception;

	void stop() throws Exception;

}
