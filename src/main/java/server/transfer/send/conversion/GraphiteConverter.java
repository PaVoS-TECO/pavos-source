package server.transfer.send.conversion;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.python.core.PyList;
import org.slf4j.Logger;

import server.transfer.serialization.ObservationData;

/**
 * Converts different observed properties to python metrics
 */
public final class GraphiteConverter {
	
	private GraphiteConverter() {
		
	}
	
	/**
     * Adds the sensor-observed property 'particulate matter' to the collection of properties that will be sent
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     */
    public static void addPM(ConsumerRecord<String, ObservationData> record, PyList list, Logger logger) {
    	GraphiteConverterUtil.addFloatMetric(record, list, "particulateMatter", record.value().particulateMatter, logger);
    }
	
}
