package server.transfer.data;

import java.util.Map;

/**
 * A serializable object that contains the observed data from kafka
 */
public class ObservationData implements java.io.Serializable {

    /**
	 * The unique identifier of this object
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Default constructor
     */
    public ObservationData() {
    }

    /**
     * The date of the observation
     */
    public String observationDate;

    /**
     * The name of the observations location
     */
    public String locationName;

    /**
     * The height of the observations location
     */
    public String locationElevation;

    /**
     * The id of the observations location
     */
    public String locationID;

    // Attributes listed below here are observed properties //
    
    public Map<String, String> observations;

}