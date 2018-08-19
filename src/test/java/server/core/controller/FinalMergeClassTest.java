package server.core.controller;

import org.junit.Test;

import server.core.properties.KafkaAdmin;

public class FinalMergeClassTest {

	@Test
	public void setupAndMerge() {
		KafkaAdmin.init();
		System.out.println("[Test] KafkaUtils | init() | done!");
		
		FinalMergeClass fmc = new FinalMergeClass("mergeA", "mergeB", "mergeResult", "key");
		System.out.println("[Test] FinalMergeClass | constructor() | done!");
		
		fmc.start();
		System.out.println("[Test] FinalMergeClass | start() | done!");
	}

}
