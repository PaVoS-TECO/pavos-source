package com.github.masterries.kafka;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;

public class MergeObservationWithFoI {

    private static final String ObservationTopic = "Observations";
    private static final String FeatureOfIntresssTopic = "FeaturesOfInterest";
    private static final String outputTopic = "ObservationsMerges";

    public static void main(String[] args) throws InterruptedException {


        Properties props = new Properties();

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.56.3:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Merge");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "example-client");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://192.168.56.3:8081");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();



        StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, GenericRecord> views = builder.stream(ObservationTopic);
        views.mapValues(value -> {
            System.out.println(value.get("iotId"));

            return value;
        });

        views.to(outputTopic);

        KafkaStreams kafkaStreams1 = new KafkaStreams(builder.build(), props);
        kafkaStreams1.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams1.close();

        }));



    }







}
