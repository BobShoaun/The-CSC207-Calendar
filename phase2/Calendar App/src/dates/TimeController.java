package dates;

import java.util.GregorianCalendar;

/**
 * Epic time-travelling wizardry class
 */
public class TimeController {
    private GregorianCalendar currentTime;

    /**
     * Initialize this TimeTravellerController
     */
    public TimeController() {
        currentTime = new GregorianCalendar();
    }

    /**
     * Get the current time
     *
     * @return Current Time
     */
    public GregorianCalendar getTime() {
        return currentTime;
    }

    /**
     * Set the current time by travelling 80 mph in a DeLorean.
     *
     * @param newTime New Time to be set.
     */
    public void setCurrentTime(GregorianCalendar newTime) {
        currentTime = newTime;
    }

}
