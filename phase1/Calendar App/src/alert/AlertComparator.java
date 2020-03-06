package alert;

import java.util.Comparator;

public class AlertComparator implements Comparator<Alert> {

    @Override
    public int compare(Alert o1, Alert o2) {
        if (o1.getTime().equals(o2.getTime()))
            return 0;
        return o1.getTime().before(o2.getTime()) ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AlertComparator;
    }
}
