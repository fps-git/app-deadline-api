package ru.netology.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;

public class APITestHelper {

    static DataBaseHelper.UserData validUser = DataBaseHelper.getValidUserData();

    private static String token;
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/api")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    public static void login() {
        JSONObject loginRequestBody = new JSONObject()
                .put("login", validUser.getLogin())
                .put("password", validUser.getPassword());
        given()
                .spec(requestSpec)
                .body(loginRequestBody.toString())
                .when()
                .post("/auth")
                .then()
                .statusCode(200);
    }

    public static void authentication() {
        JSONObject authRequestBody = new JSONObject()
                .put("login", validUser.getLogin())
                .put("code", DataBaseHelper.getValidVerificationCode(validUser));

        token = given()
                .spec(requestSpec)
                .body(authRequestBody.toString())
                .when()
                .post("/auth/verification")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

    public static String getCardAmount(String cardNumber) {

        String response = given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards")
                .then()
                .statusCode(200)
                .extract().response().asString();

        Gson gson = new Gson();
        List<DataBaseHelper.CardData> cardList = gson.
                fromJson(response, new TypeToken<List<DataBaseHelper.CardData>>() {
                }.getType());

        var cardID = DataBaseHelper.getCardID(cardNumber);

        String cardBalance = null;
        for (int i = 0; i < cardList.size(); i++) {
            if (Objects.equals(cardList.get(i).getId(), cardID)) {
                cardBalance = cardList.get(i).getBalance();
            }
        }
        return cardBalance;
    }

    public static void moneyTransfer(String cardFrom, String cardTo, int amount) {

        JSONObject moneyTransferBody = new JSONObject()
                .put("from", cardFrom)
                .put("to", cardTo)
                .put("amount", amount);

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(moneyTransferBody.toString())
                .when()
                .post("/transfer")
                .then()
                .statusCode(200);
    }
}
