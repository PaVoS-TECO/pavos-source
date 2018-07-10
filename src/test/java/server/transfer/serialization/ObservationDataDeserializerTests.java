package server.transfer.serialization;

import static org.junit.Assert.*;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Test;

public class ObservationDataDeserializerTests {

	private static boolean print = true;
	
	@Test
	public void deserialize_serializedObjectCheck_returnKafkaObservationData() {
		KafkaObservationData data = new KafkaObservationData();
		setupCorrectData(data);
		
		ObjectMapper mapper = new ObjectMapper();
		boolean canSerialize = mapper.canSerialize(KafkaObservationData.class);
		if (print) System.out.println("Mapper can serialize object: " + canSerialize);
		assert(canSerialize == true);
		
		String sData = null;
		byte[] bData = null;
		try {
			sData = mapper.writeValueAsString(data);
			bData = mapper.writeValueAsBytes(data);
		} catch (JsonProcessingException e) {
			fail("JsonProcessingException thrown");
		}
		if (print) {
			System.out.println("Serialized data as String: " + sData);
			System.out.println("Serialized data as byte array: " + bData);
		}
		
		KafkaObservationData result = null;
		Deserializer<KafkaObservationData> des = new ObservationDataDeserializer();
		result = des.deserialize("deserializationTest", bData);
		des.close();
		
		if (print) {
		System.out.println("\nThe mapping-result is shown below:");
		System.out.println("locationElevation: " + result.locationElevation);
		System.out.println("locationID: " + result.locationID);
		System.out.println("locationName: " + result.locationName);
		System.out.println("observationDate: " + result.observationDate);
		System.out.println("particulateMatter: " + result.particulateMatter);
		}
		
		assert(result.locationElevation.equals(data.locationElevation)
				&& result.locationID.equals(data.locationID)
				&& result.locationName.equals(data.locationName)
				&& result.observationDate.equals(data.observationDate)
				&& result.particulateMatter.equals(data.particulateMatter));
	}
	
	private KafkaObservationData setupCorrectData(KafkaObservationData data) {
		return setupData(data, "8848", "Mt.Everest_27-59-16_86-55-29", "Mt.Everest", new Date().toString(), "0");
	}
	
	private KafkaObservationData setupData(KafkaObservationData data, String locationElevation, String locationID, String locationName, String date, String particulateMatter) {
		data.locationElevation = locationElevation;
		data.locationID = locationID;
		data.locationName = locationName;
		data.observationDate = date;
		data.particulateMatter = particulateMatter;
		return data;
	}

}
