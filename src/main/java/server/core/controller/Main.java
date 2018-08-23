package server.core.controller;

import java.awt.geom.Point2D;

import server.core.controller.testClasses.DummyTable;
import server.core.grid.GeoGrid;
import server.core.grid.GeoRectangleGrid;
import server.core.grid.config.WorldMapData;
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
