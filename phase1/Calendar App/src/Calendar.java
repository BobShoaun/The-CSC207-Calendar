import java.util.ArrayList;
import java.util.List;

/**
 * Calendar class
 */
public class Calendar {

    List<EventCollection> eventCollections;
    List<AlertCollection> alertCollections;
    List<MT> memos;
    List<MT> tags;

    public Calendar () {
        eventCollections = new ArrayList<>();
        alertCollections = new ArrayList<>();
        memos = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public void getPastEvents () {}

    public void getOngoingEvents () {}

    public void getFutureEvents () {}

}
