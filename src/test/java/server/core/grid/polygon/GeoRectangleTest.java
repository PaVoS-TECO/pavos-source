package server.core.grid.polygon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import server.core.grid.exceptions.ClusterNotFoundException;
import server.core.grid.polygon.GeoPolygon;
import server.core.grid.polygon.GeoRectangle;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GeoRectangleTest {

	@Test
	public void testGenerateJson() {
		Point2D.Double start = new Point2D.Double(2.3, 1.7);
		Point2D.Double dim = new Point2D.Double(10.0, 5.0);
		List<Point2D.Double> points = new ArrayList<>();
		points.add(start);
		points.add(new Point2D.Double(start.getX() + dim.getX(), start.getY()));
		points.add(new Point2D.Double(start.getX() + dim.getX(), start.getY() + dim.getY()));
		points.add(new Point2D.Double(start.getX(), start.getY() + dim.getY()));
		
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(start.getX(), start.getY(), dim.getX(), dim.getY()), 1, 1, 0, "test");
		assertEquals(points, rect.getPoints());
		assertTrue(rect.getJson("pM10").matches("\\{ \"type\": \"FeatureCollection\", \"timestamp\": \".{4}-.{2}-.{2}T.{2}:.{2}:.{2}Z\","
				+ " \"observationType\": \"pM10\", \"features\": \\[ \\{ \"type\": \"Feature\", \"properties\": \\{ \"value\": null, "
				+ "\"clusterID\": \"test\", \"content\": \\[ \\] \\}, \"geometry\": \\{ \"type\": \"Polygon\", \"coordinates\": \\[ \\[ \\[ 2.3, 1.7\\],"
				+ " \\[ 12.3, 1.7\\], \\[ 12.3, 6.7\\], \\[ 2.3, 6.7\\], \\[ 2.3, 1.7\\]\\] \\] \\} \\}] \\}"));
	}
	
	@Test
	public void testAddValueAndGetNumberOfSensors() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 1, 1, 0, "test");
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		data.sensorID = "testSensorID";
		String property = "temperature_celsius";
		data.observations.put(property, "28.0");
		rect.addObservation(data);
		Collection<String> properties = new HashSet<>();
		int numTotal = rect.getNumberOfSensors();
		int numBla = rect.getNumberOfSensors("bla");
		properties.add(property);
		properties.add("bla");
		int numPropAndBla = rect.getNumberOfSensors(properties);
		assertEquals(1, numTotal);
		assertEquals(0, numBla);
		assertEquals(0, numPropAndBla);
		if (numTotal != 1 || numBla != 0) fail("Numbers of Sensors not requested correctly.");
		
		rect.resetObservations();
		numTotal = rect.getNumberOfSensors();
		assertEquals(0, numTotal);
	}
	
	@Test
	public void generateSubPolygons() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		Collection<GeoPolygon> subPolygons = rect.getSubPolygons();
		Iterator<GeoPolygon> it = subPolygons.iterator();
		assertEquals("[Point2D.Double[0.0, 0.0], Point2D.Double[5.0, 0.0], Point2D.Double[5.0, 2.5], Point2D.Double[0.0, 2.5]]",
				it.next().getPoints().toString());
		assertEquals("[Point2D.Double[5.0, 0.0], Point2D.Double[10.0, 0.0], Point2D.Double[10.0, 2.5], Point2D.Double[5.0, 2.5]]",
				it.next().getPoints().toString());
		assertEquals("[Point2D.Double[0.0, 2.5], Point2D.Double[5.0, 2.5], Point2D.Double[5.0, 5.0], Point2D.Double[0.0, 5.0]]",
				it.next().getPoints().toString());
		assertEquals("[Point2D.Double[5.0, 2.5], Point2D.Double[10.0, 2.5], Point2D.Double[10.0, 5.0], Point2D.Double[5.0, 5.0]]",
				it.next().getPoints().toString());
		try {
			rect.getSubPolygon("test-0_0");
		} catch (ClusterNotFoundException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void searchForPoint() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		rect.getSubPolygons();
		boolean contains = rect.contains(new Point2D.Double(1.4, 3.3), false);
		assertTrue(contains);
		if (!contains) fail("Incorrect Shape created.");
	}
	
	@Test
	public void getAllSensorIDs() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeNowString();
		String sensorID = "testSensorID";
		data.sensorID = sensorID;
		String property = "temperature_celsius";
		data.observations.put(property, "28.0");
		rect.addObservation(data);
		
		Collection<String> check = new HashSet<>();
		check.add(sensorID);
		assertEquals(check, rect.getAllSensorIDs());
		
		ObservationData data2 = new ObservationData();
		data2.observationDate = TimeUtil.getUTCDateTimeNowString();
		String sensorID2 = "testSensorID2";
		data2.sensorID = sensorID2;
		String property2 = "temperature_fahrenheit";
		data2.observations.put(property2, "61.0");
		rect.addObservation(data2);
		
		Collection<String> properties = new HashSet<>();
		properties.add(property);
		properties.add(property2);
		assertEquals(properties, rect.getAllProperties());
	}
	
	@Test
	public void getClusterObservations() {
		GeoRectangle rect = new GeoRectangle(new Rectangle2D.Double(0.0, 0.0, 10.0, 5.0), 2, 2, 1, "test");
		Collection<ObservationData> check = rect.getClusterObservations();
		check.forEach((data) -> {
			assertTrue(data.observations.isEmpty());
		});
	}
	
}
