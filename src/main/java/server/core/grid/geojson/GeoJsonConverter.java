package server.core.grid.geojson;

import java.util.ArrayList;
import java.util.Collection;

import server.core.grid.GeoGrid;
import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;

public final class GeoJsonConverter {
	
	private GeoJsonConverter() {
		
	}
	
	public static String convert(GeoPolygon geoPolygon, String keyProperty) {
		Collection<GeoPolygon> col = new ArrayList<>();
		col.add(geoPolygon);
		return convertPolygons(col, keyProperty);
	}
	
	public static String convertObservations(Collection<ObservationData> observations, String keyProperty, GeoGrid geoGrid) {
		GeoJsonBuilder builder = new GeoJsonBuilder(keyProperty);
		builder.addDBObservations(observations, geoGrid);
		return builder.toString();
	}

	public static String convertPolygons(Collection<GeoPolygon> geoPolygons, String keyProperty) {
		GeoJsonBuilder builder = new GeoJsonBuilder(keyProperty);
		builder.addGeoPolygons(geoPolygons);
		return builder.toString();
	}
	
}
