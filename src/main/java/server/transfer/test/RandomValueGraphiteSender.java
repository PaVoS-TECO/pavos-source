package server.transfer.test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import server.transfer.data.ObservationData;
import server.transfer.data.util.ObservationDataUtil;
import server.transfer.send.GraphiteSender;
import server.transfer.send.util.TimeUtil;

public class RandomValueGraphiteSender {

	private static final int MAX_VALUE = 30;
	private static final String TOPIC = "RandomGraphiteSenderTest";
	private static boolean loop = true;

	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			public void run() {
				
				GraphiteSender sender = new GraphiteSender();
				
					while (loop) {
						ObservationData data = null;
						data = generateRandomData(data, MAX_VALUE);
						
						try {
							TimeUnit.SECONDS.sleep(9);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						syncDateTime();
						sender.send(TOPIC, data);
					}
			}
			
			private void syncDateTime() {
				boolean sync = true;
				while (sync) {
					LocalDateTime dateTime = LocalDateTime.now();
					String time = String.valueOf(dateTime.toEpochSecond(ZoneOffset.UTC));
					char c = time.charAt(time.length() - 1);
					if (c=='0') sync = false;
				}
			}
			
			private ObservationData generateRandomData(ObservationData data, int maxValue) {
				int value = (int) (Math.random() * maxValue);
				data = new ObservationData();
				return ObservationDataUtil.setupData(data, "", "", "", TimeUtil.getDateTimeString(), String.valueOf(value));
			}
			
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
