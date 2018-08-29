package server.core.web;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.GeoGrid;
import server.core.grid.GeoGridManager;
import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.exceptions.SensorNotFoundException;
import server.core.grid.geojson.GeoJsonConverter;
import server.core.grid.polygon.GeoPolygon;
import server.database.Facade;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class WebWorker implements Runnable {
	
	Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private int statusCode = HttpStatus.SC_OK;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    public WebWorker(Socket socket) {
        clientSocket = socket;
    }
	
	@Override
	public void run() {
		try {
			
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        out = new PrintWriter(clientSocket.getOutputStream());
	        
	        String request = in.readLine();
	        if (request.startsWith("GET /") && request.endsWith(" HTTP/1.1")) {
	        	request = request.replaceFirst("GET /", "").replaceFirst(" HTTP/1.1", "");
	        }
	        
	        String[] splitRequest = request.split("\\?", 2);
	        String type = splitRequest[0];
			try {
				splitRequest = splitRequest[1].split("&");
				if (type.equals("getGeoJsonCluster")) {
					getGeoJsonCluster(splitRequest, out);
				} else if (type.equals("getGeoJsonSensor")) {
					getGeoJsonSensor(splitRequest, out);
				} else if (type.equals("reportSensor")) {
					reportSensor(splitRequest, out);
				} else if (type.equals("getObservationTypes")) {
					getObservationTypes(splitRequest, out);
				}
			} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | NullPointerException e) {
				statusCode = HttpStatus.SC_BAD_REQUEST;
				printOut(null);
			}
			
	        shutdownConnection();

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
	
	private void shutdownConnection() {
		out.close();
        try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getObservationTypes(String[] req, PrintWriter out) {
		GeoGridManager manager = GeoGridManager.getInstance();
	    printOut(manager.getAllProperties().toString());
	}

	private void reportSensor(String[] req, PrintWriter out) {
		String sensor = getParameter(req, "sensorID");
		String reason = getParameter(req, "reason");
		InetAddress ip = clientSocket.getInetAddress();
		logger.info("[Webinterface][Sensor-Reported] Sensor = " + sensor + ", reason = " + reason + ", ip = " + ip.getHostAddress());
	    printOut("Sensor reported successfully!");
	}

	private void getGeoJsonSensor(String[] req, PrintWriter out) {
		String result = null;
		try {
		String gridID = getParameter(req, "gridID");
		String sensorID = getParameter(req, "sensorID");
		String keyProperty = getParameter(req, "property");
		
		result = getLiveDataSensor(sensorID, gridID, keyProperty);
		} catch (IllegalArgumentException e) {
			shutdownConnection();
		}
	    printOut(result);
	}

	private void getGeoJsonCluster(String[] req, PrintWriter out) throws IllegalArgumentException {
		String fusedClusterIDs = getParameter(req, "clusterID");
		String keyProperty = getParameter(req, "property");
		String[] clusterIDs = fusedClusterIDs.split(",");
		String gridID = null;
		for (int i = 0; i < clusterIDs.length; i++) {
			if (i == 0) {
				gridID = clusterIDs[i].split(":")[0];
			} else {
				String gridID2 = clusterIDs[i].split(":")[0];
				if (!gridID.equals(gridID2)) {
					throw new IllegalArgumentException();
				}
			}
		}
		
		String result = null;
		try {
			String fusedTime = getParameter(req, "time");
			
			String[] time = fusedTime.split(",");
			
			String stepsString = getParameter(req, "steps");
			
			result = getDatabaseDataCluster(gridID, keyProperty, clusterIDs, time, stepsString);
		} catch(IllegalArgumentException e) {
			if (e.getMessage().equals("time")) {
				result = getLiveDataCluster(gridID, keyProperty, clusterIDs);
			} else {
				statusCode = HttpStatus.SC_BAD_REQUEST;
			}
		}
		if (result == null) {
			statusCode = HttpStatus.SC_BAD_REQUEST;
			throw new IllegalArgumentException();
		} else {
			printOut(result);
		}
	}
	
	private String getLiveDataSensor(String sensorID, String gridID, String keyProperty) {
		
		final GeoGrid grid = getGrid(gridID);
		Point2D.Double point = null;
		try {
			point = grid.getSensorLocation(sensorID);
			return GeoJsonConverter.convertSensorObservations(grid.getSensorObservation(sensorID, point), keyProperty, point);
		} catch (PointNotOnMapException | SensorNotFoundException | NullPointerException e) {
			statusCode = HttpStatus.SC_BAD_REQUEST;
		}
		return null;
	}
	
	private String getLiveDataCluster(String gridID, String keyProperty, String[] clusterIDs) {
		Collection<GeoPolygon> polygons = new HashSet<>();
		final GeoGrid grid = getGrid(gridID);
		for (int i = 0; i < clusterIDs.length; i++) {
			GeoPolygon polygon = null;
			try {
				polygon = grid.getPolygon(clusterIDs[i]);
			} catch (ClusterNotFoundException e) {
				statusCode = HttpStatus.SC_BAD_REQUEST;
			}
			if (polygon != null) {
				polygons.add(polygon);
			}
		}
		return GeoJsonConverter.convertPolygons(polygons, keyProperty);
	}
	
	private String getDatabaseDataCluster(String gridID, String keyProperty, String[] clusterIDs, String[] time, String stepsString) {
		if (time.length == 1) {
			
			Facade database = new Facade();
			Collection<ObservationData> observations = new HashSet<>();
			
			for (String clusterID : clusterIDs) {
				
				String val = database.getObservationData(clusterID, time[0], keyProperty);
				ObservationData data = new ObservationData();
				data.clusterID = clusterID;
				data.observationDate = time[0];
				data.observations.put(keyProperty, val);
				observations.add(data);
			}
			
			GeoGridManager manager = GeoGridManager.getInstance();
			return GeoJsonConverter.convertPolygonObservations(observations, keyProperty, manager.getGrid(gridID));
		} else if (time.length == 2) {
			
			DateTime dt1 = TimeUtil.getUTCDateTime(time[0]).toDateTime(DateTimeZone.UTC);
			DateTime dt2 = TimeUtil.getUTCDateTime(time[1]).toDateTime(DateTimeZone.UTC);
			long dt1Millis = dt1.getMillis();
			long dt2Millis = dt2.getMillis();
			long minMillis = Math.min(dt1Millis, dt2Millis);
			
			int steps = Integer.parseInt(stepsString);
			long diff = Math.abs(dt1Millis - dt2Millis) / steps;
			
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < steps; i++) {
				
				long currentMillis = minMillis + (long) steps * diff;
				DateTime dtCurrent = new DateTime(currentMillis, DateTimeZone.UTC);
				String[] currentTimestamp = new String[1];
				currentTimestamp[0] = TimeUtil.getUTCDateTimeString(dtCurrent.toLocalDateTime());
				builder.append(getDatabaseDataCluster(gridID, keyProperty, clusterIDs, time, stepsString));
				if (i < steps - 1) {
					builder.append(", ");
				}
			}
			
		}
		
		throw new IllegalArgumentException("Time format unacceptable.");
	}
	
	private GeoGrid getGrid(String gridID) {
		GeoGridManager gridManager = GeoGridManager.getInstance();
		if (!gridManager.isGridActive(gridID)) throw new IllegalArgumentException("Grid is not active and therefore can not be fetched.");
		return gridManager.getGrid(gridID);
	}
	
	private String getParameter(String[] source, String parameter) throws IllegalArgumentException {
		for (int i = 0; i < source.length; i++) {
			if (source[i].startsWith(parameter + "=")) {
				return source[i].replaceFirst(parameter + "=", "");
			}
		}
		throw new IllegalArgumentException(parameter);
	}
	
	private void printOut(String result) {
        // Start sending our reply, using the HTTP 1.1 protocol
        out.print("HTTP/1.1 " + statusCode + " \r\n"); 			// Version & status code
        out.print("Content-Type: text/plain\r\n"); 	// The type of data
        out.print("Connection: close\r\n"); 		// Will close stream
        out.print("\r\n"); 							// End of headers
        if (result != null) {
        	out.write(result);
        }
        if (statusCode != 200) {
        	printErr();
        }
	}

	private void printErr() {
		switch (statusCode) {
		case HttpStatus.SC_BAD_REQUEST:
			out.write("Error " + statusCode + " - Requested parameters do not match internal data.");
			break;
		}
	}
	
}
