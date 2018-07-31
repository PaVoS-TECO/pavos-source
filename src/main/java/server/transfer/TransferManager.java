package server.transfer;

import java.util.ArrayList;
import java.util.List;

import server.transfer.consumer.Consumer;
import server.transfer.consumer.GraphiteConsumer;
import server.transfer.sender.GraphiteSender;

/**
 * The Control-Unit in charge of creating and destroying KafkaToGraphiteConsumer
 *  as well as passing on the users request.
 */
public class TransferManager {
	
	private Consumer consumer;
	
    /**
     * Default constructor
     */
    public TransferManager() {
    }

    /**
     * Starts data-transfer
     * @param topics Kafka-Topics that should be subscribed
     * @param dest The destination the data should be sent to
     */
    public void startDataTransfer(List<String> topics, Destination dest) {
    	if (consumer != null) stopDataTransfer();
        if (dest.equals(Destination.GRAPHITE)) startGraphiteTransfer(topics);
    }
    
    /**
     * Starts data-transfer
     * @param topic Kafka-Topic that should be subscribed
     * @param dest The destination the data should be sent to
     */
    public void startDataTransfer(String topic, Destination dest) {
    	List<String> topics = new ArrayList<>();
    	topics.add(topic);
    	if (consumer != null) stopDataTransfer();
        if (dest.equals(Destination.GRAPHITE)) startGraphiteTransfer(topics);
    }
    
    /**
     * Stops the data-transfer.
     * @param topics Kafka-Topics that should no longer be subscribed
     */
    public void stopDataTransfer() {
    	if (consumer == null) return;
    	consumer.stop();
    }

	private void startGraphiteTransfer(List<String> topics) {
    	consumer = new GraphiteConsumer(topics, new GraphiteSender());
    	consumer.run();
    }
    
}