package consoleui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Terminal UI for displaying menu options.
 *
 * @param <T>
 */
public class ListUIView<T> extends UserInterface {
    private Iterator<T> iterator;
    List<T> elementsShown;
    List<String> elements;
    int start;
    int rawStart;
    int end;
    int size = 10;
    private Function<T, String> converter;

    /**
     * Initialize this ListUIView.
     *
     * @param iterator  Iterator to show elements
     * @param converter Converter
     * @param start     Start index
     */
    public ListUIView(Iterator<T> iterator, Function<T, String> converter, int start) {
        this.converter = converter;
        this.start = start;
        rawStart = start;
        elements = new ArrayList<>();
        this.iterator = iterator;
        elementsShown = new ArrayList<>();
        getMore();
        end = Math.min(10, rawStart + elements.size());
    }

    private void getMore() {
        for (int i = 0; i < size; i++) {
            if (!iterator.hasNext())
                break;
            T nextElement = iterator.next();
            elements.add(converter.apply(nextElement));
            elementsShown.add(nextElement);
        }
    }

    /**
     * Display the list.
     */
    @Override
    public void display() {
        for (int i = start; i < end; i++) {
            System.out.println("(" + i + ") " + elements.get(i - rawStart));
        }
    }

    /**
     * Show the contents of the list
     */
    @Override
    public void show() {
        if (!iterator.hasNext() && elements.size() == 0) {
            System.out.println("No elements found!");
            return;
        }
        do {
            display();
            getMore();
            start = end;
            end = Math.min(end + 10, rawStart + elements.size());
        } while (getOptionsInput(new String[]{"End", "Continue"}) == 1 && start != elements.size());
    }

    /**
     * Get the shown elements
     *
     * @return Elements that have been shown.
     */
    public List<T> getElementsShown() {
        return elementsShown;
    }
}
