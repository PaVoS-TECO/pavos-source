package edu.teco.pavos.exporter;

import java.util.Set;

/**
 * Contains the Properties of an Export Request.
 */
public class ExportProperties {
	
	private String extension;
	private Set<String> observedProperties;
	private Set<String> clusters;
	private Set<String> sensors;
	private TimeIntervall time;

    /**
     * Default constructor
     */
    public ExportProperties(String ext, String tf, String ops, String cIDs, String sIDs) {
    	//TODO
    }

    /**
     * Get the FileExtension for the Export File.
     * @return The FileExtension for the File to export.
     */
    public String getFileExtension() {
        return this.extension;
    }

    /**
     * Get the TimeFrame of the Data that should be exported.
     * @return The TimeIntervall of the Data to be exported.
     */
    public TimeIntervall getTimeFrame() {
        return this.time;
    }

    /**
     * Get the ObsorvedProperties that should be exported.
     * @return The ObservedProperties that should be used for the export.
     */
    public Set<String> getObservedProperties() {
        return this.observedProperties;
    }

    /**
     * Get the ClusterIDs that should be exported. Always only exports a Groupd of Sensors or a Group of Clusters. The other Option is Empty.
     * @return The Clusters that should be taken in the Export.
     */
    public Set<String> getClusters() {
        return this.clusters;
    }

    /**
     * Get the SensorIDs that should be exported. Always only exports a Groupd of Sensors or a Group of Clusters. The other Option is Empty.
     * @return The SensorIDs of the Data that should be exported.
     */
    public Set<String> getSensorIDs() {
        return this.sensors;
    }

}
