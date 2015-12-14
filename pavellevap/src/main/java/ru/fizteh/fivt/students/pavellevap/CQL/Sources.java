package ru.fizteh.fivt.students.pavellevap.CQL;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Sources {

    @SafeVarargs
    public static <T> List<T> list(T... items) {
        return Arrays.asList(items);
    }

    @SafeVarargs
    public static <T> Set<T> set(T... items) {
        throw new UnsupportedOperationException();
    }

    public static <T> Stream<T> lines(InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    public static <T> Stream<T> lines(Path file) {
        throw new UnsupportedOperationException();
    }

}
