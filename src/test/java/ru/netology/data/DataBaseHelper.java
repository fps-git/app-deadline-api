package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class DataBaseHelper {

    @Value
    public static class UserData {
        String login;
        String password;
    }

    @Value
    public static class CardData {
        String id;
        String number;
        String balance;
    }

    static String loginDB = "app";
    static String passwordDB = "pass";

    public static UserData getValidUserData() {
        return new UserData("vasya", "qwerty123");
    }

    @SneakyThrows
    public static String getCardID(String cardNumber) {
        var runner = new QueryRunner();
        String cardID;

        var getID = "SELECT id FROM cards WHERE number = ?;";
        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", loginDB, passwordDB
                );
        ) {
            cardID = runner.query(conn, getID, new ScalarHandler<>(), cardNumber);
        }
        return cardID;
    }

    @SneakyThrows
    public static String getValidVerificationCode(UserData user) {
        var runner = new QueryRunner();
        String verificationCode;

        var getID = "SELECT id FROM users WHERE login = ?;";
        var getCode = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC LIMIT 1;";
        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", loginDB, passwordDB
                );
        ) {
            String id = runner.query(conn, getID, new ScalarHandler<>(), user.getLogin());
            verificationCode = runner.query(conn, getCode, new ScalarHandler<>(), id);
        }
        return verificationCode;
    }


    @SneakyThrows
    public static void setCardBalance(String cardNumber, String balance) {
        var runner = new QueryRunner();

        var setBalance = "UPDATE cards SET balance_in_kopecks= ? WHERE number = ?;";
        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", loginDB, passwordDB
                );
        ) {
            runner.update(conn, setBalance, balance + "00", cardNumber);
        }
    }
}
