package server.core.web;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.core.grid.GeoGrid;
import server.core.grid.GeoGridManager;
import server.core.grid.geojson.GeoJsonConverter;
import server.core.grid.polygon.GeoPolygon;

public class DataServlet  extends HttpServlet {
	
	private static final long serialVersionUID = 4505621561403961545L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String type = req.getParameter("requestType");
		if (type.equals("getGeoJson")) {
			getGeoJson(req, res);
		}
	}
	
	private void getGeoJson(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String gridID = req.getParameter("gridID");
		String fusedClusterIDs = req.getParameter("clusterID");
		String keyProperty = req.getParameter("property");
		
		String[] clusterIDs = fusedClusterIDs.split(",");
		Collection<GeoPolygon> polygons = new HashSet<>();
		final GeoGrid grid = getGrid(gridID);
		for (int i = 0; i < clusterIDs.length; i++) {
			GeoPolygon polygon = grid.getPolygon(clusterIDs[i]);
			if (polygon != null) {
				polygons.add(polygon);
			}
		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
	    res.getWriter().write(GeoJsonConverter.convertPolygons(polygons, keyProperty));
	}
	
	private GeoGrid getGrid(String gridID) throws ServletException {
		GeoGridManager gridManager = GeoGridManager.getInstance();
		if (!gridManager.isGridActive(gridID)) throw new ServletException("Grid is not active and therefore can not be fetched.");
		return gridManager.getGrid(gridID);
	}
	
}
