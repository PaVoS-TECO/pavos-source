package com.github.masterries.kafka.model;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Objects;


public class TimeInterval implements TimeValue {

    private Interval interval;

    private TimeInterval() {
    }

    private TimeInterval(Interval interval) {
        assert (interval != null);
        this.interval = interval;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.interval);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimeInterval other = (TimeInterval) obj;
        if (!Objects.equals(this.interval, other.interval)) {
            return false;
        }
        return true;
    }

    public static TimeInterval create(long start, long end) {
        return new TimeInterval(new Interval(start, end));
    }

    public static TimeInterval create(long start, long end, DateTimeZone timeZone) {
        return new TimeInterval(new Interval(start, end, timeZone));
    }

    public static TimeInterval parse(String value) {
        return new TimeInterval(Interval.parse(value));
    }

    public Interval getInterval() {
        return interval;
    }

    @Override
    public String asISO8601() {
        DateTimeFormatter printer = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
        printer = printer.withChronology(interval.getChronology());
        StringBuffer buf = new StringBuffer(48);
        printer.printTo(buf, interval.getStartMillis());
        buf.append('/');
        printer.printTo(buf, interval.getEndMillis());
        return buf.toString();
    }

    @Override
    public String toString() {
        return asISO8601();
    }

}
