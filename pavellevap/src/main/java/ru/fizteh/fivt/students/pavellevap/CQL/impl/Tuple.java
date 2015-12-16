package ru.fizteh.fivt.students.pavellevap.CQL.impl;

/**
 * @author akormushin
 */
public class Tuple<F, S> {

    private final F first;
    private final S second;

    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Tuple(Tuple<F, S> tuple) {
        this(tuple.getFirst(), tuple.getSecond());
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Tuple{"
                + "first=" + first
                + ", second=" + second
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple) {
            Tuple t = (Tuple) obj;
            return first.equals(t.first) && second.equals(t.second);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 1000000009 + second.hashCode();
    }
}
