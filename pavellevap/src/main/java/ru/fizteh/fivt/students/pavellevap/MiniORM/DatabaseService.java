package ru.fizteh.fivt.students.pavellevap.MiniORM;

import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public class DatabaseService<T> {
    private Class<T> clazz;
    private String tableName;
    private List<Field> columns;
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

    private void validateTableClass() {
        tableName = getTableName(clazz);

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Table class must have @Table annotation");
        }

        Field primaryKey = null;
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
            pool = JdbcConnectionPool.create("jdbc:h2:test","sa", "");
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

    public DatabaseService(Class<T> clazz) throws IOException {
        this(clazz, null);
    }

    public DatabaseService(Class<T> clazz, String properties) throws IOException {
        this.clazz = clazz;

        validateTableClass();
        connectToDatabase(properties);
    }


}