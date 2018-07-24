package server.transfer.send;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import server.transfer.config.GraphiteConfig;
import server.transfer.convert.GraphiteConverter;
import server.transfer.data.ObservationData;

/**
 * Sends data to Graphite
 */
public class GraphiteSender extends Sender {

	private Map<TopicPartition, List<ConsumerRecord<String, ObservationData>>> recordsMap;
	private List<ConsumerRecord<String, ObservationData>> recordList;
	private ConsumerRecord<String, ObservationData> record;
	private ConsumerRecords<String, ObservationData> records;
	private Socket socket;

	/**
	 * Default constructor
	 */
	public GraphiteSender() {
		try {
			this.socket = new Socket(GraphiteConfig.getGraphiteHostName(), GraphiteConfig.getGraphitePort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the recorded data to Graphite.
	 * Uses a record of multiple data objects.
	 * <p>
	 * {@link ConsumerRecords}<{@link String}, {@link ObservationData}> records
	 */
	@Override
	public void sendToGraphite(ConsumerRecords<String, ObservationData> records) {
		PyList list = new PyList();

		records.forEach(record -> {
			GraphiteConverter.addObservations(record, list);
		});

		PyString payload = cPickle.dumps(list);
		byte[] header = ByteBuffer.allocate(4).putInt(payload.__len__()).array();

		try {
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(header);
			outputStream.write(payload.toBytes());
			outputStream.flush();
		} catch (IOException e) {
			logger.error("Failed writing to Graphite.", e);
		}
	}

	/**
	 * Sends the recorded data to Graphite.
	 * Uses a single data object.
	 * <p>
	 * {@link String} topic, {@link ObservationData} data
	 * 
	 * @param topic The name of the topic that this data belongs to
	 * @param data  The data that will be sent to Graphite
	 */
	public void send(String topic, ObservationData data) {
		recordsMap = new HashMap<TopicPartition, List<ConsumerRecord<String, ObservationData>>>();
		recordList = new ArrayList<ConsumerRecord<String, ObservationData>>();
		record = new ConsumerRecord<String, ObservationData>(topic, 0, 0, null, data);

		recordList.add(record);
		recordsMap.put(new TopicPartition(topic, 0), recordList);
		records = new ConsumerRecords<String, ObservationData>(recordsMap);

		this.sendToGraphite(records);
	}

}