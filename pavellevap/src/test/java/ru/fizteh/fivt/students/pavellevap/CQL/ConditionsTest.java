package ru.fizteh.fivt.students.pavellevap.CQL;

import static org.junit.Assert.assertThat;
import static ru.fizteh.fivt.students.pavellevap.CQL.Sources.list;
import static org.hamcrest.Matchers.*;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.pavellevap.CQL.CollectionQuery.*;
import static ru.fizteh.fivt.students.pavellevap.CQL.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.pavellevap.CQL.Conditions.rlike;
import static ru.fizteh.fivt.students.pavellevap.CQL.Conditions.like;
import static ru.fizteh.fivt.students.pavellevap.CQL.impl.FromStmt.from;

import java.time.LocalDate;
import java.util.List;

public class ConditionsTest {
    private static List<Student> students;

    @BeforeClass
    public static void setUp() {
        students = list(student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                student("smirnov", LocalDate.parse("1986-08-06"), "495"),
                student("smith", LocalDate.parse("1986-08-06"), "495"),
                student("golovanov", LocalDate.parse("1985-04-13"), "497"),
                student("frolov", LocalDate.parse("1989-06-18"), "497"),
                student("petrov", LocalDate.parse("2006-08-06"), "494"));
    }

    @Test
    public void testRlike() {
        assertThat(list(from(students).select(s -> s)
                .where(rlike(Student::getName, ".*ov")).execute()), hasSize(6));
        assertThat(list(from(students).select(s -> s)
                .where(rlike(Student::getName, ".*ov")).execute()), not(hasItem(students.get(3))));

        assertThat(list(from(students).select(s -> s)
                .where(rlike(Student::getName, ".*olo.*")).execute()), hasSize(2));
        assertThat(list(from(students).select(s -> s)
                .where(rlike(Student::getName, ".*olo.*")).execute()), hasItems(students.get(5), students.get(4)));
    }

    @Test
    public void testLike() {
        assertThat(list(from(students).select(s -> s)
                .where(like(Student::getGroup, "495")).execute()), hasSize(3));
        assertThat(list(from(students).select(s -> s)
                .where(like(Student::getGroup, "495")).execute()), not(hasItem(students.get(0))));

        assertThat(list(from(students).select(s -> s)
                .where(like(Student::getGroup, "497")).execute()), hasSize(2));
        assertThat(list(from(students).select(s -> s)
                .where(like(Student::getGroup, "497")).execute()), hasItems(students.get(5), students.get(4)));
    }
}