package server.core.controller;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import server.core.grid.GeoGrid;
import server.core.grid.GeoRecRectangleGrid;
import server.core.grid.config.WorldMapData;
import server.transfer.data.ObservationData;

public class GridProcessClass {

	public static void main(String[] args) {

		KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(getProps());
		consumer.subscribe(Arrays.asList("ObservationsMerges12"));
		System.out.println("Consumer Grid gestartet!");
		GeoGrid grid = new GeoRecRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3);

		long t= System.currentTimeMillis();
		long end = t+15000;
		
		while (System.currentTimeMillis() < end) {

			final ConsumerRecords<String, GenericRecord> observations = consumer.poll(100);
			System.out.println(observations.count());
			observations.forEach(record1 -> {
				GenericRecord value = (record1.value());
				String time = (String) value.get("phenomenonTime").toString();
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
					
					Point2D.Double location = new  Point2D.Double(coord1, coord2);
					
					grid.addObservation(location, data);
					grid.updateObservations();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				

				
			});
		}
		
		grid.produceSensorDataMessages();

	}
	
	private static Map<String, String> setPropetysSensoring(JSONObject json){
		 Map<String, String> map = new HashMap<String, String>();
		try {

	        ObjectMapper mapper = new ObjectMapper();


	       

	        // convert JSON string to Map
	        map = mapper.readValue(json.toJSONString(), new TypeReference<Map<String, String>>(){});
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


	private static Properties getProps() {
		// Client Props
		Properties props = new Properties();
		props.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.56.3:9092");
		props.put(GROUP_ID_CONFIG, "i");
		props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put("schema.registry.url", "http://192.168.56.3:8081");

		return props;

	}

}
