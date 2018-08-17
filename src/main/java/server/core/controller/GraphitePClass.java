package server.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.core.properties.KafkaUtils;
import server.core.properties.PropertiesFile;
import server.core.properties.PropertiesFileReader;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import main.java.pw.oliver.jmkb.avroclasses.Observation;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import server.transfer.data.ObservationData;
import server.transfer.data.ObservationType;

import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;

public class GraphitePClass {


    private String ObservationTopic;
    private String outputTopic;
    private Properties props;
    private KafkaStreams kafkaStreams;


    public GraphitePClass(String topic1, String iot) {
        if (KafkaUtils.existsTopic(topic1)) {
            System.out.println("The choosen Input-Topics doesn´t exits in Kafka");
        } else {
            this.ObservationTopic = topic1;

            this.outputTopic = iot;
            this.props = PropertiesFileReader.getGraphiteStreamProperties();
        }

    }

    public GraphitePClass(String topic1, String iot,  Properties properties) {
        if (KafkaUtils.existsTopic(topic1)) {
            System.out.println("The choosen Input-Topics doesn´t exits in Kafka");
        } else {
            new  GraphitePClass(topic1, iot);
        }
    }

    public GraphitePClass(String iot) {
        this.ObservationTopic = "Observations";
        this.outputTopic = iot;
        this.props = PropertiesFileReader.getGraphiteStreamProperties();
    }



    public boolean start() {
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();


        StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, GenericRecord> obsT = builder.stream(ObservationTopic);
        KStream<String,String> iot = obsT.mapValues(value -> {
            ObservationData obs =  new ObservationData();
            value.get("");
            obs.locationElevation = "test";
            obs.locationID ="test";
            obs.locationName ="test";
            obs.observationDate = "test";
            obs.observations.put(ObservationType.PARTICULATE_MATTER_PM10.toString(), "10000");
            obs.observations.put(ObservationType.PARTICULATE_MATTER_PM2P5.toString(), "10000");

            ObjectMapper mapper = new ObjectMapper();
            String sData = null;
            try {
                sData = mapper.writeValueAsString(obs);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return sData;



        });




        iot.to(outputTopic+"2", Produced.with(stringSerde, stringSerde));

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
