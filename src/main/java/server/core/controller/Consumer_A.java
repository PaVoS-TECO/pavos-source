package com.github.masterries.kafka;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

public class Consumer_A {




    public static void main(String[] args) throws InterruptedException {


        //Client Props
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.56.3:9092");
        props.put(GROUP_ID_CONFIG, "i");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer.class");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer.class");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");






        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("Observations"));

        System.out.println("Consumer A gestartet!");


        JSONObject obj;

        while(true) {

            ConsumerRecords<String, String> records = consumer.poll(1000);
            if (records.count() == 0)
                continue;

            for (ConsumerRecord<String, String> record : records){

                String result = record.value();
               // int i = result.indexOf("{");
               // result = result.substring(i);
               // try {
               //     JSONObject jo = (JSONObject) new JSONParser().parse(result.toString());
                //    System.out.println(jo.get("@iot.id"));
               // } catch (ParseException e) {
                //    e.printStackTrace();
               // }
                System.out.println(result);



            }


        }
    }
}