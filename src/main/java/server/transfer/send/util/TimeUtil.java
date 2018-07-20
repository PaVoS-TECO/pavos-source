package server.transfer.send.util;

import java.time.Clock;
import java.time.LocalDateTime;

public final class TimeUtil {
	
	private TimeUtil() {
		
	}
	
	/**
	 * Returns the String of the current {@link LocalDateTime} in UTC time
	 * @return localDateTimeString {@link String}
	 */
	public static String getDateTimeString() {
		return LocalDateTime.now(Clock.systemUTC()).toString();
	}
	
}
