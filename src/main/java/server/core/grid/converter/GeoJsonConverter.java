package server.core.grid.converter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public final class GeoJsonConverter {
	
	private GeoJsonConverter() {
		
	}
	
	public static String convert(Collection<GeoPolygon> geoPolygons, String keyProperty) {
		String comma = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append("{ " + toSProperty("type", "FeatureCollection") + comma);
		builder.append(toSProperty("timestamp", TimeUtil.getUTCDateTimeString()) + comma);
		builder.append(toSProperty("observationType", keyProperty) + comma);
		builder.append(toEntry("features") + ": [ ");
		int countFeature = 1;
		for (GeoPolygon geoPolygon : geoPolygons) {
			ObservationData data = geoPolygon.cloneObservation();
			builder.append("{ " + toSProperty("type", "Feature") + comma);
			builder.append(toEntry("properties") + ": { ");
			builder.append(toNProperty("value", data.observations.get(keyProperty)) + comma);
			builder.append(toSProperty("clusterID", geoPolygon.ID) + comma);
			builder.append(toEntry("content") + ": [ ");
			int count = 1;
			for (GeoPolygon sub2Polygon : geoPolygon.getSubPolygons()) {
				builder.append(toEntry(sub2Polygon.ID));
				if (count < geoPolygon.getSubPolygons().size()) {
					builder.append(comma);
				}
				count++;
			}
			builder.append("]" + comma);
//			builder.append(toSProperty("stroke", "#000000") + comma);
//			builder.append(toNProperty("stroke-width", "2") + comma);
//			builder.append(toSProperty("fill", "#000000") + comma);
//			builder.append(toNProperty("fill-opacity", "0.5"));
			builder.append("}" + comma);
			builder.append(toEntry("geometry") + ": { ");
			builder.append(toSProperty("type", "Polygon") + comma);
			builder.append(toEntry("coordinates") + ": [ [ ");
			count = 1;
			Point2D.Double tempPoint = null;
			for (Point2D.Double point : geoPolygon.getPoints()) {
				builder.append("[ " + point.getX() + comma + point.getY() + "]");
				if (tempPoint == null) {
					tempPoint = point;
				}
				builder.append(comma);
				count++;
			}
			builder.append("[ " + tempPoint.getX() + comma + tempPoint.getY() + "]");
			builder.append("] ] } }");
			if (countFeature < geoPolygons.size()) {
				builder.append(comma);
			}
		}
		builder.append("] }");
		return builder.toString();
	}
	
	private static String toNProperty(String key, String value) {
		return toEntry(key) + ": " + value;
	}
	
	private static String toSProperty(String key, String value) {
		return toEntry(key) + ": " + toEntry(value);
	}
	
	private static String toEntry(String name) {
		return "\"" + name + "\"";
	}

	public static String convert(GeoPolygon geoPolygon, String keyProperty) {
		Collection<GeoPolygon> col = new ArrayList<>();
		col.add(geoPolygon);
		return convert(col, keyProperty);
	}
	
}
