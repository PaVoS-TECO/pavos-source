package server.transfer.serialization;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deserializes KafkaObservationData objects
 * @param <T>
 */
public class ObservationDataDeserializer implements Deserializer<KafkaObservationData> {

    /**
     * Default constructor
     */
    public ObservationDataDeserializer() {
    	this.logger = LoggerFactory.getLogger(ObservationDataDeserializer.class);
    }

    /**
     * Documents the deserialization of our objects
     */
    private Logger logger = null;

    /**
     * Closes this object
     */
    public void close() {
        // TODO implement here
    }
    
	/**
     * Configures the deserializer
     * @param configs The Configuration
     * @param isKey A variable, telling us whether we want to configure the key or the value
     */
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub
	}
	
	/**
     * Deserializes an object
     * @param topic Kafka-Topic
     * @param data These are our serialized bytes
     * @return A serializable object that contains the observed data from kafka
     */
	public KafkaObservationData deserialize(String topic, byte[] data) {
		KafkaObservationData observationData = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            observationData = mapper.readValue(data, KafkaObservationData.class);
        } catch (IOException e) {
        	logger.error("Failed to deserialize object: " + data.toString(), e);
        }
        return observationData;
	}

}