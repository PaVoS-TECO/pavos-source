package edu.teco.pavos.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Implementation of the FileReaderStrategy interface for CSV files.
 */
public class CSVReaderStrategy implements FileReaderStrategy {
	
	private int errorlines = 0;
	private String iotIDImport;
	private String url;
	private static String OBSERVED_PROPERTY = "observedProperty";
	private static String SENSOR = "sensor";
	private static String LOCATION = "location";
	//private static String HISTORIC_LOCATION = "historicLocation";
	private static String THING = "thing";
	private static String DATASTREAM = "dataStream";
	private static String OBSERVATION = "observation";

    /**
     * Default constructor
     * @param url is the destination server for the data.
     */
    public CSVReaderStrategy(String url) {
    	this.url = url;
    	this.iotIDImport = ""; //import.date.YYYY/MM/DD.from.file.filename.csv/
    }

    /**
     * Reads from a File as specified by the FilePath and sends the information in
     * it to the FROST-Server using the FrostSender that was provided.
     * @param file Is the File to Import.
     */
    public void sendFileData(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] separated = line.split("|");
				if (separated.length >= 2) {
					if (separated[0].equals(OBSERVED_PROPERTY) && separated.length >= 5) {
						this.importObservedProperty(separated);
					} else if (separated[0].equals(SENSOR) && separated.length >= 6) {
						this.importSensor(separated);
					} else if (separated[0].equals(LOCATION) && separated.length >= 6) {
						this.importLocation(separated);
					} else if (separated[0].equals(THING) && separated.length >= 6) {
						this.importThing(separated);
					} else if (separated[0].equals(DATASTREAM) && separated.length >= 12) {
						this.importDataStream(separated);
					} else if (separated[0].equals(OBSERVATION) && separated.length >= 10) {
						this.importObservation(separated);
					} else {
						this.errorlines++;
					}
				} else {
					this.errorlines++;
				}
			}
			br.close();
			System.out.println(this.errorlines);
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
    }
    
    private void importObservedProperty(String[] data) {
    	JSONObject obj = new JSONObject();
        obj.put("@iot.id", this.iotIDImport + data[1]);
        obj.put("name", data[2]);
        obj.put("description", data[3]);
        obj.put("definition", data[4]);
        String json = obj.toJSONString();
        if (!FrostSender.sendSafeToFrostServer(this.url, json)) {
        	this.errorlines++;
        }
    }
    
    private void importSensor(String[] data) {
    	JSONObject obj = new JSONObject();
		obj.put("@iot.id", iotIDImport + data[1]);
        obj.put("name", data[2]);
        obj.put("description", data[3]);
        obj.put("encodingType", data[4]);
        obj.put("metadata", data[5]);
        String json = obj.toJSONString();
        if (!FrostSender.sendSafeToFrostServer(url, json)) {
        	this.errorlines++;
        }
    }
    
    private void importLocation(String[] data) {
    	JSONObject obj = new JSONObject();
		obj.put("@iot.id", iotIDImport + data[1]);
        obj.put("name", data[2]);
        obj.put("description", data[3]);
        obj.put("encodingType", data[4]);
        
        JSONParser parser = new JSONParser();
		try {
			Object o = parser.parse(data[5]);
			JSONObject location = (JSONObject) o;
	        obj.put("location", location);
	        String json = obj.toJSONString();
	        if (!FrostSender.sendSafeToFrostServer(url, json)) {
	        	this.errorlines++;
	        }
		} catch (ParseException e) {
			this.errorlines++;
			System.out.println(e.getLocalizedMessage());
		}
    }
    
    private void importThing(String[] data) {
    	JSONObject obj = new JSONObject();
		obj.put("@iot.id", iotIDImport + data[1]);
        obj.put("name", data[2]);
        obj.put("description", data[3]);
        
        JSONParser parser = new JSONParser();
		try {
			if (!data[4].equals("")) {
				Object o = parser.parse(data[4]);
				JSONObject properties = (JSONObject) o;
		        obj.put("properties", properties);
			}
	        
	        JSONArray locations = new JSONArray();
        	String[] ids = data[5].split(";");
        	for (String id : ids) {
        		JSONObject iotID = new JSONObject();
        		iotID.put("@iot.id", iotIDImport + id);
        		locations.add(iotID);
        	}
        	obj.put("Locations", locations);
        	
        	/*JSONArray histLocations = new JSONArray();
        	ids = data[6].split(";");
        	for (String id : ids) {
        		JSONObject iotID = new JSONObject();
        		iotID.put("@iot.id", iotIDImport + id);
        		histLocations.add(iotID);
        	}
        	obj.put("HistoricalLocations", histLocations);*/
        	
        	String json = obj.toJSONString();
            if (!FrostSender.sendSafeToFrostServer(url, json)) {
            	this.errorlines++;
            }
		} catch (ParseException e) {
			this.errorlines++;
			System.out.println(e.getLocalizedMessage());
		}
        
    }
    
    private void importDataStream(String[] data) {
    	JSONObject obj = new JSONObject();
		obj.put("@iot.id", iotIDImport + data[1]);
        obj.put("name", data[2]);
        obj.put("description", data[3]);
        obj.put("observationType", data[4]);
        
        JSONParser parser = new JSONParser();
		try {
			Object o = parser.parse(data[5]);
			JSONObject uom = (JSONObject) o;
	        obj.put("unitOfMeasurement", uom);
	        
	        JSONObject thing = new JSONObject();
	        thing.put("@iot.id", iotIDImport + data[6]);
	        obj.put("Thing", thing);
	        
	        JSONObject observerProperty = new JSONObject();
	        observerProperty.put("@iot.id", iotIDImport + data[7]);
	        obj.put("ObservedProperty", observerProperty);
	        
	        JSONObject sensor = new JSONObject();
	        sensor.put("@iot.id", iotIDImport + data[8]);
	        obj.put("Sensor", sensor);
	        

	        if (!data[9].equals("")) {
	        	obj.put("observedArea", data[9]);
	        }
	        if (!data[10].equals("")) {
	        	obj.put("phenomenonTime", data[10]);
	        }
	        if (!data[11].equals("")) {
	        	obj.put("resultTime", data[11]);
	        }
	        
	        String json = obj.toJSONString();
	        if (!FrostSender.sendSafeToFrostServer(url, json)) {
	        	this.errorlines++;
	        }
		} catch (ParseException e) {
			this.errorlines++;
			System.out.println(e.getLocalizedMessage());
		}
    }
    
    private void importObservation(String[] data) {
    	JSONObject obj = new JSONObject();
		obj.put("@iot.id", iotIDImport + data[1]);
        obj.put("phenomenonTime", data[2]);
        obj.put("result", data[3]);
        obj.put("resultTime", data[4]);
        
        JSONObject dataStream = new JSONObject();
        dataStream.put("@iot.id", iotIDImport + data[5]);
        obj.put("Datastream", dataStream);
        
        JSONParser parser = new JSONParser();
		try {
			Object o = parser.parse(data[6]);
			JSONObject foi = (JSONObject) o;
	        obj.put("FeatureOfInterest", foi);
	        
	        if (!data[7].equals("")) {
	        	obj.put("resultQuality", data[7]);
	        }
	        if (!data[8].equals("")) {
	        	obj.put("validTime", data[8]);
	        }
	        if (!data[9].equals("")) {
	        	Object ob = parser.parse(data[9]);
				JSONObject param = (JSONObject) ob;
		        obj.put("parameters", param);
	        }
	        
	        String json = obj.toJSONString();
	        FrostSender.sendToFrostServer(url, json);
		} catch (ParseException e) {
			this.errorlines++;
			System.out.println(e.getLocalizedMessage());
		}
    }

}
