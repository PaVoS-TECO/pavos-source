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
     * Saves the geoJson String with the given key into the database for 10 minutes.
     * @param key The key
     * @param geoJson The GeoJSON String
     */
    public void add(String key, String geoJson) {
    	try {
			cli.set(key, 600, geoJson);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Extracts the value to the key "clusterID" from the geoJson String and uses that as a key to
     * save the geoJson String into the database for 10 minutes.
     * @param geoJson The GeoJSON String
     */
    public void add(String geoJson) {
    	try {
			JSONObject jo = (JSONObject) new JSONParser().parse(geoJson);
			if (!jo.containsKey("timestamp") || !jo.containsKey("features")) {
				throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, "Key 'timestamp' not found in given geoJson String:\n" + geoJson);
			}
			this.add(jo.get("clusterID").toString(), geoJson);
		} catch (ParseException e) {
			logger.warn("Could not parse given geoJson String!", e);
		}
    }

}