package Repositories;

import Pojos.BookingDto;
import Utils.Validations;
import clients.RestClient;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class BookingApi {
    private Response response;
    private int bookingId;
    private BookingDto createdBookingDto;
    private BookingDto updatedBookingDto;
    private Map<String, Object> patchedBookingPayload;
    private Map<String, String> bookingDates;

    public BookingApi createBooking(BookingDto bookingDto) {
        this.createdBookingDto = bookingDto; // keep original dto for later validation
        response = given()
                .contentType("application/json")
                .body(bookingDto)
                .when()
                .post("/booking")
                .then().log().all().extract().response();
        // Only extract bookingId if status code is 200 or 201
        if (response.statusCode() == 200 || response.statusCode() == 201) {
            bookingId = response.then().extract().path("bookingid");
        }
        return this;
    }


    public BookingApi validateCreateBookingResponse() {
        Validations.validateEquals(response.statusCode(), 200, "booking not created");
        Validations.validateTrue(matchesJsonSchemaInClasspath("Schema/create-booking-schema.json")
                        .matches(response.getBody().asString()),
                "Response does not match schema!");

        return this;
    }

    public BookingApi validateBookingIsCreated() {
        getBookingById();
        validateBookingIsFetched();
        Validations.validateEquals(response.jsonPath().getString("firstname"), createdBookingDto.getFirstname(), "firstname not matched");
        Validations.validateEquals(response.jsonPath().getString("lastname"), createdBookingDto.getLastname(), "lastname not matched");
        return this;
    }


    public BookingApi validateBookingIsNotCreated(int expectedStatusCode, String expectedMessage) {
        Validations.validateEquals(
                response.statusCode(),
                expectedStatusCode,
                "Unexpected status code: Booking should NOT be created"
        );
        if (expectedMessage != null && !expectedMessage.isEmpty()) {
            Validations.validateEquals(
                    response.asString(),
                    expectedMessage,
                    "Expected error message not found in response body"
            );
        }

        return this;
    }

    public BookingApi updateBooking(BookingDto updatedDto ,String authType) {
        this.updatedBookingDto = updatedDto; // keep original dto for later validation
        response = RestClient.withAuth(authType)
                .body(updatedDto)
                .log().all()
                .when()
                .put("/booking/" + bookingId)
                .then().log().all().extract().response();
        return this;
    }

    public BookingApi validateAuthResponse(int expectedStatusCode, String expectedMessage) {
        Validations.validateEquals(expectedStatusCode, response.getStatusCode(),
                "Expected status code " + expectedStatusCode + " but got " + response.getStatusCode());
        if (expectedMessage != null && !expectedMessage.isEmpty()) {
            Validations.validateTrue( response.asString().contains(expectedMessage),
                    "Expected response to contain message: " + expectedMessage + "\nActual: " +  response.asString());
        }

        return this; // chainable
    }
    public BookingApi getBookingById() {
        response = given()
                .when()
                .log().all()
                .get("/booking/" + bookingId)
                .then()
                .log().all().extract().response();
        return this;
    }

    public BookingApi validateUpdateBookingResponse() {
        Validations.validateEquals(response.statusCode(), 200, "booking not updated");
        Validations.validateTrue(matchesJsonSchemaInClasspath("Schema/update-booking-schema.json").matches(response.getBody().asString()), "Response does not match schema!");
        return this;
    }

    public BookingApi validateBookingIsUpdated() {
        getBookingById();
        validateBookingIsFetched();
        Validations.validateEquals(response.jsonPath().getString("firstname"), updatedBookingDto.getFirstname(), "firstname not matched");
        Validations.validateEquals(response.jsonPath().getString("lastname"), updatedBookingDto.getLastname(), "lastname not matched");

        return this;
    }

    public BookingApi partialUpdateBooking(Map<String, Object> patchPayload) {
        this.patchedBookingPayload = patchPayload; // keep original dto for later validation
        response = given()
                .contentType("application/json")
                .cookie("token", AuthApi.generateToken()) // Token from auth API
                .body(patchPayload)
                .log().all()
                .when()
                .patch("/booking/" + bookingId)
                .then().log().all().extract().response();
        return this;
    }

    public BookingApi validatePatchBookingResponse() {
        Validations.validateEquals(response.statusCode(), 200, "booking not patched");
        Validations.validateTrue(matchesJsonSchemaInClasspath("Schema/update-booking-schema.json").matches(response.getBody().asString()), "Response does not match schema!");
        return this;
    }

    public BookingApi validateBookingIsPatched1(String Scenario) {
        getBookingById();
        validateBookingIsFetched();
        if (Scenario.equalsIgnoreCase("updateName")) {
            Validations.validateEquals(response.jsonPath().getString("firstname"), patchedBookingPayload.get("firstname").toString(), "firstname not matched");
            Validations.validateEquals(response.jsonPath().getString("lastname"), patchedBookingPayload.get("lastname").toString(), "lastname not matched");
        }
        if (Scenario.equalsIgnoreCase("updatePrice")) {
            Validations.validateEquals(response.jsonPath().getString("totalprice"), patchedBookingPayload.get("totalprice").toString(), "totalprice not matched");

        }
        if (Scenario.equalsIgnoreCase("updateNeeds")) {

            Validations.validateEquals(response.jsonPath().getString("additionalneeds"), patchedBookingPayload.get("additionalneeds").toString(), "additionalneeds not matched");

        }
        if (Scenario.equalsIgnoreCase("updateDates")) {

            Validations.validateEquals(response.jsonPath().getString("bookingdates.checkin"),
                    patchedBookingPayload.get("bookingdates.checkin").toString(), "checkin date not matched");
            Validations.validateEquals(response.jsonPath().getString("bookingdates.checkout"),
                    patchedBookingPayload.get("bookingdates.checkout").toString(), "checkout date not matched");

        }

        return this;
    }

    public BookingApi validateBookingIsPatched(String scenario) {
        getBookingById();
        validateBookingIsFetched();

        switch (scenario.toLowerCase()) {
            case "updatename":
                Validations.validateEquals(
                        response.jsonPath().getString("firstname"),
                        patchedBookingPayload.get("firstname").toString(),
                        "Firstname not matched"
                );
                Validations.validateEquals(
                        response.jsonPath().getString("lastname"),
                        patchedBookingPayload.get("lastname").toString(),
                        "Lastname not matched"
                );
                break;

            case "updateprice":
                Validations.validateEquals(
                        response.jsonPath().getInt("totalprice"),
                        Integer.parseInt(patchedBookingPayload.get("totalprice").toString()),
                        "Total price not matched"
                );
                break;

            case "updateneeds":
                Validations.validateEquals(
                        response.jsonPath().getString("additionalneeds"),
                        patchedBookingPayload.get("additionalneeds").toString(),
                        "Additional needs not matched"
                );
                break;

            case "updatedates":
                bookingDates = (Map<String, String>) patchedBookingPayload.get("bookingdates");
                Validations.validateEquals(
                        response.jsonPath().getString("bookingdates.checkin"),
                        bookingDates.get("checkin").toString(),
                        "checkin date not matched");

                Validations.validateEquals(
                        response.jsonPath().getString("bookingdates.checkout"),
                        bookingDates.get("checkout").toString(),
                        "checkout date not matched");
                break;

            default:
                throw new IllegalArgumentException("Unsupported patch validation scenario: " + scenario);
        }

        return this;
    }

    public BookingApi deleteBooking() {
        response = given()
                .contentType("application/json")
                .cookie("token", AuthApi.generateToken())
                .when()
                .delete("/booking/" + bookingId)
                .then().log().all().extract().response();
        return this;
    }


    public BookingApi validateBookingIsFetched() {
        Validations.validateEquals(response.statusCode(), 200, "booking didn't retrieve");
        return this;
    }

    public BookingApi validateBookingIsDeleted() {
        getBookingById();
        Validations.validateEquals(response.statusCode(), 404, "booking didn't Delete");
        return this;
    }
}
