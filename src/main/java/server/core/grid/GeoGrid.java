package server.core.grid;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.polygon.GeoPolygon;
import server.core.properties.KafkaTopicAdmin;
import server.database.Facade;
import server.transfer.data.ObservationData;

/**
 * A geographically oriented approach to a polygon-tiled map.<br>
 * Uses {@link GeoPolygon}s to tile the map.
 */
public abstract class GeoGrid {
	
	public final Point2D.Double MAP_BOUNDS;
	public final int ROWS;
	public final int COLUMNS;
	public final int MAX_LEVEL;
	public final String GRID_ID;
	protected List<GeoPolygon> polygons = new ArrayList<>();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private GeoGridManager manager = GeoGridManager.getInstance();
	
	public GeoGrid(Point2D.Double mapBounds, int rows, int columns, int maxLevel, String gridID) {
		this.MAP_BOUNDS = mapBounds;
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.MAX_LEVEL = maxLevel;
		this.GRID_ID = gridID;
		
		this.manager.addGeoGrid(this);
	}
	
	/**
	 * Adds a single observation to the {@link GeoGrid}.
	 * Searches for the smallest cluster to put the values into.
	 * @param sensorID The id of the sensor
	 * @param location The {@link Point2D.Double} point on the map
	 * @param data The {@link ObservationData} to be added
	 */
	public void addObservation(Point2D.Double location, ObservationData data) {
		GeoPolygon targetPolygon = null;
		try {
			targetPolygon = getPolygonContaining(location, MAX_LEVEL);
			targetPolygon.addObservation(data);
		} catch (PointNotOnMapException e) {
			logger.warn("Could not add Observation to map. Point '" + location 
					+ "' not in map boundaries! SensorID: " + data.sensorID + " " + e);
		}
	}
	
	public void close() {
		this.manager.removeGeoGrid(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		GeoGrid oGrid = (GeoGrid) o;
		return (this.GRID_ID.equals(oGrid.GRID_ID));
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point. If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives a better representation for the selected {@link Point2D.Double}.
	 * @param point The {@link Point2D.Double} that is contained by the cluster that we are searching for
	 * @param level The {@link int} that controls the level of detail
	 * @return id The cluster id
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	public String getClusterID(Point2D.Double point, int level) throws PointNotOnMapException {
		return getPolygonContaining(point, level).ID;
	}
	
	public Collection<ObservationData> getGridObservations() {
		Collection<ObservationData> observations = new ArrayList<>();
		for (GeoPolygon polygon : polygons) {
			observations.addAll(polygon.getSubObservations());
			observations.add(polygon.cloneObservation());
		}
		return observations;
	}
	
	/**
	 * Returns the {@link String} topic for kafka, in which all values should be written.
	 * @return topic {@link String}
	 */
	public String getOutputTopic() {
		return this.GRID_ID + ".out";
	}
	
	/**
	 * Returns the {@link GeoPolygon} that is associated with the specified {@link String} clusterID.
	 * @param clusterID {@link String}
	 * @return polygon {@link GeoPolygon}
	 */
	public GeoPolygon getPolygon(String clusterID) {
		for (GeoPolygon polygon : this.polygons) {
			if (polygon.ID.equals(clusterID)) {
				return polygon;
			}
		}
		return null;
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point. If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives a better representation for the selected {@link Point2D.Double}.
	 * @param point The {@link Point2D.Double} that is contained by the cluster that we are searching for
	 * @param level The {@link int} that controls the level of detail
	 * @return id The cluster id
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	public GeoPolygon getPolygonContaining(Point2D.Double point, int level) throws PointNotOnMapException {
		GeoPolygon targetPolygon = getPolygonContainingPointFromCollection(point, polygons);
		
		// t2Polygon, meaning: tier-2-polygon
		GeoPolygon t2Polygon = targetPolygon;
		int levelBounds = Math.min(level, MAX_LEVEL);
		for (int currentLevel = 1; currentLevel < levelBounds; currentLevel++) {
			try {
				t2Polygon = getPolygonContainingPointFromCollection(point, t2Polygon.getSubPolygons());
				targetPolygon = t2Polygon;
			} catch (PointNotOnMapException e) {
				break;
			}
		}
		return targetPolygon;
	}
	
	/**
	 * Produces messages for the output kafka-topic.
	 * Each message contains a single {@link ObservationData} object.
	 * This method Produces recursively and starts with the smallest clusters.
	 */
	public void produceSensorDataMessages() {
		String topic = getOutputTopic();
		KafkaTopicAdmin kAdmin = KafkaTopicAdmin.getInstance();
		if (!kAdmin.existsTopic(topic)) {
			kAdmin.createTopic(topic);
		}
		for (GeoPolygon polygon : polygons) {
			polygon.produceSensorDataMessage(topic);
		}
	}
	
	/**
	 * Updates all values of this {@link GeoGrid} and it's {@link GeoPolygon}s.<p>
	 * The process takes into account that {@link GeoPolygon} may have more or less data
	 * about a certain property.
	 * It sums up all values (that were factored by the amount of data) and finally divides it
	 * by the total amount of data.
	 * This way, we achieve the most realistic representation of our data.
	 */
	public void updateObservations() {
		for (GeoPolygon polygon : polygons) {
			polygon.updateObservations();
		}
		updateDatabase();
	}
	
	private void updateDatabase() {
		Facade database = new Facade();
		Collection<ObservationData> observations = getGridObservations();
		for (ObservationData entry : observations) {
			database.addObservationData(entry);
		}
	}
	
	/**
	 * Generates the {@link GeoPolygon}s that make up all clusters of the grid.
	 */
	protected abstract void generateGeoPolygons();

	/**
	 * Returns the first {@link GeoPolygon} from the {@link Collection} of {@link GeoPolygon}s that contains the specified {@link Point2D.Double}.
	 * @param point {@link Point2D.Double}
	 * @param collection {@link Collection} of {@link GeoPolygon}s
	 * @return polygonContainingPoint {@link GeoPolygon}
	 * @throws PointNotOnMapException Thrown when the Point was not located in the map-boundaries.
	 */
	protected GeoPolygon getPolygonContainingPointFromCollection(Double point, Collection<GeoPolygon> collection) throws PointNotOnMapException {
		for (GeoPolygon entry : collection) {
			if (entry.contains(point, false)) {
				return entry;
			}
		}
		throw new PointNotOnMapException(point);
	}
	
}
