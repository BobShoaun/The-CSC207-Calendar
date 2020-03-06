package dates;

import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Date> {

    @Override
    public int compare(Date o1, Date o2) {
        if (o1.equals(o2))
            return 0;
        return o1.before(o2) ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DateComparator;
    }
}
