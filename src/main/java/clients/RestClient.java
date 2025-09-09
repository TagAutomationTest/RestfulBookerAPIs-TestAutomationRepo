package clients;

import Repositories.AuthApi;
import Utils.AllureRestAssuredFilter;
import Utils.PropertiesUtils;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;


public class RestClient {
    static {
        RestAssured.baseURI = PropertiesUtils.getPropertyValue("base.url");
        RestAssured.useRelaxedHTTPSValidation(); // if https cert issue
        RestAssured.filters(new AllureRestAssuredFilter()); // log requests/responses to Allure
        System.out.println("✅ RestClient initialized with BaseURI: " + RestAssured.baseURI);
    }

    public static RequestSpecification withAuth(String authType) {
        RequestSpecification request = RestAssured.given()
                .contentType("application/json")
                .log().all();

        if (authType == null) return request;

        switch (authType.toLowerCase()) {
            case "valid":
                request.cookie("token", AuthApi.generateToken());
                break;
            case "invalid":
                request.cookie("token", "invalid_token_123");
                break;
            case "none":
                // no cookie
                break;
            case "expired":
                request.cookie("token", AuthApi.getExpiredToken()); // 🔑 implement this
                break;
            default:
                throw new IllegalArgumentException("Unknown authType: " + authType);
        }
        return request;
    }
}


