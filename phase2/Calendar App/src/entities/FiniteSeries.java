package entities;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import user.DataSaver;

import java.io.IOException;
import java.util.GregorianCalendar;

public class FiniteSeries extends InfiniteSeries {
    private GregorianCalendar endTime;

    /**
     * @param name      the name of this Finite Series
     * @param baseEvent the base Event this Series is modelled upon
     * @param calGen    List of start date of the events of the series, CalGen basically contains the user input of
     *                  display periods, however in a finite Series calGen.getEndTime == fixedEndTime
     * @param endTime   the end date where the repeating series definitively ends, unlike infinite series that goes
     *                  on forever as long its within the period that user inputs
     * @param saver     saver object handling save and load of this series
     * @throws InvalidDateException invalid dates in events
     */
    public FiniteSeries(String name, Event baseEvent, CalendarGenerator calGen, GregorianCalendar endTime, DataSaver saver) throws InvalidDateException {
        super(name, baseEvent, new CalendarGenerator(calGen.getStartTime(), calGen.getPeriods(), endTime), saver);
        this.endTime = endTime;
    }

    public void setEndTime(GregorianCalendar endTime) {
        this.endTime = endTime;
    }
}
