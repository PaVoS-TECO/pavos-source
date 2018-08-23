package server.core.controller;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.StreamsBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.core.properties.PropertiesFileManager;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GridProcess implements ProcessInterface{
	
	private Properties props;
	private int timeIntervall;
	private String inputTopic;
	
	public GridProcess(String inputTopic, int timeIntervall) {
		PropertiesFileManager propManager = PropertiesFileManager.getInstance();
		this.props = propManager.getGridStreamProperties();
		this.timeIntervall = timeIntervall;
		this.inputTopic = inputTopic;
	}
	
	public GridProcess() {
		this("ObservationsMergesGeneric", 15000);
	}

	private static Map<String, String> setPropetysSensoring(JSONObject json) {
		Map<String, String> map = new HashMap<String, String>();
		try {

			ObjectMapper mapper = new ObjectMapper();

			// convert JSON string to Map
			map = mapper.readValue(json.toJSONString(), new TypeReference<Map<String, String>>() {
			});
			System.out.println(map);
			return map;

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	
	//todo Threading
	public boolean kafkaStreamStart() {
		KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(inputTopic));
		System.out.println("Consumer Grid gestartet!");
		GeoGrid grid = new GeoRecRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2),
				2, 2, 3);

		long t = System.currentTimeMillis();
		long end = t + timeIntervall;

		while (System.currentTimeMillis() < end) {

			final ConsumerRecords<String, GenericRecord> observations = consumer.poll(100);
			System.out.println(observations.count());
			observations.forEach(record1 -> {
				GenericRecord value = (record1.value());

				String time = TimeUtil.removeMillis((String) value.get("phenomenonTime").toString());
				;
				String resultValue = (String) value.get("result").toString();
				JSONParser parser = new JSONParser();
				try {
					JSONObject json = (JSONObject) parser.parse(resultValue);
					String sensorID = record1.value().get("Datastream").toString();
					ObservationData data = new ObservationData();

					data.observationDate = time;
					data.sensorID = sensorID;
					data.observations = setPropetysSensoring(json);

					double coord1 = Double.parseDouble(value.get("FeatureOfInterest").toString().split(",")[0]);
					double coord2 = Double.parseDouble(value.get("FeatureOfInterest").toString().split(",")[1]);

					Point2D.Double location = new Point2D.Double(coord1, coord2);

					grid.addObservation(location, data);
					grid.updateObservations();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		}

		grid.produceSensorDataMessages();

		return false;
	}

	
	public boolean kafkaStreamClose() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void apply(StreamsBuilder builder) {
		// TODO Auto-generated method stub
		
	}

}
