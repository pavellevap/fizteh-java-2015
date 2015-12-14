package ru.fizteh.fivt.students.pavellevap.CQL.impl;

import java.util.List;

public class UnionStmt<R> {
    Iterable<R> lastResult;

    public UnionStmt(Iterable<R> lastResult) {
        this.lastResult = lastResult;
    }

    public <T> FromStmt<T> from(Iterable<T> list) {
        return new FromStmt(list, lastResult);
    }
}
