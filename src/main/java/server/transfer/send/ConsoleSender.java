package server.transfer.send;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.modules.cPickle;

import server.transfer.convert.GraphiteConverter;
import server.transfer.data.ObservationData;


/**
 * Reformats the data and outputs it into the console
 */
public class ConsoleSender extends Sender {

	@Override
	public void send(ConsumerRecords<String, ObservationData> records) {
		PyList list = new PyList();

		records.forEach(record -> {
			GraphiteConverter.addPM10(record, list, logger);
		});

		PyString payload = cPickle.dumps(list);
		System.out.println(payload.asString());
	}

}
