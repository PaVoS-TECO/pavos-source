package server.transfer.send;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import server.transfer.data.ObservationData;

/**
 * Reformats the data and sends it to another component
 */
public abstract class Sender {
	
    /**
     * Sends the resulting data to the specified component
     * @param records Multiple records of data from Kafka
     */
    public abstract void send(ConsumerRecords<String, ObservationData> records);

}