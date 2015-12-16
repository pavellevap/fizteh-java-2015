package ru.fizteh.fivt.students.pavellevap.CQL.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.*;

@SuppressWarnings("unchecked")
public class FromStmt<T> {
    private List<T> data;
    private List<Object> lastResult;

    FromStmt(Iterable<T> iterable, Iterable<?> lastResult) {
        data = new LinkedList<>();
        iterable.forEach(data::add);
        this.lastResult = new LinkedList<>();
        lastResult.forEach(this.lastResult::add);
        if (data.size() == 0) {
            throw new RuntimeException();
        }
    }

    private FromStmt(Iterable<T> iterable) {
        data = new LinkedList<>();
        iterable.forEach(data::add);
        lastResult = new LinkedList<>();
        if (data.size() == 0) {
            throw new RuntimeException();
        }
    }

    private FromStmt(Stream<T> stream) {
        data = new LinkedList<>();
        stream.forEach(data::add);
        lastResult = new LinkedList<>();
        if (data.size() == 0) {
            throw new RuntimeException();
        }
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
    }

    public static <T> FromStmt<T> from(Query query) {
        return new FromStmt<>(query.execute());
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, s, false, (Iterable<R>) lastResult);
    }

    public final <R> SelectStmt<T, R> select(Function<T, R> s) {
        return select((Class) s.apply(data.get(0)).getClass(), s);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        Function<T, Tuple<F, S>> producer = element -> new Tuple(first.apply(element), second.apply(element));
        return select(producer);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, s, true, (Iterable<R>) lastResult);
    }

    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) {
        return selectDistinct((Class) s.apply(data.get(0)).getClass(), s);
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        throw new UnsupportedOperationException();
    }

    public <J> JoinClause<T, J> join(Stream<J> stream) {
        throw new UnsupportedOperationException();
    }

    public <J> JoinClause<T, J> join(Query<J> stream) {
        throw new UnsupportedOperationException();
    }

    public class JoinClause<T, J> {

        public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
            throw new UnsupportedOperationException();
        }

        public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
                Function<T, K> leftKey,
                Function<J, K> rightKey) {
            throw new UnsupportedOperationException();
        }
    }
}
