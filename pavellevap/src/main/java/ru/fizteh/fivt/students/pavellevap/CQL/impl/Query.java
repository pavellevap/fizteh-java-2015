package ru.fizteh.fivt.students.pavellevap.CQL.impl;

import java.util.stream.Stream;

/**
 * @author akormushin
 */
interface Query<R> {

    Iterable<R> execute();

    Stream<R> stream();
}
