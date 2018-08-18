package server.core.properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;
import java.util.concurrent.ExecutionException;

public final class KafkaUtils {

    private static Properties props;
    private static KafkaConsumer<String, String> consumer;
    private static AdminClient admin;
    private static int timeout = 2000;
    
    private KafkaUtils() {
    	
    }

    public static void init(){
        props = PropertiesFileReader.getStandartConsumerProperties();
        Properties adminp = new Properties();
        adminp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,PropertiesFileReader.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        admin = AdminClient.create(adminp);
        consumer = new KafkaConsumer<String, String>(props);
    }

    public static boolean existsTopic(String topicname){
    	Collection<TopicListing> topicListings = getExistingTopics();
    	
        return topicListings.contains(new TopicListing(topicname, false));
    }
    
    public static boolean existsTopic(String topicName1,String topicName2){
    	Collection<TopicListing> topicListings = getExistingTopics();
    	Collection<TopicListing> topicsToCheck = new ArrayList<TopicListing>();
    	topicsToCheck.add(new TopicListing(topicName1, false));
    	topicsToCheck.add(new TopicListing(topicName2, false));
    	
        return (topicListings.containsAll(topicsToCheck));
    }
    
    private static Collection<TopicListing> getExistingTopics() {
    	Collection<TopicListing> topicListings = new ArrayList<>();
		try {
			topicListings = admin.listTopics().listings().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return topicListings;
    }

    public static boolean deleteTopic(String topic){
    	Collection<TopicListing> topicListings = getExistingTopics();
    	TopicListing tl = new TopicListing(topic, false);
    	if (!topicListings.contains(tl)) return true;
    	
    	Collection<String> topicsToRemove = new ArrayList<String>();
    	topicsToRemove.add(topic);
    	DeleteTopicsResult result = admin.deleteTopics(topicsToRemove);
    	
        return result.all().isDone();
    }

}
