package alert;

import java.util.Comparator;

public class AlertComparator implements Comparator<Alert> {

    /**
     * Compare two alerts.
     *
     * @param o1 Alert 1
     * @param o2 Alert 2
     * @return -1 if o1 occurs before o2 and 1 otherwise.
     */
    @Override
    public int compare(Alert o1, Alert o2) {
        if (o1.getTime().equals(o2.getTime()))
            return 0;
        return o1.getTime().before(o2.getTime()) ? -1 : 1;
    }

    /**
     * Equals method.
     *
     * @param obj The object being compared
     * @return True iff the object is another AlertComparator.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AlertComparator;
    }
}
