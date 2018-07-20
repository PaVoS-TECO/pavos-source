package server.transfer.send.conversion;

import org.junit.Test;

import server.transfer.convert.util.PythonMetricUtil;

public class GraphiteConverterUtilTests {

	@Test
	public void addFloatMetric_nullValue_skip() {
		PythonMetricUtil.addFloatMetric(null, null, null, null, null);
	}

}
