package server.core.controller;

import server.core.properties.KafkaUtils;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		KafkaUtils.init();
		GraphitePClass pc = new GraphitePClass("test");
		pc.start();

		int a = 0;
		for (int i = 0; i < 1000000000; i++) {
			a = a + a;
		}
		System.out.println(a);

	}
}
