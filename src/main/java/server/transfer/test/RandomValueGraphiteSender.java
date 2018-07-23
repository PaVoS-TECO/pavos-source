package server.transfer.test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import server.transfer.data.ObservationData;
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
							TimeUnit.MILLISECONDS.sleep(9900);;
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
				int value_PM10 = (int) (Math.random() * maxValue);
				int value_PM2p5 = (int) (Math.random() * maxValue);
				data = new ObservationData();
				data.locationElevation = "";
				data.locationID = "";
				data.locationName = "";
				data.observationDate = TimeUtil.getDateTimeString();
				data.observations.put("particulateMatter_PM10", String.valueOf(value_PM10));
				data.observations.put("particulateMatter_PM2p5", String.valueOf(value_PM2p5));
				return data;
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
