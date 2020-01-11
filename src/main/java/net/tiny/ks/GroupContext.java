package net.tiny.ks;

import java.util.Iterator;
import java.util.TreeSet;

public class GroupContext<T> {

    protected TreeSet<T> group = new TreeSet<T>();
    private T current;

    public GroupContext(T t) {
        current = t;
        group.add(current);
    }

    public GroupContext<T> append(T t) {
        if (!group.contains(t)) {
            group.add(t);
        }
        return this;
    }

    public T getCurrent() {
        return current;
    }

    public T getFrist() {
        if (group.isEmpty())
            return null;
        return group.first();
    }

    public T getLast() {
        if (group.isEmpty())
            return null;
        return group.last();
    }

    public T getNext() {
        return getNext(current);
    }

    public T getNext(T sc) {
        if (group.isEmpty())
            return null;
        return group.higher(sc);
    }

    public T next() {
        current = getNext();
        return current;
    }

    public GroupContext<T> remove(T t) {
        if (group.remove(t)) {
            if (t.equals(current)) {
                if (group.isEmpty()) {
                    current = null;
                } else {
                    current = group.first();
                }
            }
        }
        return this;
    }

    public void clear() {
        group.clear();
        current = null;
    }

    public int size() {
        return group.size();
    }

    public T[] toArray(T[] t) {
        return group.toArray(t);
    }

    public int indexOf(T t) {
        int i = -1;
        Iterator<T> it = group.iterator();
        while (it.hasNext()) {
            i++;
            if (it.next().equals(t)) {
                break;
            }
        }
        return i;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("@" + hashCode() + " ");
        sb.append(group.toString());
        return sb.toString();
    }
}
