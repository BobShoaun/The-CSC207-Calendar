package event;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;

import java.util.GregorianCalendar;

/**
 * A finite series of Events. Must have an end time, unlike infinite series.
 */
public class FiniteSeries extends Series {

    private GregorianCalendar endTime;

    /**
     * @param name      the name of this Finite Series
     * @param baseEvent the base Event this Series is modelled upon
     * @param calGen    List of start date of the events of the series, CalGen basically contains the user input of
     *                  display periods, however in a finite Series calGen.getEndTime == fixedEndTime
     * @throws InvalidDateException invalid dates in events
     */
    public FiniteSeries(String name, Event baseEvent, CalendarGenerator calGen) throws InvalidDateException {
        super(name, baseEvent, calGen);
        this.endTime = calGen.getEndTime();
    }

    /**
     * @param start startDisplay time of this Finite Series
     * @param end   endDisplay time of this Finite Series
     */
    @Override
    public void setDisplayPeriod(GregorianCalendar start, GregorianCalendar end) {
        setStartDisplayTime(start);
        if (end.after(endTime)) {
            getCalGen().setEndTime(endTime);
        } else {
            getCalGen().setEndTime(end);
        }
    }
}
