package alert;

import dates.CalendarGenerator;
import event.Event;
import exceptions.PeriodAlreadyExistsException;
import user.DataSaver;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * A collection/group of one alert. Has a list of manually created alerts and possibly
 * a dates.CalendarGenerator for repeating Alerts.
 *
 * @author colin
 */
public class AlertCollection implements Observer {
    private List<Alert> manAlerts;
    private String eventId;
    private GregorianCalendar eventTime;
    private CalendarGenerator calGen;
    private DataSaver saver;
    private String eventName;

    /**
     * Creates a new alert.Alert group (possibly repeating)
     *
     * @param e The event.Event attached to the alert.Alert.
     */
    public AlertCollection(Event e, DataSaver saver) {
        this.eventId = e.getId();
        this.eventName = e.getName();
        this.eventTime = new GregorianCalendar();
        this.eventTime.setTime(e.getStartDate().getTime());
        manAlerts = new ArrayList<>();
        this.saver = saver;
    }

    /**
     * Get the ID of the event associated with this alert.AlertCollection
     *
     * @return The ID of the event
     */
    public String getEventId() {
        return eventId;
    }

    public List<Alert> getManAlerts() {
        return manAlerts;
    }

    /**
     * Add an alert manually by setting the time.
     *
     * @param time The time of the alert.Alert to be added.
     * @return Whether or not the alert.Alert could be added.
     */
    public boolean addAlert(GregorianCalendar time) {
        if (calGen != null) {
            for (GregorianCalendar c : calGen) {
                if (c.equals(time))
                    return false;
            }
        }
        for (Alert a : manAlerts) {
            if (a.getTime().getTime().equals(time.getTime()))
                return false;
        }
        manAlerts.add(new Alert(eventId, time));
        manAlerts.sort(new AlertComparator());
        save();
        return true;
    }

    /**
     * Add a recurring alert.Alert until the event.Event occurs
     *
     * @param start  The start time
     * @param period The time between each alert
     */
    public void addAlert(GregorianCalendar start, Duration period) throws PeriodAlreadyExistsException {
        if (this.calGen == null) {
            ArrayList<Duration> d = new ArrayList<>();
            d.add(period);
            this.calGen = new CalendarGenerator(start, d, eventTime);
        } else if (this.calGen.getPeriods().contains(period)) {
            throw new PeriodAlreadyExistsException();
        } else
            this.calGen.addPeriod(period);
        save();
    }

    /**
     * Remove a single alert.Alert.
     *
     * @param d The date of the alert.Alert to be removed
     * @return Whether or not the alert.Alert could be removed.
     */
    public boolean removeAlert(GregorianCalendar d) {
        return removeManualAlert(d) || removeGeneratedAlert(d);
    }

    /**
     * Remove an automatically generated alert.Alert.
     *
     * @param d The time of the alert being removed.
     * @return Whether the alert could be removed
     */
    public boolean removeGeneratedAlert(GregorianCalendar d) {
        for (GregorianCalendar ignored : calGen.getIgnoreList()) {
            if (d.equals(ignored))
                return false;
        }
        save();
        return calGen.getIgnoreList().add(d);
    }

    /**
     * Remove a manually created alert.Alert.
     *
     * @param d The time of the alert being removed.
     * @return Whether the alert could be removed
     */
    public boolean removeManualAlert(GregorianCalendar d) {
        boolean result = manAlerts.removeIf(a -> a.getTime().getTime().equals(d.getTime()));
        save();
        return result;
    }

    /**
     * Shift the time of all Alerts in this alert.AlertCollection.
     *
     * @param newEventTime The new time of the event.Event
     */
    private void shiftAlerts(GregorianCalendar newEventTime) {
        if (calGen == null) {
            throw new IllegalStateException();
        }
        GregorianCalendar newStart = new GregorianCalendar();
        long diff = newEventTime.getTimeInMillis() - eventTime.getTimeInMillis();
        long newMillis = calGen.getStartTime().getTimeInMillis() + diff;
        newStart.setTimeInMillis(newMillis);
        this.calGen.setStartTime(newStart);
        save();
    }

//    /**
//     * Replace the current dates.CalendarGenerator with a new one.
//     *
//     * @param cg The new dates.CalendarGenerator
//     */
//    public void setCalendarGenerator(dates.CalendarGenerator cg) {
//        this.calGen = cg;
//    }

//    /**
//     * Get the current dates.CalendarGenerator.
//     *
//     * @return The current dates.CalendarGenerator
//     */
//    public dates.CalendarGenerator getCalendarGenerator() {
//        return calGen;
//    }

