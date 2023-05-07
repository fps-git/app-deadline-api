package ru.netology.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import ru.netology.data.DataBaseHelper;

import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;

public class APITestHelper {

    DataBaseHelper data = new DataBaseHelper();
    DataBaseHelper.UserData validUser = data.getValidUserData();

    static String token;
    private RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/api")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    public void login() {
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

    public void authentication() {
        JSONObject authRequestBody = new JSONObject()
                .put("login", validUser.getLogin())
                .put("code", data.getValidVerificationCode(validUser));

        token = given()
                .spec(requestSpec)
                .body(authRequestBody.toString())
                .when()
                .post("/auth/verification")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

    public String getCardAmount(String cardNumber) {

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

        var cardID = data.getCardID(cardNumber);

        String cardBalance = null;
        for (int i = 0; i < cardList.size(); i++) {
            if (Objects.equals(cardList.get(i).getId(), cardID)) {
                cardBalance = cardList.get(i).getBalance();
            }
        }
        return cardBalance;
    }

    public void moneyTransfer(String cardFrom, String cardTo, int amount) {

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
