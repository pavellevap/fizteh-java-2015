package ru.fizteh.fivt.students.pavellevap.CQL.impl;

import static org.junit.Assert.assertThat;
import static ru.fizteh.fivt.students.pavellevap.CQL.Sources.list;
import static org.hamcrest.Matchers.*;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.pavellevap.CQL.Data;
import ru.fizteh.fivt.students.pavellevap.CQL.CollectionQuery.*;
import static ru.fizteh.fivt.students.pavellevap.CQL.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.pavellevap.CQL.Conditions.rlike;
import static ru.fizteh.fivt.students.pavellevap.CQL.impl.FromStmt.from;

import java.time.LocalDate;
import java.util.List;

public class SelectStmtTest {

    public static List<Data> data;
    public static List<Student> students;

    @BeforeClass
    public static void setUp() {
        students = list(student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                student("smirnov", LocalDate.parse("1986-08-06"), "495"),
                student("smith", LocalDate.parse("1986-08-06"), "495"),
                student("golovanov", LocalDate.parse("1985-04-13"), "497"),
                student("frolov", LocalDate.parse("1989-06-18"), "497"),
                student("petrov", LocalDate.parse("2006-08-06"), "494"));

        data = list(new Data(1, "a"),
                new Data(2, "b"),
                new Data(3, "c"),
                new Data(4, "d"),
                new Data(1, "a"),
                new Data(2, "b"),
                new Data(3, "c"),
                new Data(4, "d"),
                new Data(1, "d"));
    }

    @Test
    public void testWhere() {
        assertThat(list(from(students).select(Student.class, s -> s).where(rlike(Student::getName, ".*ov"))
                .execute()), hasSize(6));
        assertThat(list(from(students).select(Student.class, s -> s).where(rlike(Student::getName, ".*ov"))
                .execute()), hasItem(student("golovanov", LocalDate.parse("1985-04-13"), "497")));
        assertThat(list(from(students).select(String.class, Student::getGroup)
                .where(s -> s.getGroup().equals("495")).execute()), hasSize(3));
        assertThat(list(from(students).select(String.class, Student::getGroup)
                .where(s -> s.getGroup().equals("495")).execute()), hasItem("495"));
        assertThat(list(from(students).select(String.class, Student::getGroup)
                .where(s -> s.getGroup().equals("495")).execute()), not(hasItem("497")));
        assertThat(list(from(students).select(String.class, Student::getGroup)
                .where(s -> s.getGroup().equals("495")).execute()), not(hasItem("494")));
    }

    @Test
    public void testGroupBy() {
        assertThat(list(from(students).select(Student.class, s -> s).groupBy(Student::getGroup)
                .execute()), hasSize(3));
        assertThat(list(from(students).select(Student.class, s -> s).groupBy(Student::getGroup)
                .execute()), hasItem(hasProperty("group", is("497"))));
        assertThat(list(from(data).select(String.class, Data::getB).groupBy(Data::getB).execute()),
                hasSize(4));
        assertThat(list(from(data).select(String.class, Data::getB).groupBy(Data::getB).execute()),
                containsInAnyOrder("a", "b", "c", "d"));
    }
}