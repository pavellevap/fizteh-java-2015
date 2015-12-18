package ru.fizteh.fivt.students.pavellevap.MiniORM;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface PreparedStatementGenerator {
    PreparedStatement generateStatement(Connection connection) throws Exception;
}
