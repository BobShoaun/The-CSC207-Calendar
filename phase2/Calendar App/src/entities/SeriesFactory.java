package entities;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import user.DataSaver;

import java.time.Duration;
import java.util.GregorianCalendar;
import java.util.List;

public class SeriesFactory {

    public Series getSeries(String name, Event baseEvent, GregorianCalendar start, GregorianCalendar end, List<Duration> durs, DataSaver saver) throws InvalidDateException {

        CalendarGenerator calGen = new CalendarGenerator(start, durs, end);
        if (end == null) {
            return new Series(name, baseEvent, calGen, saver);
        } else {
            return new FiniteSeries(name, baseEvent, calGen, saver);
        }
    }
}
