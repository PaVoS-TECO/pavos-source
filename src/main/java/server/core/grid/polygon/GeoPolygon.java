package server.core.grid.polygon;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import server.core.grid.converter.GeoJsonConverter;
import server.core.grid.polygon.math.Tuple;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

import java.util.Set;

/**
 * A geographically oriented approach to polygons with double precision.<p>
 * Uses {@link Path2D.Double} for the polygon base.
 * Can contain sub-{@link GeoPolygon}s.
 * Provides a method to convert this polygon to a JSON-{@link String}.
 */
public abstract class GeoPolygon {
	
	public final boolean USE_SCALE;
	public final double X_OFFSET;
	public final double Y_OFFSET;
	public final double WIDTH;
	public final double HEIGHT;
	public final int ROWS;
	public final int COLUMNS;
	public final double SCALE;
	public final String ID;
	public final int LEVELS_AFTER_THIS;
	protected Path2D.Double path;
	protected Map<String, GeoPolygon> subPolygons;
	protected Map<String, ObservationData> sensorValues;
	protected ObservationData observationData;
	
	/**
	 * Creates a {@link GeoPolygon} with the given offsets, width, height and id.<p>
	 * Sets {@code USE_SCALE} to {@code false}!
	 * @param xOffset The horizontal offset
	 * @param yOffset The vertical offset
	 * @param width
	 * @param height
	 * @param rows How many times the {@link GeoPolygon} will be subdivided horizontally
	 * @param columns How many times the {@link GeoPolygon} will be subdivided vertically
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoPolygon}
	 */
	public GeoPolygon(double xOffset, double yOffset, double width, double height, int rows, int columns, int levelsAfterThis, String id) {
		this.USE_SCALE = false;
		this.X_OFFSET = xOffset;
		this.Y_OFFSET = yOffset;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.ID = id;
		this.SCALE = 0;
		this.LEVELS_AFTER_THIS = Math.max(levelsAfterThis, 0);;
		
		this.path = new Path2D.Double();
		this.subPolygons = new HashMap<>();
		this.sensorValues = new HashMap<>();
	}
	
	/**
	 * Creates a {@link GeoPolygon} with the given offsets, scale and id.<p>
	 * Sets {@code USE_SCALE} to {@code true}!
	 * @param xOffset The horizontal offset
	 * @param yOffset The vertical offset
	 * @param scale
	 * @param subdivisions How many times the {@link GeoPolygon} will be subdivided horizontally and vertically
	 * @param levelsAfterThis The depth of the map
	 * @param id The identifier {@link String} of this {@link GeoPolygon}
	 */
	public GeoPolygon(double xOffset, double yOffset, double scale, int subdivisions, int levelsAfterThis, String id) {
		this.USE_SCALE = true;
		this.X_OFFSET = xOffset;
		this.Y_OFFSET = yOffset;
		this.WIDTH = 0;
		this.HEIGHT = 0;
		this.ROWS = subdivisions;
		this.COLUMNS = subdivisions;
		this.ID = id;
		this.SCALE = scale;
		this.LEVELS_AFTER_THIS = Math.max(levelsAfterThis, 0);
		
		this.path = new Path2D.Double();
		this.subPolygons = new HashMap<>();
		this.sensorValues = new HashMap<>();
	} 
	
	/**
	 * Creates or overrides a map-entry with the new value in double-precision.
	 * @param sensorID The {@link String} ID of the Sensor. Not a cluster.
	 * @param observationData The {@link Double} value
	 */
	public void addValuesToCalculation(String sensorID, ObservationData data) {
		this.sensorValues.put(sensorID, data);
	}
	
	/**
	 * Returns the number of sensors in this {@link GeoPolygon} in total.
	 * @return numberOfSensors {@link int}
	 */
	public int getNumberOfSensors() {
		int sum = 0;
		
		for (Map.Entry<String, GeoPolygon> entry : this.subPolygons.entrySet()) {
			sum += entry.getValue().getNumberOfSensors();
		}
		sum += this.sensorValues.size();
		
		return sum;
	}
	
	/**
	 * Returns the number of sensors in this {@link GeoPolygon} that send data about a specific property.
	 * @return numberOfSensors {@link int}
	 */
	public int getNumberOfSensors(String property) {
		int sum = 0;
		
		for (Map.Entry<String, GeoPolygon> entry : this.subPolygons.entrySet()) {
			sum += entry.getValue().getNumberOfSensors(property);
		}
		for (Map.Entry<String, ObservationData> entry : this.sensorValues.entrySet()) {
			ObservationData obs = entry.getValue();
			if (obs.observations.containsKey(property)) {
				sum++;
			}
		}
		
		return sum;
	}
	
	/**
	 * Returns the number of sensors in this {@link GeoPolygon} that send data about a specific {@link Collection} of properties.
	 * @return numberOfSensors {@link int}
	 */
	public int getNumberOfSensors(Collection<String> properties) {
		int sum = 0;
		
		for (Map.Entry<String, GeoPolygon> entry : this.subPolygons.entrySet()) {
			sum += entry.getValue().getNumberOfSensors(properties);
		}
		for (Map.Entry<String, ObservationData> entry : this.sensorValues.entrySet()) {
			ObservationData obs = entry.getValue();
			boolean containsAll = true;
			for (String property : properties) {
				if (!obs.observations.containsKey(property)) {
					containsAll = false;
				}
			}
			if (containsAll) sum++;
		}
		
		return sum;
	}
	
