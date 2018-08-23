package server.database;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import server.transfer.data.ObservationData;

/**
 * This class converts KafkaStream records to data that can be inserted into the StorageSolution.
 */
public class KafkaToStorageProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private MemcachedClient cli;
	
    /**
     * Default constructor
     */
    public KafkaToStorageProcessor(String host) {
    	try {
			cli = new XMemcachedClient(host, 11211);
		} catch (IOException e) {
			logger.error("Could not connect to memcached client!", e);
		}
    	
    }


    /**
     * Subscribes to the given KafkaStream and converts the data to the appropriate format for the StorageSolution.
     * If a stream is already subscribed to, unsubscribes from the old stream and subscribes to the new one.
     * @param stream The KStream to subscribe to.
     */
    public void subscribe(KStream stream) {
        KafkaConsumer<?, ?> consumer = new KafkaConsumer<>(new Properties());
    }

    /**
     * Saves the ObservationData object into the database for 24 hours.
     * @param observationData
     */
	public void add(ObservationData observationData) {
		// TODO Auto-generated method stub
		
	}

}