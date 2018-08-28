package server.core.web;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

	private static final int PORT = 7700;
	private static final int BACKLOG = 10000;
	private static boolean SHUTDOWN = false;
	private static WebServer instance;
	
	private WebServer() {
		
	}
	
	public static WebServer getInstance() {
		if (instance == null) {
			instance = new WebServer();
		}
		return instance;
	}
	
	public static void main(String[] args) {
		try {
			
			SHUTDOWN = false;
			ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG);
			while (!SHUTDOWN) {
				Socket defaultSocket = serverSocket.accept();
				Thread t = new Thread(new WebWorker(defaultSocket));
				t.start();
				t.join();
				defaultSocket.close();
			}
			serverSocket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		SHUTDOWN = true;
	}

}
