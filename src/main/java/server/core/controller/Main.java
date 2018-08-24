package server.core.controller;

import java.awt.geom.Point2D;

import org.python.modules.thread.thread;

import server.core.controller.testClasses.DummyTable;
import server.core.grid.GeoGrid;

import server.core.grid.config.WorldMapData;
import server.core.properties.KafkaTopicAdmin;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		KafkaTopicAdmin.getInstance();
//		GraphitePClass pc = new GraphitePClass("test");
//		pc.start();
		//Merge process
		MergeObsToFoiProcess foiProcess = new MergeObsToFoiProcess();
		foiProcess.kafkaStreamStart();
		//Thread.sleep(5000);
		//GridProcess gridProcess = new GridProcess();
		//gridProcess.kafkaStreamStart();

		
		
		System.out.println("[Main] finished successfully!");
	}
}
