package entities;

import dates.CalendarGenerator;

import java.time.Duration;
import java.util.GregorianCalendar;

public class SubSeries {
    private Event base;
    private CalendarGenerator calGen;
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    private boolean inf;

    public SubSeries(Event base, CalendarGenerator calGen) {
        this.base = base;
        this.calGen = calGen;
        this.startTime = calGen.getStartTime();
        if (calGen.getEndTime()==null){
            //set default display end time to 1 year if it's infinite sub series
            inf = true;
            GregorianCalendar newTime = new GregorianCalendar();
            newTime.setTimeInMillis(startTime.getTimeInMillis()+Duration.ofDays(365).toMillis());
            this.endTime = newTime;
        }else {
            inf = false;
            this.endTime = calGen.getEndTime();
        }
    }

    public Event getBase() {
        return base;
    }

    public CalendarGenerator getCalGen() {
        return calGen;
    }

    public void setDisplayPeriod(GregorianCalendar start, GregorianCalendar end){
        //TODO: set display time for all sub series
        setStartDisplayTime(start);
        if(inf) { this.calGen.setEndTime(end);}
        else{setEndDisplayTime(end);}
    }
    private void setStartDisplayTime(GregorianCalendar start){
        if(start.before(startTime)){
            //The anchor for the startTime
            calGen.setStartTime(startTime);
        }else{
            calGen.setStartTime(start);
        }
    }
    private void setEndDisplayTime(GregorianCalendar end) {
        if(end.after(endTime)){
            calGen.setEndTime(endTime);
        }else{
            calGen.setEndTime(end);
        }
    }
}
