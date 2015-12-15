package ru.fizteh.fivt.students.pavellevap.CQL.impl;

@SuppressWarnings("unchecked")
public class UnionStmt<R> {
    private Iterable<R> lastResult;

    public UnionStmt(Iterable<R> lastResult) {
        this.lastResult = lastResult;
    }

    public <T> FromStmt<T> from(Iterable<T> list) {
        return new FromStmt(list, lastResult);
    }
}
