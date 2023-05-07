package ru.netology.test;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;

public class ClearTables {

    @Test
    @SneakyThrows
    void serviceMethodForDBsClearance() {
        var runner = new QueryRunner();
        String userExist;
        var deleteT1 = "DELETE FROM card_transactions;";
        var deleteT2 = "DELETE FROM cards";
        var deleteT3 = "DELETE FROM auth_codes;";
        var deleteT4 = "DELETE FROM users;";
        var checkUsersTable = "SELECT login FROM users LIMIT 1;";
        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            runner.execute(conn, deleteT1);
            runner.execute(conn, deleteT2);
            runner.execute(conn, deleteT3);
            runner.execute(conn, deleteT4);
            userExist = runner.query(conn, checkUsersTable, new ScalarHandler<>());
        }
        Assertions.assertNull(userExist);
    }
}
