package edu.teco.pavos.importer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Sends Data to the FROST-Server.
 */
public final class FrostSender {

    /**
     * Default constructor
     */
    private FrostSender() { }
    
    //TODO Create one with backchecking if really created


    /**
     * Sends the given JsonObject to the FROST-Server.
     * @param surl is the url to which information has to be sent.
     * @param json contains the information to send.
     */
    public static void sendToFrostServer(final String surl, final String json) {
    	Thread actualThread = new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    URL url = new URL(surl);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setDoOutput(true);
                    http.setFixedLengthStreamingMode(bytes.length);
                    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    http.setRequestProperty("Content-Encoding", "charset=UTF-8");
                    http.setRequestProperty("Accept", "application/json");
                    http.connect();
                    try {
                    	http.getOutputStream().write(bytes);
                    } catch (IOException e) {
                    	System.out.println(e.getLocalizedMessage());
                    }
                } catch (IOException ex) {
                	System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        actualThread.start();
        try {
            actualThread.join();
        } catch (InterruptedException e) {
        	System.out.println(e.getLocalizedMessage());
        }
    }

}
