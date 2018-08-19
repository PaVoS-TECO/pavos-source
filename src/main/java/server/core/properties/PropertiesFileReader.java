package server.core.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

public final class PropertiesFileReader {

	private static Properties properties;

	private PropertiesFileReader() {

	}

	public static void init() {
		properties = new Properties();

		// check if properties file is missing keys
		try {
			FileInputStream file = new FileInputStream("src/main/resources/KafkaCore.properties");
			properties.load(file);
			file.close();

			if (!properties.containsKey("BOOTSTRAP_SERVERS_CONFIG")
					|| !properties.containsKey("SCHEMA_REGISTRY_URL_CONFIG")
					|| !properties.containsKey("M_AUTO_OFFSET_RESET_CONFIG")
					|| !properties.containsKey("M_APPLICATION_ID_CONFIG")
					|| !properties.containsKey("M_CLIENT_ID_CONFIG")) {
				throw new InvalidParameterException();
			}
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			System.err.println("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- BOOTSTRAP_SERVERS_CONFIG\n" + "\t- SCHEMA_REGISTRY_URL_CONFIG\n"
					+ "\t- M_AUTO_OFFSET_RESET_CONFIG\n" + "\t- M_APPLICATION_ID_CONFIG\n"
					+ "\t- M_CLIENT_ID_CONFIG\n");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("There was an error reading the configuration file.\n"
					+ "Please make sure that there is a file named 'KafkaCore.properties' at the root directory of the program.");
			System.exit(-1);
		}
	}

	public static Properties getMergecStreamProperties() {
		initProperties();
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty("M_APPLICATION_ID_CONFIG"));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty("M_CLIENT_ID_CONFIG"));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty("SCHEMA_REGISTRY_URL_CONFIG"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty("M_AUTO_OFFSET_RESET_CONFIG"));
		return props;
	}

	public static Properties getGraphiteStreamProperties() {
		initProperties();
		Properties props = new Properties();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, getProperty("P_APPLICATION_ID_CONFIG"));
		props.put(StreamsConfig.CLIENT_ID_CONFIG, getProperty("P_CLIENT_ID_CONFIG"));
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, getProperty("SCHEMA_REGISTRY_URL_CONFIG"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty("P_AUTO_OFFSET_RESET_CONFIG"));
		return props;
	}

	public static Properties getStandartConsumerProperties() {
		initProperties();
		Properties props = new Properties();
		props.put("bootstrap.servers", getProperty("BOOTSTRAP_SERVERS_CONFIG"));
		props.put("group.id", "test-consumer-group");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	public static String getProperty(String keys) {
		initProperties();
		return properties.getProperty(keys);
	}

	private static void initProperties() {
		if (properties == null)
			init();
	}

}
