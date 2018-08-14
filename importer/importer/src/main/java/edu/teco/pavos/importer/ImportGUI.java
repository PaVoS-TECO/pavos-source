package edu.teco.pavos.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		DataImporter importer = new DataImporter();
        importer.startImportingFileData();
        
        //getSensorUpData();
	}
	
	private static void getSensorUpData() {
		
		String path = System.getProperty("user.home") + File.separator;
        path += "Desktop/sensorup.csv";
		
		String baseUrl = "http://example.sensorup.com/v1.0/";
		JSONParser parser = new JSONParser();
		try {
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			
			System.out.println("Starting Getting Data.");
			int amount;
			int skip;
			int max = 25000;
			
			// ObservedProperties
			System.out.println("Waiting for Observed Property Informations from Server.");
			amount = getAmount(baseUrl, "ObservedProperties", parser);
			skip = 0;
			while (skip < amount && skip < max) {
				obsPropsGetter(baseUrl, parser, skip, amount, writer);
				skip += 1000;
			}
			System.out.println("Observed Properties Done.");
			
			// Sensors
			System.out.println("Waiting for Sensor Informations from Server.");
			amount = getAmount(baseUrl, "Sensors", parser);
			skip = 0;
			while (skip < amount && skip < max) {
				sensorGetter(baseUrl, parser, skip, amount, writer);
				skip += 1000;
			}
			System.out.println("Sensors Done.");
			
			// Locations
			System.out.println("Waiting for Location Informations from Server.");
			amount = getAmount(baseUrl, "Locations", parser);
			skip = 0;
			while (skip < amount && skip < max) {
				locationGetter(baseUrl, parser, skip, amount, writer);
				skip += 1000;
			}
			System.out.println("Locations Done.");
			
			// Things
			System.out.println("Waiting for Thing Informations from Server.");
			amount = getAmount(baseUrl, "Things", parser);
			skip = 0;
			while (skip < amount && skip < max) {
				thingGetter(baseUrl, parser, skip, amount, writer);
				skip += 1000;
			}
			System.out.println("Things Done.");
			
			// Datastreams
			System.out.println("Waiting for Datastream Informations from Server.");
			amount = getAmount(baseUrl, "Datastreams", parser);
			skip = 0;
			while (skip < amount && skip < max) {
				datastreamGetter(baseUrl, parser, skip, amount, writer);
				skip += 1000;
			}
			System.out.println("Datastreams Done.");
			
			// Observations
			System.out.println("Waiting for Observation Informations from Server.");
			amount = getAmount(baseUrl, "Observations", parser);
			skip = 0;
			while (skip < amount && skip < max) {
				observationGetter(baseUrl, parser, skip, amount, writer);
				skip += 1000;
			}
			System.out.println("Observations Done.");
			
			writer.close();
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	private static void obsPropsGetter(String baseUrl, JSONParser parser, int skip, int tot, PrintWriter writer)
			throws ParseException {
		int number = skip + 1;
		JSONArray arrObj = getValues(baseUrl, parser, skip, "ObservedProperties");
		for (Object obj : arrObj) {
			JSONObject value = (JSONObject) obj;
			String line = "observedProperty|" + value.get("@iot.id") + "|";
			line += value.get("name") + "|";
			line += value.get("description") + "|";
			line += value.get("definition");
			writer.println(line);
			System.out.println("Working on Observed Properties : " + number++ + " / " + tot);
		}
	}
	
	private static void sensorGetter(String baseUrl, JSONParser parser, int skip, int tot, PrintWriter writer)
			throws ParseException {
		int number = skip + 1;
		JSONArray arrObj = getValues(baseUrl, parser, skip, "Sensors");
		for (Object obj : arrObj) {
			JSONObject value = (JSONObject) obj;
			String line = "sensor|" + value.get("@iot.id") + "|";
			line += value.get("name") + "|";
			line += value.get("description") + "|";
			line += value.get("encodingType") + "|";
			line += value.get("metadata");
			writer.println(line);
			System.out.println("Working on Sensors : " + number++ + " / " + tot);
		}
	}
	
	private static void locationGetter(String baseUrl, JSONParser parser, int skip, int tot, PrintWriter writer)
			throws ParseException {
		int number = skip + 1;
		JSONArray arrObj = getValues(baseUrl, parser, skip, "Locations");
		for (Object obj : arrObj) {
			JSONObject value = (JSONObject) obj;
			String line = "location|" + value.get("@iot.id") + "|";
			line += value.get("name") + "|";
			line += value.get("description") + "|";
			line += value.get("encodingType") + "|";
			line += ((JSONObject) value.get("location")).toJSONString();
			writer.println(line);
			System.out.println("Working on Locations : " + number++ + " / " + tot);
		}
	}
	
	private static void thingGetter(String baseUrl, JSONParser parser, int skip, int tot, PrintWriter writer)
			throws ParseException {
		int number = skip + 1;
		JSONArray arrObj = getValues(baseUrl, parser, skip, "Things");
		for (Object obj : arrObj) {
			JSONObject value = (JSONObject) obj;
			String line = "thing|" + value.get("@iot.id") + "|";
			line += value.get("name") + "|";
			line += value.get("description") + "|";
			line += ((JSONObject) value.get("properties")).toJSONString() + "|";
			
			String id = "" + value.get("@iot.id");
			String locs = getDataForLink(baseUrl + "Things(" + id + ")/Locations");
			Object locats = parser.parse(locs);
			JSONObject locObj = (JSONObject) locats;
			JSONArray locArr = (JSONArray) locObj.get("value");
			for (Object locObjP : locArr) {
				JSONObject val = (JSONObject) locObjP;
				line += val.get("@iot.id") + ";";
			}
			
			writer.println(line);
			System.out.println("Working on Things : " + number++ + " / " + tot);
		}
	}
	
	private static void datastreamGetter(String baseUrl, JSONParser parser, int skip, int tot, PrintWriter writer)
			throws ParseException {
		int number = skip + 1;
		JSONArray arrObj = getValues(baseUrl, parser, skip, "DataStreams");
		for (Object obj : arrObj) {
			JSONObject value = (JSONObject) obj;
			String line = "dataStream|" + value.get("@iot.id") + "|";
			line += value.get("name") + "|";
			line += value.get("description") + "|";
			line += value.get("observationType") + "|";
			line += ((JSONObject) value.get("unitOfMeasurement")).toJSONString() + "|";
			
			String id = "" + value.get("@iot.id");
			String thing = getDataForLink(baseUrl + "DataStreams(" + id + ")/Thing");
			String obsProp = getDataForLink(baseUrl + "DataStreams(" + id + ")/ObservedProperty");
			String sensor = getDataForLink(baseUrl + "DataStreams(" + id + ")/Sensor");
			Object oT = parser.parse(thing);
			Object oO = parser.parse(obsProp);
			Object oS = parser.parse(sensor);
			JSONObject jT = (JSONObject) oT;
			JSONObject jO = (JSONObject) oO;
			JSONObject jS = (JSONObject) oS;
			line += jT.get("@iot.id") + "|";
			line += jO.get("@iot.id") + "|";
			line += jS.get("@iot.id") + "|";
			
			String opt = "";
			Object optional = value.get("observedArea");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "|";
			opt = "";
			optional = value.get("phenomenonTime");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "|";
			opt = "";
			optional = value.get("resultTime");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "|";
			
			writer.println(line);
			System.out.println("Working on DataStreams : " + number++ + " / " + tot);
		}
	}
	
	private static void observationGetter(String baseUrl, JSONParser parser, int skip, int tot, PrintWriter writer)
			throws ParseException {
		int number = skip + 1;
		JSONArray arrObj = getValues(baseUrl, parser, skip, "Observations");
		for (Object obj : arrObj) {
			JSONObject value = (JSONObject) obj;
			String line = "observation|" + value.get("@iot.id") + "|";
			line += value.get("phenomenonTime") + "|";
			line += value.get("result") + "|";
			line += value.get("resultTime") + "|";
			
			String id = "" + value.get("@iot.id");
			String ds = getDataForLink(baseUrl + "Observations(" + id + ")/Datastream");
			Object oD = parser.parse(ds);
			JSONObject jD = (JSONObject) oD;
			line += jD.get("@iot.id") + "|";
			
			String foi = getDataForLink(baseUrl + "Observations(" + id + ")/FeatureOfInterest");
			Object ofoi = parser.parse(foi);
			JSONObject jfoi = (JSONObject) ofoi;
			JSONObject newfoi = new JSONObject();
			newfoi.put("name", jfoi.get("name"));
			newfoi.put("description", jfoi.get("description"));
			newfoi.put("encodingType", jfoi.get("encodingType"));
			newfoi.put("feature", jfoi.get("feature"));
			line += newfoi.toJSONString() + "|";
			
			String opt = "";
			Object optional = value.get("resultQuality");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "|";
			opt = "";
			optional = value.get("validTime");
			if (optional != null) {
				opt = "" + optional;
			}
			line += opt + "|";
			opt = "";
			optional = value.get("parameters");
			if (optional != null) {
				JSONObject op = (JSONObject) optional;
				opt = "" + op.toJSONString();
			}
			line += opt + "|";
			
			writer.println(line);
			System.out.println("Working on Observations : " + number++ + " / " + tot);
		}
	}
	
	private static JSONArray getValues(String baseUrl, JSONParser parser, int skip, String type)
			throws ParseException {
		String currentLink = baseUrl + type + "?$top=1000&$skip=" + skip;
		System.out.println("Waiting for " + type + " from Server.");
		String data = getDataForLink(currentLink);
		Object o = parser.parse(data);
		JSONObject dataObj = (JSONObject) o;
		return (JSONArray) dataObj.get("value");
	}
	
	private static int getAmount(String baseUrl, String type, JSONParser parser)
			throws ParseException {
		String currentLink = baseUrl + type + "?$top=1";
		String data = getDataForLink(currentLink);
		Object o = parser.parse(data);
		JSONObject dataObj = (JSONObject) o;
		return Integer.parseInt("" + dataObj.get("@iot.count"));
	}
	
	private static String getDataForLink(String link) {
		try {
			URL url = new URL(link);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("Content-Encoding", "charset=UTF-8");
			http.setRequestProperty("Accept", "application/json");
			http.connect();
			String allInput = "";
			try {
			    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
			    String inputLine;

			    while ((inputLine = in.readLine()) != null) {
			    	allInput += inputLine;
			    }
			    in.close();
			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
			}
	    	return allInput;
		} catch (MalformedURLException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (ProtocolException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
    	return "";
	}

}
