package server.transfer.data.util;

import server.transfer.data.ObservationData;

public final class ObservationDataUtil {
	
	private ObservationDataUtil() {
		
	}
	
	/**
	 * Sets the received values and returns the {@link ObservationData} data
	 * @param data
	 * @param locationElevation
	 * @param locationID
	 * @param locationName
	 * @param date
	 * @param particulateMatter
	 * @return
	 */
	public static ObservationData setupData(ObservationData data, String locationElevation, String locationID,
			String locationName, String date, String particulateMatter) {
		data.locationElevation = locationElevation;
		data.locationID = locationID;
		data.locationName = locationName;
		data.observationDate = date;
		data.particulateMatter = particulateMatter;
		return data;
	}
	
}
