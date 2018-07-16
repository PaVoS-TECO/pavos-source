package server.transfer.send;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import server.transfer.serialization.ObservationData;

public class GraphiteSenderTests {
	
	//private static boolean print = false;
	private static final String topic = "GraphiteSenderTest";
	
	@Test
	public void test() {
		Map<TopicPartition, List<ConsumerRecord<String, ObservationData>>> recordsMap 
		= new HashMap<TopicPartition, List<ConsumerRecord<String, ObservationData>>>();
		List<ConsumerRecord<String, ObservationData>> recordList = new ArrayList<ConsumerRecord<String, ObservationData>>();
		
		ObservationData data = setupCorrectData(new ObservationData());
		ConsumerRecord<String, ObservationData> record = new ConsumerRecord<String, ObservationData>(topic, 0, 0, null, data);
		
		recordList.add(record);
		recordsMap.put(new TopicPartition(topic, 0), recordList);
		ConsumerRecords<String, ObservationData> records = new ConsumerRecords<String, ObservationData>(recordsMap);
		GraphiteSender sender = new GraphiteSender();
		sender.send(records);
	}
	
	private ObservationData setupCorrectData(ObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest"
				, LocalDateTime.now().toString(), "0");
	}
	
	private ObservationData setupData(ObservationData data, String locationElevation, String locationID, String locationName, String date, String particulateMatter) {
		data.locationElevation = locationElevation;
		data.locationID = locationID;
		data.locationName = locationName;
		data.observationDate = date;
		data.particulateMatter = particulateMatter;
		return data;
	}

}
