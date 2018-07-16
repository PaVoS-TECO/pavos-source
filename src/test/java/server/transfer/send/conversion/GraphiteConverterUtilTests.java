package server.transfer.send.conversion;

import org.junit.Test;

public class GraphiteConverterUtilTests {

	@Test
	public void addFloatMetric_nullValue_skip() {
		GraphiteConverterUtil.addFloatMetric(null, null, null, null, null);
	}

}
