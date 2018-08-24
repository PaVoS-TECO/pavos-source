package server.core.controller;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import server.core.properties.KafkaTopicAdmin;
import server.core.properties.PropertiesFileManager;

/**
 * @author Patrick
 * This Class merges bascily the ObservationTopic with the FeatureOfInterested topic to an Output topic.
 * It's needed for other Processing Classes
 *
 */
public class MergeObsToFoiProcess  implements ProcessInterface{

	private String ObservationTopic;
	private String FeatureOfIntresssTopic;
	private String outputTopic;
	private String keyEqual;
	private Properties props;
	private KafkaStreams kafkaStreams;

	/**
	 * This is the custom Constructor for the Merge Class if you want to merge other Topics
	 * @param topic1
	 * @param topic2
	 * @param outputTopic
	 * @param key is the where you want to merge the topics
	 */
	public MergeObsToFoiProcess(String topic1, String topic2, String outputTopic, String key) {
		KafkaTopicAdmin kAdmin = KafkaTopicAdmin.getInstance();

		if (!kAdmin.existsTopic(topic1, topic2)) {
			kAdmin.createTopic(topic1);
			kAdmin.createTopic(topic2);
		}

		this.ObservationTopic = topic1;
		this.FeatureOfIntresssTopic = topic2;
		this.outputTopic = outputTopic;
		this.keyEqual = key;

		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		this.props = propManager.getMergeStreamProperties();
	}

	/**
	 *  Default Constructer 
	 */
	public MergeObsToFoiProcess() {
		this("Observations", "FeaturesOfInterest", "ObservationsMergesGeneric", "Observations");
	}

	/**
	 *  This Starts the process with String Serializer
	 */
	
	public boolean startDifferent(){
		final Serde<String> stringSerde = Serdes.String();

		StreamsBuilder builder = new StreamsBuilder();
		final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
		final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, String> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put("FeatureOfInterest", obj.get("coordinates").toString());
				} else {
					// Observation ohne Location ?
					return value.toString();
				}

				return value.toString();
			}
			return null;

		});

		transformfoITTable.to(outputTopic, Produced.with(stringSerde, stringSerde));

		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();

		return true;
	}
	
	/**
	 *  This Starts the process with Generic Avro Serialiser
	 */

	public boolean kafkaStreamStart() {
		StreamsBuilder builder = new StreamsBuilder();
		
		apply(builder);
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();

		return true;
	}
	
	/* (non-Javadoc)
	 * @see server.core.controller.ProcessInterface#apply(org.apache.kafka.streams.StreamsBuilder)
	 */
	public void apply(StreamsBuilder builder) {
		final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
		final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, GenericRecord> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put("FeatureOfInterest", obj.get("coordinates").toString());
				} else {
					//TODO ? Observation ohne Location ?
					return value;
				}

				return value;
			}
			return null;

		});

		transformfoITTable.to(outputTopic);
	}

	
	
	/**
	 *  This closes the process
	 */
	public boolean  kafkaStreamClose() {
		if (kafkaStreams == null) {
			System.out.println("Applikation 'Merge' is not Running");
			return false;
		}
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
		return true;
	}

}
