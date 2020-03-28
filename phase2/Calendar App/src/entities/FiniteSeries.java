package entities;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import user.DataSaver;

import java.util.GregorianCalendar;

public class FiniteSeries extends Series {
    //the end date where the repeating series definitively ends, unlike infinite series that goes
    //on forever as long its within the period that user inputs
    private GregorianCalendar endTime;

    /**
     * @param name      the name of this Finite Series
     * @param baseEvent the base Event this Series is modelled upon
     * @param calGen    List of start date of the events of the series, CalGen basically contains the user input of
     *                  display periods, however in a finite Series calGen.getEndTime == fixedEndTime
     * @param saver     saver object handling save and load of this series
     * @throws InvalidDateException invalid dates in events
     */
    public FiniteSeries(String name, Event baseEvent, CalendarGenerator calGen, DataSaver saver) throws InvalidDateException {
        super(name, baseEvent, calGen, saver);
        this.endTime = calGen.getEndTime();
    }

    public void setEndTime(GregorianCalendar endTime) {
        this.endTime = endTime;
        getCalGen().setEndTime(endTime);
    }

    /**
     *
     * @param start startDisplay time of this Finite Series
     * @param end endDisplay time of this Finite Series
     */
    @Override
    public void setDisplayPeriod(GregorianCalendar start, GregorianCalendar end) {
        if(start.after(getCalGen().getStartTime())) {getCalGen().setStartTime(start);}
        if(end.after(endTime)){
            getCalGen().setEndTime(endTime);
        }else{
            getCalGen().setEndTime(end);
        }
    }
}
