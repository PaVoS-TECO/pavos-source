package server.transfer.producer;

import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import server.core.properties.PropertiesFileManager;
import server.transfer.config.KafkaConfig;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationDataSerializer;

public class GraphiteProducer {
	
	private KafkaProducer<String, ObservationData> producer;
	
	public void produceMessage(String topic, ObservationData data) {
		producer = new KafkaProducer<>(getProducerProperties());
		producer.send(new ProducerRecord<String, ObservationData>(topic, data));
		producer.close();
	}
	
	public void produceMessages(String topic, Collection<ObservationData> dataSet) {
		producer = new KafkaProducer<>(getProducerProperties());
		for (ObservationData data : dataSet) {
			producer.send(new ProducerRecord<String, ObservationData>(topic, data));
		}
		producer.close();
	}
	
	private Properties getProducerProperties() {
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		Properties props = propManager.getProducerGridProperties();
		return props;
    }
	
}
