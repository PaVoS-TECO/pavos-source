package server.transfer.sender.util;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * 
 */
public final class TimeUtil {
	
	private TimeUtil() {
		
	}
	
	/**
	 * Returns the String of the current {@link LocalDateTime} in UTC time
	 * @return localDateTimeString {@link String}
	 */
	public static String getUTCDateTimeString() {
		return getUTCDateTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time
	 * @return localDateTime {@link LocalDateTime}
	 */
	public static LocalDateTime getUTCDateTime() {
		return LocalDateTime.now(DateTimeZone.UTC);
	}
	
}
