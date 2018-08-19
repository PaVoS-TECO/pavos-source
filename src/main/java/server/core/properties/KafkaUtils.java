package server.core.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;

public final class KafkaUtils {

	private static AdminClient admin;

	private KafkaUtils() {

	}

	public static void init() {
		Properties adminp = new Properties();
		adminp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
				PropertiesFileReader.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		admin = AdminClient.create(adminp);
	}

	public static boolean existsTopic(String topicName) {
		Collection<String> topicNames = new ArrayList<>();
		topicNames.add(topicName);

		return existsTopic(topicNames);
	}

	public static boolean existsTopic(String topicName1, String topicName2) {
		Collection<String> topicNames = new ArrayList<>();
		topicNames.add(topicName1);
		topicNames.add(topicName2);

		return existsTopic(topicNames);
	}

	public static boolean existsTopic(Collection<String> topicNames) {
		Collection<TopicListing> topicListings = getExistingTopics();
		Collection<TopicListing> topicsToCheck = new ArrayList<TopicListing>();

		for (String topicName : topicNames) {
			topicsToCheck.add(new TopicListing(topicName, false));
		}
		if (!topicListings.containsAll(topicsToCheck)) {
			System.out.println("The chosen Input-Topics does not exits in Kafka");
			return false;
		} else {
			return true;
		}
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

	public static boolean deleteTopic(String topic) {
		Collection<TopicListing> topicListings = getExistingTopics();
		TopicListing tl = new TopicListing(topic, false);
		if (!topicListings.contains(tl))
			return true;

		Collection<String> topicsToRemove = new ArrayList<String>();
		topicsToRemove.add(topic);
		DeleteTopicsResult result = admin.deleteTopics(topicsToRemove);

		return result.all().isDone();
	}

}
