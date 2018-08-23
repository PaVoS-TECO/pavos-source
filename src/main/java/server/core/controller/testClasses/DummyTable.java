package server.core.controller.testClasses;

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
import org.apache.kafka.streams.kstream.Reducer;

import server.core.properties.KafkaTopicAdmin;
import server.core.properties.PropertiesFileManager;

public class DummyTable {

	private String ObservationTopic;
	private String FeatureOfIntresssTopic;
	private String outputTopic;
	private String keyEqual;
	private Properties props;
	private KafkaStreams kafkaStreams;

	public DummyTable(String topic1, String topic2, String outputTopic, String key) {
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
		this.props = propManager.getDummyStreamProperties();
	}

	public DummyTable() {
		this("testGrid12.out", "FeaturesOfInterest", "ObservationsMerges12", "Observations");
	}

	public boolean start() {
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

	public void startGeneric() {
		StreamsBuilder builder = new StreamsBuilder();
		final Serde<String> stringSerde = Serdes.String();
		final KStream<String, String> stream = builder.stream("testGrid9.out");
		KTable<String, String> table = stream.groupByKey().reduce(
			    new Reducer<String>() {
			        @Override
			        public String apply(String aggValue, String newValue) {
			            return newValue;
			        }
			    },
			    "dummy-aggregation-store");
		
		table.to("dummy9");
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();
	}

	public void close() {
		if (kafkaStreams == null) {
			System.out.println("Applikation 'Merge' is not Running");
			return;
		}
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
	}

}
