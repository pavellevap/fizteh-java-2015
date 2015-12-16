package  ru.fizteh.fivt.students.pavellevap.CQL.impl;

import static org.junit.Assert.assertThat;
import static ru.fizteh.fivt.students.pavellevap.CQL.impl.FromStmt.from;
import static ru.fizteh.fivt.students.pavellevap.CQL.Sources.list;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.Matchers.*;
import ru.fizteh.fivt.students.pavellevap.CQL.Data;

@SuppressWarnings("unchecked")
public class FromStmtTest {

    private static List<Data> data;
    private static List<Tuple<String, String>> anotherData;

    @BeforeClass
    public static void setUp() {
        data = list(new Data(1, "a"),
                new Data(2, "b"),
                new Data(3, "c"),
                new Data(4, "d"),
                new Data(1, "a"),
                new Data(2, "b"),
                new Data(3, "c"),
                new Data(4, "d"),
                new Data(1, "d"));

        anotherData = list(new Tuple("a", "A"),
                new Tuple("b", "B"),
                new Tuple("c", "C"),
                new Tuple("d", "D"));

    }

    @Test
    public void testSelectDistinct() {
        assertThat(list(from(data).selectDistinct(Data.class, Data::getA, Data::getB).execute()), hasSize(5));
        assertThat(list(from(list("abra", "cadabra", "aba", "c", "aba")).selectDistinct(s -> s)
                .execute()), hasSize(4));
        assertThat(list(from(list("abra", "cadabra", "aba", "c", "aba")).selectDistinct(s -> s)
                .execute()), containsInAnyOrder("abra", "cadabra", "aba", "c"));
    }

    @Test
    public void testSelect() {
        assertThat(list(from(data).select(Data.class, Data::getA, Data::getB).execute()), hasSize(9));
        assertThat(list(from(list("abra", "cadabra", "aba", "c", "aba")).select(s -> s)
                .execute()), hasSize(5));
        assertThat(list(from(list("abra", "cadabra", "aba", "c", "aba")).select(s -> s)
                .execute()), containsInAnyOrder("abra", "cadabra", "aba", "c", "aba"));
    }

    @Test
    public void testJoin() {
        assertThat(list(from(data).join(anotherData).on((s, t) -> s.getB() == t.getFirst())
                .select(s -> s).execute()), hasSize(9));
        assertThat(list(from(data).join(anotherData).on((s, t) -> s.getB() == t.getFirst())
                .select(s -> s).execute()), hasItems(
                new Tuple(new Data(1, "a"), new Tuple("a", "A")),
                new Tuple(new Data(2, "b"), new Tuple("b", "B")),
                new Tuple(new Data(3, "c"), new Tuple("c", "C")),
                new Tuple(new Data(4, "d"), new Tuple("d", "D")),
                new Tuple(new Data(1, "d"), new Tuple("d", "D"))
        ));

        assertThat(list(from(data).join(anotherData).on(Data::getB, Tuple::getFirst)
                .select(s -> s).execute()), hasSize(9));
        assertThat(list(from(data).join(anotherData).on(Data::getB, Tuple::getFirst)
                .select(s -> s).execute()), hasItems(
                new Tuple(new Data(1, "a"), new Tuple("a", "A")),
                new Tuple(new Data(2, "b"), new Tuple("b", "B")),
                new Tuple(new Data(3, "c"), new Tuple("c", "C")),
                new Tuple(new Data(4, "d"), new Tuple("d", "D")),
                new Tuple(new Data(1, "d"), new Tuple("d", "D"))
        ));
    }
}