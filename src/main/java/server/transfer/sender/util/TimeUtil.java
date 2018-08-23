package server.transfer.sender.util;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
		return getUTCDateTime().toString();
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time
	 * @return localDateTime {@link LocalDateTime}
	 */
	public static LocalDateTime getUTCDateTime() {
		return LocalDateTime.now(Clock.systemUTC());
	}
	
	public static org.joda.time.DateTime getJodaDateTime(LocalDateTime ldt) {
		ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
		Instant inst = zdt.toInstant();
		long millis = inst.toEpochMilli();
		org.joda.time.LocalDateTime jodaLDT = new org.joda.time.LocalDateTime(millis);
		return jodaLDT.toDateTime();
	}
	
}
