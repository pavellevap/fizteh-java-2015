package ru.fizteh.fivt.students.pavellevap.MiniORM;

import java.sql.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import ru.fizteh.fivt.students.pavellevap.MiniORM.Annotations.*;
import ru.fizteh.fivt.students.pavellevap.MiniORM.Exceptions.*;

import org.h2.jdbcx.JdbcConnectionPool;

public class DatabaseService<T> implements Closeable {
    private Class<T> clazz;
    private String tableName;
    private List<Field> columns;
    private List<String> columnNames;
    private Field primaryKey;
    private JdbcConnectionPool pool;

    public static String getGoodName(String name) throws IllegalArgumentException {
        if (name.length() == 0) {
            return "";
        }

        if (!name.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Incorrect name");
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
        return name.matches("[a-z_][a-z0-9_]*");
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
        columnNames = new ArrayList<>();
        primaryKey = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.forEach(column -> {
                    if (getColumnName(column).equals(
                            getColumnName(field))) {
                        throw new IllegalArgumentException("Column name must be unique");
                    }
                });

                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    if (primaryKey == null) {
                        primaryKey = field;
                    } else {
                        throw new IllegalArgumentException("Primary key could be only one");
                    }
                }

                columns.add(field);
                columnNames.add(getColumnName(field));
            } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                throw new IllegalArgumentException("Primary key must be column");
            }
        }
    }

    private void connectToDatabase(String path) throws IOException {
        if (path == null) {
            pool = JdbcConnectionPool.create("jdbc:h2:~/test", "test", "test");
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

    public DatabaseService(Class<T> clazz) throws IOException {
        this(clazz, null);
    }

    public DatabaseService(Class<T> clazz, String properties) throws IOException {
        this.clazz = clazz;

        validateTableClass();
        connectToDatabase(properties);
    }

    private void setRecord(T record, ResultSet resultSet) throws ORMException {
        try {
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).getClass().isAssignableFrom(Number.class)) {
                    columns.get(i).set(record, resultSet.getLong(i + 1));
                } else if (columns.get(i).getType() == String.class) {
                    Clob str = resultSet.getClob(i + 1);
                    columns.get(i).set(record, str.getSubString(1, (int) str.length()));
                } else {
                    columns.get(i).set(record, resultSet.getObject(i + 1));
                }
            }
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }
    }

    public <K> T queryById(K key) throws ORMException {
        if (primaryKey == null) {
            throw new IllegalArgumentException("Can't retrieve from table without primary key");
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(tableName).append(" WHERE ")
                .append(primaryKey.getName()).append(" = ?");

        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            statement.setString(1, key.toString());
            statement.execute();

            try (ResultSet resultSet = statement.getResultSet()) {
                resultSet.next();
                T record = clazz.newInstance();
                setRecord(record, resultSet);

                return record;
            }
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }
    }

    public List<T> queryForAll() throws ORMException {
        List<T> result = new LinkedList<>();

        try (Connection connection = pool.getConnection()) {
            Statement statement = connection.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
                while (resultSet.next()) {
                    T record = clazz.newInstance();
                    setRecord(record, resultSet);
                    result.add(record);
                }
            }
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }

        return result;
    }

    public void insert(T record) throws ORMException {
        StringBuilder query = new StringBuilder();

        query.append(columnNames.stream().collect(Collectors.
                joining(", ", "INSERT INTO " + tableName + " (", ")")));

        List<String> values = new LinkedList<>();
        columns.forEach(column -> values.add("?"));

        query.append(values.stream().collect(Collectors.
                joining(", ", "VALUES (", ")")));

        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            for (int i = 0; i < columns.size(); i++) {
                statement.setObject(i + 1, columns.get(i).get(record));
            }
            statement.execute();
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }
    }

    public void update(T record) throws ORMException {
        if (primaryKey == null) {
            throw new IllegalArgumentException("Can't update table without primary key");
        }

        String query = columnNames.stream().collect(Collectors.joining(" = ?, ", "UPDATE " + tableName + " SET ",
                " = ? WHERE " + getColumnName(primaryKey) + " = ?"));

        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < columns.size(); i++) {
                statement.setObject(i + 1, columns.get(i).get(record));
            }
            statement.setObject(columns.size() + 1, primaryKey.get(record));
            statement.execute();
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }
    }

    public void delete(T record) throws ORMException {
        if (primaryKey == null) {
            throw new IllegalArgumentException("Can't delete from table without primary key");
        }

        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ").append(tableName).append(" WHERE ")
                .append(getColumnName(primaryKey)).append(" = ?");

        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            statement.setObject(1, primaryKey.get(record));
            statement.execute();
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }
    }

    void createTable() throws ORMException {
        List<String> columnsDescriptions = new LinkedList<>();

        for (Field column : columns) {
            StringBuilder columnDescription = new StringBuilder();

            columnDescription.append(getColumnName(column)).append(" ").append(getH2TypeName(column.getType()));
            if (column.equals(primaryKey)) {
                columnDescription.append(" PRIMARY KEY");
            }

            columnsDescriptions.add(columnDescription.toString());
        }

        String query = columnsDescriptions.stream().collect(
                Collectors.joining(", ", "CREATE TABLE IF NOT EXISTS " + tableName + " (", ")"));

        try (Connection connection = pool.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }
    }

    void dropTable() throws ORMException {
        try (Connection connection = pool.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (Exception ex) {
            throw new ORMException(ex.getMessage());
        }

    }

    @Override
    public void close() throws IOException {
        if (pool != null) {
            pool.dispose();
        }
    }
}
