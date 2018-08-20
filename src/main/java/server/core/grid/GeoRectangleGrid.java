package server.core.grid;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Map;
import java.util.Map.Entry;

import server.core.grid.config.Seperators;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.polygon.GeoPolygon;
import server.core.grid.polygon.GeoRectangle;

import java.util.Set;

public class GeoRectangleGrid extends GeoGrid {
	
	public GeoRectangleGrid(Point2D.Double mapBounds, int rows, int columns, int maxLevel) {
		super(mapBounds, rows, columns, maxLevel);
		generateGeoPolygons();
	}
	
	@Override
	public String getClusterID(Double point, int level){
		GeoPolygon targetPolygon;
		try {
			targetPolygon = getPolygonContainingPoint(point, polygons.entrySet());
		} catch (PointNotOnMapException e) {
			logger.error("Point is outside of mapped area. Can not get clusterID. Returning null." + e);
			return null; //TODO Find a better way to deal with this error
		}
		
		// t2Polygon, sprich: tier-2-polygon
		GeoPolygon t2Polygon = targetPolygon;
		int levelBounds = Math.min(level, MAX_LEVEL);
		
		for (int currentLevel = 1; currentLevel < levelBounds; currentLevel++) {
			try {
				t2Polygon = getPolygonContainingPoint(point, t2Polygon.getSubPolygons());
				targetPolygon = t2Polygon;
			} catch (PointNotOnMapException e) {
				break;
			}
		}
		return targetPolygon.ID;
	}

	@Override
	public void generateGeoPolygons() {
		double width = MAP_BOUNDS.getX() / (double) COLUMNS;
		double height = MAP_BOUNDS.getY() / (double) ROWS;
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				double xOffset = (double) col * width;
				double yOffset = (double) row * height;
				String id = String.valueOf(row) + Seperators.ROW_COLUMN_SEPERATOR + String.valueOf(col);
				
				GeoRectangle subPolygon = new GeoRectangle(xOffset, yOffset, width, height
						, ROWS, COLUMNS, (MAX_LEVEL - 1), id);
				polygons.put(id, subPolygon);
			}
		}
	}
	
	private GeoPolygon getPolygonContainingPoint(Double point, Set<Entry<String, GeoPolygon>> set) throws PointNotOnMapException {
		for (Map.Entry<String, GeoPolygon> entry : set) {
			GeoPolygon p = entry.getValue();
			if (p.contains(point)) {
				return p;
			}
		}
		throw new PointNotOnMapException(point);
	}
	
}
