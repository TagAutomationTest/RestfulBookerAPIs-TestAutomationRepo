package Listners;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class AllureRestAssuredFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        // Execute request
        Response response = ctx.next(requestSpec, responseSpec);
        // Log request as Allure Step
        Allure.step("➡ Request: " + requestSpec.getMethod() + " " + requestSpec.getURI(),
                () -> {
                    Allure.addAttachment("Request Headers", requestSpec.getHeaders().toString());
                    if (requestSpec.getBody() != null) {
                        Allure.addAttachment("Request Body", "application/json", requestSpec.getBody().toString(), "json");
                    }
                }
        );
// Log response as Allure Step
        Allure.step("⬅ Response: " + response.getStatusCode(),
                () -> {
                    Allure.addAttachment("Response Headers", response.getHeaders().toString());
                    Allure.addAttachment("Response Body", "application/json", response.getBody().asPrettyString(), "json");

                }
        );

        return response;
    }
}
