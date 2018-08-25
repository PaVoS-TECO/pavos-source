package server.core.controller;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.core.properties.PropertiesFileManager;

public class ExportMergeProcess implements ProcessInterface {

	private Properties props;
	private KafkaStreams kafkaStreams;
	private final String threadName = "ExportProcess";
	private boolean obsPro = false;

	public ExportMergeProcess() {
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		this.props = propManager.getExportStreamProperties();
	}

	@Override
	public boolean kafkaStreamStart() {
		StreamsBuilder builder = new StreamsBuilder();

		apply(builder);
		kafkaStreams = new KafkaStreams(builder.build(), props);
		kafkaStreams.start();
		return false;
	}

	@Override
	public boolean kafkaStreamClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void apply(StreamsBuilder builder) {
		final KStream<String, GenericRecord> observationStream = builder.stream("Observations");
		final KStream<String, GenericRecord> foIStream = builder.stream("FeaturesOfInterest");

		final KStream<String, GenericRecord> foIStreamKey = foIStream
				.map((key, value) -> KeyValue.pair(value.get("Observations").toString(), value));

		final KStream<String, GenericRecord> mergedFoIObs = observationStream.join(foIStreamKey, (value, location) -> {
			value.put("FeatureOfInterest", location.toString());
//			JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
			return value;
			// return null;

		}, JoinWindows.of(100000));

		// get DataStream
		final KStream<String, GenericRecord> DataStream = builder.stream("Datastreams");
		// Transform merged to Equals Keys to DataStream.Iot
		final KStream<String, GenericRecord> mergedKey = mergedFoIObs
				.map((key, value) -> KeyValue.pair(value.get("Datastream").toString(), value));
		// Join the DataStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsData = mergedKey.join(DataStream, (value, data) -> {

			value.put("Datastream", data.toString());
			// JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
			return value;

		},JoinWindows.of(100000));

		// get ThingsStream
		final KStream<String, GenericRecord> ThingStream = builder.stream("Things");
		// Tranfrom mergedFoIObsData to Equals Key to Things
		final KStream<String, GenericRecord> mergedKeyThing = mergedFoIObsData
				.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "Thing"), value));

		// Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThing = mergedKeyThing.join(ThingStream,
				(value, thing) -> {
					JSONObject ds = toJson(value, "Datastream");
					ds.put("Thing", thing.toString());

					value.put("Datastream", ds.toString());
					// JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
					return value;

				},JoinWindows.of(100000));

		// get SensorStream
		final KStream<String, GenericRecord> SensorStream = builder.stream("Sensors");
		// Tranfrom mergedFoIObsData to Equals Key to Sensor
		final KStream<String, GenericRecord> mergedFoIObsDataThingKey = mergedFoIObsDataThing
				.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "Sensor"), value));

		// Join ThingsStream with MergedStream
		final KStream<String, GenericRecord> mergedFoIObsDataThingSensor = mergedFoIObsDataThingKey.join(SensorStream,
				(value, thing) -> {
					JSONObject ds = toJson(value, "Datastream");
					ds.put("Sensor", thing.toString());

					value.put("Datastream", ds.toString());
					// JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
					return value;

				},JoinWindows.of(100000));
		if(obsPro) {
			// get SensorStream
			final KStream<String, GenericRecord> propertyStream = builder.stream("ObservedProperty");
			// Tranfrom mergedFoIObsData to Equals Key to Sensor
			final KStream<String, GenericRecord> mergedFoIObsDataThingSensorKey = mergedFoIObsDataThingSensor
					.map((key, value) -> KeyValue.pair(toJson(value, "Datastream", "ObservedProperty"), value));

			// Join ThingsStream with MergedStream
			final KStream<String, GenericRecord> finalStream = mergedFoIObsDataThingSensorKey.join(propertyStream,
					(value, thing) -> {
						JSONObject ds = toJson(value, "Datastream");
						ds.put("ObservedProperty", thing.toString());

						value.put("Datastream", ds.toString());
						// JSONObject jo = (JSONObject) new JSONParser().parse(value.toString());
						return value;

					},JoinWindows.of(100000));
		}
		

		final Serde<String> stringSerde = Serdes.String();

		mergedFoIObsDataThingSensor.to("TestExport4");
	}

	public String toJson(GenericRecord record, String Stream, String key) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse(record.get(Stream).toString());

			return (String) ds.get(key);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public JSONObject toJson(GenericRecord record, String Stream) {
		JSONObject ds;
		try {
			ds = (JSONObject) new JSONParser().parse(record.get(Stream).toString());

			return ds;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
