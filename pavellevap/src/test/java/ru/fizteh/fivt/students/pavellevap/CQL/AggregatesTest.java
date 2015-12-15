package ru.fizteh.fivt.students.pavellevap.CQL;

import static ru.fizteh.fivt.students.pavellevap.CQL.Aggregates.count;
import static ru.fizteh.fivt.students.pavellevap.CQL.Aggregates.avg;
import static ru.fizteh.fivt.students.pavellevap.CQL.Aggregates.min;
import static ru.fizteh.fivt.students.pavellevap.CQL.Aggregates.max;
import static ru.fizteh.fivt.students.pavellevap.CQL.Sources.list;

import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;

public class AggregatesTest {

    private static List<Data> data;

    @BeforeClass
    public static void setUp() {
        data = list(new Data(1, "a"),
                new Data(1, "b"),
                new Data(2, "c"),
                new Data(2, "d"),
                new Data(3, "a"),
                new Data(3, "b"),
                new Data(4, "c"),
                new Data(4, "d"));
    }

    @Test
    public void countTest() {
        assertThat(count(Data::getA).apply(data), is(4L));
        assertThat(count(Data::getB).apply(data), is(4L));
        assertThat(count((Double s) -> s).apply(list(1.75, 2.25, 2.75, 3.25, 3.75)), is(5L));
    }

    @Test
    public void avgTest() {
        assertThat(avg(Data::getA).apply(data), is(2.5));
        assertThat(avg((Double s) -> s).apply(list(1.75, 2.25, 2.75, 3.25, 3.75)), is(2.75));
    }

    @Test
    public void minTest() {
        assertThat(min(Data::getA).apply(data), is(1));
        assertThat(min(Data::getB).apply(data), is("a"));
        assertThat(min((Double s) -> s).apply(list(3.75, 3.25, 2.75, 2.25, 1.75)), is(1.75));
    }

    @Test
    public void maxTest() {
        assertThat(max(Data::getA).apply(data), is(4));
        assertThat(max(Data::getB).apply(data), is("d"));
        assertThat(max((Double s) -> s).apply(list(3.75, 3.25, 2.75, 2.25, 1.75)), is(3.75));
    }
}