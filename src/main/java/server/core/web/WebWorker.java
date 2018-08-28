package server.core.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WebWorker implements Runnable {
	
	Socket clientSocket;

    public WebWorker(Socket socket) {
        clientSocket = socket;
    }
	
	@Override
	public void run() {
		try {
			
	        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

	        // Start sending our reply, using the HTTP 1.1 protocol
	        out.print("HTTP/1.1 200 \r\n"); 			// Version & status code
	        out.print("Content-Type: text/plain\r\n"); 	// The type of data
	        out.print("Connection: close\r\n"); 		// Will close stream
	        out.print("\r\n"); 							// End of headers

//	        String line;
//	        while ((line = in.readLine()) != null) {
//	          if (line.length() == 0)
//	            break;
//	          out.print(line + "\r\n");
//	        }
	        
	        
	        
	        out.close();
	        in.close();
	        clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

}
