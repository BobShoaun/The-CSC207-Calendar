import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ListUIView<T> extends UserInterface{
    private Iterator<T> iterator;
    List<String> elements;
    int start = 0;
    int end = 10;
    int size = 10;
    private Function<T, String> converter;

    public ListUIView(Iterator<T> iterator, Function<T, String> converter){
        this.converter = converter;
        elements = new ArrayList<>();
        this.iterator = iterator;
        getMore();
        end = Math.min(10, elements.size());
    }

    private void getMore() {
        for (int i = 0; i < size; i++) {
            if(!iterator.hasNext())
                break;
            elements.add(converter.apply(iterator.next()));
        }
    }

    @Override
    public void display() {
        for (int i = start; i < end; i++) {
            System.out.println(elements.get(i));
        }
    }

    @Override
    public void show() {
        if(!iterator.hasNext() && elements.size() == 0)
        {
            System.out.println("No elements found!");
            return;
        }
        do {
            display();
            getMore();
            start = end;
            end = Math.min(end + 10, elements.size());
        } while(getOptionsInput(new String[]{"End", "Continue"}) == 1 && start != elements.size());
    }
}
