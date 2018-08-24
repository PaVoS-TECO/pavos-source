package server.core.grid;

import java.util.ArrayList;
import java.util.List;

public final class GeoGridManager {
	
	private static GeoGridManager instance;
	private List<GeoGrid> grids = new ArrayList<>();
	
	private GeoGridManager() {
		
	}
	
	public static GeoGridManager getInstance() {
		if (instance == null) {
			instance = new GeoGridManager();
		}
		return instance;
	}
	
	public void addGeoGrid(GeoGrid grid) {
		this.grids.add(grid);
	}
	
	public GeoGrid getGrid(String gridID) {
		for (GeoGrid entry : this.grids) {
			if (entry.GRID_ID.equals(gridID)) {
				return entry;
			}
		}
		return null;
	}
	
	public boolean isGridActive(GeoGrid grid) {
		return this.grids.contains(grid);
	}
	
	public boolean isGridActive(String gridID) {
		for (GeoGrid entry : this.grids) {
			if (entry.GRID_ID.equals(gridID)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeGeoGrid(GeoGrid grid) {
		this.grids.remove(grid);
	}
	
}
