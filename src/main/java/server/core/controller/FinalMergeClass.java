package com.github.masterries.kafka;

import com.github.masterries.kafka.properties.KafkaUtils;
import com.github.masterries.kafka.properties.PropertiesFile;
import com.github.masterries.kafka.properties.PropertiesFileReader;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;

public class FinalMergeClass {


    private String ObservationTopic;
    private String FeatureOfIntresssTopic;
    private String outputTopic;
    private String keyEqual;
    private Properties props;
    private KafkaStreams kafkaStreams;


    public FinalMergeClass(String topic1, String topic2, String outputTopic, String key) {
        if (KafkaUtils.existsTopic(topic1, topic2)) {
            System.out.println("The choosen Input-Topics doesn´t exits in Kafka");
        } else {
            this.ObservationTopic = topic1;
            this.FeatureOfIntresssTopic = topic2;
            this.outputTopic = outputTopic;
            this.keyEqual = key;
            this.props = PropertiesFileReader.getMergecStreamProperties();
        }

    }

    public FinalMergeClass(String topic1, String topic2, String outputTopic, String key, Properties properties) {
        if (KafkaUtils.existsTopic(topic1, topic2)) {
            System.out.println("The choosen Input-Topics doesn´t exits in Kafka");
        } else {
            new FinalMergeClass(topic1, topic2, outputTopic, key);
        }
    }

    public FinalMergeClass() {
        this.ObservationTopic = "Observations";
        this.FeatureOfIntresssTopic = "FeaturesOfInterest";
        this.outputTopic = "ObservationsMerges1";
        this.keyEqual = "Observations";
        this.props = PropertiesFileReader.getMergecStreamProperties();
    }


    public boolean start() {
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();


        StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
        final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
        final KStream<String, GenericRecord> transformfoIT = foIT.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

        final KStream<String, String> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {


            if (value != null) {
                GenericRecord obj = (GenericRecord) location.get("feature");
                if (obj != null) {
                    value.put("FeatureOfInterest", obj.get("coordinates").toString());
                } else {
                    //Observation ohne Location ?
                    return value.toString();
                }

                return value.toString();
            }
            return null;


        });

        transformfoITTable.to(outputTopic, Produced.with(stringSerde, stringSerde));

        kafkaStreams = new KafkaStreams(builder.build(), props);
        kafkaStreams.start();

        return true;
    }

    public boolean startGeneric() {
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();


        StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, GenericRecord> foIT = builder.stream(FeatureOfIntresssTopic);
        final KTable<String, GenericRecord> obsT = builder.table(ObservationTopic);
        final KStream<String, GenericRecord> transformfoIT = foIT.map((key, value) -> KeyValue.pair(value.get(keyEqual).toString(), value));

        final KStream<String, GenericRecord> transformfoITTable = transformfoIT.join(obsT, (location, value) -> {


            if (value != null) {
                GenericRecord obj = (GenericRecord) location.get("feature");
                if (obj != null) {
                    value.put("FeatureOfInterest", obj.get("coordinates").toString());
                } else {
                    //Observation ohne Location ?
                    return value;
                }

                return value;
            }
            return null;


        });

        transformfoITTable.to(outputTopic);

        kafkaStreams = new KafkaStreams(builder.build(), props);
        kafkaStreams.start();


        return true;
    }

    public void close() {
        if (kafkaStreams == null) {
            System.out.println("Applikation Merge is not Running");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }


}
