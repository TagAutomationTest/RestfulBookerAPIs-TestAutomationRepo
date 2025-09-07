package Repositories;

import Pojos.BookingDto;
import Utils.Validations;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BookingApi {
    private Response response;
    private int bookingId;

    public BookingApi createBooking(BookingDto booking) {
        response = given()
                .contentType("application/json")
                .body(booking)
                .when()
                .post("/booking")
                .then().log().all().extract().response();
        bookingId = response.then().extract().path("bookingid");
        return this;
    }


    public BookingApi validateBookingIsCreated() {
        Validations.validateEquals(response.statusCode(), 200, "booking not created");
        Validations.validateTrue(matchesJsonSchemaInClasspath("Schema/create-booking-schema.json").matches(response.getBody().asString()), "Response does not match schema!");

        return this;
    }

    public BookingApi updateBooking(BookingDto booking) {
        response = given()
                .contentType("application/json")
                .cookie("token", AuthApi.generateToken()) // Token from auth API
                .body(booking)
                .log().all()
                .when()
                .put("/booking/" + bookingId)
                .then().log().all().extract().response();
        return this;
    }


    public BookingApi validateBookingIsUpdated() {
        Validations.validateEquals(response.statusCode(), 200, "booking not updated");
        Validations.validateTrue(matchesJsonSchemaInClasspath("Schema/update-booking-schema.json").matches(response.getBody().asString()), "Response does not match schema!");
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

    public BookingApi validateBookingIsDeleted() {
        response = given()
                .when()
                .log().all()
                .get("/booking/" + bookingId)
                .then()
                .log().all().extract().response();
        Validations.validateEquals(response.statusCode(), 404, "booking not updated");
        return this;
    }
}
