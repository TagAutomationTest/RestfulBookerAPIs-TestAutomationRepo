package Factories;

import Pojos.BookingDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;

import java.io.InputStream;
import java.util.UUID;

public class BookingFactory {
    static BookingDto booking;
    static ObjectMapper mapper;
    static InputStream input;
    private static JsonNode bookingData;

    static {
        try {
            mapper = new ObjectMapper();
            input = BookingFactory.class
                    .getClassLoader()
                    .getResourceAsStream("testdata/bookings.json");
            bookingData = mapper.readTree(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bookings.json", e);
        }
    }

    public static BookingDto getBooking(String scenarioKey) {
        try {
            mapper = new ObjectMapper();
            booking = mapper.treeToValue(bookingData.get(scenarioKey), BookingDto.class);
            // Make fields unique
            booking.setFirstname(booking.getFirstname() + "_" + UUID.randomUUID().toString().substring(0, 6));
            booking.setLastname(booking.getLastname() + "_" + System.currentTimeMillis());

            return booking;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map booking for key: " + scenarioKey, e);
        }
    }
}


