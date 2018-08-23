package server.core.controller;

import server.core.controller.testClasses.DummyTable;
import server.core.properties.KafkaTopicAdmin;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		KafkaTopicAdmin.getInstance();
//		GraphitePClass pc = new GraphitePClass("test");
//		pc.start();
		//Merge process
		System.out.println("Merge form Observatiosn to FeatureOfIntrested Started to Output Topic => ObservationsMerges1");
		DummyTable dummyTable = new DummyTable();
		dummyTable.startGeneric();
	
		
		
		System.out.println("[Main] finished successfully!");
	}
}
