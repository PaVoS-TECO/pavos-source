package com.github.masterries.kafka.properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;

public class KafkaUtils {

    private static Properties props;
    private static KafkaConsumer<String, String> consumer;
    private static AdminClient admin;
    private static int timeout = 2000;


    public static void init(){
        props = PropertiesFileReader.getStandartConsumerProperties();
        Properties adminp = new Properties();
        adminp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,PropertiesFileReader.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        admin = AdminClient.create(adminp);
        consumer = new KafkaConsumer<String, String>(props);
    }


    public static boolean existsTopic(String topicname){
        boolean exists = (consumer.listTopics().get(topicname)!=null);
        consumer.poll(timeout);
        return exists;


    }
    public static boolean existsTopic(String topicname1,String topicname2){
        boolean exists1 = (consumer.listTopics().get(topicname1)!=null);
        boolean exists2 = (consumer.listTopics().get(topicname2)!=null);
        consumer.poll(timeout);
        return (exists1&&exists2);


    }

    //TODO
/*
    public static boolean deleteTopic(String topic){
        ArrayList<String> list = new ArrayList<>();
        list.add(topic);
        admin.deleteTopics(list);
        return true;


    }*/



}
