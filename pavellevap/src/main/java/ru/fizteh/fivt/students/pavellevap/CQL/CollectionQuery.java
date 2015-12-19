package ru.fizteh.fivt.students.pavellevap.CQL;

import ru.fizteh.fivt.students.pavellevap.CQL.impl.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static ru.fizteh.fivt.students.pavellevap.CQL.Aggregates.avg;
import static ru.fizteh.fivt.students.pavellevap.CQL.Aggregates.count;
import static ru.fizteh.fivt.students.pavellevap.CQL.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.pavellevap.CQL.Conditions.rlike;
import static ru.fizteh.fivt.students.pavellevap.CQL.OrderByConditions.asc;
import static ru.fizteh.fivt.students.pavellevap.CQL.OrderByConditions.desc;
import static ru.fizteh.fivt.students.pavellevap.CQL.Sources.list;
import static ru.fizteh.fivt.students.pavellevap.CQL.impl.FromStmt.from;

public class CollectionQuery {

    public static void main(String[] args) {
        List<Student> students = list(
                student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                student("smirnov", LocalDate.parse("1986-08-06"), "495"),
                student("smith", LocalDate.parse("1986-08-06"), "495"),
                student("golovanov", LocalDate.parse("1985-04-13"), "497"),
                student("frolov", LocalDate.parse("1989-06-18"), "497"),
                student("petrov", LocalDate.parse("2006-08-06"), "494"));

        List<Group> groups = list(new Group("494", "mr.sidorov"),
                new Group("495", "mr.noname"),
                new Group("497", "ms.somename"));

        Iterable<Tuple<Statistics, Group>> statistics =
                from(from(students)
                .select(Statistics.class, Student::getGroup, count(Student::getName), avg(Student::age))
                .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                .groupBy(Student::getGroup)
                .having(s -> s.getGroup().equals("495") || s.getGroup().equals("494"))
                .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                .union()
                .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494"),
                           student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age)))
                        .join(groups)
                        .on(Statistics::getGroup, Group::getGroup)
                        .select(s -> s).execute();
        System.out.println(statistics);


        Iterable<Tuple<String, String>> mentorsByStudent =
                from(students)
                .join(groups)
                .on(Student::getGroup, Group::getGroup)
                .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                .execute();
        System.out.println(mentorsByStudent);
    }

    public static class Student {
        private final String name;

        private final LocalDate dateOfBirth;

        private final String group;

        public String getName() {
            return name;
        }

        public Student(String name, LocalDate dateOfBirth, String group) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.group = group;
        }

        public Student(Student s) {
            this(s.getName(), s.getDateOfBirth(), s.getGroup());
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public String getGroup() {
            return group;
        }

        public long age() {
            return ChronoUnit.YEARS.between(getDateOfBirth(), LocalDateTime.now());
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Student) {
                Student s = (Student) obj;
                return s.toString().equals(toString());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        public String toString() {
            return name + " " + dateOfBirth + " " + group;
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(String group, String mentor) {
            this.group = group;
            this.mentor = mentor;
        }

        @Override
        public String toString() {
            return "Group{"
                    + "group='" + group + "\'"
                    + ", mentor='" + mentor + "\'}";
        }

        public String getGroup() {
            return group;
        }

        public String getMentor() {
            return mentor;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Group) {
                Group g = (Group) obj;
                return group.equals(g.group) && mentor.equals(g.mentor);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    public static class Statistics {

        private final String group;
        private final Long count;
        private final Long age;

        public String getGroup() {
            return group;
        }

        public Long getCount() {
            return count;
        }

        public Long getAge() {
            return age;
        }

        public Statistics(String group, Long count, Long age) {
            this.group = group;
            this.count = count;
            this.age = age;
        }

        public Statistics(String group, Long count, Double age) {
            this.group = group;
            this.count = count;
            this.age = age.longValue();
        }

        @Override
        public String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + '}';
        }

        @Override
        public int hashCode() {
            return (group + age + count).hashCode();
        }

        @Override
        public boolean equals(Object s) {
            if (s instanceof Statistics) {
                Statistics statistics = (Statistics) s;
                return group.equals(statistics.group) && age.equals(statistics.age) && count.equals(statistics.count);
            } else {
                return false;
            }
        }
    }
}
