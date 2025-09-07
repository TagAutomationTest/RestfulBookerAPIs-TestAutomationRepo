package clients;

import Listners.AllureRestAssuredFilter;
import Utils.PropertiesUtils;
import io.restassured.RestAssured;


public class RestClient {
    static {
        RestAssured.baseURI = PropertiesUtils.getPropertyValue("base.url");
        RestAssured.useRelaxedHTTPSValidation(); // if https cert issue
        RestAssured.filters(new AllureRestAssuredFilter()); // log requests/responses to Allure
        System.out.println("✅ RestClient initialized with BaseURI: " + RestAssured.baseURI);
    }
}


