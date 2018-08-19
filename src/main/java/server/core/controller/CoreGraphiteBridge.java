package server.core.controller;

import server.transfer.Destination;
import server.transfer.TransferManager;

public class CoreGraphiteBridge {
	
	private static final String KAFKA_TOPIC = "CoreToTransferTest";
	
	public static void main(String[] args) {
		
		TransferManager tm = new TransferManager();
		tm.startDataTransfer(KAFKA_TOPIC, Destination.GRAPHITE);
		
		//TODO Shutdown of TransferManager before System.exit
		
		tm.stopDataTransfer();
		
	}
	
}
