package ru.fizteh.fivt.students.pavellevap.MiniORM;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import static ru.fizteh.fivt.students.pavellevap.MiniORM.DatabaseService.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.pavellevap.MiniORM.Annotations.*;
import ru.fizteh.fivt.students.pavellevap.MiniORM.Exceptions.ORMException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DatabaseServiceTest {

    @Table
    static class SimpleTable{
        SimpleTable() { }

        SimpleTable(Integer a, String b) {
            x = a;
            y = b;
        }

        @PrimaryKey
        @Column
        Integer x;

        @Column
        String y;

        @Override
        public String toString() {
            return x + " " + y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof  SimpleTable) {
                SimpleTable st = (SimpleTable) obj;
                return x.equals(st.x) && y.equals(st.y);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    DatabaseService<SimpleTable> dbs;

    @Before
    public void setUp() throws IOException {
        dbs = new DatabaseService<SimpleTable>(SimpleTable.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructor_() throws IOException {
        class TestClass {
        }
        new DatabaseService<TestClass>(TestClass.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructor_2() throws IOException {
        @Table(name = "BadName")
        class TestClass {
        }
        new DatabaseService<TestClass>(TestClass.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructor_3() throws IOException {
        @Table(name = "good_name")
        class TestClass {
            @Column (name = "BadName")
            Integer x;
        }
        new DatabaseService<TestClass>(TestClass.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructor_4() throws IOException {
        @Table(name = "good_name")
        class TestClass {
            @Column(name = "good_name")
            Integer x;
            @Column(name = "good_name")
            Integer y;
        }
        new DatabaseService<TestClass>(TestClass.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructor_5() throws IOException {
        @Table(name = "good_name")
        class TestClass {
            @PrimaryKey
            @Column(name = "good_name_1")
            Integer x;
            @PrimaryKey
            @Column(name = "good_name_2")
            Integer y;
        }
        new DatabaseService<TestClass>(TestClass.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructor_6() throws IOException {
        @Table(name = "good_name")
        class TestClass {
            @PrimaryKey
            Integer x;
        }
        new DatabaseService<TestClass>(TestClass.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetGoodName() {
        assertThat(getGoodName("AbraCadabra"), is("abra_cadabra"));
        assertThat(getGoodName("abra_cadabra"), is("abra_cadabra"));
        assertThat(getGoodName("abracadabra"), is("abracadabra"));
        assertThat(getGoodName("_ABC123"), is("__a_b_c123"));
        getGoodName("123abc");
    }

    @Test
    public void testEverything() throws ORMException {
        try {
            dbs.createTable();

            dbs.insert(new SimpleTable(1, "a"));
            dbs.insert(new SimpleTable(2, "b"));
            dbs.insert(new SimpleTable(3, "c"));
            dbs.insert(new SimpleTable(4, "d"));

            assertThat(dbs.queryForAll(), containsInAnyOrder(
                    new SimpleTable(1, "a"),
                    new SimpleTable(2, "b"),
                    new SimpleTable(3, "c"),
                    new SimpleTable(4, "d")
            ));
            assertThat(dbs.queryById(1), is(new SimpleTable(1, "a")));
            assertThat(dbs.queryById(2), is(new SimpleTable(2, "b")));
            assertThat(dbs.queryById(3), is(new SimpleTable(3, "c")));
            assertThat(dbs.queryById(4), is(new SimpleTable(4, "d")));

            dbs.update(new SimpleTable(4, "D"));
            dbs.update(new SimpleTable(3, "C"));
            dbs.update(new SimpleTable(2, "B"));

            assertThat(dbs.queryForAll(), containsInAnyOrder(
                    new SimpleTable(1, "a"),
                    new SimpleTable(2, "B"),
                    new SimpleTable(3, "C"),
                    new SimpleTable(4, "D")
            ));
            assertThat(dbs.queryById(1), is(new SimpleTable(1, "a")));
            assertThat(dbs.queryById(2), is(new SimpleTable(2, "B")));
            assertThat(dbs.queryById(3), is(new SimpleTable(3, "C")));
            assertThat(dbs.queryById(4), is(new SimpleTable(4, "D")));

            dbs.delete(new SimpleTable(4, ""));

            assertThat(dbs.queryForAll(), containsInAnyOrder(
                    new SimpleTable(1, "a"),
                    new SimpleTable(2, "B"),
                    new SimpleTable(3, "C")
                ));
            assertThat(dbs.queryById(1), is(new SimpleTable(1, "a")));
            assertThat(dbs.queryById(2), is(new SimpleTable(2, "B")));
            assertThat(dbs.queryById(3), is(new SimpleTable(3, "C")));
        } finally {
            dbs.dropTable();
        }
    }
}
