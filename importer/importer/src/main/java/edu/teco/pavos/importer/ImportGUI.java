package edu.teco.pavos.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Main Class of the Importer
 */
public final class ImportGUI {
	
	private ImportGUI() { }
	
	/**
	 * Main method
	 * @param args of main
	 */
	public static void main(String[] args) {
		//DataImporter importer = new DataImporter();
        //importer.startImportingFileData();
    	DummyReaderStrategy dummy = new DummyReaderStrategy();
		File file = new File("");
		dummy.sendFileData(file);
    	
    	/*ArrayList<String> things = getAllThingIotIds();
    	for (String thing : things) {
    		delete(thing);
    	}*/
	}
    
    private static ArrayList<String> getAllThingIotIds() {
    	try {
			String surl = "http://pavos-01.teco.edu/8080/v1.0/Things";
			URL url = new URL(surl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("Content-Encoding", "charset=UTF-8");
			http.setRequestProperty("Accept", "application/json");
			http.connect();
			try {
			    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
			    String inputLine;
			    ArrayList<String> ids = new ArrayList<String>();

			    while ((inputLine = in.readLine()) != null) {
			    	if (inputLine.trim().startsWith("\"@iot.id\"")) {
			    		String[] cont = inputLine.split("\"");
			    		if (cont.length >= 4) {
			    			ids.add(cont[3]);
			    		}
			    	}
			    }
			    in.close();
			    return ids;
			} catch (IOException e) {
			    //TODO: Handle Exception
			    e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ArrayList<String>();
    }
    
    private static void delete(String id) {
    	try {
			String surl = "http://pavos-01.teco.edu/8080/v1.0/Things('" + id + "')";
			URL url = new URL(surl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("DELETE");
            http.setDoInput(true);
            http.setDoOutput(true);
			http.connect();
			try {
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();
                System.out.println(response.toString());
            } catch (IOException e) {
                //TODO: Handle Exception
                e.printStackTrace();
            }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
