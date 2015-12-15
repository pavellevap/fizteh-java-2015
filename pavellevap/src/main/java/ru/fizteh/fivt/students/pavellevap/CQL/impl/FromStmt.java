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
        iterable.forEach(element -> data.add(element));
        lastResult.forEach(element -> this.lastResult.add(element));
    }

    private FromStmt(Iterable<T> iterable) {
        iterable.forEach(element -> data.add(element));
        lastResult = new LinkedList<>();
    }

    private FromStmt(Stream<T> stream) {
        stream.forEach(element -> data.add(element));
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
        throw new UnsupportedOperationException();
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, s, true, (Iterable<R>) lastResult);
    }

    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) {
        throw new UnsupportedOperationException();
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
