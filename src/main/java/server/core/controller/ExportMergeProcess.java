package server.core.controller;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import server.core.properties.PropertiesFileManager;

public class ExportMergeProcess implements ProcessInterface {


	private Properties props;
	private KafkaStreams kafkaStreams;
	private final String threadName = "ExportProcess";
	
	public ExportMergeProcess() {
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		this.props = propManager.getExportProperties();
	}
	
	
	@Override
	public boolean kafkaStreamStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean kafkaStreamClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void apply(StreamsBuilder builder) {
		final KStream<String, GenericRecord> foIT = builder.stream("FeatureOfIntresssTopic");
		final KTable<String, GenericRecord> obsT = builder.table("ObservationTopic");
		final KStream<String, GenericRecord> transformfoIT = foIT
				.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

		final KStream<String, GenericRecord> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {

			if (value != null) {
				GenericRecord obj = (GenericRecord) location.get("feature");
				if (obj != null) {
					value.put("FeatureOfInterest", obj.get("coordinates").toString());
				} else {
					// TODO ? Observation ohne Location ?
					return value;
				}

				return value;
			}
			return null;

		});

		transformfoITTable.to(outputTopic);
		
	}

}
