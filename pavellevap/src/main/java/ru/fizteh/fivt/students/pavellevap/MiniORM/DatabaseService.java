package ru.fizteh.fivt.students.pavellevap.MiniORM;

import org.h2.jdbcx.JdbcConnectionPool;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class DatabaseService<T> implements Closeable {
    private Class<T> clazz;
    private String tableName;
    private List<Field> columns;
    private Field primaryKey;
    private JdbcConnectionPool pool;

    public static String getGoodName(String name) throws IllegalArgumentException {
        if (name.length() == 0) {
            return "";
        }

        if (!name.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Некорректное имя");
        }

        StringBuilder goodName = new StringBuilder();
        goodName.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            if (Character.isUpperCase(name.charAt(i))) {
                goodName.append('_');
            }
            goodName.append(Character.toLowerCase(name.charAt(i)));
        }

        return goodName.toString();
    }

    public static boolean isNameGood(String name) {
        return name.matches("[a-z0-9_]*");
    }

    public static <R> String getTableName(Class<R> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Table class must have @Table annotation");
        }

        String tableName = clazz.getAnnotation(Table.class).name();
        if (tableName.equals("")) {
            tableName = getGoodName(clazz.getSimpleName());
        }

        if (!isNameGood(tableName))  {
            throw new IllegalArgumentException("Wrong table name");
        }

        return tableName;
    }

    public static String getColumnName(Field column) {
        if (!column.isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("Column field must have @Column annotation");
        }

        String columnName = column.getAnnotation(Column.class).name();
        if (columnName.equals("")) {
            columnName = getGoodName(column.getName());
        }

        if (!isNameGood(columnName))  {
            throw new IllegalArgumentException("Wrong column name");
        }

        return columnName;
    }

    private void validateTableClass() {
        tableName = getTableName(clazz);

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Table class must have @Table annotation");
        }

        columns = new ArrayList<>();
        primaryKey = null;
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.forEach(column -> {
                    if (column.getAnnotation(Column.class).name().equals(
                            field.getAnnotation(Column.class).name())) {
                        throw new IllegalArgumentException("Column name must be unique");
                    }
                });

                if (!isNameGood(field.getAnnotation(Column.class).name())) {
                    throw new IllegalArgumentException("Column name must be good");
                }

                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    if (primaryKey == null) {
                        primaryKey = field;
                    } else {
                        throw new IllegalArgumentException("Primary key could be only one");
                    }
                }

                columns.add(field);
            } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                throw new IllegalArgumentException("Primary key must be column");
            }
        }
    }

    private void connectToDatabase(String path) throws IOException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No H2 driver found");
        }

        if (path == null) {
            pool = JdbcConnectionPool.create("jdbc:h2:test", "sa", "");
        } else {
            Properties properties = new Properties();
            try (InputStream inputStream = this.getClass().getResourceAsStream(path)) {
                properties.load(inputStream);
            }

            pool = JdbcConnectionPool.create(properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password"));
        }
    }

    public static String getH2TypeName(Class clazz) {
        if (clazz.isArray()) {
            return "ARRAY";
        } else if (clazz.equals(Integer.class)) {
            return "INTEGER";
        } else if (clazz.equals(Byte.class)) {
            return "TINYINT";
        } else if (clazz.equals(Short.class)) {
            return "SMALLINT";
        } else if (clazz.equals(Long.class)) {
            return "BIGINT";
        } else if (clazz.equals(Boolean.class)) {
            return "BOOLEAN";
        } else if (clazz.equals(Float.class)) {
            return "FLOAT";
        } else if (clazz.equals(Double.class)) {
            return "DOUBLE";
        } else if (clazz.equals(Character.class)) {
            return "CHAR";
        } else if (clazz.equals(Time.class)) {
            return "TIME";
        } else if (clazz.equals(Date.class)) {
            return "DATE";
        } else if (clazz.equals(String.class)) {
            return "CLOB";
        } else if (clazz.equals(UUID.class)) {
            return "UUID";
        } else if (clazz.equals(Timestamp.class)) {
            return "TIMESTAMP";
        } else {
            return "OTHER";
        }
    }

    private void execute(String query) throws SQLException {
        try (Connection connection = pool.getConnection()) {
            connection.createStatement().execute(query);
        }
    }

    public DatabaseService(Class<T> clazz) throws IOException {
        this(clazz, null);
    }

    public DatabaseService(Class<T> clazz, String properties) throws IOException {
        this.clazz = clazz;

        validateTableClass();
        connectToDatabase(properties);
    }

    void createTable() throws SQLException {
        List<String> columnsDescriptions = new LinkedList<>();

        for (Field column : columns) {
            StringBuilder columnDescription = new StringBuilder();

            columnDescription.append(getColumnName(column)).append(" ").append(getH2TypeName(column.getType()));
            if (column == primaryKey) {
                columnDescription.append(" PRIMARY KEY");
            }

            columnsDescriptions.add(columnDescription.toString());
        }

        String query = columnsDescriptions.stream().collect(
                Collectors.joining(", ", "CREATE TABLE IF NOT EXISTS (", ")"));
        execute(query);
    }

    void dropTable() throws SQLException {
        execute("DROP TABLE IF EXISTS " + tableName);
    }

    public void insert(T record) {
        throw new UnsupportedOperationException();
    }

    public void delete(T record) {
        throw new UnsupportedOperationException();
    }

    public void update(T record) {
        throw new UnsupportedOperationException();
    }

    public List<T> queryForAll() {
        throw new UnsupportedOperationException();
    }

    public <K> T queryById(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        if (pool != null) {
            pool.dispose();
        }
    }
}
