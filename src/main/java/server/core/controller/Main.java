package com.github.masterries.kafka;

import com.github.masterries.kafka.properties.KafkaUtils;
import com.github.masterries.kafka.properties.PropertiesFile;
import com.github.masterries.kafka.properties.PropertiesFileReader;

public class Main {

    public static void main(String[] args) throws InterruptedException {
      //  KafkaUtils.init();
       // GraphitePClass pc = new GraphitePClass("test");
       // pc.start();

        int a = 0;
        for (int i = 0; i < 1000000000; i++) {
              a= a+a;
        }
        System.out.println(a);

    }
}
