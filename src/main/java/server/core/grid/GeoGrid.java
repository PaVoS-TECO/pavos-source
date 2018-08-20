package server.core.grid;

import java.awt.geom.Point2D;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.polygon.GeoPolygon;

public abstract class GeoGrid {
	
	public final Point2D.Double MAP_BOUNDS;
	public final int ROWS;
	public final int COLUMNS;
	public final int MAX_LEVEL;
	protected Map<String, GeoPolygon> polygons;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public GeoGrid(Point2D.Double mapBounds, int rows, int columns, int maxLevel) {
		this.MAP_BOUNDS = mapBounds;
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.MAX_LEVEL = maxLevel;
	}
	
	/**
	 * Returns the ID for the Cluster which contains the {@link Point2D.Double} point. If the point is not on the mapped area, returns {@link null}!<p>
	 * The ID is also specified by the {@link int} level of grid-scaling.
	 * The level ranges from 0 to {@code MAX_LEVEL}.
	 * A higher level means more {@link GeoPolygon}s to check but gives a better representation for the selected {@link Point2D.Double}.
	 * @param point The {@link Point2D.Double} that is contained by the cluster that we are searching for
	 * @param level The {@link int} that controls the level of detail
	 * @return id The cluster id
	 */
	public abstract String getClusterID(Point2D.Double point, int level) throws PointNotOnMapException;
	
	/**
	 * Generates the {@link GeoPolygon}s that make up all clusters of the grid.
	 */
	public abstract void generateGeoPolygons();
	
}
