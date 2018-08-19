package server.core.controller;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.core.properties.KafkaAdmin;
import server.core.properties.PropertiesFileReader;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;

public class GraphitePClass {

	private String ObservationTopic;
	private String outputTopic;
	private Properties props;
	private KafkaStreams kafkaStreams;

	public GraphitePClass(String topic, String iot) {
		KafkaAdmin kAdmin = KafkaAdmin.getInstance();
		
		if (kAdmin.existsTopic(topic)) {
			this.ObservationTopic = topic;
			this.outputTopic = iot;
			
			PropertiesFileReader propReader = PropertiesFileReader.getInstance();
			this.props = propReader.getGraphiteStreamProperties();
		}
	}

	public GraphitePClass(String iot) {
		this("Observations", iot);
	}

	public boolean start() {
		final Serde<String> stringSerde = Serdes.String();

		StreamsBuilder builder = new StreamsBuilder();
		final KStream<String, GenericRecord> obsT = builder.stream(ObservationTopic);
		KStream<String, String> iot = obsT.mapValues(value -> {
			ObservationData obs = new ObservationData();
			value.get("");
			obs.locationElevation = "test";
			obs.locationID = "test";
			obs.locationName = "test";
			obs.observationDate = "test";
			obs.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), "10000");
			obs.observations.put(ObservationType.PARTICULATE_MATTER_PM2P5.toString(), "10000");

			ObjectMapper mapper = new ObjectMapper();
			String sData = null;
			try {
				sData = mapper.writeValueAsString(obs);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return sData;

		});

		iot.to(outputTopic + "2", Produced.with(stringSerde, stringSerde));

		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();

		return true;
	}

	public void close() {
		if (kafkaStreams == null) {
			System.out.println("Applikation Merge is not Running");
			return;
		}
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
	}

}
