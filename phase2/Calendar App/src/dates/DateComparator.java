package dates;

import java.util.Comparator;
import java.util.Date;

/**
 * Compares Dates.
 */
public class DateComparator implements Comparator<Date> {

    /**
     * Compare two dates
     *
     * @param o1 First date
     * @param o2 Second date
     * @return -1 if o1 is before o2, 0 if they are equal, and 1 otherwise
     */
    @Override
    public int compare(Date o1, Date o2) {
        if (o1.equals(o2))
            return 0;
        return o1.before(o2) ? -1 : 1;
    }

    /**
     * Equals method
     *
     * @param obj Object to compare this DateComparator to
     * @return True iff obj is an instance of DateComparator.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DateComparator;
    }
}
