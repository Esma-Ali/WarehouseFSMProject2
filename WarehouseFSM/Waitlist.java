import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Waitlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedList<WaitlistItem> items =
            new LinkedList<>();

    public void add(Client client, int quantity) {
        items.addLast(new WaitlistItem(client, quantity));
    }

    // Oldest request first (First In - First Out), or null if no client is waiting
    public WaitlistItem getFirst() {
        return items.peekFirst();
    }

    public WaitlistItem removeFirst() {
        return items.pollFirst();
    }

    // Read-only so waitlist cannot change while while displaying the List
    public Iterator<WaitlistItem> getIterator() {
        return Collections.unmodifiableList(items).iterator();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
