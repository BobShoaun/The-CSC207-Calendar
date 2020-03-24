package user;

import java.util.Date;
import java.util.GregorianCalendar;

public class TimeController {
    GregorianCalendar currentTime;

    public TimeController(){
        currentTime = new GregorianCalendar();
    }

    public GregorianCalendar getTime(){
        return currentTime;
    }

    public void setCurrentTime(GregorianCalendar newTime){
        currentTime = newTime;
    }

    public void incrementTime(GregorianCalendar timeIncrease){
        currentTime.setTime(new Date(currentTime.getTime().getTime() + timeIncrease.getTime().getTime()));
    }
}