	/**
	 * Updates all values of this {@link GeoPolygon} and it's sub-{@link GeoPolygon}s.<p>
	 * The process takes into account that sub-{@link GeoPolygon} may have more or less data
	 * about a certain property.
	 * It sums up all values (that were factored by the amount of data) and finally divides it
	 * by the total amount of data.
	 * This way, we achieve the most realistic representation of our data.
	 */
	public void updateValues() {
		ObservationData obs = new ObservationData();
		obs.observationDate = TimeUtil.getUTCDateTimeString();
		Set<Tuple<String, Integer, Double>> values = new HashSet<>();
		Set<String> properties = new HashSet<>();
		
		// create entries for sub-polygons & sensors (and save properties found)
		for (Map.Entry<String, GeoPolygon> entry : this.subPolygons.entrySet()) {
			GeoPolygon poly = entry.getValue();
			poly.updateValues();
			
			Map<String, String> obsTemp = poly.observationData.observations;
			for (String property : obsTemp.keySet()) {
				values.add(new Tuple<String, Integer, Double>(property
						, Integer.valueOf(poly.getNumberOfSensors(property)), Double.valueOf(obsTemp.get(property))));
				properties.add(property);
			}
		}
		for (Map.Entry<String, ObservationData> entry : this.sensorValues.entrySet()) {
			Map<String, String> obsTemp = entry.getValue().observations;
			for (String property : obsTemp.keySet()) {
				values.add(new Tuple<String, Integer, Double>(property
						, Integer.valueOf(1), Double.valueOf(obsTemp.get(property))));
				properties.add(property);
			}
		}
		
		// save data in ObservationData obs after calculation
		for (String property : properties) {
			double value = 0;
			int totalSensors = 0;
			
			for (Tuple<String, Integer, Double> tuple : values) {
				if (tuple.getFirstValue().equals(property)) {
					value += tuple.getThirdValue().doubleValue() * tuple.getSecondValue().doubleValue();
					totalSensors += tuple.getSecondValue().intValue();
				}
			}
			
			value = value / (double) totalSensors;
			obs.observations.put(property, String.valueOf(value));
		}
		this.observationData = obs;
	}
	
	/**
	 * Generates and sets the {@link Path2D.Double} of this {@link GeoPolygon}
	 */
	protected abstract void generatePath();
	
	/**
	 * Generates {@link GeoPolygon}s inside of this {@link GeoPolygon}.<p>
	 * Uses {@code SCALE}.
	 * @param subdivisions The amount of subdivisions
	 */
	protected abstract void generateSubPolygons(int subdivisions);
	
	/**
	 * Generates {@link GeoPolygon}s inside of this {@link GeoPolygon}.<p>
	 * Uses {@code WIDTH} and  {@code HEIGHT}
	 * @param xSubdivisions The amount of horizontal subdivisions
	 * @param ySubdivisions The amount of vertical subdivisions
	 */
	protected abstract void generateSubPolygons(int xSubdivisions, int ySubdivisions);
	
	/**
	 * Returns the current {@link GeoPolygon} as JSON-String
	 * @return json {@link String}
	 */
	public String getJson() {
		return GeoJsonConverter.convert(getPoints());
	}
	
	/**
	 * Returns true if the current {@link GeoPolygon} contains the specified {@link Point2D.Double}
	 * @param point {@link Point2D.Double}
	 * @return containsPoint {@link boolean}
	 */
	public boolean contains(Point2D.Double point) {
		return path.contains(point);
	}
	
	/**
	 * Returns a {@link Collection} of {@link Point2D.Double}s that make up the current {@link GeoPolygon}
	 * @return points {@code Collection<Point2D.Double>}
	 */
	public Collection<Point2D.Double> getPoints() {
		PathIterator pi = path.getPathIterator(null);
		double[] values = new double[6];
		Collection<Point2D.Double> points = new HashSet<>();

		while (!pi.isDone()) {
		    int type = pi.currentSegment(values);
		    if (type == PathIterator.SEG_LINETO) {
		    	points.add(new Point2D.Double(values[0], values[1]));
		    }
		    else {
		        // SEG_MOVETO, SEG_QUADTO, SEG_CUBICTO
		    }
		    pi.next();
		}
		return points;
	}
	
	/**
	 * Returns the ID of this {@link GeoPolygon}
	 * @return id {@link String}
	 */
	public String getID() {
		return this.ID;
	}
	
	/**
	 * Returns a {@link Set} of all {@link Entry}s in the format {@code Entry<String, GeoPolygon>}
	 * @return subPolygons {@code Set<Entry<String, GeoPolygon>>}
	 */
	public Set<Entry<String, GeoPolygon>> getSubPolygons() {
		return subPolygons.entrySet();
	}
	
}
