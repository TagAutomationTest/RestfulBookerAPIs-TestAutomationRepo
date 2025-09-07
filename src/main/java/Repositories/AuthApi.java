package Repositories;


import Utils.PropertiesUtils;
import io.qameta.allure.Step;
import io.restassured.RestAssured;

import java.util.Map;


public class AuthApi {
    @Step("Generate authentication token")
    public static String generateToken() {
        return RestAssured.given()
                .contentType("application/json")
                .body(Map.of("username", PropertiesUtils.getPropertyValue("auth.username"),
                        "password", PropertiesUtils.getPropertyValue("auth.password")))
                .log()
                .all()
                .post("/auth")
                .then()
                .statusCode(200)
                .log()
                .all()
                .extract().jsonPath().getString("token");

    }
}

