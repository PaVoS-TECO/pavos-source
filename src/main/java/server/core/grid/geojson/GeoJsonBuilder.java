package server.core.grid.geojson;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.joda.time.LocalDateTime;

import server.core.grid.GeoGrid;
import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public final class GeoJsonBuilder {
	
	private static final String COMMA = ", ";
	private final String keyProperty;
	private String ldtString;
	private StringBuilder builder;
	private StringBuilder polygonsBuilder;
	
	public GeoJsonBuilder(String keyProperty) {
		this.keyProperty = keyProperty;
		
		this.builder = new StringBuilder();
		this.polygonsBuilder = new StringBuilder();
	}
	
	public void addGeoPolygons(Collection<GeoPolygon> geoPolygons) {
		StringBuilder polyBuilder = new StringBuilder();
		
		int countFeature = 1;
		for (GeoPolygon geoPolygon : geoPolygons) {
			polyBuilder.append(geoPolygonToString(geoPolygon));
			if (countFeature < geoPolygons.size()) {
				polyBuilder.append(COMMA);
			}
			countFeature++;
		}
		this.polygonsBuilder.append(polyBuilder.toString());
	}
	
	public void addDBObservations(Collection<ObservationData> observations, GeoGrid geoGrid) {
		Collection<GeoPolygon> geoPolygons = new HashSet<>();
		StringBuilder polyBuilder = new StringBuilder();
		
		int countFeature = 1;
		for (ObservationData data : observations) {
			GeoPolygon geoPolygon = geoGrid.getPolygon(data.clusterID);
			polyBuilder.append(geoPolygonToStringQuick(data, geoPolygon.getSubPolygons(), geoPolygon.getPoints()));
			if (countFeature < geoPolygons.size()) {
				polyBuilder.append(COMMA);
			}
		}
			
		this.polygonsBuilder.append(polyBuilder.toString());
	}
	
	private String geoPolygonToString(GeoPolygon geoPolygon) {
		return geoPolygonToStringQuick(geoPolygon.cloneObservation(), geoPolygon.getSubPolygons(), geoPolygon.getPoints());
	}
	
	private String geoPolygonToStringQuick(ObservationData data, List<GeoPolygon> subPolygons, List<Point2D.Double> points) {
		LocalDateTime ldt = TimeUtil.getUTCDateTime(data.observationDate);
		if (ldtString == null || ldt.isAfter(TimeUtil.getUTCDateTime(ldtString))) {
			ldtString = data.observationDate;
		}
		
		StringBuilder polyBuilder = new StringBuilder();
		polyBuilder.append("{ " + toSProperty("type", "Feature") + COMMA);
		polyBuilder.append(toEntry("properties") + ": { ");
		polyBuilder.append(toNProperty("value", data.observations.get(keyProperty)) + COMMA);
		polyBuilder.append(toSProperty("clusterID", data.clusterID) + COMMA);
		polyBuilder.append(toEntry("content") + ": [ ");
		int count = 1;
		for (GeoPolygon sub2Polygon : subPolygons) {
			polyBuilder.append(toEntry(sub2Polygon.ID));
			if (count < subPolygons.size()) {
				polyBuilder.append(COMMA);
			}
			count++;
		}
		polyBuilder.append("] }" + COMMA);
		polyBuilder.append(toEntry("geometry") + ": { ");
		polyBuilder.append(toSProperty("type", "Polygon") + COMMA);
		polyBuilder.append(toEntry("coordinates") + ": [ [ ");
		count = 1;
		Point2D.Double tempPoint = null;
		for (Point2D.Double point : points) {
			polyBuilder.append("[ " + point.getX() + COMMA + point.getY() + "]");
			if (tempPoint == null) {
				tempPoint = point;
			}
			polyBuilder.append(COMMA);
			count++;
		}
		polyBuilder.append("[ " + tempPoint.getX() + COMMA + tempPoint.getY() + "]");
		polyBuilder.append("] ] } }");
		return polyBuilder.toString();
	}
	
	private String build() {
	builder.append("{ " + toSProperty("type", "FeatureCollection") + COMMA);
	builder.append(toSProperty("timestamp", ldtString) + COMMA);
	builder.append(toSProperty("observationType", keyProperty) + COMMA);
	builder.append(toEntry("features") + ": [ ");
	builder.append(polygonsBuilder.toString());
	builder.append("] }");
	return builder.toString();
	}
	
	@Override
	public String toString() {
		return build();
	}
	
	private String toNProperty(String key, String value) {
		return toEntry(key) + ": " + value;
	}
	
	private String toSProperty(String key, String value) {
		return toEntry(key) + ": " + toEntry(value);
	}
	
	private String toEntry(String name) {
		return "\"" + name + "\"";
	}
	
}