    /**
     * Get all Alerts for the event between a set of times.
     *
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return The list of Alerts between start and end time.
     */
    public List<Alert> getAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = getManualAlerts(start, end);
        alerts.addAll(getGeneratedAlerts(start, end));
        alerts.sort(new AlertComparator());
        return alerts;
    }

    /**
     * Get a single alert with a given date.
     *
     * @param time Date of the alert
     * @return The alert
     */
    public Alert getAlert(GregorianCalendar time) {
        return getAlerts(time, time).get(0);
    }

    /**
     * Get manually created Alerts for the event between a set of times.
     *
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return The list of Alerts between start and end time.
     */
    private List<Alert> getManualAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new LinkedList<>();
        for (Alert a : manAlerts) {
            if (a.getTime().getTime().compareTo(start.getTime()) >= 0
                    && a.getTime().getTime().compareTo(end.getTime()) <= 0)
                alerts.add(a);
        }
        return alerts;
    }

    /**
     * Get automatically generated Alerts for the event between a set of times.
     *
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return The list of Alerts between start and end time.
     */
    private List<Alert> getGeneratedAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new ArrayList<>();
        for (GregorianCalendar d : calGen) {
            if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0)
                alerts.add(new Alert(eventId, d));
        }
        return alerts;
    }

    /**
     * Update this AC when its associated event changes date.
     *
     * @param o   The event being updated.
     * @param arg The new time for the event.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof GregorianCalendar) {
            shiftAlerts((GregorianCalendar) arg);
        } else if(arg == null){
            //Do nothing, in this case we are only saving in event collection
        }
        else
            throw new IllegalArgumentException();
    }

    /**
     * Get a String representation of data in this alert.AlertCollection.
     *
     * @return String representation of all the data in this AC, including the dates.CalendarGenerator.
     */
    public String getString() {
        StringBuilder result = new StringBuilder(eventId + "\n" + eventTime.getTimeInMillis() + "\n");
        for (Alert a : getManAlerts()) {
            result.append(a.getString()).append(" ");
        }
        if (calGen != null)
            result.append("\n").append(calGen.getString());
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Alert for \"" + eventName
                + "\", which occurs at " + eventTime.getTime().toString() + ".\n");
        result.append("     ===== MANUALLY CREATED ALERTS =====\n");
        if (manAlerts.size() == 0)
            result.append("None.");
        else {
            for (Alert a : manAlerts) {
                result.append(a.toString()).append("\n");
            }
        }
        result.append("        ===== REPEATING ALERTS =====\n");
        if (calGen != null)
            result.append(calGen.toString());
        else
            result.append("None.");
        return result.toString();
    }

    /**
     * Load the data into this alert.AlertCollection.
     *
     * @param eventId The ID of the event for which the Alerts are being loaded
     */
    public void load(String eventId) {
        List<String> strings = null;
        try {
            strings = saver.loadStringsFromFile("/alerts/" + eventId + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        assert strings != null;
        this.eventId = strings.get(0).trim();

        String[] manTimes = strings.get(1).trim().split("\\n+");
        for (String timeStr : manTimes) {
            manAlerts.add(new Alert(eventId, timeStr));
        }

        StringBuilder cgStr = new StringBuilder();
        for (int i = 2; i < strings.size(); i++) {
            cgStr.append(strings.get(i));
        }
        this.calGen = new CalendarGenerator(cgStr.toString());
    }

    /**
     * Save this AlertCollection's data into a text file.
     */
    public void save() {
        List<String> contents = Arrays.asList(getString().split("\\n+"));
        try {
            saver.saveToFile("alerts/" + eventId + ".txt", contents);
        } catch (IOException e) {
            System.out.println("Error while saving AlertCollection " + eventId);
            e.printStackTrace();
        }
    }

}
