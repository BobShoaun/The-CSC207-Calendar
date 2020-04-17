package event;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;

import java.time.Duration;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Factory class for creating Series.
 */
public class SeriesFactory {

    /**
     * Create a finite or infinite series.
     *
     * @param name      Name of series
     * @param baseEvent Base Event
     * @param start     Start time
     * @param end       End time (optional)
     * @param durs      Repetition periods
     * @return Series, may be FiniteSeries
     * @throws InvalidDateException if incorrect dates are passed
     */
    public Series getSeries(String name, Event baseEvent, GregorianCalendar start, GregorianCalendar end, List<Duration> durs) throws InvalidDateException {

        CalendarGenerator calGen = new CalendarGenerator(start, durs, end);
        if (end == null) {
            return new Series(name, baseEvent, calGen);
        } else {
            return new FiniteSeries(name, baseEvent, calGen);
        }
    }
}
