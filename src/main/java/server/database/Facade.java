package server.database;

import server.transfer.data.ObservationData;
import server.transfer.data.ObservationDataDeserializer;
import web.grid.Grid;

/**
 * A facade to simplify access to a StorageSolution, such as a database. Through the methods, data can be inserted into the StorageSolution and certain information about its content requested.
 */
public class Facade {

	private ObservationDataToStorageProcessor storageProcessor;
	
    /**
     * Default constructor
     */
    public Facade() {
    	// TODO set host by property list
    	storageProcessor = new ObservationDataToStorageProcessor("localhost");
    }
    
    /**
     * Add an ObservationData object to the storage solution.
     * @param observationData The ObservationData object.
     */
    public void addObservationData(ObservationData observationData) {
    	storageProcessor.add(observationData);
    }
    
    /**
     * Add a byte array (which represents a serialized ObservationData object)
     * to the storage solution.
     * @param observationData The serialized ObservationData object.
     */
    public void addObservationData(byte[] observationData) {
    	ObservationDataDeserializer deserializer = new ObservationDataDeserializer();
    	ObservationData obsDataObject = deserializer.deserialize(null, observationData);
    	deserializer.close();
    	if (obsDataObject != null) {
    		addObservationData(obsDataObject);
    	}
    }

    /**
     * Returns an appropriate grid of clusters in the requested grid section for the specified ZoomLevel and time. The (first) two values of the ClusterID array define the grid section from which to get the data.
     * @param clusters An array of ClusterIDs from which the first two entries are taken to compute the section of the Grid to get the data from.
     * @param zoom The ZoomLevel from which to get the data.
     * @param time The point in time.
     * @return A grid with the computed data.
     */
    public Grid getGrid(ClusterID[] clusters, ZoomLevel zoom, String timestamp) {
        // TODO implement here
        return null;
    }
    
    /**
     * Get the value of an observedProperty from a clusterID at or before the given timestamp.
     * The returned value is guaranteed to come from an observation in the given cluster at or before
     * the given timestamp (i.e. no values from the future).
     * @param clusterID The cluster from which to get the value
     * @param timestamp The time to check
     * @param observedProperty The observedProperty needed
     * @return The value to the observedProperty key. Returns {@code null} in any of the following cases:<br>
     * - There is no entry for the cluster<br>
     * - There is no entry for the cluster before or at the given timestamp<br>
     * - There is no {@code observedProperty} key in the observations Map<br>
     * - The value to the {@code observedProperty} key is literally {@code null}<br>
     * - Any of the parameters is badly formatted (see logs)
     */
    public String getObservationData(String clusterID, String timestamp, String observedProperty) {
    	return storageProcessor.get(clusterID, timestamp, observedProperty);
    }

}