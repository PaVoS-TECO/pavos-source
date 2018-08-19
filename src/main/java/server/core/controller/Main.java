package server.core.controller;

import server.core.properties.KafkaAdmin;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		KafkaAdmin kAdmin = KafkaAdmin.getInstance();
		GraphitePClass pc = new GraphitePClass("test");
		pc.start();

		int a = 0;
		for (int i = 0; i < 1000000000; i++) {
			a = a + a;
		}
		System.out.println(a);

	}
}
